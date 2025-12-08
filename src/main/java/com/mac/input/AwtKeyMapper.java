package com.mac.input;

import com.mac.model.EventSource;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.*;

/**
 * Clean, unified InputEvent → FriendlyName formatter.
 */
public final class AwtKeyMapper {

    // =====================================================================
    // BASE NAME MAP (mouse masks, custom wheel codes, key names if needed)
    // =====================================================================
    private static final Map<Integer, String> BASE_NAMES;

    private static final Map<Class<?>, EventHandler<? extends InputEvent>> HANDLERS = new HashMap<>();

    static {
        Map<Integer, String> map = new HashMap<>();

        // Mouse buttons
        map.put(InputEvent.BUTTON1_DOWN_MASK, "Left");
        map.put(InputEvent.BUTTON2_DOWN_MASK, "Right");
        map.put(InputEvent.BUTTON3_DOWN_MASK, "Middle");

        try {
            map.put(InputEvent.getMaskForButton(4), "Button4");
            map.put(InputEvent.getMaskForButton(5), "Button5");
        } catch (Exception ignore) {
        }

        // Mouse actions
        map.put(MouseEvent.MOUSE_MOVED, "Move");
        map.put(MouseEvent.MOUSE_DRAGGED, "Drag");
        map.put(MouseEvent.MOUSE_PRESSED, "Press");
        map.put(MouseEvent.MOUSE_RELEASED, "Release");
        map.put(MouseEvent.MOUSE_CLICKED, "Click");
        map.put(MouseEvent.MOUSE_ENTERED, "Enter");
        map.put(MouseEvent.MOUSE_EXITED, "Exit");

        // Wheel custom codes
        map.put(-1001, "Wheel Up");
        map.put(-1002, "Wheel Down");

        BASE_NAMES = Collections.unmodifiableMap(map);

        // Handlers
        HANDLERS.put(KeyEvent.class, new KeyEventHandler());
        HANDLERS.put(MouseEvent.class, new MouseEventHandler());
        HANDLERS.put(MouseWheelEvent.class, new WheelEventHandler());
    }

    private AwtKeyMapper() {
    }

    // =====================================================================
    // PUBLIC API — produces: "<Name> <Action> <Modifiers…>"
    // =====================================================================
    public static String describe(InputEvent event) {

        EventHandler<InputEvent> handler = resolveHandler(event);

        String primary = handler.getObjectName(event);
        String action = handler.getActionName(event);
        String mods = joinModifiers(event.getModifiersEx());

        if (mods.isEmpty())
            return primary + " " + action;

        return primary + " " + action + " " + mods;
    }

    public static EventSource getSource(InputEvent event) {
        if (event instanceof KeyEvent) {
            return EventSource.KEY_EVENT;
        } else if (event instanceof MouseWheelEvent) {
            return EventSource.MOUSE_WHEEL;
        } else if (event instanceof MouseEvent) {
            return EventSource.MOUSE_EVENT;
        } else {
            return EventSource.NOT_DETECTED;
        }
    }

    @SuppressWarnings("unchecked")
    private static EventHandler<InputEvent> resolveHandler(InputEvent e) {

        EventHandler<?> h = HANDLERS.get(e.getClass());
        if (h != null) return (EventHandler<InputEvent>) h;

        for (var entry : HANDLERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(e.getClass()))
                return (EventHandler<InputEvent>) entry.getValue();
        }

        return new UnknownHandler();
    }

    // =====================================================================
    // MODIFIER FORMATTER
    // =====================================================================
    private static String joinModifiers(int mods) {
        List<String> list = new ArrayList<>();

        if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) list.add("Ctrl");
        if ((mods & InputEvent.SHIFT_DOWN_MASK) != 0) list.add("Shift");
        if ((mods & InputEvent.ALT_DOWN_MASK) != 0) list.add("Alt");
        if ((mods & InputEvent.META_DOWN_MASK) != 0) list.add("Super");

        if ((mods & InputEvent.BUTTON1_DOWN_MASK) != 0) list.add("Left-Down");
        if ((mods & InputEvent.BUTTON2_DOWN_MASK) != 0) list.add("Right-Down");
        if ((mods & InputEvent.BUTTON3_DOWN_MASK) != 0) list.add("Middle-Down");

        return String.join("+", list);
    }

    private static String lookup(int code) {
        return BASE_NAMES.getOrDefault(code, "Unknown");
    }

    // =====================================================================
    // HANDLER INTERFACE
    // =====================================================================
    private interface EventHandler<T extends InputEvent> {
        String getObjectName(T e);   // e.g. "A", "Left", "Wheel"

        String getActionName(T e);   // e.g. "Pressed", "Released", "Moved"
    }

    // =====================================================================
    // KEY HANDLER
    // =====================================================================
    private static final class KeyEventHandler implements EventHandler<KeyEvent> {

        @Override
        public String getObjectName(KeyEvent e) {

            // Printable keys
            if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(e.getKeyChar()))
                return String.valueOf(e.getKeyChar()).toUpperCase();

            // Non-printable: a key code lookup
            String name = KeyEvent.getKeyText(e.getKeyCode());
            return (name != null && !name.isEmpty()) ? name : "Key(" + e.getKeyCode() + ")";
        }

        @Override
        public String getActionName(KeyEvent e) {
            return switch (e.getID()) {
                case KeyEvent.KEY_PRESSED -> "Pressed";
                case KeyEvent.KEY_RELEASED -> "Released";
                case KeyEvent.KEY_TYPED -> "Typed";
                default -> "Unknown";
            };
        }
    }

    // =====================================================================
    // MOUSE HANDLER
    // =====================================================================
    private static final class MouseEventHandler implements EventHandler<MouseEvent> {

        @Override
        public String getObjectName(MouseEvent e) {

            int button = e.getButton();
            if (button > 0)
                return lookup(InputEvent.getMaskForButton(button));

            return "Pointer";
        }

        @Override
        public String getActionName(MouseEvent e) {
            return lookup(e.getID());  // Move, Press, Release, Click…
        }
    }

    // =====================================================================
    // WHEEL HANDLER
    // =====================================================================
    private static final class WheelEventHandler implements EventHandler<MouseWheelEvent> {

        @Override
        public String getObjectName(MouseWheelEvent e) {
            return "Wheel";
        }

        @Override
        public String getActionName(MouseWheelEvent e) {
            return (e.getWheelRotation() < 0)
                    ? lookup(-1001)
                    : lookup(-1002);
        }
    }

    // =====================================================================
    // FALLBACK HANDLER
    // =====================================================================
    private static final class UnknownHandler implements EventHandler<InputEvent> {
        public String getObjectName(InputEvent e) {
            return "Unknown";
        }

        public String getActionName(InputEvent e) {
            return "Unknown";
        }
    }

}
