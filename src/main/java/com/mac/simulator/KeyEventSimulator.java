package com.mac.simulator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Set;

public class KeyEventSimulator extends AbstractEventSimulator {

    public KeyEventSimulator(Robot robot) {
        super(robot);
    }

    @Override
    public void simulate(InputEvent event) {
        // Cast the generic InputEvent to the specific KeyEvent
        KeyEvent keyEvent = (KeyEvent) event;
        if (keyEvent.getKeyCode() == KeyEvent.VK_UNDEFINED) {
            return;
        }
        // Determine the key code and event type
        int keyCode = keyEvent.getKeyCode();
        int eventType = keyEvent.getID();

        // Use a switch statement to dispatch based on the event type
        switch (eventType) {
            case KeyEvent.KEY_PRESSED:
                // Simulate pressing the key down
                robot.keyPress(keyCode);
                break;
            case KeyEvent.KEY_RELEASED:
                // Simulate releasing the key
                robot.keyRelease(keyCode);
                break;
            case KeyEvent.KEY_TYPED:
                break;
            default:
                System.err.println("Unsupported KeyEvent ID: " + eventType);
                break;
        }
    }



    @Override
    public Set<Class<? extends InputEvent>> getApplicableInputEvent() {
        return Set.of(KeyEvent.class);
    }
}
