package com.mac.ui.decorator.statebanner;

import javax.swing.*;
import java.awt.*;

public class RecordingStateBannerDecorator extends StateBannerDecorator {


    public RecordingStateBannerDecorator(JLabel stateBanner) {
        super(stateBanner);
    }

    @Override
    public void decorate() {
        super.decorate();
        label.setText("● Recording");
        label.setForeground(new Color(0xDC2626));
    }
}
