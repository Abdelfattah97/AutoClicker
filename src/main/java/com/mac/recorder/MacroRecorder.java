package com.mac.recorder;

import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.mac.mapper.ActionMapper;
import com.mac.model.Macro;
import com.mac.model.Action;
import com.mac.service.NativeListenerService;
import com.mac.service.Observer;
import lombok.Getter;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MacroRecorder implements NativeMouseListener, NativeMouseMotionListener, NativeKeyListener, MacroRecorderObservable {
    private final List<Action> actions = Collections.synchronizedList(new ArrayList<>());
    private long lastTime;
    @Getter
    private volatile boolean isRecording = false;
    private String macroName;
    private final NativeListenerService nativeListenerService;
    private final List<Observer<Macro>> macroRecordedObservers = new CopyOnWriteArrayList<>();
    private volatile long lastMoveTime = 0;
    private final long captureMoveDelay = 5;

    public MacroRecorder() {
        nativeListenerService = NativeListenerService.getInstance();
    }

    public void startRecording(String name) throws NativeHookException {
        actions.clear();
        resetCaptureMoveTime();
        nativeListenerService.addNativeMouseListener(this);
        nativeListenerService.addNativeMouseMotionListener(this);
        nativeListenerService.addNativeKeyListener(this);

        isRecording = true;
        lastTime = System.currentTimeMillis();

        macroName = name;
    }

    private boolean canCaptureMove() {
        return System.currentTimeMillis() - lastMoveTime >= captureMoveDelay;
    }

    private void resetCaptureMoveTime() {
        lastMoveTime = System.currentTimeMillis();
    }

    private int computeDelay() {
        long now = System.currentTimeMillis();
        int delay = (int) (now - lastTime);
        lastTime = now;
        return delay;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        notifyAction(e);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        notifyAction(e);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        if (!canCaptureMove()) {
            return;
        }
        notifyAction(e);
        resetCaptureMoveTime();
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        notifyAction(e);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        notifyAction(e);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        notifyAction(e);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    public Macro stopRecording() {
        try {
            Thread.sleep(Math.max(captureMoveDelay, 10));
            nativeListenerService.removeNativeMouseListener(this);
            nativeListenerService.removeNativeMouseMotionListener(this);
            nativeListenerService.removeNativeKeyListener(this);
        } catch (Exception ignored) {
        }
        Macro macro = new Macro(macroName, actions);
        macroName = null;
        isRecording = false;
        lastTime = System.currentTimeMillis();
        notifyObservers(macro);
        return macro;
    }

    public void removeStopHotKey(List<Action> actions) {
        if (actions == null) {
            return;
        }
        int loops = 0;
        while (!actions.isEmpty() && loops < 2) {
            var event = actions.get(actions.size() - 1).getEvent();
            if (event instanceof KeyEvent
                && ((KeyEvent) event).getKeyCode() == KeyEvent.VK_F9) {
                actions.remove(actions.size() - 1);
            } else {
                break;
            }
            loops++;
        }
    }

    @Override
    public void addObserver(Observer<Macro> observer) {
        this.macroRecordedObservers.add(observer);
    }

    @Override
    public void removeObserver(Observer<Macro> observer) {
        this.macroRecordedObservers.remove(observer);
    }

    @Override
    public void notifyObservers(Macro macro) {
        for (Observer<Macro> observer : macroRecordedObservers) {
            observer.update(macro);
        }
    }

    public Action buildAction(NativeInputEvent e, int delay) {
        return ActionMapper.toAction(e, delay);
    }

    public Action buildAction(NativeInputEvent e) {
        return buildAction(e, computeDelay());
    }

    private void notifyAction(NativeInputEvent e) {
        if (!isRecording) {
            return;
        }
        actions.add(buildAction(e));
    }

}
