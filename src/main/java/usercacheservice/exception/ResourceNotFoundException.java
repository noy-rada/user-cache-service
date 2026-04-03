package usercacheservice.exception;

public final class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message, null, true, false); // suppress stacktrace for perf
    }
}
