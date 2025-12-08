package com.mac.simulator;

import java.awt.event.InputEvent;
import java.util.Set;

public class NullSimulator implements EventSimulator {
    @Override
    public void simulate(InputEvent event) {
        throw new RuntimeException("No suitable simulator for " + event.getClass().getName());
    }

    @Override
    public Set<Class<? extends InputEvent>> getApplicableInputEvent() {
        return Set.of(InputEvent.class);
    }
}
