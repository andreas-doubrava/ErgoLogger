package org.doubrava.ergologger.bl;

public interface DataSubject {
    public void registerObserver(DataObserver o);
    public void removeObserver(DataObserver o);
    public void notifyObservers_updateData();
    public void notifyObservers_onStart();
    public void notifyObservers_onPause();
    public void notifyObservers_onRestart();
    public void notifyObservers_onStop();
}
