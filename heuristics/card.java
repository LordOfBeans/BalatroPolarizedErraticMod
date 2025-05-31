public class Card {

	public enum Suit {
		CLUBS,
		DIAMONDS,
		HEARTS,
		SPADES;
	}

	public enum Rank {
		TWO (2),
		THREE (3),
		FOUR (4),
		FIVE (5),
		SIX (6),
		SEVEN (7),
		EIGHT (8),
		NINE (9),
		TEN (10),
		JACK (10),
		QUEEN (10),
		KING (10),
		ACE (11);

		public final int score;
		
		private Rank(int score) {
			this.score = score;
		}
	}

	public Suit suit;
	public Rank rank;

	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
	} 
}
