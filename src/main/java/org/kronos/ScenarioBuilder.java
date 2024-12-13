/*
 * Created by JFormDesigner on Thu Dec 05 21:25:11 EET 2024
 */

package org.kronos;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Worker
 */
public class ScenarioBuilder extends JPanel {
    ArrayList<ArrayList<JComboBox>> comboBoxGroups;
    ArrayList<String> options;
    ArrayList<String> patterns;

    public ScenarioBuilder() {
        initComponents();

        options = new ArrayList<>();
        options.add("Test 1");
        options.add("Test 2");
        options.add("Test 3");
        options.add("Test 4");

        init();
    }

    private ArrayList<String> getExRand() {
        DefaultTableModel dtm = (DefaultTableModel) exRandtable.getModel();
        ArrayList<String> exRandList = new ArrayList<>();

        for (int i = 0; i < dtm.getRowCount(); i++) {
            if ((Boolean) dtm.getValueAt(i, 1)) {
                exRandList.add((String) dtm.getValueAt(i, 0));
            }
        }

        return exRandList;
    }

    // a better rand for the other lib (keep for later)
//    private String getExRand(ArrayList<String> exclude) {
//        DefaultTableModel dtm = (DefaultTableModel) exRandtable.getModel();
//
//        ArrayList<String> exRandList = new ArrayList<>();
//
//        for (int i = 0; i < dtm.getRowCount(); i++) {
//            if ((Boolean) dtm.getValueAt(i, 1)) {
//                exRandList.add((String) dtm.getValueAt(i, 0));
//            }
//        }
//
//        exRandList.removeAll(exclude);
//
//        Collections.shuffle(exRandList);
//
//        return exRandList.get(0);
//    }
//
//    private String getRand(ArrayList<String> exclude) {
//        ArrayList<String> rem = new ArrayList<>(options);
//        rem.removeAll(exclude);
//
//        Collections.shuffle(rem);
//        return rem.get(0);
//    }

    private ArrayList<JComboBox> createComboBoxGroup(ArrayList<String> opts) {
        ArrayList<JComboBox> arr = new ArrayList<>();
        for (int i = 0; i < permTable.getColumnCount() - 1; i++) {
            JComboBox x = new JComboBox();
            for (String s : opts) {
                x.addItem(s);
            }
            x.addItem("RANDOM");
            x.addItem("EX-RANDOM");
            x.addItem("Clear option [x]");
            x.setSelectedIndex(-1);

            AtomicInteger oldSel = new AtomicInteger(-1);

            x.addItemListener(e -> {
                if (x.getSelectedIndex() == -1)
                    return;

                if (x.getSelectedIndex() == x.getItemCount() - 1) {
                    x.setSelectedIndex(-1);
                    oldSel.set(-1);
                    return;
                }

                int myInd = arr.indexOf(x);

                for (int j = 0; j < arr.size(); j++) {
                    if (j == myInd) // ignore self
                        continue;
                    if (x.getSelectedIndex() >= options.size()) // ignore random, ex-random
                        continue;
                    if (arr.get(j).getSelectedIndex() == x.getSelectedIndex()) {
                        DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();

                        // swap elements
                        int newSel = x.getSelectedIndex();
                        if (oldSel.get() == newSel) {
                            arr.get(j).setSelectedIndex(-1);
                        } else {
                            arr.get(j).setSelectedIndex(oldSel.get());
                        }
                        int ind = permTable.getSelectedRow();
                        dtm.setValueAt(arr.get(j).getSelectedItem(), ind, j+1);
                    }
                }

                oldSel.set(x.getSelectedIndex());
            });

            arr.add(x);
        }

        return arr;
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
        for (String s : options) {
            dtm.addRow(new Object[]{s, Boolean.FALSE});
        }

        String multInfo = "<html>Multiplier values : <br>" +
                "<b>Any number</b> - Number of votes<br>" +
                "<b> ? </b> - Remaining votes";

        label4.setText(multInfo);

        permTable = new JTable() {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column != 0) {
                    return new DefaultCellEditor(comboBoxGroups.get(row).get(column-1));
                }
                return super.getCellEditor(row, column);
            }
        };

        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();

        dtm2.addColumn("Multiplier");

        for (int i = 0; i < 4; i++) {
            dtm2.addColumn("Choice " + i);
        }

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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );

        for (int i = 0; i < permTable.getColumnCount(); i++) {
            permTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        scrollPane2.setViewportView(permTable);

        comboBoxGroups = new ArrayList<>();
    }

    private void addBtn(ActionEvent e) {
        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();
        comboBoxGroups.add(createComboBoxGroup(options));
        dtm2.addRow(new Object[] {0, null, null, null, null});
    }

    private void remBtn(ActionEvent e) {
        int[] rows = permTable.getSelectedRows();
        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();
        for (int r : rows) {
            comboBoxGroups.remove(r);
            dtm2.removeRow(r);
        }
    }

    private void buildBtn(ActionEvent e) {
        patterns = new ArrayList<>();

        patterns.add(Integer.toString((Integer) spinner1.getValue()));
        ArrayList<String> exRand = getExRand();
        StringBuilder exRandStr = new StringBuilder("#={" + exRand.get(0));
        for (int i = 1; i < exRand.size(); i++) {
            exRandStr.append(",").append(exRand.get(i));
        }
        exRandStr.append("}");
        patterns.add(exRandStr.toString());

        DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();

        for (int i = 0; i < dtm.getRowCount(); i++) {
            String first;

            if (dtm.getValueAt(i, 1).equals("EX-RANDOM"))
                first = "#";
            else if (dtm.getValueAt(i, 1).equals("RANDOM"))
                first = "$";
            else
                first = (String) dtm.getValueAt(i, 1);

            StringBuilder line = new StringBuilder(dtm.getValueAt(i, 0) + "*" + first);
            for (int j = 2; j < dtm.getColumnCount(); j++) {
                if (dtm.getValueAt(i,j) != null)
                    if (dtm.getValueAt(i,j).equals("EX-RANDOM"))
                        line.append("|").append("#");
                    else if (dtm.getValueAt(i,j).equals("RANDOM"))
                        line.append("|").append("$");
                    else
                        line.append("|").append(dtm.getValueAt(i,j));

            }
            patterns.add(line.toString());
        }

        ScenarioGenerator sg = new ScenarioGenerator(options, patterns, (Integer)spinner2.getValue());
        sg.ballotsToCSV("b.csv");
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
        buildBtn = new JButton();
        label5 = new JLabel();
        spinner2 = new JSpinner();

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
        remBtn.addActionListener(e -> remBtn(e));

        //---- label4 ----
        label4.setText("Multiplier possible values :");

        //---- buildBtn ----
        buildBtn.setText("Build");
        buildBtn.addActionListener(e -> buildBtn(e));

        //---- label5 ----
        label5.setText("Seats");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buildBtn))
                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup()
                                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup()
                                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label2)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label4)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createParallelGroup()
                                        .addComponent(label5)
                                        .addComponent(spinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))))
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
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label4)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label5)
                                    .addGap(6, 6, 6)
                                    .addComponent(spinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn)
                        .addComponent(buildBtn))
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
    private JButton buildBtn;
    private JLabel label5;
    private JSpinner spinner2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
