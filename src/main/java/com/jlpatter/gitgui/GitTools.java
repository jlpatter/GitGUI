package com.jlpatter.gitgui;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitTools {
    private static final String NAME = "";
    private static final String EMAIL = "";

    private Git git;
    private String username;
    private String password;

    public void openRepo(File file) {
        try {
            git = Git.open(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void fetch() {
        if (git != null) {
            try {
                CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
                git.fetch().setCredentialsProvider(cp).call();
            } catch (GitAPIException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void pull() {
        if (git != null) {
            try {
                CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
                git.pull().setCredentialsProvider(cp).call();
            } catch (GitAPIException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void push() {
        if (git != null) {
            try {
                CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
                git.push().setCredentialsProvider(cp).call();
            } catch (GitAPIException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stageAll() throws GitAPIException {
        git.add().addFilepattern(".").call();
    }

    public void commit(String message) throws GitAPIException {
        git.commit().setAuthor(NAME, EMAIL).setCommitter(NAME, EMAIL).setMessage(message).call();
    }

    public Status getStatus() throws GitAPIException {
        return git.status().call();
    }

    public Iterable<RevCommit> getLog() throws IOException, GitAPIException {
        return git.log().all().call();
    }

    public List<Ref> getAllBranches() throws GitAPIException {
        return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
