package com.mac.simulator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Set;

public class MouseEventSimulator extends AbstractEventSimulator {
    public MouseEventSimulator(Robot robot) {
        super(robot);
    }

    @Override
    public void simulate(InputEvent event) {
        // 1. Cast the generic InputEvent to the specific MouseEvent
        MouseEvent mouseEvent = (MouseEvent) event;

        // 2. Extract common parameters
        int eventType = mouseEvent.getID();

        // 3. Dispatch based on the event type
        switch (eventType) {
            case MouseEvent.MOUSE_MOVED:
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_DRAGGED:
                robot.mouseMove(mouseEvent.getX(), mouseEvent.getY());
                break;

            case MouseEvent.MOUSE_PRESSED:
            case MouseEvent.MOUSE_RELEASED:
                // Extract the buttons mask (e.g., InputEvent.BUTTON1_DOWN_MASK)
                int buttonMask = mouseEvent.getButton() > 0 ?
                        InputEvent.getMaskForButton(mouseEvent.getButton()) : 0;

                if (buttonMask != 0) {
                    if (eventType == MouseEvent.MOUSE_PRESSED) {
                        robot.mousePress(buttonMask);
                    } else { // MOUSE_RELEASED
                        robot.mouseRelease(buttonMask);
                    }
                }
                break;

            case MouseEvent.MOUSE_WHEEL:
                // Handle scrolling/wheel events
                if (mouseEvent instanceof MouseWheelEvent wheelEvent) {
                    robot.mouseWheel(wheelEvent.getWheelRotation());
                }
                break;

            default:
                break;
        }
    }

    @Override
    public Set<Class<? extends InputEvent>> getApplicableInputEvent() {
        return Set.of(MouseEvent.class, MouseWheelEvent.class);
    }
}
