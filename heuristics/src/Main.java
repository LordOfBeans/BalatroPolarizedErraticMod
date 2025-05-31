import java.util.Random;

class Main {

	private static int GENERATE_COUNT = 100;

	public static Deck[] generateDecks(int deckCount) {
		// Generate array of all cards
		Card[] cardPool = new Card[52];
		int currIndex = 0;
		for (Card.Suit suit: Card.Suit.values()) {
			for (Card.Rank rank: Card.Rank.values()) {
				Card currCard = new Card(suit, rank);
				cardPool[currIndex] = currCard;
				currIndex++;
			}
		}

		// Generate array of randomized decks
		Deck[] decks = new Deck[deckCount];
		Random rand = new Random();
		for (int i = 0; i < deckCount; i++) {
			Deck currDeck = new Deck();
			for (int j = 0; j < Deck.MAX_SIZE; j++) {
				Card currCard = cardPool[rand.nextInt(52)];
				currDeck.add(currCard);
			}
			decks[i] = currDeck;
		}
		return decks;
	}

	public static void main(String[] args) {
		Deck[] randomDecks = generateDecks(GENERATE_COUNT);
		for (Deck deck: randomDecks) {
			DeckAnalyzer analyzer = new DeckAnalyzer(deck);
			int score = analyzer.getTotalScore();
			System.out.printf("Got total score of %d\n", score);
		}
	}
}
