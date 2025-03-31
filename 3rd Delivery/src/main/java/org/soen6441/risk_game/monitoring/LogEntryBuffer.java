package org.soen6441.risk_game.monitoring;

import java.util.Observable;

/**
 * Observable class for game action monitoring.
 * @author AhmedFakhir
 */
public class LogEntryBuffer extends Observable {
    private String value;

    private LogEntryBuffer() {}

    private static LogEntryBuffer instance = new LogEntryBuffer();

    /**
     * Returns the singleton instance of the class.
     */
    public static LogEntryBuffer getInstance() {
        return instance;
    }

    /**
     * Setter for field.
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
        setChanged();
        notifyObservers();
    }

    /**
     * Getter for field.
     * @return
     */
    public String getValue() {
        return value;
    }
}
