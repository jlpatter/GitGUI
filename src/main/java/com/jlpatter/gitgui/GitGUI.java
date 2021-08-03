package com.jlpatter.gitgui;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.*;
import java.io.IOException;

public class GitGUI {
    public static void main(String[] args) throws IOException, GitAPIException {
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


    }
}
