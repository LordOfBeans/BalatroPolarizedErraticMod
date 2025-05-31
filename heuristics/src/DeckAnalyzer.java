class DeckAnalyzer {
	private Deck deck;

	public DeckAnalyzer(Deck deck) {
		this.deck = deck;
	}

	public int getTotalScore() {
		int totalScore = 0;
		for (Card card: deck) {
			totalScore += card.rank.score;
		}
		return totalScore;
	}

	public class DeckReport {
		private Deck deck;
		public int totalScore;

		private DeckReport(Deck deck, int totalScore) {
			this.deck = deck;
			this.totalScore = totalScore;
		}
	}
}
