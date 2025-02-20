public class ModelStats {
    private final double tokensPerSeconds;
    private final double timeToFirstToken;
    private final double generationTime;
    private final String stopReason;

    public ModelStats(double tokensPerSeconds, double timeToFirstToken, double generationTime, String stopReason) {
        this.tokensPerSeconds = tokensPerSeconds;
        this.timeToFirstToken = timeToFirstToken;
        this.generationTime = generationTime;
        this.stopReason = stopReason;
    }
}
