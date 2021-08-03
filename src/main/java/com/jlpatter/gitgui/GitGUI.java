package com.jlpatter.gitgui;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GitGUI {
    public static void main(String[] args) throws IOException, GitAPIException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose Git Repo");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
        }

        Git git = Git.open(new File("/home/joshua/Documents/Work/GitResume/"));
        Status status = git.status().call();
        System.out.println(status.isClean());
    }
}
