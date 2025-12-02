package com.mac.ui.decorator.buttons;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;

public class PlayButtonDecorator extends ControlButtonDecorator {

    public PlayButtonDecorator(JButton button) {
        super(button);
    }

    public void decorate() {
        super.decorate();
        btn.setText("Play (F8)");
        btn.setBackground(new Color(0x16A34A));
        btn.setForeground(Color.WHITE);
        Color iconColor = btn.getForeground();
        FlatSVGIcon icon = new FlatSVGIcon("icons/black-triangle-right.svg", 9, 9);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter((c) -> iconColor));
        btn.setIcon(icon);
    }

}
