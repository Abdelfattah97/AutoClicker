package com.mac.service;


import com.github.kwhat.jnativehook.NativeHookException;
import com.mac.ActionType;
import com.mac.MacroAction;
import com.mac.MacroRecorder;
import com.mac.Observer;
import com.mac.model.Macro;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;
import java.util.List;

/* ==========================
 * Service Layer (business logic)
 * - Holds session state, macros, and plays macros using Robot
 * - In a real implementation this would hook global keyboard/mouse hooks
 * ==========================
 */
public class MacroService {
    private volatile boolean sessionActive = false;
//    private volatile boolean recording = false;

    private final Map<UUID, Macro> macros = new LinkedHashMap<>();
    private volatile MacroRecorder recorder;

    public MacroService() {
        this.recorder = new MacroRecorder();
    }

    public boolean isSessionActive() {
        return sessionActive;
    }

    public void startManualSession() {
        if (!sessionActive) sessionActive = true;
    }

//    public void stopManualSessionAndSave(String name) {
//        if (!sessionActive) return;
//        try {
//            MacroRecorder recorder = new MacroRecorder();
//            Macro macro = recorder.startRecording(name);
//            macros.put(macro.getId(), macro);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            sessionActive = false;
//        }
//    }

    public boolean isRecording() {
        return recorder != null && recorder.isRecording();
    }

    public void startAutoRecording() {
        try {
          recorder.startRecording("RecordedMacro-" + (macros.size() + 1));
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

    }

    public void stopAutoRecording() {
        Macro m = recorder.stopRecording();
        macros.put(m.getId(), m);
    }

    public List<Macro> listMacros() {
        return new ArrayList<>(macros.values());
    }

    public void deleteMacro(UUID macroId) {
        macros.remove(macroId);
    }

    public void playMacroCycles(UUID macroId, int cycles, int delayMs) {
        Macro m = macros.get(macroId);
        if (m == null) return;
        try {
            Robot robot = new Robot();
            for (int c = 0; c < cycles; c++) {
                for (MacroAction a : m.getActions()) {
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    if (a.getX() >= 0) {
                        robot.mouseMove(a.getX(), a.getY());
                        if (a.actionType().equals(ActionType.CLICK) || a.actionType().equals(ActionType.PRESSED)) {
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        }
                    }
                    Thread.sleep(Math.max(1, a.getDelay()) + delayMs);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public void playMacroForTime(UUID macroId, int seconds, int delayMs) {
        Macro m = macros.get(macroId);
        if (m == null) return;
        long end = System.currentTimeMillis() + seconds * 1000L;
        try {
            Robot robot = new Robot();
            while (System.currentTimeMillis() < end) {
                for (MacroAction a : m.getActions()) {
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    if (a.getX() >= 0) {
                        robot.mouseMove(a.getX(), a.getY());
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    }
                    Thread.sleep(Math.max(1, a.getDelay()) + delayMs);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public Macro getMacroByID(String uuid) {
        return macros.get(UUID.fromString(uuid));
    }

    public void subscribeForNewMacros(Observer<Macro> observer){
        recorder.addObserver(observer);
    }
}