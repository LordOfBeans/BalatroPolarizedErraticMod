import java.util.Random;

class Main {
	private static int GENERATE_COUNT = 5000000;

	public static Card[] getCardPool() {
		Card[] cardPool = new Card[52];
		int currIndex = 0;
		for (Card.Suit suit: Card.Suit.values()) {
			for (Card.Rank rank: Card.Rank.values()) {
				Card currCard = new Card(suit, rank);
				cardPool[currIndex] = currCard;
				currIndex++;
			}
		}
		return cardPool;
	}

	public static Deck generateDeck(Card[] cardPool, Random rand) {
		Deck deck = new Deck();
		for (int j = 0; j < Deck.MAX_SIZE; j++) {
			Card currCard = cardPool[rand.nextInt(52)];
			deck.add(currCard);
		}
		return deck;
	}

	public static void main(String[] args) {
		Card[] cardPool = getCardPool();
		Random rand = new Random();
		ReportAggregator agg = new ReportAggregator();
		DeckAnalyzer.DeckReport[] reports = new DeckAnalyzer.DeckReport[GENERATE_COUNT];
		for (int i = 0; i < GENERATE_COUNT; i++) {
			Deck deck = generateDeck(cardPool, rand);
			DeckAnalyzer analyzer = new DeckAnalyzer(deck);
			agg.add(analyzer.getReport());
		}
		double[] normalStats = agg.getNormalStats();
		System.out.printf("Mean: %f\nStandard Deviation: %f\n", normalStats[0], normalStats[1]);
	}
}
