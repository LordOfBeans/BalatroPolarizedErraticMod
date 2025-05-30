local function checkPolarization(cards)
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

	for i = #cards, 1, -1 do
		_suit = cards[i].base.suit
		suit_counts[_suit] = suit_counts[_suit] + 1
		_rank = cards[i].base.value
		rank_counts[_rank] = rank_counts[_rank] + 1
		_score = cards[i].base.nominal
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

	if score_total >= 415 and suit_imbalance >= 12 then
		return true
	end
	return false
end

SMODS.Back:take_ownership(
	'erratic', -- Erratic Back
	{
		apply = function()
			G.E_MANAGER:add_event(Event({
				func = function()
					decks = 1
					while (not checkPolarization(G.playing_cards)) do
						for i = #G.playing_cards, 1, -1 do
							local suits = {
								'C', 'D', 'H', 'S'
							}
							local ranks = {
								'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'
							}

							local random_suit = suits[math.random( #suits )]
							local random_rank = ranks[math.random( #ranks )]

							G.playing_cards[i]:set_base(G.P_CARDS[random_suit .. "_" .. random_rank])
						end
						decks = decks + 1
					end
					print("Tried "..decks.." decks")
					return true
				end
			}))
		end
	}
)
