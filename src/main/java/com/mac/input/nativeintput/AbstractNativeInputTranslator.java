package com.mac.input.nativeintput;

import javax.swing.*;

public abstract class AbstractNativeInputTranslator implements NativeInputTranslator {

    private static JComponent component;

    protected JComponent getDummySource() {
        if (component == null) {
            component = new JLabel();
        }
        return component;
    }

    protected int getJavaModifiers(int nativeModifiers) {
        int modifiers = 0;
        if ((nativeModifiers & 17) != 0) {
            modifiers |= 1;
            modifiers |= 64;
        }

        if ((nativeModifiers & 68) != 0) {
            modifiers |= 4;
            modifiers |= 256;
        }

        if ((nativeModifiers & 68) != 0) {
            modifiers |= 2;
            modifiers |= 128;
        }

        if ((nativeModifiers & 136) != 0) {
            modifiers |= 8;
            modifiers |= 512;
        }

        if ((nativeModifiers & 256) != 0) {
            modifiers |= 16;
            modifiers |= 1024;
        }

        if ((nativeModifiers & 512) != 0) {
            modifiers |= 8;
            modifiers |= 2048;
        }

        if ((nativeModifiers & 1024) != 0) {
            modifiers |= 4;
            modifiers |= 4096;
        }

        return modifiers;
    }

}
