package com.mac.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

public class NativeListenerService {
    private static boolean isNativeHookRegistered = false;
    private static NativeListenerService nativeListenerService;

    private NativeListenerService() {
        isNativeHookRegistered = true;
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static NativeListenerService getInstance() {
        if (nativeListenerService == null) {
            nativeListenerService = new NativeListenerService();
        }
        return nativeListenerService;
    }

    public void addNativeKeyListener(NativeKeyListener nativeKeyListener) {
        GlobalScreen.addNativeKeyListener(nativeKeyListener);
    }

    public void removeNativeKeyListener(NativeKeyListener nativeKeyListener) {
        GlobalScreen.removeNativeKeyListener(nativeKeyListener);
    }

    public void addNativeMouseMotionListener(NativeMouseMotionListener nativeMouseMotionListener) {
        GlobalScreen.addNativeMouseMotionListener(nativeMouseMotionListener);
    }

    public void removeNativeMouseMotionListener(NativeMouseMotionListener nativeMouseMotionListener) {
        GlobalScreen.removeNativeMouseMotionListener(nativeMouseMotionListener);
    }

    public void addNativeMouseWheelListener(NativeMouseWheelListener nativeMouseWheelListener) {
        GlobalScreen.addNativeMouseWheelListener(nativeMouseWheelListener);
    }

    public void removeNativeMouseWheelListener(NativeMouseWheelListener nativeMouseWheelListener) {
        GlobalScreen.removeNativeMouseWheelListener(nativeMouseWheelListener);
    }

    public void addNativeMouseListener(NativeMouseListener nativeMouseListener) {
        GlobalScreen.addNativeMouseListener(nativeMouseListener);
    }

    public void removeNativeMouseListener(NativeMouseListener nativeMouseListener) {
        GlobalScreen.removeNativeMouseListener(nativeMouseListener);
    }
}
