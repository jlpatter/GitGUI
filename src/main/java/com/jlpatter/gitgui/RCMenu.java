package com.jlpatter.gitgui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RCMenu extends MouseAdapter {
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
            System.out.println("BLURG");
        });

        menu.add(testItem);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}
