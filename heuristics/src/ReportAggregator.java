import java.util.ArrayList;
import java.lang.Math;

class ReportAggregator {
	public static int SCORE_INDEX_OFFSET = 276;
	public static int SUIT_INDEX_OFFSET = -13;
	public static int RANK_INDEX_OFFSET = 0; // For consistency

	private ArrayList<DeckAnalyzer.DeckReport> reports;
	private int[] scoreOffsetDist;
	private int[] suitMaxDist;
	private int[] rankMovementDist;

	public ReportAggregator() {
		this.reports = new ArrayList<DeckAnalyzer.DeckReport>();
		this.scoreOffsetDist = new int[469];
		this.suitMaxDist = new int[40];
		this.rankMovementDist = new int[49];
	}

	public void add(DeckAnalyzer.DeckReport report) {
		reports.add(report);
		scoreOffsetDist[report.scoreOffset + SCORE_INDEX_OFFSET]++;
		suitMaxDist[report.suitMax + SUIT_INDEX_OFFSET]++;
		rankMovementDist[report.rankMovement]++;
	}

	// Takes a distribution array and returns the cumulatives

	private double[] getCumulatives(int[] dist) {
		double[] cumulatives = new double[dist.length];
		int currSeen = 0;
		for (int i = 0; i < dist.length; i++) {
			currSeen += dist[i];
			cumulatives[i] = (double) currSeen / reports.size();
		}
		return cumulatives;
	}

	public void printScoreOffsetCumulatives() {
		double[] cumulatives = this.getCumulatives(scoreOffsetDist);
		double currCum = 0;
		for (int i = 0; i < cumulatives.length; i++) {
			if (currCum != cumulatives[i]) {
				System.out.printf("%d: %f%%\n", i - SCORE_INDEX_OFFSET, cumulatives[i] * 100);
				currCum = cumulatives[i];
			}
		}
	}

	public class AggregatorResults {
		public double[] scoreCumulatives;
		public double[] suitCumulatives;
		public double[] rankCumulatives;
		public double normalMean;
		public double normalSd;

		private AggregatorResults(double[] scoreC, double[] suitC, double[] rankC, double mean, double sd) {
			this.scoreCumulatives = scoreC;
			this.suitCumulatives = suitC;
			this.rankCumulatives = rankC;
			this.normalMean = mean;
			this.normalSd = sd;
		}
	}

	public AggregatorResults getNormalStats() {
		// Get cumulative distributions for deck metrics
		double[] scoreCumulatives = this.getCumulatives(scoreOffsetDist);
		double[] suitCumulatives = this.getCumulatives(suitMaxDist);
		double[] rankCumulatives = this.getCumulatives(rankMovementDist);

		// Rescore reports by getting their positions on distributions and summing
		double[] reportScores = new double[reports.size()];
		double scoresSum = 0; // Used to calculate the mean
		for (int i = 0; i < reports.size(); i++) {
			DeckAnalyzer.DeckReport currReport = reports.get(i);
			// I use the term but 'percentile' but these numbers are bounded on [0,1]
			double scorePercentile = scoreCumulatives[currReport.scoreOffset + SCORE_INDEX_OFFSET];
			double suitPercentile = suitCumulatives[currReport.suitMax + SUIT_INDEX_OFFSET];
			double rankPercentile = rankCumulatives[currReport.rankMovement + RANK_INDEX_OFFSET];
			double avgPercentile = (scorePercentile + suitPercentile + rankPercentile) / 3; // Purely for aesthetics
			reportScores[i] = avgPercentile;
			scoresSum += avgPercentile;
		}

		// Calculate normal distribution stats
		double mean = scoresSum / reports.size();
		double sum_squares = 0;
		for (int i = 0; i < reportScores.length; i++) {
			double diff = mean - reportScores[i];
			sum_squares += diff * diff;
		}
		double sd = Math.sqrt(sum_squares / (reportScores.length - 1));
		AggregatorResults ret = new AggregatorResults(
			scoreCumulatives,
			suitCumulatives,
			rankCumulatives,
			mean,
			sd
		);
		return ret;
	}
}
