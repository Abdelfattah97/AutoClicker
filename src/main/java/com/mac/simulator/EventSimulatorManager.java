package com.mac.simulator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSimulatorManager {

    private final Robot robot;
    private final Map<Class<? extends InputEvent>, EventSimulator> simulatorRegistry;
    private final NullSimulator nullSimulator;


    public EventSimulatorManager() throws AWTException {
        robot = new Robot();
        simulatorRegistry = new HashMap<>();
        nullSimulator = new NullSimulator();
        registerSimulator(new KeyEventSimulator(robot));
        registerSimulator(new MouseEventSimulator(robot));
    }

    public void registerSimulator(EventSimulator eventSimulator) {
        if (eventSimulator == null) {
            return;
        }
        eventSimulator.getApplicableInputEvent()
                .forEach(e -> simulatorRegistry.put(e, eventSimulator));
    }

    public void simulate(InputEvent inputEvent) {
        if (inputEvent == null) {
            return;
        } else if (simulatorRegistry == null) {
            throw new NullPointerException("Simulator registry not initialized");
        }
//        printEvent(inputEvent);
        try {
            simulatorRegistry.getOrDefault(inputEvent.getClass(),nullSimulator).simulate(inputEvent);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(inputEvent);
        }
    }

    private void printEvent(InputEvent inputEvent) {
        if (inputEvent == null) {
            return;
        }
        int code=-1;
        String text;
        if (inputEvent instanceof KeyEvent keyEvent) {
            code = keyEvent.getKeyCode();
            if(code==0) {
                System.out.println(0);
            }
            text = KeyEvent.getKeyText(code);
        } else if (inputEvent instanceof MouseEvent mouseEvent) {
            code = mouseEvent.getButton();
            text = "button"+code;
        } else return;

        System.out.printf("KeyCode: %d, KeyText:%s\n", code, text);


    }

    public void pressKeyModifiers(InputEvent event) {
        var modifiers = getModifiersList(event);
        for (var modifier : modifiers) {
            robot.keyPress(modifier);
        }
    }

    public void releaseKeyModifiers(InputEvent event) {
        var modifiers = getModifiersList(event);
        for (var modifier : modifiers) {
            robot.keyRelease(modifier);
        }
    }

    public java.util.List<Integer> getModifiersList(InputEvent event) {
        List<Integer> modifiersList = new ArrayList<>();
        int modifiers = event.getModifiersEx();

        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
            modifiersList.add(KeyEvent.VK_SHIFT);
        }
        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
            modifiersList.add(KeyEvent.VK_CONTROL);
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
            modifiersList.add(KeyEvent.VK_ALT);
        }
        if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
            modifiersList.add(KeyEvent.VK_META);
        }
        return modifiersList;
    }


}
