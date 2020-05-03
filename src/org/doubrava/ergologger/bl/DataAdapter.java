package org.doubrava.ergologger.bl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public abstract class DataAdapter implements DataSubject, Runnable {
    private static long CHECK_START_INTERVAL = 100;
    private static long DEFAULT_REQUEST_INTERVAL = 1000;

    protected Instant startTimestamp;
    protected Instant currentTimestamp;
    protected HashMap<SensorType, Double> currentValueMap;

    private ArrayList<DataObserver> observers;

    private long requestInterval;

    protected boolean isConnected;
    private boolean isRunning;
    private boolean isActive;

    public DataAdapter() {
        this.startTimestamp = null;
        this.currentTimestamp = null;
        this.currentValueMap = new HashMap<SensorType, Double>();
        this.observers = new ArrayList<DataObserver>();

        this.requestInterval = DataAdapter.DEFAULT_REQUEST_INTERVAL;
        this.isConnected = false;
        this.isRunning = false;
        this.isActive = false;
    }

    public abstract String getName();

    // *** Connection *********************************************************

    public abstract void openConnection();
    public abstract boolean testConnection();
    public abstract void closeConnection();

    // *** Threading **********************************************************

    @Override
    public void run() {
        this.isRunning = true;
        while (this.isRunning) {

            if (this.isActive) {

                this.doRequest();
                this.notifyObservers_updateData();

                try {
                    TimeUnit.MILLISECONDS.sleep(this.requestInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(DataAdapter.CHECK_START_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    public void startService() {
        this.isActive = true;
        this.startTimestamp = Instant.now();
        this.currentTimestamp = null;
        this.currentValueMap.clear();
        this.notifyObservers_onStart();
    }

    public void pauseService() {
        this.isActive = false;
        this.notifyObservers_onPause();
    }

    public void restartService() {
        this.isActive = true;
        this.notifyObservers_onRestart();
    }

    public void stopService() {
        this.startTimestamp = null;
        this.currentTimestamp = null;
        this.currentValueMap.clear();
        this.isActive = false;
        this.isRunning = false;
        this.notifyObservers_onStop();
    }

    public boolean isConnected() { return this.isConnected; }
    public boolean isRunning() { return this.isRunning; }
    public boolean isActive() { return this.isActive; }

    // *** Request Data *******************************************************

    public abstract HashMap<SensorType, Boolean> getSensorTypeAvailability();

    protected abstract void doRequest();

    // *** Observers **********************************************************

    @Override
    public void registerObserver(DataObserver o) {
        this.observers.add(o);
    }

    @Override
    public void removeObserver(DataObserver o) {
        int i = this.observers.indexOf(o);
        if (i >= 0) {
            this.observers.remove(i);
        }
    }

    @Override
    public void notifyObservers_updateData() {
        for (int i = 0; i < this.observers.size(); i++) {
            this.observers.get(i).updateData(currentTimestamp, this.currentValueMap);
        }
    }

    @Override
    public void notifyObservers_onStart() {
        for (int i = 0; i < this.observers.size(); i++) {
            this.observers.get(i).onStart();
        }
    }

    @Override
    public void notifyObservers_onPause() {
        for (int i = 0; i < this.observers.size(); i++) {
            this.observers.get(i).onPause();
        }
    }

    @Override
    public void notifyObservers_onRestart() {
        for (int i = 0; i < this.observers.size(); i++) {
            this.observers.get(i).onRestart();
        }
    }

    @Override
    public void notifyObservers_onStop() {
        for (int i = 0; i < this.observers.size(); i++) {
            this.observers.get(i).onStop();
        }
    }
}
