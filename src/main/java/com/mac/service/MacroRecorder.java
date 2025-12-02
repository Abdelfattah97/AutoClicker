package com.mac.service;

import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.mac.model.ActionType;
import com.mac.model.Macro;
import com.mac.model.MacroAction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MacroRecorder implements NativeMouseListener, NativeMouseMotionListener, NativeKeyListener, MacroRecorderObservable {
    private final List<MacroAction> actions = new CopyOnWriteArrayList<>();
    private long lastTime;
    private boolean isRecording = false;
    private String macroName;
    private NativeListenerService nativeListenerService;
    private List<Observer<Macro>> observers = new CopyOnWriteArrayList<>();

    public MacroRecorder() {
        nativeListenerService = NativeListenerService.getInstance();
    }

    public void startRecording(String name) throws NativeHookException {
        actions.clear();
        lastTime = System.currentTimeMillis();

        nativeListenerService.addNativeMouseListener(this);
        nativeListenerService.addNativeMouseMotionListener(this);
        nativeListenerService.addNativeKeyListener(this);

        isRecording = true;

        macroName = name;
    }

    private int computeDelay() {
        long now = System.currentTimeMillis();
        int delay = (int) (now - lastTime);
        lastTime = now;
        return delay;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        if (!isRecording) {
            return;
        }
        actions.add(new MacroAction(e.getX(), e.getY(), computeDelay(), ActionType.CLICK));
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        if (!isRecording) {
            return;
        }
        actions.add(new MacroAction(e.getX(), e.getY(), computeDelay(), ActionType.MOVE));
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }

    public boolean isRecording() {
        return isRecording;
    }

    public Macro stopRecording() {
        try {
            nativeListenerService.removeNativeMouseListener(this);
            nativeListenerService.removeNativeMouseMotionListener(this);
            nativeListenerService.removeNativeKeyListener(this);
            nativeListenerService.removeNativeMouseListener(this);
        } catch (Exception ignored) {
        }
        Macro macro = new Macro(macroName, actions);
        macroName = null;
        isRecording = false;
        lastTime = 0;
        notifyObservers(macro);
        return macro;
    }

    @Override
    public void addObserver(Observer<Macro> observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<Macro> observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(Macro macro) {
        for (Observer<Macro> observer : observers) {
            observer.update(macro);
        }
    }

}
