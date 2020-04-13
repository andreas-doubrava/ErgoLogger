package org.doubrava.ergologger.bl;

public interface ClockSubject {
    public void registerObserver(ClockObserver o);
    public void removeObserver(ClockObserver o);
    public void notifyObservers();
}
