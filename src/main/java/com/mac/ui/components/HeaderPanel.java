package com.mac.ui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mac.config.IconConfig;
import com.mac.ui.decorator.buttons.IconButtonDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HeaderPanel extends JPanel {
    private Point initialClick;
    private boolean maximized = false;
    private Rectangle prevBounds;

    public HeaderPanel(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(0x1E293B));
        setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        // Left: Icon
        var iconPath = IconConfig.getIconPathMap().get("app-icon");
        JLabel appIcon = new JLabel(new FlatSVGIcon(iconPath,48,48));
        appIcon.setPreferredSize(new Dimension(48, 48));
        add(appIcon, BorderLayout.WEST);

        // Center: Title + subtitle
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        JLabel title = new JLabel("Auto Clicker");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Mouse Auto Clicker");
        subtitle.setForeground(new Color(0x94A3B8));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textPanel.add(title);
        textPanel.add(subtitle);
        add(textPanel, BorderLayout.CENTER);

        // Right: Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttons.setOpaque(false);

        HeaderButton settingsBtn = new HeaderButton();
        settingsBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Settings clicked"));
        new IconButtonDecorator(settingsBtn,new FlatSVGIcon("")).decorate();

        HeaderButton minBtn = new HeaderButton("—");
        minBtn.addActionListener(e -> frame.setState(Frame.ICONIFIED));

        HeaderButton maxBtn = new HeaderButton("□");
        maxBtn.addActionListener(e -> toggleMaximize(frame));

        HeaderButton closeBtn = new HeaderButton("x");
        closeBtn.addActionListener(e -> System.exit(0));

        buttons.add(settingsBtn);
        buttons.add(minBtn);
        buttons.add(maxBtn);
        buttons.add(closeBtn);
        add(buttons, BorderLayout.EAST);

        // Make draggable
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!maximized) {
                    int thisX = frame.getLocation().x;
                    int thisY = frame.getLocation().y;
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;
                    frame.setLocation(thisX + xMoved, thisY + yMoved);
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void toggleMaximize(JFrame frame) {
        if (!maximized) {
            prevBounds = frame.getBounds();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setBounds(prevBounds);
            frame.setExtendedState(JFrame.NORMAL);
        }
        maximized = !maximized;
    }
}
