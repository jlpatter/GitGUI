package com.jlpatter.gitgui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class JGitGUIForm {
    private Git git;
    private String username;
    private String password;

    private static final String NAME = "";
    private static final String EMAIL = "";

    private JButton openBtn;
    private JButton exitBtn;
    private JTable commitTable;
    private JPanel panel;
    private JButton fetchBtn;
    private JButton pullBtn;
    private JButton pushBtn;
    private JButton loginBtn;
    private JButton refreshBtn;
    private JTable unstagedTable;
    private JTable stagedTable;
    private JButton stageAllBtn;
    private JTextField cMessageTxt;
    private JButton commitBtn;
    private JLabel cMessageLbl;
    private CommitTreePanel commitTree;

    public JGitGUIForm() {
        git = null;

        loginBtn.addActionListener(e -> {
            JFrame frame = new JFrame("Please Login");
            frame.setContentPane(new LoginForm(this, frame).getPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

        fetchBtn.addActionListener(e -> {
            if (git != null) {
                try {
                    CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
                    git.fetch().setCredentialsProvider(cp).call();

                    UpdateAll();
                } catch (GitAPIException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        pullBtn.addActionListener(e -> {
            if (git != null) {
                try {
                    CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
                    git.pull().setCredentialsProvider(cp).call();

                    UpdateAll();
                } catch (GitAPIException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        pushBtn.addActionListener(e -> {
            if (git != null) {
                try {
                    CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
                    git.push().setCredentialsProvider(cp).call();

                    UpdateAll();
                } catch (GitAPIException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        openBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose Git Repo");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    git = Git.open(chooser.getSelectedFile());
                    UpdateAll();
                }
            } catch (IOException | GitAPIException ex) {
                ex.printStackTrace();
            }
        });

        refreshBtn.addActionListener(e -> {
            try {
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        exitBtn.addActionListener(e -> {
            System.exit(0);
        });

        stageAllBtn.addActionListener(e -> {
            try {
                git.add().addFilepattern(".").call();
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        commitBtn.addActionListener(e -> {
            try {
                git.commit().setAuthor(NAME, EMAIL).setCommitter(NAME, EMAIL).setMessage(cMessageTxt.getText()).call();
                cMessageTxt.setText("");
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        // commitTable.addMouseListener(new RCMenu());
    }

    private void UpdateAll() throws GitAPIException, IOException {
        UpdateCommitTable();
        UpdateStatusTables();
    }

    private void UpdateCommitTable() throws GitAPIException, IOException {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Commits"}, 0);
        Iterable<RevCommit> commits = git.log().all().call();
        List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();

        Map<ObjectId, RevCommit> commitMap = new HashMap<>();
        int currentIndent = -1;
        List<GraphMessage> graphMessages = new ArrayList<>();

        for (RevCommit commit : commits) {
            commitMap.put(commit.getId(), commit);
        }

        commits = git.log().all().call();
        for (RevCommit commit : commits) {
            GraphMessage gm = new GraphMessage(commit, currentIndent);
            for (Ref ref : refs) {
                if (ref.getObjectId().equals(commit.getId())) {
                    if (gm.isFirstBranch()) {
                        currentIndent++;
                        gm.setIndent(currentIndent);
                    }
                    gm.addBranch(ref);
                }
            }

            int counter = 0;
            for (Ref ref : refs) {
                RevCommit initialCommit = commitMap.get(ref.getObjectId());
                RevCommit[] parentCommits = initialCommit.getParents();
                for (RevCommit branchCommit : parentCommits) {
                    if (branchCommit.equals(commit)) {
                        counter++;
                        break;
                    }
                }
            }

            if (counter > 1) {
                currentIndent--;
                gm.setIndent(currentIndent);
            }

            graphMessages.add(gm);
        }

        for (GraphMessage gm : graphMessages) {
            model.addRow(new Object[]{gm.toString()});
        }
        commitTable.setModel(model);
    }

    private void UpdateStatusTables() throws GitAPIException {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Unstaged Changes"}, 0);
        Status status = git.status().call();

        Set<String> missing = status.getMissing();
        for (String m : missing) {
            model.addRow(new Object[]{"(Missing) " + m});
        }

        Set<String> modified = status.getModified();
        for (String m : modified) {
            model.addRow(new Object[]{"(Modified) " + m});
        }

        Set<String> untracked = status.getUntracked();
        for (String u : untracked) {
            model.addRow(new Object[]{"(Untracked) " + u});
        }

        unstagedTable.setModel(model);

        DefaultTableModel stagedModel = new DefaultTableModel(new Object[]{"Staged Changes"}, 0);

        Set<String> added = status.getAdded();
        for (String a : added) {
            stagedModel.addRow(new Object[]{"(Added) " + a});
        }

        Set<String> changed = status.getChanged();
        for (String c : changed) {
            stagedModel.addRow(new Object[]{"(Changed) " + c});
        }

        Set<String> removed = status.getRemoved();
        for (String r : removed) {
            stagedModel.addRow(new Object[]{"(Removed) " + r});
        }

        stagedTable.setModel(stagedModel);
    }

    public void setUsername(String u) {
        username = u;
    }

    public void setPassword(String p) {
        password = p;
    }

    public JPanel getPanel() {
        return panel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
        fetchBtn = new JButton();
        fetchBtn.setText("Fetch");
        panel.add(fetchBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pullBtn = new JButton();
        pullBtn.setText("Pull");
        panel.add(pullBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pushBtn = new JButton();
        pushBtn.setText("Push");
        panel.add(pushBtn, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loginBtn = new JButton();
        loginBtn.setText("Login");
        panel.add(loginBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(1, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        unstagedTable = new JTable();
        scrollPane1.setViewportView(unstagedTable);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel.add(scrollPane2, new GridConstraints(2, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stagedTable = new JTable();
        scrollPane2.setViewportView(stagedTable);
        stageAllBtn = new JButton();
        stageAllBtn.setText("Stage All");
        panel.add(stageAllBtn, new GridConstraints(0, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cMessageTxt = new JTextField();
        panel.add(cMessageTxt, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        commitBtn = new JButton();
        commitBtn.setText("Commit");
        panel.add(commitBtn, new GridConstraints(4, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitBtn = new JButton();
        exitBtn.setText("Exit");
        panel.add(exitBtn, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        refreshBtn = new JButton();
        refreshBtn.setText("Refresh");
        panel.add(refreshBtn, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openBtn = new JButton();
        openBtn.setText("Open");
        panel.add(openBtn, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cMessageLbl = new JLabel();
        cMessageLbl.setText("Message:");
        panel.add(cMessageLbl, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commitTree = new CommitTreePanel();
        commitTree.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(commitTree, new GridConstraints(1, 0, 2, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
