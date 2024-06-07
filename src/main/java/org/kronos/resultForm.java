/*
 * Created by JFormDesigner on Mon Apr 29 15:57:32 EEST 2024
 */

package org.kronos;

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
        table1.setRowSelectionAllowed(false);
        table1.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );

        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        populateTable();
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

    private void viewAnalysisBtn(ActionEvent e) {
        analysisForm f = new analysisForm(r.stvInput);
        JDialog j = new JDialog(Main.mainFrame, "Analysis", true);
        j.setContentPane(f);
        j.pack();
        j.setLocationRelativeTo(null);
        j.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        viewAnalysisBtn = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- viewAnalysisBtn ----
        viewAnalysisBtn.setText("View analysis");
        viewAnalysisBtn.addActionListener(e -> viewAnalysisBtn(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 289, Short.MAX_VALUE)
                            .addComponent(viewAnalysisBtn)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(viewAnalysisBtn)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton viewAnalysisBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
