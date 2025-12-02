package com.mac.ui.decorator.buttons;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

public class DeleteButtonDecorator extends ControlButtonDecorator{

    public DeleteButtonDecorator(JButton button) {
        super(button);
    }

    public void decorate(){
        super.decorate();
        btn.setIcon(new FlatSVGIcon("icons/trash.svg", 20, 20));
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
    }

}
