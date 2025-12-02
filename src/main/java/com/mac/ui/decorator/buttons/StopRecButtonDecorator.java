package com.mac.ui.decorator.buttons;

import javax.swing.*;
import java.awt.*;

public class StopRecButtonDecorator extends ControlButtonDecorator {

    public StopRecButtonDecorator(JButton recordBtn) {
        super(recordBtn);
    }

    @Override
    public void decorate() {
        super.decorate();
        btn.setText("■ Stop (F9)");
        btn.setBackground(new Color(0xFFFFFF));
        btn.setForeground(new Color(0xA8A8AF));
    }
}
