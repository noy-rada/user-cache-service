package usercacheservice.exception;

public final class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message, null, true, false); // suppress stacktrace for perf
    }
}
