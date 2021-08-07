package com.jlpatter.gitgui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Insets;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class JGitGUIForm {
    private Git git;

    private JButton openBtn;
    private JButton exitBtn;
    private JTable table1;
    private JPanel panel;
    private JButton fetchBtn;
    private JButton pullBtn;
    private JButton pushBtn;

    private static final String USERNAME = "jlpatter";
    private static final String PASSWORD = "";

    public JGitGUIForm() {
        git = null;

        fetchBtn.addActionListener(e -> {
            if (git != null) {
                try {
                    CredentialsProvider cp = new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD);
                    git.fetch().setCredentialsProvider(cp).call();

                    UpdateTable();
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
                    UpdateTable();
                }
            } catch (IOException | GitAPIException ex) {
                ex.printStackTrace();
            }
        });

        exitBtn.addActionListener(e -> {
            System.exit(0);
        });

        table1.addMouseListener(new RCMenu());
    }

    private void UpdateTable() throws GitAPIException, IOException {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Commits"}, 0);
        Iterable<RevCommit> commits = git.log().all().call();
        List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (RevCommit commit : commits) {
            Vector<String> stringVector = new Vector<>();
            StringBuilder strToAdd = new StringBuilder();

            for (Ref ref : refs) {
                if (ref.getObjectId().equals(commit.getId())) {
                    strToAdd.append("(").append(ref.getName()).append(") ");
                }
            }

            strToAdd.append(commit.getShortMessage());
            stringVector.addElement(strToAdd.toString());
            model.addRow(stringVector);
        }
        table1.setModel(model);
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
        panel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        openBtn = new JButton();
        openBtn.setText("Open");
        panel.add(openBtn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        fetchBtn = new JButton();
        fetchBtn.setText("Fetch");
        panel.add(fetchBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pullBtn = new JButton();
        pullBtn.setText("Pull");
        panel.add(pullBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pushBtn = new JButton();
        pushBtn.setText("Push");
        panel.add(pushBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitBtn = new JButton();
        exitBtn.setText("Exit");
        panel.add(exitBtn, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
