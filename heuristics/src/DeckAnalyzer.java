// This is built exclusively for 52-card decks
class DeckAnalyzer {
	private Deck deck;

	public DeckAnalyzer(Deck deck) {
		this.deck = deck;
	}

	// Returns how far card scores sum is from the standard 380
	public int getScoreOffset() {
		int totalScore = 0;
		for (Card card: deck) {
			totalScore += card.rank.score;
		}
		return totalScore - 380;
	}

	// Returns maximum count of any given suit in deck
	public int getSuitMax() {
		int[] counts = new int[4];	// Values are initialized to zero in Java
		for (Card card: deck) {
			switch (card.suit) {
				case CLUBS:
					counts[0]++; break;
				case DIAMONDS:
					counts[1]++; break;
				case HEARTS:
					counts[2]++; break;
				case SPADES:
					counts[3]++; break; 
			}
		}
		int max = counts[0];
		for (int i = 1; i < counts.length; i++) {
			if (counts[i] > max) {
				max = counts[i];
			}
		}
		return max;
	}

	// Returns how many ranks have "moved" compared to a standard deck
	// For example, if a deck has five Aces and three Kings, with everything else normal, this function would return 1
	public int getRankMovement() {
		int[] counts = new int[13];
		for (Card card: deck) {
			switch (card.rank) {
				case TWO:
					counts[0]++; break;
				case THREE:
					counts[1]++; break;
				case FOUR:
					counts[2]++; break;
				case FIVE:
					counts[3]++; break;
				case SIX:
					counts[4]++; break;
				case SEVEN:
					counts[5]++; break;
				case EIGHT:
					counts[6]++; break;
				case NINE:
					counts[7]++; break;
				case TEN:
					counts[8]++; break;
				case JACK:
					counts[9]++; break;
				case QUEEN:
					counts[10]++; break;
				case KING:
					counts[11]++; break;
				case ACE:
					counts[12]++; break;
			}
		}

		int movement = 0;
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 4) {
				movement += counts[i] - 4;
			}
		}
		return movement;
	}

	// Returns a deck report object containing summary information
	public DeckReport getReport() {
		int scoreOffset = this.getScoreOffset();
		int suitMax = this.getSuitMax();
		int rankMovement = this.getRankMovement();
		return new DeckReport(
			scoreOffset,
			suitMax,
			rankMovement
		);
	}

	// I might want to keep some info on each deck but not necessarily the decks themselves
	// Could be necessary for memory if I test 1M+ decks
	public class DeckReport {
		// Min = -276, Max = 192, Range = 469
		public int scoreOffset;
		// Min = 13, Max = 52, Range = 40
		public int suitMax;
		// Min = 0, Max = 48, Range = 49
		public int rankMovement;

		private DeckReport(
			int scoreOffset,
			int suitMax,
			int rankMovement
		) {
			this.scoreOffset = scoreOffset;
			this.suitMax = suitMax;
			this.rankMovement = rankMovement;
		}
	}
}
