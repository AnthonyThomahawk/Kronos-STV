/*
 * Created by JFormDesigner on Sat Apr 20 07:25:22 EEST 2024
 */

package org.kronos;

import jdk.nashorn.internal.scripts.JD;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 * @author Enterprise
 */
public class createScenario extends JPanel {
    private int ballotCount = 1;
    public static STVResults electionResults;
    ArrayList<JComboBox[]> cbGroups;

    public createScenario() {
        initComponents();
        spinner1.setEnabled(false);
        spinner1.setValue(1);
        initTable();
    }

    private JComboBox[] createCBGroup() {
        JComboBox[] cbGroup = new JComboBox[inputCandidates.candidateCount];
        for (int i = 0; i < cbGroup.length; i++) {
            cbGroup[i] = new JComboBox(inputCandidates.candidates);

            cbGroup[i].setSelectedIndex(-1); // for some reason not all comboboxes had 0 as index and they need to be init with blank (-1)
            final int currentBox = i;
            cbGroup[currentBox].addItemListener(e -> { // CAUTION! use ITEM LISTENER instead of ACTION LISTENER to track combobox changes!
                if (cbGroup[currentBox].getSelectedIndex() == -1)
                    return;

                for (int j = 0; j < cbGroup.length; j++) {
                    if (j == currentBox)
                        continue;

                    if (cbGroup[j].getSelectedIndex() == -1)
                        continue;

                    if (cbGroup[currentBox].getSelectedIndex() == cbGroup[j].getSelectedIndex()) {
                        cbGroup[currentBox].setSelectedIndex(-1); // clear selection (! only works with item listener !)
                        JOptionPane.showMessageDialog(null, "Candidate cannot be repeated within a permutation.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            });
        }

        return cbGroup;
    }

    private void initTable() {
        Class<?>[] columnTypes = new Class[inputCandidates.candidateCount+2];
        columnTypes[0] = Integer.class;
        for (int i = 1; i < columnTypes.length-1; i++) {
            columnTypes[i] = Object.class;
        }
        columnTypes[columnTypes.length-1] = Integer.class;

        String[] tCol = new String[inputCandidates.candidateCount+2];
        tCol[0] = "Permutation #";
        for (int i = 1; i < tCol.length - 1; i++) {
            tCol[i] = "Option " + i;
        }
        tCol[tCol.length-1] = "Multiplier";

        Object[] tRow = new Object[inputCandidates.candidateCount+2];
        tRow[0] = ballotCount;
        for (int i = 1; i < tRow.length - 1; i++) {
            tRow[i] = null;
        }
        tRow[tRow.length-1] = 1;

        cbGroups = new ArrayList<>();
        cbGroups.add(createCBGroup());

        table1 = new JTable() {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column != 0 && column != inputCandidates.candidateCount+1){
                    return new DefaultCellEditor(cbGroups.get(row)[column-1]);
                }
                return super.getCellEditor(row, column);
            }
        };

        scrollPane1.setViewportView(table1);

        table1.setModel(new DefaultTableModel(new Object[][] {
                tRow
        }, tCol)
        {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex != 0;
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
    }

    private void button2(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        ballotCount++;
        Object[] row = new Object[inputCandidates.candidateCount+2];
        row[0] = ballotCount;
        for (int i = 1; i < row.length - 1; i++) {
            row[i] = null;
        }
        row[row.length-1] = 1;
        cbGroups.add(createCBGroup());
        model.addRow(row);
    }

    private void generateBallotFile(String filename) {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int multipliers[] = new int[dtm.getRowCount()];
        for (int i = 0; i < dtm.getRowCount(); i++) {
            multipliers[i] = (Integer)dtm.getValueAt(i, inputCandidates.candidateCount+1);
        }

        try {
            OutputStream outputStream = new FileOutputStream(filename);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            for (int i = 0; i < cbGroups.size(); i++) {
                JComboBox[] cbg = cbGroups.get(i);
                StringBuilder permutation = new StringBuilder();
                ArrayList<String> selections = new ArrayList<>();

                for (int j = 0; j < cbg.length; j++) {
                    if (cbg[j].getSelectedItem() != null) {
                        selections.add((String)cbg[j].getSelectedItem());
                    }
                }

                for (int z = 0; z < selections.size(); z++) {
                    permutation.append(selections.get(z));
                    if (z != selections.size() - 1) {
                        permutation.append(", ");
                    }
                }

                for (int k = 0; k < multipliers[i]; k++) {
                    out.println(permutation);
                }
            }

            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void centerChildDialog(JDialog childDlg) {
        int c1 = this.getWidth()/2;
        int c2 = this.getHeight()/2;
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width/2)-c1;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height/2)-c2;
        childDlg.setLocation(x,y);
    }

    private void button1(ActionEvent e) {
        generateBallotFile("b1.csv");
        STVpy stv = new STVpy();
        String stvOutput;

        try {
            if (checkBox1.isSelected()) {
                stvOutput = stv.callSTV("b1.csv", (Integer)spinner1.getValue());
            } else {
                stvOutput = stv.callSTV("b1.csv");
            }
        } catch (Exception x) {
            return;
        }

        File ballotsFile = new File("b1.csv");
        ballotsFile.delete();
        electionResults = new STVResults(stvOutput, ballotCount);

        JDialog j = new JDialog(Main.mainFrame, "", true);
        centerChildDialog(j);
        resultForm x = new resultForm();
        j.setContentPane(x);
        j.pack();
        j.setVisible(true);

//        for (int rank = 1; rank <= electionResults.lastRank; rank++) {
//            System.out.println("Rank : " + rank + " Name : " + electionResults.getElected(rank) + " Votes : " + electionResults.getVotes(rank));
//        }
    }

    private void checkBox1(ActionEvent e) {
        spinner1.setEnabled(checkBox1.isSelected());
    }

    private void spinner1StateChanged(ChangeEvent e) {
        if ((Integer)spinner1.getValue() < 1) {
            spinner1.setValue(1);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        button1 = new JButton();
        button2 = new JButton();
        checkBox1 = new JCheckBox();
        spinner1 = new JSpinner();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- button1 ----
        button1.setText("Get results");
        button1.addActionListener(e -> button1(e));

        //---- button2 ----
        button2.setText("Add +");
        button2.addActionListener(e -> button2(e));

        //---- checkBox1 ----
        checkBox1.setText("Custom seats to be filled");
        checkBox1.addActionListener(e -> checkBox1(e));

        //---- spinner1 ----
        spinner1.addChangeListener(e -> spinner1StateChanged(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(button2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 205, Short.MAX_VALUE)
                            .addComponent(checkBox1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button1, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(button1)
                        .addComponent(button2)
                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(checkBox1))
                    .addGap(8, 8, 8))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton button1;
    private JButton button2;
    private JCheckBox checkBox1;
    private JSpinner spinner1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
