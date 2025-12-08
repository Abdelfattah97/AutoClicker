package com.mac.service;


import com.github.kwhat.jnativehook.NativeHookException;
import com.mac.model.Action;
import com.mac.model.Macro;
import com.mac.recorder.MacroRecorder;
import com.mac.simulator.EventSimulatorManager;

import java.awt.*;
import java.util.*;
import java.util.List;


public class MacroService {

    private final Map<UUID, Macro> macros = new LinkedHashMap<>();
    private final MacroRecorder recorder;
    private final EventSimulatorManager eventSimulatorManager;

    private final AutoClickExecutorService executorService;

    public MacroService() {
        this.recorder = new MacroRecorder();
        this.executorService = new AutoClickExecutorService() {
        };
        try {
            this.eventSimulatorManager = new EventSimulatorManager();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

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

    private void playMacroCycles(UUID macroId, int cycles, int delayMs) {
        Macro m = macros.get(macroId);
        if (m == null) return;
        int triggered=0;
        try {
            System.out.println("Macro actions size: " + m.getActions().size());
            for (int c = 0; c < cycles; c++) {
                for (Action a : m.getActions()) {
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    triggered++;
                    simulateAction(delayMs, a);
                }
            }
            System.out.println("Macro actions triggered: " + triggered);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void playMacroForTime(UUID macroId, int seconds, int delayMs) {
        Macro m = macros.get(macroId);
        if (m == null) return;
        long end = System.currentTimeMillis() + seconds * 1000L;
        try {
            while (System.currentTimeMillis() < end) {
                for (Action a : m.getActions()) {
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    simulateAction(delayMs, a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void simulateAction(int delayMs, Action a) throws InterruptedException {
        eventSimulatorManager.simulate(a.event());
        Thread.sleep(Math.max(1, a.getDelay()) + delayMs);
    }

    public void executeMacroCycles(UUID macroId, int cycles, int delayMs) {
        executorService.startBackgroundTask(() -> playMacroCycles(macroId, cycles, delayMs));
    }

    public void executeMacroForTime(UUID macroId, int seconds, int delayMs) {
        executorService.startBackgroundTask(() -> playMacroForTime(macroId, seconds, delayMs));
    }

    public void stopRunningMacro() {
        executorService.stopBackgroundTask();
    }

    public Macro getMacroByID(String uuid) {
        return macros.get(UUID.fromString(uuid));
    }

    public void subscribeForNewMacros(Observer<Macro> observer) {
        recorder.addObserver(observer);
    }

    public void subscribeForMacroExecution(AutoClickExecutorObserver observer) {
        executorService.addObserver(observer);
    }

    public boolean isPlaying() {
        return executorService.isWorking();
    }
}