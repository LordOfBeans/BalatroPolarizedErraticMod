class ReportAggregator {
	private static int SCORE_OFFSET_COUNT = 469;
	private static int SUIT_MAX_COUNT = 40;
	private static int RANK_MOVEMENT_COUNT = 49;
	private static int SCORE_INDEX_OFFSET = 276;
	private static int SUIT_INDEX_OFFSET = -13;

	int reportCount;
	int[] scoreOffsetDist;
	int[] suitMaxDist;
	int[] rankMovementDist;

	public ReportAggregator() {
		this.reportCount = 0;
		this.scoreOffsetDist = new int[SCORE_OFFSET_COUNT];
		this.suitMaxDist = new int[SUIT_MAX_COUNT];
		this.rankMovementDist = new int[RANK_MOVEMENT_COUNT];
	}

	public void add(DeckAnalyzer.DeckReport report) {
		reportCount++;
		scoreOffsetDist[report.scoreOffset + SCORE_INDEX_OFFSET]++;
		suitMaxDist[report.suitMax + SUIT_INDEX_OFFSET]++;
		rankMovementDist[report.rankMovement]++;
	}

	public double[] getScoreOffsetCumulatives() {
		double[] cumulatives = new double[SCORE_OFFSET_COUNT];
		int currSeen = 0;
		for (int i = 0; i < SCORE_OFFSET_COUNT; i++) {
			currSeen += scoreOffsetDist[i];
			cumulatives[i] = (double) currSeen / reportCount;
		}
		return cumulatives;
	}

	public void printScoreOffsetCumulatives() {
		double[] cumulatives = this.getScoreOffsetCumulatives();
		double currCum = 0;
		for (int i = 0; i < SCORE_OFFSET_COUNT; i++) {
			if (currCum != cumulatives[i]) {
				System.out.printf("%d: %f%%\n", i - SCORE_INDEX_OFFSET, cumulatives[i] * 100);
				currCum = cumulatives[i];
			}
		}
	}
}
