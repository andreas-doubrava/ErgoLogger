package org.doubrava.ergologger.bl;

import java.time.Instant;

public interface ClockObserver {
    public void updateClock(Instant timestamp);
}
