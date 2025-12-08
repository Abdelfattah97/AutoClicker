package com.mac.ui.decorator.buttons;

import javax.swing.*;

public class IconButtonDecorator extends ControlButtonDecorator{

    private final ImageIcon icon;

    public IconButtonDecorator(JButton button , ImageIcon icon) {
        super(button);
        this.icon = icon;
    }

    @Override
    public void decorate() {
        super.decorate();
        btn.setIcon(icon);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);

    }
}
