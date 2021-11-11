package com.jlpatter.gitgui;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class JCommit {
    private final RevCommit commit;
    private final JCommit mergeChildCommit;
    private final List<Ref> branches;
    private int indent;

    public JCommit(RevCommit commit, JCommit mergeChildCommit, int indent) {
        this.commit = commit;
        this.mergeChildCommit = mergeChildCommit;
        branches = new ArrayList<>();
        this.indent = indent;
    }

    public RevCommit getCommit() {
        return commit;
    }

    public JCommit getMergeChildCommit() {
        return mergeChildCommit;
    }

    public void addBranch(Ref branch) {
        branches.add(branch);
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public int getIndent() {
        return indent;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == JCommit.class && this.commit.equals(((JCommit) obj).commit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }

        sb.append("* ");

        sb.append(commit.getShortMessage());
        return sb.toString();
    }

    public String branchesToString() {
        StringBuilder sb = new StringBuilder();
        for (Ref branch : branches) {
            sb.append("(").append(branch.getName()).append(") ");
        }
        return sb.toString();
    }
}
