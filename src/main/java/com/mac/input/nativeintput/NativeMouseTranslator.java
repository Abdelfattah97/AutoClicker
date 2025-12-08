package com.mac.input.nativeintput;

import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class NativeMouseTranslator extends AbstractNativeInputTranslator {
    @Override
    public InputEvent toAwtEvent(NativeInputEvent nativeInputEvent) {
        if (nativeInputEvent instanceof NativeMouseEvent nativeMouseEvent) {

            int fixedBtn = nativeMouseEvent.getButton() == 2 ? 3 : nativeMouseEvent.getButton();
            return new MouseEvent(
                    getDummySource()
                    ,
                    nativeMouseEvent.getID() - (NativeMouseEvent.NATIVE_MOUSE_FIRST
                                                - MouseEvent.MOUSE_FIRST),
                    System.currentTimeMillis(),
                    getJavaModifiers(nativeMouseEvent.getModifiers()),
                    nativeMouseEvent.getX(),
                    nativeMouseEvent.getY(),
                    nativeMouseEvent.getClickCount(),
                    false,
                    fixedBtn);


        } else {
            throw new RuntimeException("Not a NativeMouseEvent object.");
        }
    }

    @Override
    public Class<? extends NativeInputEvent> getApplicableClass() {
        return NativeMouseEvent.class;
    }
}
