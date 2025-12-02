package com.mac.ui.decorator.statebanner;

import com.mac.ui.decorator.JComponentDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class StateBannerDecorator implements JComponentDecorator {
    JLabel label;

    public StateBannerDecorator(JLabel label) {
        this.label = label;
    }

    private StateBannerDecorator() {
    }

    public void decorate() {
        if (label == null)
            return;
        label.setOpaque(false);
        label.setBackground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

}
