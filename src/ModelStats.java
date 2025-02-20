public class ModelStats {
    private final double tokensperSeconds;
    private final double timeToFirstToken;
    private final double generationTime;
    private final String stopReason;

    public ModelStats(double tokensperSeconds, double timeToFirstToken, double generationTime, String stopReason) {
        this.tokensperSeconds = tokensperSeconds;
        this.timeToFirstToken = timeToFirstToken;
        this.generationTime = generationTime;
        this.stopReason = stopReason;
    }
}
