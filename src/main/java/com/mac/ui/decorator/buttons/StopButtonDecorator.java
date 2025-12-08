package com.mac.ui.decorator.buttons;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mac.config.IconConfig;

import javax.swing.*;
import java.awt.*;

public class StopButtonDecorator extends ControlButtonDecorator {

    public StopButtonDecorator(JButton button) {
        super(button);
    }

    public void decorate() {
        super.decorate();
        btn.setText("Stop (F8)");
        btn.setBackground(new Color(0xA31630));
        btn.setForeground(Color.WHITE);
        var iconPath = IconConfig.getIconPathMap().get("stop-button");
        FlatSVGIcon icon = new FlatSVGIcon(iconPath,10,10);
        var iconColor = btn.getForeground();
        icon.setColorFilter(new FlatSVGIcon.ColorFilter((s) -> iconColor));
        btn.setIcon(icon);
    }

}
