package com.jlpatter.gitgui;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.Vector;

public class GitGUIForm {

    private final JFrame frame;

    public GitGUIForm() {
        frame = new JFrame("Git GUI");

        JPanel panel = new JPanel();

        JTable table = new JTable();
        table.setFillsViewportHeight(true);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Commits"}, 0);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton openBtn = new JButton("Open");
        openBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose Git Repo");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getSelectedFile(): " + chooser.getSelectedFile());

                    Git git = Git.open(chooser.getSelectedFile());
                    Iterable<RevCommit> commits = git.log().call();
                    for (RevCommit commit : commits) {
                        Vector<String> stringVector = new Vector<>();
                        stringVector.addElement(commit.getShortMessage());
                        model.addRow(stringVector);
                    }
                }
            } catch (IOException | GitAPIException ex) {
                ex.printStackTrace();
            }
        });

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> {
            System.exit(0);
        });

        table.setModel(model);
        panel.add(scrollPane);
        panel.add(openBtn);
        panel.add(exitBtn);

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    public void show() {
        frame.setVisible(true);
    }
}
