/*
 * Created by JFormDesigner on Mon Apr 29 15:57:32 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * @author Enterprise
 */
public class resultForm extends JPanel {
    STVResults r;
    String s;
    ArrayList<String> groupNames;
    ArrayList<Integer> groupCandidates;
    String[] candidates;
    public resultForm(String scenarioTitle, STVResults results, ArrayList<String> gN, ArrayList<Integer> gC, String[] c) {
        initComponents();
        r = results;
        s = scenarioTitle;
        if (gN == null)
            groupNames = new ArrayList<>();
        else
            groupNames = gN;
        if (gC == null)
            groupCandidates = new ArrayList<>();
        else
            groupCandidates = gC;

        candidates = c;
        initTable();
    }

    private void initTable() {
        if (!groupNames.isEmpty() && !groupCandidates.isEmpty()) {
            table1.setModel(new DefaultTableModel(new Object[][] {
                    null
            }, new String[]{"Rank #", "Elected candidate", "Votes", "Group"})
            {
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
            });
        } else {
            table1.setModel(new DefaultTableModel(new Object[][] {
                    null
            }, new String[]{"Rank #", "Elected candidate", "Votes"})
            {
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
            });
        }

        table1.setShowVerticalLines(true);
        table1.setShowHorizontalLines(true);
        table1.setColumnSelectionAllowed(false);
        table1.getTableHeader().setReorderingAllowed(false);

        table1.getColumnModel().getColumn(0).setMaxWidth(55);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );

        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        populateTable();

        textArea1.setText(r.stvInput);

        textArea1.setVisible(false);
        scrollPane2.setVisible(false);
        copyAnalysisBtn.setVisible(false);
    }

    private void populateTable() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.removeRow(0);

        if (!groupNames.isEmpty() && !groupCandidates.isEmpty()) {
            for (int i = 1; i <= r.lastRank; i++) {
                Object[] row = new Object[4];
                row[0] = i;
                row[1] = r.getElected(i);
                row[2] = r.getVotes(i);
                int ind = Arrays.asList(candidates).indexOf(row[1]);
                int groupCandidateIndex = groupCandidates.get(ind);
                if (groupCandidateIndex == -1) {
                    row[3] = "No group";
                } else {
                    row[3] = groupNames.get(groupCandidateIndex);
                }
                model.addRow(row);
            }
        } else {
            for (int i = 1; i <= r.lastRank; i++) {
                Object[] row = new Object[3];
                row[0] = i;
                row[1] = r.getElected(i);
                row[2] = r.getVotes(i);
                model.addRow(row);
            }
        }
    }

    private void copyBtn(ActionEvent e) {
        table1.selectAll();
        Action copy = table1.getActionMap().get("copy");
        ActionEvent ae = new ActionEvent(table1, ActionEvent.ACTION_PERFORMED, "");
        copy.actionPerformed(ae);
    }

    private void copyAnalysisBtn(ActionEvent e) {
        StringSelection selection = new StringSelection(r.stvInput);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private void button1(ActionEvent e) {
        if (!scrollPane2.isVisible()) {
            scrollPane2.setVisible(true);
            textArea1.setVisible(true);
            copyAnalysisBtn.setVisible(true);
            button1.setText("Hide analysis");
        } else {
            scrollPane2.setVisible(false);
            textArea1.setVisible(false);
            copyAnalysisBtn.setVisible(false);
            button1.setText("Show analysis");
        }

        revalidate();
        repaint();

        JDialog x = (JDialog) this.getRootPane().getParent();
        x.pack();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        copyBtn = new JButton();
        scrollPane2 = new JScrollPane();
        textArea1 = new JTextArea();
        copyAnalysisBtn = new JButton();
        button1 = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- copyBtn ----
        copyBtn.setText("Copy results");
        copyBtn.addActionListener(e -> copyBtn(e));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(textArea1);
        }

        //---- copyAnalysisBtn ----
        copyAnalysisBtn.setText("Copy analysis");
        copyAnalysisBtn.addActionListener(e -> copyAnalysisBtn(e));

        //---- button1 ----
        button1.setText("Show analysis");
        button1.addActionListener(e -> button1(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(button1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                            .addComponent(copyAnalysisBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(copyBtn)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(button1)
                        .addComponent(copyAnalysisBtn)
                        .addComponent(copyBtn))
                    .addGap(11, 11, 11))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton copyBtn;
    private JScrollPane scrollPane2;
    private JTextArea textArea1;
    private JButton copyAnalysisBtn;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
