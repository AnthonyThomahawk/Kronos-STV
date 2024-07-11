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
    public resultForm(String scenarioTitle, STVResults results) {
        initComponents();
        r = results;
        s = scenarioTitle;
        initTable();
    }

    private void initTable() {
        table1.setModel(new DefaultTableModel(new Object[][] {
                null
        }, new String[]{"Rank #", "Elected candidate", "Votes"})
        {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

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
    }

    private void populateTable() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.removeRow(0);
        for (int i = 1; i <= r.lastRank; i++) {
            Object[] row = new Object[3];
            row[0] = i;
            row[1] = r.getElected(i);
            row[2] = r.getVotes(i);
            model.addRow(row);
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        copyBtn = new JButton();
        scrollPane2 = new JScrollPane();
        textArea1 = new JTextArea();
        copyAnalysisBtn = new JButton();
        label1 = new JLabel();

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

        //---- label1 ----
        label1.setText("Analysis");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 240, Short.MAX_VALUE)
                            .addComponent(copyAnalysisBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(copyBtn))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addGap(0, 400, Short.MAX_VALUE))
                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(copyBtn)
                        .addComponent(copyAnalysisBtn))
                    .addGap(4, 4, 4))
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
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
