package com.jlpatter.gitgui;

import org.eclipse.jgit.lib.Ref;

import java.util.ArrayList;
import java.util.List;

public class GraphMessage {
    private final JCommit commit;
    private final List<Ref> branches;

    public GraphMessage(JCommit commit) {
        this.commit = commit;
        branches = new ArrayList<>();
    }

    public void addBranch(Ref branch) {
        branches.add(branch);
    }

    public boolean isFirstBranch() {
        return branches.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < commit.getIndent(); i++) {
            sb.append("    ");
        }

        sb.append("* ");

        for (Ref branch : branches) {
            sb.append("(").append(branch.getName()).append(") ");
        }

        sb.append(commit.getCommit().getShortMessage());
        return sb.toString();
    }
}
