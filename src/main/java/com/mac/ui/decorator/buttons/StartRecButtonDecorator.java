package com.mac.ui.decorator.buttons;

import javax.swing.*;
import java.awt.*;

public class StartRecButtonDecorator extends ControlButtonDecorator {

    public StartRecButtonDecorator(JButton recordBtn) {
        super(recordBtn);
    }

    @Override
    public void decorate() {
        super.decorate();
        btn.setText("● Record (F9)");
        btn.setBackground(new Color(0xFFFFFF));
        btn.setForeground(new Color(0xA31630));
    }
}
