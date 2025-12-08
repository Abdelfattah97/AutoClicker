package com.mac;/*
 * AutoClickerApp.java with real macro recording using JNativeHook
 */

import com.github.kwhat.jnativehook.GlobalScreen;
import com.mac.controller.MacroController;
import com.mac.service.MacroService;
import com.mac.service.NativeListenerService;

import javax.swing.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoClickerApp {
    public static void main(String[] args) {
//        logging();
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            MacroController controller = new MacroController(new MacroService());
            AutoClickerView view = new AutoClickerView(controller, NativeListenerService.getInstance());
            view.setVisible(true);
        });
    }

    static void logging() {
        // Get the logger for JNativeHook
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());

        // Remove default handlers
        logger.setUseParentHandlers(false);

        // Add a console handler
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        // Set logger level to ALL
        logger.setLevel(Level.ALL);

        System.out.println("JNativeHook logging enabled!");
    }
}



