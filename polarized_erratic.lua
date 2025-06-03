require("math")

local config = SMODS.current_mod.config

-- Could just write these out but should be neglibible compared to RNG elements
local function getCards()
	local suits = { "Clubs", "Diamonds", "Hearts", "Spades" }
	local ranks = {
		["2"] = 2,
		["3"] = 3,
		["4"] = 4,
		["5"] = 5,
		["6"] = 6,
		["7"] = 7,
		["8"] = 8,
		["9"] = 9,
		["10"] = 10,
		["Jack"] = 10,
		["Queen"] = 10,
		["King"] = 10,
		["Ace"] = 11,
	}

	local cards = {}
	for _, _suit in ipairs(suits) do
		for _rank, _score in pairs(ranks) do
			local _suit_abbr = string.sub(_suit, 1, 1)
			local _rank_abbr = string.sub(_rank, 1, 1)
			if _rank_abbr == "1" then
				_rank_abbr = "T"
			end
			local _key = _suit_abbr .. "_" .. _rank_abbr
			cards[#cards + 1] = { key = _key, suit = _suit, rank = _rank, score = _score }
		end
	end
	return cards
end

-- Balatro starts by generating its own random deck so this function helps me check if that deck is adequate
local function convertDeck(game_deck)
	local deck = {}
	for _, v in ipairs(game_deck) do
		deck[#deck + 1] = {
			-- Don't need key because I only use it to modify the game deck
			suit = v.base.suit,
			rank = v.base.value,
			score = v.base.nominal,
		}
	end
	return deck
end

-- Used to calculate normal CDF
-- Credit: John D. Cook & Greg Hewgill
-- URL: https://hewgill.com/picomath/lua/erf.lua.html
local function erf(x)
	-- constants
	local a1 = 0.254829592
	local a2 = -0.284496736
	local a3 = 1.421413741
	local a4 = -1.453152027
	local a5 = 1.061405429
	local p = 0.3275911

	-- Save the sign of x
	local sign = 1
	if x < 0 then
		sign = -1
	end
	x = math.abs(x)

	-- A&S formula 7.1.26
	local t = 1.0 / (1.0 + p * x)
	local y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * math.exp(-x * x)

	return sign * y
end

-- Given a point x, a mean, and a standard deviation
-- Returns the CDF of x on corresponding normal distribution
local function normalCdf(x, mean, sd)
	local z = (x - mean) / sd
	return (1 + erf(z / 1.41421356)) / 2
end

local function evaluateDeck(deck, score_info, suit_info, rank_info, normal_dist)
	local suit_counts = {
		["Clubs"] = 0,
		["Diamonds"] = 0,
		["Hearts"] = 0,
		["Spades"] = 0,
	}
	local rank_counts = {
		["2"] = 0,
		["3"] = 0,
		["4"] = 0,
		["5"] = 0,
		["6"] = 0,
		["7"] = 0,
		["8"] = 0,
		["9"] = 0,
		["10"] = 0,
		["Jack"] = 0,
		["Queen"] = 0,
		["King"] = 0,
		["Ace"] = 0,
	}

	-- Get counting stats for deck
	local score_total = 0
	for _, _card in ipairs(deck) do
		local _suit = _card.suit
		suit_counts[_suit] = suit_counts[_suit] + 1
		local _rank = _card.rank
		rank_counts[_rank] = rank_counts[_rank] + 1
		local _score = _card.score
		score_total = score_total + _score
	end

	-- Calculate score offset
	local score_offset = score_total - 380

	-- Get max number of any suit in deck
	local max_suit = 13
	for _, v in pairs(suit_counts) do
		if v > max_suit then
			max_suit = v
		end
	end

	-- Get rank movement
	local rank_movement = 0
	for _, v in pairs(rank_counts) do
		if v > 4 then
			rank_movement = rank_movement + v - 4
		end
	end

	-- Retrieve percentiles
	local score_index_offset = score_info.offset
	local score_dist = score_info.dist
	local score_percentile = score_dist[score_offset + score_index_offset]
	local suit_index_offset = suit_info.offset
	local suit_dist = suit_info.dist
	local suit_percentile = suit_dist[max_suit + suit_index_offset]
	local rank_index_offset = rank_info.offset
	local rank_dist = rank_info.dist
	local rank_percentile = rank_dist[rank_movement + rank_index_offset]

	-- Calculate placement on normal distribution
	local combined = (score_percentile + suit_percentile + rank_percentile) / 3
	local mean = normal_dist.mean
	local sd = normal_dist.sd
	local ovr_percentile = normalCdf(combined, mean, sd)

	return ovr_percentile
end

SMODS.Back:take_ownership("erratic", {
	apply = function()
		G.E_MANAGER:add_event(Event({
			func = function()
				local deck_count = 0
				local deck = convertDeck(G.playing_cards)
				local cumulatives = config.cumulatives
				local normal_dist = config.normal_dist
				local threshold = 1 - (1 / config.avg_tries)
				local cards = getCards()

				local deck_score = evaluateDeck(
					deck,
					cumulatives.score_total,
					cumulatives.suit_max,
					cumulatives.rank_movement,
					normal_dist
				)
				while deck_score < threshold do
					-- Create a random deck
					deck = {}
					for i = 52, 1, -1 do
						deck[i] = cards[math.random(#cards)]
					end
					deck_count = deck_count + 1
					-- Score the deck
					deck_score = evaluateDeck(
						deck,
						cumulatives.score_total,
						cumulatives.suit_max,
						cumulatives.rank_movement,
						normal_dist
					)
				end

				-- Deck passed threshold, update if necessary
				if deck_count > 0 then
					for i = #G.playing_cards, 1, -1 do
						G.playing_cards[i]:set_base(G.P_CARDS[deck[i].key])
					end
				end

				print("Tried " .. (deck_count + 1) .. " decks until " .. deck_score .. " >= " .. threshold)
				return true
			end,
		}))
	end,
})
