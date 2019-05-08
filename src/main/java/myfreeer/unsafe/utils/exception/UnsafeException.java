package myfreeer.unsafe.utils.exception;

public class UnsafeException extends RuntimeException {
    private static final long serialVersionUID = -1201190518070455522L;

    public UnsafeException(String message) {
        super(message);
    }

    public UnsafeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsafeException(Throwable cause) {
        super(cause);
    }
}