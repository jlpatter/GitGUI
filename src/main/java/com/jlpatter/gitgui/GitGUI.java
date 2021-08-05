package com.jlpatter.gitgui;

import javax.swing.*;

public class GitGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Josh's Git GUI");
        frame.setContentPane(new JGitGUIForm().getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
