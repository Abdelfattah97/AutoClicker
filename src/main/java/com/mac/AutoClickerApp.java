package com.mac;/*
 * AutoClickerApp.java with real macro recording using JNativeHook
 */

import com.mac.controller.MacroController;
import com.mac.service.MacroService;

import javax.swing.*;
import java.util.*;
import java.util.List;

public class AutoClickerApp {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MacroController controller = new MacroController(new MacroService());
            AutoClickerView view = new AutoClickerView(controller);
            view.setVisible(true);
        });
    }
}



