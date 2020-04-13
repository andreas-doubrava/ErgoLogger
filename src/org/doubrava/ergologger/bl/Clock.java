package org.doubrava.ergologger.bl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Clock implements ClockSubject, Runnable {

    // A
    private static long DEFAULT_REQUEST_INTERVAL = 1000;

    private boolean isRunning;
    private Instant currentTimestamp;
    private ArrayList<ClockObserver> observers;

    public Clock() {
        this.isRunning = false;
        this.observers = new ArrayList<ClockObserver>();
    }

    @Override
    public void registerObserver(ClockObserver o) {
        this.observers.add(o);
    }

    @Override
    public void removeObserver(ClockObserver o) {
        int i = this.observers.indexOf(o);
        if (i >= 0) {
            this.observers.remove(i);
        }
    }

    @Override
    public void notifyObservers() {
        for (int i = 0; i < this.observers.size(); i++) {
            this.observers.get(i).updateClock(currentTimestamp);
        }
    }

    @Override
    public void run() {
        this.isRunning = true;
        while (this.isRunning) {

            this.currentTimestamp = Instant.now();
            this.notifyObservers();

            try {
                TimeUnit.MILLISECONDS.sleep(Clock.DEFAULT_REQUEST_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopService() {
        this.currentTimestamp = null;
        this.isRunning = false;
    }
}
