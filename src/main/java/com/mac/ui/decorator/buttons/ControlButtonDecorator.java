package com.mac.ui.decorator.buttons;

import com.mac.ui.decorator.JComponentDecorator;

import javax.swing.*;
import java.awt.*;

public abstract class ControlButtonDecorator implements JComponentDecorator {

    JButton btn;

    private ControlButtonDecorator() {
    }

    public ControlButtonDecorator(JButton button) {
        this.btn = button;
    }

    public void decorate() {
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xCBD5E1), 0));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 25));
    }
}
