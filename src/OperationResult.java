/**
 * The outcome of an operation on the appointment collection.
 *
 * <p>Every operation that can fail returns one of these instead of throwing an
 * exception or printing straight to the console, which keeps the decision about
 * how a warning is presented in the calling code and makes the outcome easy to
 * test.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class OperationResult {

    private final boolean success;
    private final String message;

    /**
     * Creates a result. The named factory methods are the intended way to build
     * one, because they state the outcome in the call itself.
     *
     * @param success whether the operation succeeded
     * @param message the message describing the outcome
     */
    private OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a successful result.
     *
     * @param message what was achieved
     * @return a successful result
     */
    public static OperationResult success(String message) {
        return new OperationResult(true, message);
    }

    /**
     * Creates a failed result carrying a warning.
     *
     * @param message why the operation could not be performed
     * @return a failed result
     */
    public static OperationResult failure(String message) {
        return new OperationResult(false, message);
    }

    /**
     * Returns whether the operation succeeded.
     *
     * @return {@code true} on success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the message describing the outcome.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the message prefixed with a status marker.
     *
     * @return the formatted result
     */
    @Override
    public String toString() {
        return (success ? "[OK]   " : "[WARN] ") + message;
    }
}
