package com.jlpatter.gitgui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class JGitGUIForm {
    private final GitTools gitTools;

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

    public JGitGUIForm() {
        gitTools = new GitTools();

        loginBtn.addActionListener(e -> {
            JFrame frame = new JFrame("Please Login");
            frame.setContentPane(new LoginForm(gitTools, frame).getPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

        fetchBtn.addActionListener(e -> {
            gitTools.fetch();
            try {
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        pullBtn.addActionListener(e -> {
            gitTools.pull();
            try {
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        pushBtn.addActionListener(e -> {
            gitTools.push();
            try {
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        openBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose Git Repo");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                gitTools.openRepo(chooser.getSelectedFile());
            }
            try {
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
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
                gitTools.stageAll();
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        commitBtn.addActionListener(e -> {
            try {
                gitTools.commit(cMessageTxt.getText());
                cMessageTxt.setText("");
                UpdateAll();
            } catch (GitAPIException | IOException ex) {
                ex.printStackTrace();
            }
        });

        commitTable.addMouseListener(new RCMenu());
    }

    private void UpdateAll() throws GitAPIException, IOException {
        UpdateCommitTable();
        UpdateStatusTables();
    }

    private void UpdateCommitTable() throws GitAPIException, IOException {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Commits"}, 0);
        Iterable<RevCommit> commits = gitTools.getLog();
        List<Ref> refs = gitTools.getAllBranches();

        Map<ObjectId, RevCommit> commitMap = new HashMap<>();
        List<GraphMessage> graphMessages = new ArrayList<>();

        boolean first = true;
        RevCommit firstCommit = null;
        for (RevCommit commit : commits) {
            if (first) {
                firstCommit = commit;
                first = false;
            }
            commitMap.put(commit.getId(), commit);
        }

        Set<RevCommit> startingCommits = new HashSet<>();
        for (Ref ref : refs) {
            startingCommits.add(commitMap.get(ref.getObjectId()));
        }

        assert firstCommit != null;
        graphMessages.add(new GraphMessage(firstCommit, 0));
        walkGraphCommits(firstCommit, graphMessages, 0, 0);

        for (GraphMessage gm : graphMessages) {
            model.addRow(new Object[]{gm.toString()});
        }
        commitTable.setModel(model);
    }

    private void walkGraphCommits(RevCommit childCommit, List<GraphMessage> graphMessages, int depth, int indent) {
        RevCommit[] parents = childCommit.getParents();
        for (int i = parents.length - 1; i >= 0; i--) {
            graphMessages.add(new GraphMessage(parents[i], i + indent));
            if (depth < 10) {
                walkGraphCommits(parents[i], graphMessages, depth + 1, i + indent);
            }
        }
    }

    private void UpdateStatusTables() throws GitAPIException {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Unstaged Changes"}, 0);
        Status status = gitTools.getStatus();

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
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(1, 0, 3, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        commitTable = new JTable();
        commitTable.setPreferredScrollableViewportSize(new Dimension(900, 400));
        commitTable.setSelectionBackground(new Color(-13670966));
        scrollPane1.setViewportView(commitTable);
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
        final JScrollPane scrollPane2 = new JScrollPane();
        panel.add(scrollPane2, new GridConstraints(1, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        unstagedTable = new JTable();
        scrollPane2.setViewportView(unstagedTable);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel.add(scrollPane3, new GridConstraints(2, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stagedTable = new JTable();
        scrollPane3.setViewportView(stagedTable);
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
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
