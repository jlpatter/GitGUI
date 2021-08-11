package com.jlpatter.gitgui;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class GraphMessage {
    private final RevCommit commit;
    private final List<Ref> branches;
    private final int indent;

    public GraphMessage(RevCommit commit, int indent) {
        this.commit = commit;
        branches = new ArrayList<>();
        this.indent = indent;
    }

    public void AddBranch(Ref branch) {
        branches.add(branch);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }

        for (Ref branch : branches) {
            sb.append("(").append(branch.getName()).append(") ");
        }

        sb.append(commit.getShortMessage());
        return sb.toString();
    }
}
