package de.uol.vpp.load.infrastructure.scheduler;

public class LoadSchedulerException extends Exception {
    public LoadSchedulerException(String message) {
        super(message);
    }

    public LoadSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
