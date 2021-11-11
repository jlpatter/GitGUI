package com.jlpatter.gitgui;

import org.eclipse.jgit.revwalk.RevCommit;

public class JCommit {
    private final RevCommit commit;
    private final JCommit mergeChildCommit;
    private int indent;

    public JCommit(RevCommit commit, JCommit mergeChildCommit, int indent) {
        this.commit = commit;
        this.mergeChildCommit = mergeChildCommit;
        this.indent = indent;
    }

    public RevCommit getCommit() {
        return commit;
    }

    public JCommit getMergeChildCommit() {
        return mergeChildCommit;
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
}
