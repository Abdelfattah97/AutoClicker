package com.mac.input.nativeintput;

import com.github.kwhat.jnativehook.NativeInputEvent;

import java.awt.event.InputEvent;

public interface NativeInputTranslator {
    InputEvent toAwtEvent(NativeInputEvent nativeInputEvent);

    Class<? extends NativeInputEvent> getApplicableClass();
}
