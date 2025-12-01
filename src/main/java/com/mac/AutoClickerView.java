package com.mac;/*
 * AutoClickerApp.java
 *
 * Single-file Java application demonstrating a clean "GUI-only view" that
 * calls a controller in a web-service style (in-process, no HTTP).
 *
 * Structure (all classes are in one file for convenience):
 *  - AutoClickerApp (public) -> starts the GUI
 *  - AutoClickerView         -> Swing-only view layer (calls Controller only)
 *  - MacroController         -> thin controller that exposes methods to the GUI
 *  - MacroService            -> business logic (session, macro storage, play)
 *  - Model classes: Macro, MacroAction, MacroSession
 *
 * Notes about the "contract":
 *  - GUI never contains business logic; it only calls controller methods.
 *  - Controller forwards to service(s). Controller is synchronous and
 *    returns simple DTOs / booleans (like a REST controller would).
 *  - Service contains the implementation (session state, recording stub, play loop).
 *  - No HTTP, no web services — but the call style mirrors a web frontend.
 *
 * How to run:
 *  1. Save this file as AutoClickerApp.java
 *  2. Compile: javac AutoClickerApp.java
 *  3. Run:     java AutoClickerApp
 *
 * Java 8+ recommended. The UI uses the system look-and-feel (Nimbus if available).
 */

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.mac.controller.MacroController;
import com.mac.model.Macro;
import com.mac.service.NativeListenerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.awt.Robot;
import java.awt.event.InputEvent;


/* ==========================
 * GUI (View) Layer
 * - Contains only UI code and calls to MacroController
 * - No business logic inside any listeners except UI-related updates
 * ==========================
 */

public class AutoClickerView extends JFrame implements NativeKeyListener {
    private final MacroController controller;
    private final NativeListenerService nativeListenerService;

    // Left pane: Macro list + controls
    private final DefaultListModel<Macro> macroListModel = new DefaultListModel<>();
    private final JList<Macro> macroJList = new JList<>(macroListModel);

    // Right pane: Auto clicker and macro details
//    private final JButton manualRecordBtn = new JButton("🟢 Manual Record");
    private final JButton autoRecordBtn = new JButton("🔴 Start Recording");
    private final JButton playBtn = new JButton("▶ Play");
    private final JButton stopPlayBtn = new JButton("■ Stop");
    private final JButton deleteBtn = new JButton("🗑 Delete");

    private final JRadioButton cyclesRadio = new JRadioButton("Cycles");
    private final JRadioButton timeRadio = new JRadioButton("Time (sec)");
    private final JTextField cyclesField = new JTextField("10");
    private final JTextField timeField = new JTextField("5");
    private final JTextField delayField = new JTextField("100");

    private final JLabel sessionStateLabel = new JLabel("Session: inactive");

    // Executor for background tasks (playing macros, clicker)
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "background-worker");
        t.setDaemon(true);
        return t;
    });

    // Keep track of a currently running background task future
    private Future<?> runningTask;

    public AutoClickerView(MacroController controller) {
        this.controller = controller;
        this.nativeListenerService = NativeListenerService.getInstance();
        nativeListenerService.addNativeKeyListener(this);
        setTitle("AutoClicker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        refreshMacroList();
        refreshSessionState();
        installListeners();
    }

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel title = new JLabel("AutoClicker & Macro Manager");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        p.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("Beautiful, simple, and strictly layered");
        p.add(sub, BorderLayout.EAST);

        return p;
    }

    private JComponent buildMainPanel() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.35);
        split.setLeftComponent(buildMacroPanel());
        split.setRightComponent(buildControlPanel());
        return split;
    }

    private JComponent buildMacroPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Macros"));

        macroJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        macroJList.setCellRenderer(new MacroCellRenderer());

        JScrollPane scroll = new JScrollPane(macroJList);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(1, 4, 8, 8));
//        btns.add(manualRecordBtn);
        btns.add(autoRecordBtn);
        btns.add(playBtn);
        btns.add(deleteBtn);
        panel.add(btns, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Auto Clicker"));

        // Session row
        JPanel sessionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sessionRow.add(sessionStateLabel);
        panel.add(sessionRow);

        panel.add(Box.createVerticalStrut(8));

        // Mode radio
        cyclesRadio.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(cyclesRadio);
        group.add(timeRadio);

        JPanel modeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modeRow.add(cyclesRadio);
        modeRow.add(cyclesField);
        modeRow.add(Box.createHorizontalStrut(12));
        modeRow.add(timeRadio);
        modeRow.add(timeField);
        panel.add(modeRow);

        panel.add(Box.createVerticalStrut(8));

        // Delay row
        JPanel delayRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delayRow.add(new JLabel("Delay ms:"));
        delayField.setColumns(6);
        delayRow.add(delayField);
        panel.add(delayRow);

        panel.add(Box.createVerticalStrut(12));

        JPanel playControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stopPlayBtn.setEnabled(false);
        playControls.add(stopPlayBtn);
        playControls.add(new JLabel(" "));

        JPanel macroInfoPanel = new JPanel(new BorderLayout());
        macroInfoPanel.setBorder(BorderFactory.createTitledBorder("Selected macro info"));
        JTextArea info = new JTextArea(8, 40);
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        macroInfoPanel.add(new JScrollPane(info));

        // Update info when selection changes
        macroJList.addListSelectionListener(e -> {
            Macro sel = macroJList.getSelectedValue();
            if (sel == null) info.setText("");
            else info.setText(sel.detailedString());
        });

        panel.add(playControls);
        panel.add(Box.createVerticalStrut(8));
        panel.add(macroInfoPanel);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JComponent buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel("Tip: Manual record checks session state; controller handles logic."));
        return p;
    }

    private void installListeners() {
//        manualRecordBtn.addActionListener(e -> onManualRecord());
        autoRecordBtn.addActionListener(e -> onAutoRecordToggle());
        playBtn.addActionListener(e -> onPlaySelected());
        stopPlayBtn.addActionListener(e -> onStopBackground());
        deleteBtn.addActionListener(e -> onDeleteSelected());

        // Double-click to play
        macroJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) onPlaySelected();
            }
        });

        // Window close: stop background tasks
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                executor.shutdownNow();
            }
        });
    }

//    private void onManualRecord() {
//        // GUI only calls controller methods
//        boolean active = controller.isSessionActive();
//        if (!active) {
//            // We can request a name via dialog, but controller handles starting session
//            String name = JOptionPane.showInputDialog(this, "Enter manual macro name:");
//            if (name == null || name.trim().isEmpty()) return;
//
//            controller.startManualSession();
//            refreshSessionState();
//
//            // Simulate a manual recording dialog: GUI collects minimal input and calls controller to record
//            JOptionPane.showMessageDialog(this, "Recording... perform your actions now. Click OK to stop.");
//
//            // In a real app, the controller/service would hook global listeners and stop on hotkey.
//            controller.stopManualSessionAndSave(name.trim());
//            refreshSessionState();
//            refreshMacroList();
//
//            JOptionPane.showMessageDialog(this, "Manual macro saved: " + name);
//        } else {
//            JOptionPane.showMessageDialog(this, "A session is already active.");
//        }
//    }

    private void onAutoRecordToggle() {
        // This button starts/stops auto recording using controller
        if (!controller.isRecording()) {
            controller.startAutoRecording();
            autoRecordBtn.setText("⏺️ Stop Recording");
            refreshSessionState();
        } else {
            controller.stopAutoRecording();
            autoRecordBtn.setText("🔴 Start Recording");
            refreshSessionState();
            refreshMacroList();
        }
    }

    private void onPlaySelected() {
        Macro sel = macroJList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a macro first.");
            return;
        }

        // gather settings from UI
        int delayMs = safeInt(delayField.getText(), 100);

        if (cyclesRadio.isSelected()) {
            int cycles = safeInt(cyclesField.getText(), 10);
            // call controller to play macro in background
            startBackgroundTask(() -> controller.playMacroCycles(sel.getId(), cycles, delayMs));
        } else {
            int seconds = safeInt(timeField.getText(), 5);
            startBackgroundTask(() -> controller.playMacroForTime(sel.getId(), seconds, delayMs));
        }
    }

    private void onStopBackground() {
        if (runningTask != null && !runningTask.isDone()) {
            runningTask.cancel(true);
            stopPlayBtn.setEnabled(false);
            playBtn.setEnabled(true);
        }
    }

    private void onDeleteSelected() {
        Macro sel = macroJList.getSelectedValue();
        if (sel == null) return;
        int ok = JOptionPane.showConfirmDialog(this, "Delete macro '" + sel.getName() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            controller.deleteMacro(sel.getId());
            refreshMacroList();
        }
    }

    private void refreshMacroList() {
        List<Macro> macros = controller.listMacros();
        SwingUtilities.invokeLater(() -> {
            macroListModel.clear();
            for (Macro m : macros) macroListModel.addElement(m);
        });
    }

    private void refreshSessionState() {
        boolean active = controller.isSessionActive();
        sessionStateLabel.setText("Session: " + (active ? "active" : "inactive"));
    }

    private void startBackgroundTask(Runnable task) {
        // Disable play button while running
        playBtn.setEnabled(false);
        stopPlayBtn.setEnabled(true);
        runningTask = executor.submit(() -> {
            try {
                task.run();
            } catch (CancellationException e) {
                // cancelled by user
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Background task error: " + ex.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    playBtn.setEnabled(true);
                    stopPlayBtn.setEnabled(false);
                });
            }
        });
    }

    private int safeInt(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    // Custom cell renderer to show helpful macro info
    private static class MacroCellRenderer extends JLabel implements ListCellRenderer<Macro> {
        MacroCellRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(6, 6, 6, 6));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Macro> list, Macro value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(String.format("%s — %s", value.getName(), value.getSummary()));
            setFont(getFont().deriveFont(14f));
            if (isSelected) {
                setBackground(new Color(0xDCEFFC));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int code = e.getKeyCode();
        // GLOBAL HOTKEY TO STOP
        if (code == NativeKeyEvent.VC_F9) {
            onAutoRecordToggle();
        }
        if(code == NativeKeyEvent.VC_ESCAPE) {
            onStopBackground();
        }
    }
}





/* ==========================
 * Model classes
 * ==========================
 */




/*
 * End of file
 */