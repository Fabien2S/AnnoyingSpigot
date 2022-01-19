package dev.fabien2s.annoyingapi.nbt.exception;

public class NbtFormatException extends RuntimeException {

    public NbtFormatException() {
    }

    public NbtFormatException(String message) {
        super(message);
    }

    public NbtFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public NbtFormatException(Throwable cause) {
        super(cause);
    }

    public NbtFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
