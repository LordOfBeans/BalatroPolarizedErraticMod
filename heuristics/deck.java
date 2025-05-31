class Deck {
	private Card[] cards;
	private int size;
	public static MAX_SIZE = 52

	public Deck() {
		this.cards = new Card[MAX_SIZE];
		this.size = 0;
	}

	public boolean addCard(Card card) {
		if (this.size >= MAX_SIZE) {
			return false;
		}
		this.cards[this.size] = card;
		this.size++;
		return true;
	}

	// Calculates the total score of all cards in deck
	public int getTotalScore() {
		totalScore = 0;
		for (i = 0, i < this.size, i++) {
			Card curr = this.cards[i];
			total_score += curr.rank.score;
		}
		return totalScore;
	}
}
