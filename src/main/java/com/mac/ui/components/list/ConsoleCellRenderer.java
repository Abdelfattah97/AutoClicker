package com.mac.ui.components.list;

import javax.swing.*;
import java.awt.*;

import com.mac.model.Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsoleCellRenderer extends JLabel implements ListCellRenderer<Action> {


    @Override
    public Component getListCellRendererComponent(JList<? extends Action> list, Action value, int index, boolean isSelected, boolean cellHasFocus) {

        setText(value.toString());
        //            consoleArea.setForeground(new Color(0x4ADE80));

        setFont(new Font("Consolas", Font.PLAIN, 13));
        if (isSelected) {
            setForeground(Color.BLACK);
            setOpaque(true);
        } else {
            setForeground(new Color(0x4ADE80));
            setOpaque(false);

        }
        return this;
    }


}
