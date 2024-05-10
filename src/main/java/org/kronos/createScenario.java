/*
 * Created by JFormDesigner on Sat Apr 20 07:25:22 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Enterprise
 */
public class createScenario extends JPanel {
    private int ballotCount = 1;
    public static STVResults electionResults;
    private ArrayList<String[]> loadedPermutations;
    private ArrayList<Integer> loadedPermutationsMult;
    private ArrayList<JComboBox[]> cbGroups;

    public createScenario() {
        initComponents();
        loadedPermutationsMult = null;
        loadedPermutations = null;
        spinner1.setEnabled(false);
        spinner1.setValue(1);
        initTable();
    }

    public createScenario(String ballotFile) {
        initComponents();
        spinner1.setEnabled(false);
        spinner1.setValue(1);
        parseBallotFile(ballotFile);
        initTable();
    }

    private void parseBallotFile(String file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file));
            String content = new String(bytes, StandardCharsets.UTF_8);


            // parse candidates
            String contentC = content.replace("\r\n", ", ");
            String[] tokens = contentC.split(", ");
            String[] unique = new HashSet<>(Arrays.asList(tokens)).toArray(new String[0]);
            inputCandidates.candidates = unique;
            inputCandidates.candidateCount = unique.length;

            // parse permutations
            String[] lines = content.split("\r\n");

            String[] p = new HashSet<>(Arrays.asList(lines)).toArray(new String[0]);
            List<String> allPermutations = Arrays.asList(lines);

            loadedPermutations = new ArrayList<>();
            loadedPermutationsMult = new ArrayList<>();

            for (String permutation : p) {
                loadedPermutations.add(permutation.split(", "));
                loadedPermutationsMult.add(Collections.frequency(allPermutations, permutation));
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private JComboBox[] createCBGroup() {
        JComboBox[] cbGroup = new JComboBox[inputCandidates.candidateCount];
        for (int i = 0; i < cbGroup.length; i++) {
            String[] opts = Arrays.copyOf(inputCandidates.candidates, inputCandidates.candidateCount+1);
            opts[inputCandidates.candidateCount] = "Clear option [x]";

            cbGroup[i] = new JComboBox(opts);

            cbGroup[i].setSelectedIndex(-1); // for some reason not all comboboxes had 0 as index and they need to be init with blank (-1)
            final int currentBox = i;
            cbGroup[currentBox].addItemListener(e -> { // CAUTION! use ITEM LISTENER instead of ACTION LISTENER to track combobox changes!
                if (cbGroup[currentBox].getSelectedIndex() == -1)
                    return;

                if (cbGroup[currentBox].getSelectedIndex() == inputCandidates.candidateCount) {
                    cbGroup[currentBox].setSelectedIndex(-1);
                    return;
                }

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
        tCol[0] = "Ballot #";
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
            @Override
            public boolean editCellAt(int row, int column, EventObject e)
            {
                boolean result = super.editCellAt(row, column, e);
                final Component editor = getEditorComponent();

                if (editor instanceof JTextField) {
                    ((JTextField) editor).setText("");
                }

                return result;
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
        table1.setRowSelectionAllowed(true);
        table1.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );

        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        if (loadedPermutations != null) {
            populateTableFromFile();
        }

        updateStatus();

        table1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                updateStatus();
            }
        });
    }

    private void populateTableFromFile() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.removeRow(0);
        ballotCount = 0;
        cbGroups = new ArrayList<>();
        for (int i = 0; i < loadedPermutations.size(); i++) {
            ballotCount++;
            Object[] row = new Object[inputCandidates.candidateCount+2];
            row[0] = ballotCount;
            String[] sel = loadedPermutations.get(i);
            for (int j = 1; j < row.length - 1; j++) {
                try {
                    row[j] = sel[j-1];
                } catch(Exception e) {
                    row[j] = null;
                }
            }
            row[row.length-1] = loadedPermutationsMult.get(i);
            cbGroups.add(createCBGroup());
            for (int x = 0; x < sel.length; x++) {
                cbGroups.get(i)[x].setSelectedItem(sel[x]);
            }
            model.addRow(row);
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
        resultForm x = new resultForm();
        j.setContentPane(x);
        j.pack();
        j.setLocationRelativeTo(null);
        j.setVisible(true);
    }

    private void checkBox1(ActionEvent e) {
        spinner1.setEnabled(checkBox1.isSelected());
    }

    private void spinner1StateChanged(ChangeEvent e) {
        if ((Integer)spinner1.getValue() < 1) {
            spinner1.setValue(1);
        }
    }

    private void updateStatus() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();
        int cols = dtm.getColumnCount();

        if (rows < 1) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">There must be at least 1 ballot.</b>" +"</html>");
            button1.setEnabled(false);
            button3.setEnabled(false);
            return;
        }

        for (int i = 0; i < rows; i++) {
            boolean endOnNull = false;
            for (int j = 1; j < cols - 1; j++) {
                // check first option
                if (j == 1) {
                    if (dtm.getValueAt(i,j) == null) {
                        label1.setText("<html>" + "<b> Alert : </b>" +
                                "<br> <b style=\"color:RED;\">Ballot " + (i+1) + " does not have a first choice.</b>" +"</html>");
                        button1.setEnabled(false);
                        button3.setEnabled(false);
                        return;
                    }
                }

                // check the rest for skips

                // null found AND current element not null, so the ballot skips choices
                if (dtm.getValueAt(i,j) != null && endOnNull) {
                    label1.setText("<html>" + "<b> Alert : </b>" +
                            "<br> <b style=\"color:RED;\">Ballot " + (i+1) + " skips choices.</b>" +"</html>");
                    button1.setEnabled(false);
                    button3.setEnabled(false);
                    return;
                }

                // null found
                if (dtm.getValueAt(i,j) == null)
                    endOnNull = true;
            }
        }

        label1.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");
        button1.setEnabled(true);
        button3.setEnabled(true);
    }

    private void button3(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileAbsolutePath = file.getAbsolutePath() + ".csv";

            generateBallotFile(fileAbsolutePath);
        }
    }

    private void button4(ActionEvent e) {
        int rowToRemove = table1.getSelectedRow();
        if (rowToRemove != -1) {
            DefaultTableModel m = (DefaultTableModel) table1.getModel();
            m.removeRow(rowToRemove);
            for (int i = 0; i < m.getRowCount(); i++) {
                m.setValueAt(i+1, i, 0);
            }
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
        label1 = new JLabel();
        button3 = new JButton();
        button4 = new JButton();

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

        //---- label1 ----
        label1.setText("tooltip");
        label1.setVerticalAlignment(SwingConstants.TOP);

        //---- button3 ----
        button3.setText("Export As CSV");
        button3.addActionListener(e -> button3(e));

        //---- button4 ----
        button4.setText("Remove -");
        button4.addActionListener(e -> button4(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(button2)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button4)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                            .addComponent(checkBox1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button1, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button3, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                        .addComponent(label1, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(button3)
                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(checkBox1)
                        .addComponent(button1)
                        .addComponent(button2)
                        .addComponent(button4))
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
    private JLabel label1;
    private JButton button3;
    private JButton button4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
