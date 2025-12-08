package com.mac.ui.decorator.buttons;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mac.config.IconConfig;

import javax.swing.*;

public class DeleteButtonDecorator extends ControlButtonDecorator{

    public DeleteButtonDecorator(JButton button) {
        super(button);
    }

    public void decorate(){
        super.decorate();
        var iconPath = IconConfig.getIconPathMap().get("delete-icon");
        btn.setIcon(new FlatSVGIcon(iconPath, 20, 20));
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
    }

}
