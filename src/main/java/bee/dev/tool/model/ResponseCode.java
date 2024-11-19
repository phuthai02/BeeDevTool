package bee.dev.tool.model;

public enum ResponseCode {
    SUCCESS(1, "SUCCESS"),
    ERROR(2, "SYSTEM ERROR"),
    WARNING(3, "WARNING");

    private final int code;
    private final String message;

    ResponseCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
