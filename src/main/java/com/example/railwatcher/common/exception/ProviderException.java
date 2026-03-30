package com.example.railwatcher.common.exception;

public class ProviderException extends RuntimeException {

    private final boolean retriable;

    public ProviderException(String message, boolean retriable) {
        super(message);
        this.retriable = retriable;
    }

    public ProviderException(String message, boolean retriable, Throwable cause) {
        super(message, cause);
        this.retriable = retriable;
    }

    public boolean isRetriable() {
        return retriable;
    }
}
