package com.jlpatter.gitgui;

import org.eclipse.jgit.revwalk.RevCommit;

public class JCommit {
    private final RevCommit commit;
    private int indent;

    public JCommit(RevCommit commit, int indent) {
        this.commit = commit;
        this.indent = indent;
    }

    public RevCommit getCommit() {
        return commit;
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
