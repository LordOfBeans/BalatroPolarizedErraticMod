require "math"

local function checkPolarization(deck)
	suit_counts = {
		['Clubs'] = 0,
		['Diamonds'] = 0,
		['Hearts'] = 0,
		['Spades'] =  0,
	}
	rank_counts = {
		['2'] = 0,
		['3'] = 0,
		['4'] = 0,
		['5'] = 0,
		['6'] = 0,
		['7'] = 0,
		['8'] = 0,
		['9'] =  0,
		['10'] = 0,
		['Jack'] = 0,
		['Queen'] = 0,
		['King'] = 0,
		['Ace'] = 0,
	}
	score_total = 0

	for _, _card in ipairs(deck) do
		_suit = _card.suit
		suit_counts[_suit] = suit_counts[_suit] + 1
		_rank = _card.rank
		rank_counts[_rank] = rank_counts[_rank] + 1
		_score = _card.score
		score_total = score_total + _score
	end

	suit_imbalance = 0
	for _, v in pairs(suit_counts) do
		suit_imbalance = suit_imbalance + math.abs(13 - v)
	end

	rank_imbalance = 0
	for _, v in pairs(rank_counts) do
		rank_imbalance = rank_imbalance + math.abs(13 - v)
	end

	if score_total >= 440 and suit_imbalance >= 18 then
		return true
	end
	return false
end

-- Could just write these out but should be neglibible compared to RNG elements
local function getCards()
	local suits = { 'Clubs', 'Diamonds', 'Hearts', 'Spades' }
	local ranks = {
		['2'] = 2,
		['3'] = 3,
		['4'] = 4,
		['5'] = 5,
		['6'] = 6,
		['7'] = 7,
		['8'] = 8,
		['9'] = 9,
		['10'] = 10,
		['Jack'] = 10,
		['Queen'] = 10,
		['King'] = 10,
		['Ace'] = 11
	}
	
	cards = {}
	for _, _suit in ipairs(suits) do
		for _rank, _score in pairs(ranks) do
			_suit_abbr = string.sub(_suit, 1, 1)
			_rank_abbr = string.sub(_rank, 1, 1)
			if _rank_abbr == '1' then _rank_abbr = 'T' end
			_key = _suit_abbr..'_'.._rank_abbr
			cards[ #cards + 1 ] = { key = _key, suit = _suit, rank = _rank, score = _score }
		end
	end
	return cards
end

-- Balatro starts by generating its own random deck so this function helps me check if that deck is adequate
local function convertDeck(game_deck)
	deck = {}
	for _, v in ipairs(game_deck) do
		deck[ #deck + 1 ] = {
			-- Don't need key because I only use it to modify the game deck
			suit = v.base.suit,
			rank = v.base.value,
			score = v.base.nominal
		}
	end
	return deck
end

-- Used to calculate normal CDF
-- Credit: John D. Cook & Greg Hewgill
-- URL: https://hewgill.com/picomath/lua/erf.lua.html
local function erf(x)
    -- constants
    a1 =  0.254829592
    a2 = -0.284496736
    a3 =  1.421413741
    a4 = -1.453152027
    a5 =  1.061405429
    p  =  0.3275911

    -- Save the sign of x
    sign = 1
    if x < 0 then
        sign = -1
    end
    x = math.abs(x)

    -- A&S formula 7.1.26
    t = 1.0/(1.0 + p*x)
    y = 1.0 - (((((a5*t + a4)*t) + a3)*t + a2)*t + a1)*t*math.exp(-x*x)

    return sign*y
end

-- Given a point x, a mean, and a standard deviation
-- Returns the CDF of x on corresponding normal distribution
function normalCdf(x, mean, sd)
	z = ( x - mean ) / sd
	return ( 1 + erf( z / 1.41421356 ) ) / 2
end

SMODS.Back:take_ownership(
	'erratic',
	{
		apply = function()
			G.E_MANAGER:add_event(Event({
				func = function()
					deck_count = 0
					deck = convertDeck(G.playing_cards)
					if not checkPolarization(deck) then
						cards = getCards()
						repeat
							-- Create a random deck
							deck = {}
							for i = 52, 1, -1 do
								deck[i] = cards[math.random( #cards )]
							end
							deck_count = deck_count + 1
						until checkPolarization(deck)
						for i = #G.playing_cards, 1, -1 do
							G.playing_cards[i]:set_base(G.P_CARDS[deck[i].key])
						end
					end
					print("Tried "..deck_count.." addtional decks")
					return true
				end
			}))
		end
	}
)
