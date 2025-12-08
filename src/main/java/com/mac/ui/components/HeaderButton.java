package com.mac.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HeaderButton extends JButton {
    public HeaderButton() {
        this(null);
    }

    public HeaderButton(String text) {
        super(text);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setOpaque(true);
                setBackground(new Color(0x3B82F6));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setOpaque(false);
            }
        });
    }
}
