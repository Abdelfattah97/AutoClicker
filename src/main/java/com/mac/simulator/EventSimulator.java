package com.mac.simulator;

import java.awt.event.InputEvent;
import java.util.Set;

public interface EventSimulator {

    void simulate(InputEvent event);

    Set<Class<? extends InputEvent>> getApplicableInputEvent();

}
