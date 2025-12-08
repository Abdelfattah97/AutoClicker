package com.mac.input.nativeintput;

import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class NativeKeyTranslator extends AbstractNativeInputTranslator {
    @Override
    public InputEvent toAwtEvent(NativeInputEvent nativeInputEvent) {
        if (nativeInputEvent instanceof NativeKeyEvent nativeEvent) {
            int keyLocation = 0;
            switch (nativeEvent.getKeyLocation()) {
                case 1,2 -> keyLocation = 1;
                case 3 -> keyLocation = 3;
                case 4 -> keyLocation = 4;
            }

            int keyCode = 0;
            switch (nativeEvent.getKeyCode()) {
                case 1 -> keyCode = 27;
                case 2 -> keyCode = 49;
                case 3 -> keyCode = 50;
                case 4 -> keyCode = 51;
                case 5 -> keyCode = 52;
                case 6 -> keyCode = 53;
                case 7 -> keyCode = 54;
                case 8 -> keyCode = 55;
                case 9 -> keyCode = 56;
                case 10 -> keyCode = 57;
                case 11 -> keyCode = 48;
                case 12 -> keyCode = 45;
                case 13 -> keyCode = 61;
                case 14 -> keyCode = 8;
                case 15 -> keyCode = 9;
                case 16 -> keyCode = 81;
                case 17 -> keyCode = 87;
                case 18 -> keyCode = 69;
                case 19 -> keyCode = 82;
                case 20 -> keyCode = 84;
                case 21 -> keyCode = 89;
                case 22 -> keyCode = 85;
                case 23 -> keyCode = 73;
                case 24 -> keyCode = 79;
                case 25 -> keyCode = 80;
                case 26 -> keyCode = 91;
                case 27 -> keyCode = 93;
                case 28 -> keyCode = 10;
                case 29 -> keyCode = 17;
                case 30 -> keyCode = 65;
                case 31 -> keyCode = 83;
                case 32 -> keyCode = 68;
                case 33 -> keyCode = 70;
                case 34 -> keyCode = 71;
                case 35 -> keyCode = 72;
                case 36 -> keyCode = 74;
                case 37 -> keyCode = 75;
                case 38 -> keyCode = 76;
                case 39 -> keyCode = 59;
                case 40 -> keyCode = 222;
                case 41 -> keyCode = 192;
                case 42, 3638 -> keyCode = 16; // Shift
                case 43 -> keyCode = 92;
                case 44 -> keyCode = 90;
                case 45 -> keyCode = 88;
                case 46 -> keyCode = 67;
                case 47 -> keyCode = 86;
                case 48 -> keyCode = 66;
                case 49 -> keyCode = 78;
                case 50 -> keyCode = 77;
                case 51 -> keyCode = 44;
                case 52 -> keyCode = 46;
                case 53 -> keyCode = 47;
                case 56 -> keyCode = 18;
                case 57 -> keyCode = 32;
                case 58 -> keyCode = 20;
                case 59 -> keyCode = 112;
                case 60 -> keyCode = 113;
                case 61 -> keyCode = 114;
                case 62 -> keyCode = 115;
                case 63 -> keyCode = 116;
                case 64 -> keyCode = 117;
                case 65 -> keyCode = 118;
                case 66 -> keyCode = 119;
                case 67 -> keyCode = 120;
                case 68 -> keyCode = 121;
                case 69 -> keyCode = 144;
                case 70 -> keyCode = 145;
                case 83 -> keyCode = 108;
                case 87 -> keyCode = 122;
                case 88 -> keyCode = 123;
                case 91 -> keyCode = 61440;
                case 92 -> keyCode = 61441;
                case 93 -> keyCode = 61442;
                case 99 -> keyCode = 61443;
                case 100 -> keyCode = 61444;
                case 101 -> keyCode = 61445;
                case 102 -> keyCode = 61446;
                case 103 -> keyCode = 61447;
                case 104 -> keyCode = 61448;
                case 105 -> keyCode = 61449;
                case 106 -> keyCode = 61450;
                case 107 -> keyCode = 61451;
                case 112 -> keyCode = 241;
                case 115 -> keyCode = 523;
                case 121 -> keyCode = 25;
                case 123 -> keyCode = 242;
                case 3639 -> keyCode = 154;
                case 3653 -> keyCode = 19;
                case 3655 -> keyCode = 36;
                case 3657 -> keyCode = 33;
                case 3663 -> keyCode = 35;
                case 3665 -> keyCode = 34;
                case 3666 -> keyCode = 155;
                case 3667 -> keyCode = 127;
                case 3675 -> keyCode = 157;
                case 3677 -> keyCode = 525;
                case 57416 -> keyCode = 38;
                case 57419 -> keyCode = 37;
                case 57420 -> keyCode = 12;
                case 57421 -> keyCode = 39;
                case 57424 -> keyCode = 40;
                case 65397 -> keyCode = 156;
                case 65398 -> keyCode = 65482;
                case 65400 -> keyCode = 65480;
                case 65401 -> keyCode = 65481;
                case 65403 -> keyCode = 65489;
                case 65404 -> keyCode = 65485;
                case 65406 -> keyCode = 65488;
            }
            return new KeyEvent(getDummySource(), nativeEvent.getID() - 2000, System.currentTimeMillis(), this.getJavaModifiers(nativeEvent.getModifiers()), keyCode, nativeEvent.getKeyChar(), keyLocation);
        } else {
            throw new IllegalArgumentException("Not a KeyEvent instance.");
        }

    }

    @Override
    public Class<? extends NativeInputEvent> getApplicableClass() {
        return NativeKeyEvent.class;
    }
}
