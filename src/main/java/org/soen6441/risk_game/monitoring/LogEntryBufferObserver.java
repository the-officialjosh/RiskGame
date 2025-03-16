package org.soen6441.risk_game.monitoring;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Observer class for game action monitoring.
 * @author AhmedFakhir
 */
public class LogEntryBufferObserver implements Observer {
    private BufferedWriter bufferedWriter;

    /**
     * Constructor for class.
     * @param outputFile
     */
    public LogEntryBufferObserver(String outputFile) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is triggered whenever a new LogEntryBuffer change occurs
     * to write the value of the "LogEntryBuffer" class to a file.
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof LogEntryBuffer logEntryBuffer) {
            try {
                bufferedWriter.write(logEntryBuffer.getValue());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
