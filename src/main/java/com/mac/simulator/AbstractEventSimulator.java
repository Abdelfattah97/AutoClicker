package com.mac.simulator;


import java.awt.*;

public abstract class AbstractEventSimulator implements EventSimulator {

    protected final Robot robot;

    public AbstractEventSimulator(Robot robot) {
        this.robot = robot;
    }


}
