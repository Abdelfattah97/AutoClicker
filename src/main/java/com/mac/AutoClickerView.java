package com.mac;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.mac.controller.MacroController;
import com.mac.model.Macro;
import com.mac.model.AutoClickExecution;
import com.mac.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.mac.util.ParseUtil.safeInt;

public class AutoClickerView extends JFrame implements NativeKeyListener {

    private final DefaultListModel<Macro> macroListModel = new DefaultListModel<>();
    private final JList<Macro> macroList = new JList<>(macroListModel);

    private final JButton recordBtn = new JButton("Record (F9)");
    private final JButton playBtn = new JButton("Play (F8)");
    private final JButton deleteBtn = new JButton();

    private final JLabel stateBanner = new JLabel("● Idle", SwingConstants.CENTER);
    private final JTextArea consoleArea = new JTextArea();

    private final MacroController macroController;

    private final List<ObserverHandler> observerHandlers;


    public AutoClickerView(MacroController macroController, NativeListenerService nativeListenerService) {
        this.macroController = macroController;
        nativeListenerService.addNativeKeyListener(this);
        observerHandlers = new ArrayList<>();
        var macroExecuterHandler = new AutoClickExecuterHandler(this);
        var macroRecorderHandler = new MacroRecorderHandler(this);
        observerHandlers.addAll(List.of(macroExecuterHandler, macroRecorderHandler));
        macroController.subscribeForNewMacros(macroRecorderHandler);
        macroController.subscribeForMacroExecution(macroExecuterHandler);
        FlatLightLaf.setup();
        initFrame();
        initHeader();
        initBody();
        setVisible(true);
    }

    private void initFrame() {
        setTitle("AutoClicker Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(0xE2E8F0)); // slate-200
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1E293B));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        // Icon
        JLabel icon = new JLabel(new FlatSVGIcon("icons/mouse.svg", 24, 24));
        icon.setOpaque(false);
        icon.setBackground(new Color(0x2563EB));
        icon.setForeground(Color.WHITE);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setPreferredSize(new Dimension(48, 48));
        icon.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        icon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Text
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Auto Clicker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Mouse Auto Clicker");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(0x94A3B8));

        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
    }

    private void initBody() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerSize(6);
        split.setDividerLocation(350);

        split.setLeftComponent(buildMacroListPanel());
        split.setRightComponent(buildControlPanel());

        add(split, BorderLayout.CENTER);
    }

    private JPanel buildMacroListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Available Macros");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        macroList.setCellRenderer(new MacroCellRenderer());
        macroList.setFixedCellHeight(60);
        macroList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        macroList.addListSelectionListener(this::onSelectMacro);

        JScrollPane scroll = new JScrollPane(macroList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xCBD5E1), 0));

        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));
        buttons.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttons.setOpaque(false);

        styleButton(recordBtn, new Color(0xFFFFFF), new Color(0xA8A8AF));
        styleButton(playBtn, new Color(0x16A34A), Color.WHITE);

        deleteBtn.setIcon(new FlatSVGIcon("icons/trash.svg", 20, 20));
        deleteBtn.setOpaque(false);
        deleteBtn.setBorder(null);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        recordBtn.addActionListener(l -> {
            toggleRecord();
        });

        playBtn.addActionListener(l -> {
            togglePlay();
        });

        deleteBtn.addActionListener(l -> {
            Macro selected = macroList.getSelectedValue();
            if (selected != null) {
                macroController.deleteMacro(selected.getId());
                macroListModel.removeElement(selected);
            }
        });


        buttons.add(recordBtn);
        buttons.add(playBtn);
        buttons.add(deleteBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void toggleRecord() {
        if (!macroController.isRecording()) {
            // Start recording
            macroController.startAutoRecording();
            recordBtn.setText("Stop (F9)");           // change text
            setRecordingState(true);             // update banner
        } else {
            // Stop recording
            macroController.stopAutoRecording();
            recordBtn.setText("Record (F9)");         // revert text
            setRecordingState(false);            // update banner
        }
    }


    /**
     * toggles the play feature both view and functionality
     */
    private void togglePlay() {
        if (macroController.isPalying()) {
            onStopPlay();
        } else {
            onPlaySelected();
        }
        updatePlayBtn();
    }

    /**
     * toggles the play btn only without functionality changes
     */
    private void updatePlayBtn() {
        boolean isPlaying = macroController.isPalying();
        System.out.println("isPlaying" + isPlaying);
        if (!isPlaying) {
            this.playBtn.setText("Play (F8)");
            styleButton(playBtn, new Color(0x16A34A), Color.WHITE);
        } else {
            this.playBtn.setText("Stop (F8)");
            styleButton(playBtn, new Color(0xA31630), Color.WHITE);
        }
    }

    private void onStopPlay() {
        macroController.stopPlay();
    }

    private void onPlaySelected() {
        Macro sel = macroList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a macro first.");
            return;
        }

        // dialog component
        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));

        JRadioButton cyclesRadio = new JRadioButton("Run for cycles");
        JRadioButton timeRadio = new JRadioButton("Run for time (seconds)");

        ButtonGroup group = new ButtonGroup();
        group.add(cyclesRadio);
        group.add(timeRadio);

        JTextField cyclesField = new JTextField("10");
        JTextField timeField = new JTextField("5");
        JTextField delayField = new JTextField("0");

        panel.add(cyclesRadio);
        panel.add(new JLabel("Cycles:"));
        panel.add(cyclesField);

        panel.add(Box.createVerticalStrut(10));

        panel.add(timeRadio);
        panel.add(new JLabel("Seconds:"));
        panel.add(timeField);

        panel.add(Box.createVerticalStrut(10));

        panel.add(new JLabel("Extra delay (ms):"));
        panel.add(delayField);

        // force one selection
        cyclesRadio.setSelected(true);
        timeField.setEnabled(false);
        cyclesField.setEnabled(true);

        cyclesRadio.addItemListener(l -> {
            cyclesField.setEnabled(true);
            timeField.setEnabled(false);
        });
        timeRadio.addItemListener(l -> {
            timeField.setEnabled(true);
            cyclesField.setEnabled(false);
        });

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Play Macro Options",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        int delayMs = safeInt(delayField.getText(), 0);

        if (cyclesRadio.isSelected()) {
            int cycles = safeInt(cyclesField.getText(), 10);
            macroController.playMacroCycles(sel.getId(), cycles, delayMs);
        } else {
            int seconds = safeInt(timeField.getText(), 5);
            macroController.playMacroForTime(sel.getId(), seconds, delayMs);
        }
    }


    private void onSelectMacro(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) { // avoid double events
            Macro selected = macroList.getSelectedValue();
            displayMacroDetails(selected);
        }
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0xF8FAFC));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        // State Banner
        stateBanner.setOpaque(false);
        stateBanner.setBackground(Color.WHITE);
        stateBanner.setForeground(new Color(0x475569));
        stateBanner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stateBanner.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel bannerWrapper = new JPanel(new BorderLayout());
        bannerWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        bannerWrapper.setOpaque(false);
        bannerWrapper.add(stateBanner, BorderLayout.WEST);

        panel.add(bannerWrapper);
        panel.add(Box.createVerticalStrut(20));

        // Details Console
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(0x1E293B));
        consoleArea.setForeground(new Color(0x4ADE80));
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(consoleArea);
        scroll.setPreferredSize(new Dimension(0, 280));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x334155), 1));

        panel.add(scroll);
        return panel;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xCBD5E1), 0));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void update(Macro e) {
        addMacro(e);
    }

    public void update(AutoClickExecution e) {
        updatePlayBtn();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        System.out.println(nativeEvent.getKeyCode());
        switch (nativeEvent.getKeyCode()) {
            case NativeKeyEvent.VC_F8 -> {
                togglePlay();
                break;
            }
            case NativeKeyEvent.VC_F9 -> {
                toggleRecord();
                break;
            }
            default -> {
                break;
            }
        }
    }


    // Custom Renderer
    private static class MacroCellRenderer extends JPanel implements ListCellRenderer<Macro> {
        private final JLabel title = new JLabel();

        public MacroCellRenderer() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));
            add(title, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Macro> list, Macro value, int index, boolean isSelected, boolean cellHasFocus) {
            title.setText(value.getName());

            if (isSelected) {
                setBackground(new Color(0xEFF6FF));
                setBorder(BorderFactory.createLineBorder(new Color(0x60A5FA), 2));
                title.setForeground(Color.BLACK);
            } else {
                setBackground(Color.WHITE);
                setBorder(new EmptyBorder(10, 10, 10, 10));
                title.setForeground(Color.DARK_GRAY);
            }
            return this;
        }
    }

    private void displayMacroDetails(Macro macro) {
        if (macro == null) {
            clearLogText();
            return;
        }
        setLogText(macro.detailedString());

    }


    // API for Controller --------------------------------------

    public void setMacros(List<Macro> macros) {
        macroListModel.clear();
        macros.forEach(macroListModel::addElement);
    }

    public void addMacro(Macro macro) {
        macroListModel.addElement(macro);
    }

    public void removeMacro(Macro macro) {
        macroListModel.removeElement(macro);
    }

    public void appendLog(String msg) {
        consoleArea.append(msg + " ");
    }

    public void setLogText(String msg) {
        consoleArea.setText(msg);
    }

    public void clearLogText() {
        consoleArea.setText(null);
    }

    public void setRecordingState(boolean recording) {
        if (recording) {
            stateBanner.setText("● Recording");
            stateBanner.setBackground(new Color(0xFEE2E2));
            stateBanner.setForeground(new Color(0xDC2626));
        } else {
            stateBanner.setText("● Idle");
            stateBanner.setBackground(Color.WHITE);
            stateBanner.setForeground(new Color(0x475569));
        }
    }

    public JButton getRecordButton() {
        return recordBtn;
    }

    public JButton getPlayButton() {
        return playBtn;
    }

    public JButton getDeleteButton() {
        return deleteBtn;
    }

    public JList<Macro> getMacroList() {
        return macroList;
    }

    private interface ObserverHandler {
    }

    private static class AutoClickExecuterHandler implements AutoClickExecuterObserver, ObserverHandler {
        public final AutoClickerView autoClickerView;

        private AutoClickExecuterHandler(AutoClickerView autoClickerView) {
            this.autoClickerView = autoClickerView;
        }

        @Override
        public void update(AutoClickExecution e) {
            SwingUtilities.invokeLater(() -> {
                autoClickerView.update(e);
            });
        }
    }

    private static class MacroRecorderHandler implements MacroRecorderObserver, ObserverHandler {
        public final AutoClickerView autoClickerView;

        private MacroRecorderHandler(AutoClickerView autoClickerView) {
            this.autoClickerView = autoClickerView;
        }

        @Override
        public void update(Macro e) {
            SwingUtilities.invokeLater(() -> {
                autoClickerView.update(e);
            });
        }
    }

}
