import java.util.Random;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Main {
	private static int GENERATE_COUNT = 5000;

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

	public static String arrayToLua(double[] arr) {
		DecimalFormat df = new DecimalFormat("0.##########");
		String commaSep = Arrays.stream(arr)
			.mapToObj(df::format)
			.reduce((a, b) -> a.concat(", ").concat(b))
			.get();
		return "{ " + commaSep + " }";
	}

	public static void main(String[] args) {
		Card[] cardPool = getCardPool();
		Random rand = new Random();
		ReportAggregator agg = new ReportAggregator();
		for (int i = 0; i < GENERATE_COUNT; i++) {
			Deck deck = generateDeck(cardPool, rand);
			DeckAnalyzer analyzer = new DeckAnalyzer(deck);
			agg.add(analyzer.getReport());
		}
		ReportAggregator.AggregatorResults normalStats = agg.getNormalStats();

		// Create contents of config.lua file
		String scoreCumStr = arrayToLua(normalStats.scoreCumulatives);
		String suitCumStr = arrayToLua(normalStats.suitCumulatives);
		String rankCumStr = arrayToLua(normalStats.rankCumulatives);
		String configStr = String.format("""
return {
	avg_tries = 30,
	cumulatives = {
		score_total = {
			dist = %s,
			offset = %d,
		},
		suit_max = {
			dist = %s,
			offset = %d,
		},
		rank_movement = {
			dist = %s,
			offset = %d,
		},
	},
	normal_dist = {
		mean = %f,
		sd = %f,
	},
}
		""",
			scoreCumStr,
			ReportAggregator.SCORE_INDEX_OFFSET + 1, // Arrays are one-indexed in Lua
			suitCumStr,
			ReportAggregator.SUIT_INDEX_OFFSET + 1,
			rankCumStr,
			ReportAggregator.RANK_INDEX_OFFSET + 1,
			normalStats.normalMean,
			normalStats.normalSd
		);
		try {
			File configFile = new File("../config.lua");
			configFile.createNewFile();
			FileWriter configWriter = new FileWriter(configFile);
			configWriter.write(configStr);
			configWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
