package com.mac;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.mac.config.IconConfig;
import com.mac.controller.MacroController;
import com.mac.input.AwtKeyMapper;
import com.mac.model.AutoClickExecution;
import com.mac.model.EventSource;
import com.mac.model.Macro;
import com.mac.service.AutoClickExecutorObserver;
import com.mac.recorder.MacroRecorderObserver;
import com.mac.service.NativeListenerService;
import com.mac.ui.components.HeaderPanel;
import com.mac.ui.components.list.ConsoleCellRenderer;
import com.mac.ui.decorator.buttons.*;
import com.mac.ui.decorator.statebanner.IdleStateBannerDecorator;
import com.mac.ui.decorator.statebanner.RecordingStateBannerDecorator;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mac.model.Action;

import static com.mac.util.ParseUtil.safeInt;

@SuppressWarnings("FieldCanBeLocal")
public class AutoClickerView extends JFrame implements NativeKeyListener {

    private final DefaultListModel<Macro> macroListModel = new DefaultListModel<>();
    private final DefaultListModel<Action> actionListModel = new DefaultListModel<>();
    @Getter
    private final JList<Macro> macroList = new JList<>(macroListModel);

    private final JButton recordBtn = new JButton();
    private final JButton playBtn = new JButton();
    private final JButton deleteBtn = new JButton();

    private final JLabel stateBanner = new JLabel();
    private final JList<Action> consoleArea = new JList<>(actionListModel);

    private final MacroController macroController;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<ObserverHandler> observerHandlers;

    private EventSource consoleFilter = null;


    public AutoClickerView(MacroController macroController, NativeListenerService nativeListenerService) {
        this.macroController = macroController;
        nativeListenerService.addNativeKeyListener(this);
        observerHandlers = new ArrayList<>();
        var macroExecutorHandler = new AutoClickExecutorHandler(this);
        var macroRecorderHandler = new MacroRecorderHandler(this);
        observerHandlers.addAll(List.of(macroExecutorHandler, macroRecorderHandler));
        macroController.subscribeForNewMacros(macroRecorderHandler);
        macroController.subscribeForMacroExecution(macroExecutorHandler);
        FlatLightLaf.setup();
        initFrame();
        initHeader();
        initBody();
        setVisible(true);
    }

    private void initFrame() {
        setTitle("AutoClicker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(0xE2E8F0)); // slate-200
        var iconPath = IconConfig.getIconPathMap().get("app-icon");
        setIconImage(new FlatSVGIcon(iconPath).getImage());
        setUndecorated(true);
    }

    private void initHeader() {
        add(new HeaderPanel(this), BorderLayout.NORTH);
    }

    private void initBody() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerSize(3);
        split.setDividerLocation(Double.valueOf(0.45 * getWidth()).intValue());

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

        new StartRecButtonDecorator(recordBtn).decorate();
        new PlayButtonDecorator(playBtn).decorate();
        new DeleteButtonDecorator(deleteBtn).decorate();

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
                actionListModel.removeAllElements();
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
        if (macroController.isPlaying()) return;
        if (!macroController.isRecording()) {
            // Start recording
            macroController.startAutoRecording();
            new StopRecButtonDecorator(recordBtn).decorate();
            setRecordingState(true);            // update banner
        } else {
            // Stop recording
            macroController.stopAutoRecording();
            new StartRecButtonDecorator(recordBtn).decorate();    // update banner
            setRecordingState(false);            // update banner
        }
    }


    /**
     * toggles the play feature both view and functionality
     */
    private void togglePlay() {
        if (macroController.isPlaying()) {
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
        boolean isPlaying = macroController.isPlaying();
        if (!isPlaying) {
            new PlayButtonDecorator(playBtn).decorate();
        } else {
            new StopButtonDecorator(playBtn).decorate();
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
            addMacroActionsToModel(macroList.getSelectedValue());
        }
    }

    private void addMacroActionsToModel(Macro macro) {
        if (macro != null) {
            actionListModel.removeAllElements();
            actionListModel.addAll(macro.getActions());
        }
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0xF8FAFC));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // State Banner
        new IdleStateBannerDecorator(stateBanner).decorate();

        JPanel bannerWrapper = new JPanel(new BorderLayout());
        bannerWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        bannerWrapper.setOpaque(false);
        bannerWrapper.add(stateBanner, BorderLayout.WEST);

        panel.add(bannerWrapper);
        panel.add(Box.createVerticalStrut(20));

        // Details Console
        consoleArea.setBackground(new Color(0x1E293B));
        consoleArea.setForeground(new Color(0x4ADE80));
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        consoleArea.setCellRenderer(new ConsoleCellRenderer());
        JScrollPane scroll = new JScrollPane(consoleArea);
        scroll.setPreferredSize(new Dimension(0, 280));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x334155), 1));

        panel.add(scroll);

        JComboBox<String> filterBox = getFilterBox();

        panel.add(filterBox);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JComboBox<String> getFilterBox() {
        String[] filterOptions = {"All", "Key Events", "Mouse Events"};
        JComboBox<String> filterBox = new JComboBox<>(filterOptions);
        filterBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        filterBox.addActionListener(l -> {
            switch (filterBox.getSelectedIndex()) {
                case 1 -> consoleFilter = EventSource.KEY_EVENT;
                case 2 -> consoleFilter = EventSource.MOUSE_EVENT;
                default -> consoleFilter = null;
            }

            actionListModel.removeAllElements();
            if (macroList.getSelectedValue() != null) {
                actionListModel.addAll(macroList.getSelectedValue().getActions()
                        .stream().filter(this::isFilteredAction).toList());
            }
        });
        return filterBox;
    }

    public void update(Macro e) {
        addMacro(e);
    }

    public void update(AutoClickExecution e) {
        updatePlayBtn();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        switch (nativeEvent.getKeyCode()) {
            case NativeKeyEvent.VC_F8 -> {
                togglePlay();
            }
            case NativeKeyEvent.VC_F9 -> {
                toggleRecord();
            }
            default -> {
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

    private boolean isFilteredAction(Action a) {
        return consoleFilter == null || Optional.ofNullable(a)
                .map(Action::getEvent)
                .map(AwtKeyMapper::getSource)
                .map(e -> e.equals(consoleFilter))
                .orElse(false);
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

    public void setRecordingState(boolean recording) {
        if (recording) {
            new RecordingStateBannerDecorator(stateBanner).decorate();
        } else {
            new IdleStateBannerDecorator(stateBanner).decorate();
        }
    }

    private interface ObserverHandler {
    }

    private static class AutoClickExecutorHandler implements AutoClickExecutorObserver, ObserverHandler {
        public final AutoClickerView autoClickerView;

        private AutoClickExecutorHandler(AutoClickerView autoClickerView) {
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
