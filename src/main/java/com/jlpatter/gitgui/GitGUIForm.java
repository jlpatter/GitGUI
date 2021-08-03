package com.jlpatter.gitgui;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.*;
import java.io.IOException;

public class GitGUIForm {

    private final JFrame frame;

    public GitGUIForm() {
        frame = new JFrame("Git GUI");

        JPanel panel = new JPanel();

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
                        System.out.println(commit.getShortMessage());
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
