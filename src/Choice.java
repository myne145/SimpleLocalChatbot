public class Choice {
    private final int index;
    private final String finishReason;
    private final String role;
    private final String message;

    public Choice(int index, String finishReason, String role, String message) {
        this.index = index;
        this.finishReason = finishReason;
        this.role = role;
        this.message = message;
    }
}
