package com.jlpatter.gitgui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RCMenu extends MouseAdapter {
    private final JTable commitTable;

    public RCMenu(JTable commitTable) {
        this.commitTable = commitTable;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            doMenu(e);
        }
    }

    private void doMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem testItem = new JMenuItem("Click Me!");
        testItem.addActionListener(e1 -> {
            JCommit test = (JCommit) commitTable.getModel().getValueAt(commitTable.getSelectedRow(), 1);
            System.out.println(test.getCommit().getId());
        });

        menu.add(testItem);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}
