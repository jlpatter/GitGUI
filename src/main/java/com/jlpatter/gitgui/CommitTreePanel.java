package com.jlpatter.gitgui;

import javax.swing.*;
import java.awt.*;

public class CommitTreePanel extends JPanel {

    public CommitTreePanel() {
        setPreferredSize(new Dimension(500, 500));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(0, 0, 200, 200);
    }
}
