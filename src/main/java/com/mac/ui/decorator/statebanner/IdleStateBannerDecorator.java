package com.mac.ui.decorator.statebanner;

import javax.swing.*;
import java.awt.*;

public class IdleStateBannerDecorator extends StateBannerDecorator {

    public IdleStateBannerDecorator(JLabel stateBanner) {
        super(stateBanner);
    }

    @Override
    public void decorate() {
        super.decorate();
        label.setText("● Idle");
        label.setForeground(new Color(0x475569));
    }
}
