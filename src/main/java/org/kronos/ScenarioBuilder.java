/*
 * Created by JFormDesigner on Thu Dec 05 21:25:11 EET 2024
 */

package org.kronos;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.EventObject;
import java.util.Stack;
import java.util.Vector;

/**
 * @author Worker
 */
public class ScenarioBuilder extends JPanel {

    public ScenarioBuilder() {
        initComponents();
        init();
    }

    private void init() {
        exRandtable.setModel(new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return String.class;
                else if (columnIndex == 1)
                    return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex != 0;
            }
        });

        DefaultTableModel dtm = (DefaultTableModel) exRandtable.getModel();

        dtm.addColumn("Candidate");
        dtm.addColumn("Check");
        dtm.addRow(new Object[]{"Test 1", Boolean.TRUE});
        dtm.addRow(new Object[]{"Test 2", Boolean.FALSE});
        dtm.addRow(new Object[]{"Test 3", Boolean.FALSE});
        dtm.addRow(new Object[]{"Test 4", Boolean.FALSE});

        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();

        dtm2.addColumn("Multiplier");

        for (int i = 0; i < 4; i++) {
            dtm2.addColumn("Choice " + i);
        }

        String multInfo = "<html>Multiplier values : <br>" +
                "<b>Any number</b> - Number of votes<br>" +
                "<b> ? </b> - Remaining votes";

        label4.setText(multInfo);

        permTable.setShowVerticalLines(true);
        permTable.setShowHorizontalLines(true);
        permTable.setColumnSelectionAllowed(false);
        permTable.setRowSelectionAllowed(true);
        permTable.getTableHeader().setReorderingAllowed(false);

        exRandtable.setShowVerticalLines(true);
        exRandtable.setShowHorizontalLines(true);
        exRandtable.setColumnSelectionAllowed(false);
        exRandtable.setRowSelectionAllowed(true);
        exRandtable.getTableHeader().setReorderingAllowed(false);
    }

    private void addBtn(ActionEvent e) {
        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();
        dtm2.addRow(new Object[] {0, null, null, null, null});
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        spinner1 = new JSpinner();
        label3 = new JLabel();
        scrollPane2 = new JScrollPane();
        permTable = new JTable();
        scrollPane1 = new JScrollPane();
        exRandtable = new JTable();
        addBtn = new JButton();
        remBtn = new JButton();
        label4 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText("Scenario name");

        //---- label2 ----
        label2.setText("Ballot count");

        //---- label3 ----
        label3.setText("Candidates for exclusive random");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(permTable);
        }

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(exRandtable);
        }

        //---- addBtn ----
        addBtn.setText("Add +");
        addBtn.addActionListener(e -> addBtn(e));

        //---- remBtn ----
        remBtn.setText("Remove -");

        //---- label4 ----
        label4.setText("Multiplier possible values :");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup()
                                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup()
                                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label2)))
                                .addComponent(label4))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn)))
                    .addContainerGap(8, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(52, 52, 52)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label1)
                        .addComponent(label2)
                        .addComponent(label3))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(label4))
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JSpinner spinner1;
    private JLabel label3;
    private JScrollPane scrollPane2;
    private JTable permTable;
    private JScrollPane scrollPane1;
    private JTable exRandtable;
    private JButton addBtn;
    private JButton remBtn;
    private JLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
