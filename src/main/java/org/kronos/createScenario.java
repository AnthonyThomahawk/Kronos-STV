/*
 * Created by JFormDesigner on Sat Apr 20 07:25:22 EEST 2024
 */

package org.kronos;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.*;
import javax.management.openmbean.OpenDataException;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Enterprise
 */
public class createScenario extends JPanel {
    private int ballotCount = 1;
    public static boolean unsaved;
    private STVResults electionResults;
    private ArrayList<String[]> loadedPermutations;
    private ArrayList<Integer> loadedPermutationsMult;
    private ArrayList<JComboBox[]> cbGroups;
    private String electionTitle;
    private String[] candidates;
    private int candidateCount;

    public createScenario() {
        initComponents();
        loadedPermutationsMult = null;
        loadedPermutations = null;
        spinner1.setEnabled(false);
        spinner1.setValue(1);
        initTable();
    }

    public createScenario(String file, boolean scenario) {
        initComponents();
        spinner1.setEnabled(false);
        spinner1.setValue(1);

        if (!scenario)
            parseElection(file);
        else
            parseScenario(file);

        electionTitleTxt.setText("Election : " + electionTitle);
        initTable();
    }

    private void parseElection(String electionFile) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject election = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(Paths.get(electionFile)), StandardCharsets.UTF_8));
            electionTitle = (String) election.get("Title");

            JSONArray jCandidates = (JSONArray) election.get("Candidates");
            ArrayList<String> cList = new ArrayList<>();
            jCandidates.iterator().forEachRemaining((x) -> cList.add((String)x));
            candidates = cList.toArray(new String[0]);
            candidateCount = candidates.length;
        } catch (Exception ignored) {}
    }

    private void parseScenario(String scenarioFile) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject scenario = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(Paths.get(scenarioFile)), StandardCharsets.UTF_8));
            electionTitle = (String) scenario.get("ElectionTitle");

            JSONArray jCandidates = (JSONArray) scenario.get("Candidates");
            ArrayList<String> cList = new ArrayList<>();
            jCandidates.iterator().forEachRemaining((x) -> cList.add((String)x));
            candidates = cList.toArray(new String[0]);
            candidateCount = candidates.length;

            loadedPermutations = new ArrayList<>();
            loadedPermutationsMult = new ArrayList<>();

            JSONArray choices = (JSONArray) scenario.get("Choices");
            for (Object choice : choices) {
                List list = (List) choice;
                ArrayList<String> c = new ArrayList<>();
                for (Object elem : list) {
                    if (elem instanceof String) {
                        c.add((String) elem);
                    } else {
                        String[] sel = c.toArray(new String[0]);
                        loadedPermutations.add(sel);
                        loadedPermutationsMult.add(Math.toIntExact((long) elem));
                    }
                }
            }

            scenarioTitleTxt.setText((String) scenario.get("ScenarioTitle"));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "This scenario file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            mainForm.stopLoadingForm = true;
        }
    }

    private JComboBox[] createCBGroup() {
        JComboBox[] cbGroup = new JComboBox[candidateCount];
        for (int i = 0; i < cbGroup.length; i++) {
            String[] opts = Arrays.copyOf(candidates, candidateCount+1);
            opts[candidateCount] = "Clear option [x]";

            cbGroup[i] = new JComboBox(opts);

            cbGroup[i].setSelectedIndex(-1); // for some reason not all comboboxes had 0 as index and they need to be init with blank (-1)
            final int currentBox = i;
            cbGroup[currentBox].addItemListener(e -> { // CAUTION! use ITEM LISTENER instead of ACTION LISTENER to track combobox changes!
                if (cbGroup[currentBox].getSelectedIndex() == -1)
                    return;

                if (cbGroup[currentBox].getSelectedIndex() == candidateCount) {
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
                        JOptionPane.showMessageDialog(null, "Candidate cannot be repeated within a ballot.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            });
        }

        return cbGroup;
    }

    private void initTable() {
        remBtn.setEnabled(false);
        Class<?>[] columnTypes = new Class[candidateCount+2];
        columnTypes[0] = Integer.class;
        for (int i = 1; i < columnTypes.length-1; i++) {
            columnTypes[i] = Object.class;
        }
        columnTypes[columnTypes.length-1] = Integer.class;

        String[] tCol = new String[candidateCount+2];
        tCol[0] = "Ballot #";
        for (int i = 1; i < tCol.length - 1; i++) {
            if (i == 1)
                tCol[i] = "1st Choice";
            else if (i == 2)
                tCol[i] = "2nd Choice";
            else if (i == 3)
                tCol[i] = "3rd Choice";
            else
                tCol[i] = i + "th Choice";
        }
        tCol[tCol.length-1] = "# Of Voters";

        Object[] tRow = new Object[candidateCount+2];
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
                if (column != 0 && column != candidateCount+1){
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

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table1.getSelectedRows().length != 0) {
                    remBtn.setEnabled(true);
                }
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
                unsaved = true;
                //loadedFileTxt.setText("Unsaved scenario");
                updateStatus();
            }
        });

        scenarioTitleTxt.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateStatus();
            }
            public void removeUpdate(DocumentEvent e) {
                updateStatus();
            }
            public void insertUpdate(DocumentEvent e) {
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
            Object[] row = new Object[candidateCount+2];
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
        unsaved = false;
    }

    private void addBtn(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        ballotCount++;
        Object[] row = new Object[candidateCount+2];
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
            multipliers[i] = (Integer)dtm.getValueAt(i, candidateCount+1);
        }

        try {
            OutputStream outputStream = Files.newOutputStream(Paths.get(filename));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
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

    private String generateOutput(String inputFile) {
        STVpy stv = new STVpy();
        String stvOutput;
        try {
            if (customSeats.isSelected()) {
                stvOutput = stv.callSTV(inputFile, (Integer)spinner1.getValue());
            } else {
                stvOutput = stv.callSTV(inputFile);
            }
        } catch (Exception x) {
            return null;
        }

        return stvOutput;
    }

    private void viewBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();


        if (!updateStatus()) {
            JOptionPane.showMessageDialog(this, "Cannot view results, because there are active alerts.", "Error", JOptionPane.OK_OPTION);
            return;
        }

        generateBallotFile("b1.csv");

        electionResults = new STVResults(generateOutput("b1.csv"), ballotCount);

        File ballotsFile = new File("b1.csv");
        ballotsFile.delete();

        JDialog j = new JDialog(Main.mainFrame, "Results", true);
        resultForm x = new resultForm(scenarioTitleTxt.getText(), electionResults);
        j.setContentPane(x);
        j.pack();
        j.setLocationRelativeTo(null);
        j.setVisible(true);
    }

    private void customSeats(ActionEvent e) {
        spinner1.setEnabled(customSeats.isSelected());
    }

    private void spinner1StateChanged(ChangeEvent e) {
        if ((Integer)spinner1.getValue() < 1) {
            spinner1.setValue(1);
        }
    }

    private boolean updateStatus() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();
        int cols = dtm.getColumnCount();

        if (scenarioTitleTxt.getText().isEmpty()) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">Scenario must have a title.</b>" +"</html>");
            viewBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            copyBtn.setEnabled(false);
            return false;
        }

        if (scenarioTitleTxt.getText().matches(".*[\"*:|?<>/].*") || scenarioTitleTxt.getText().contains("\\")) {
            label1.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">Scenario name contains illegal characters.</b></html>");
            viewBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            copyBtn.setEnabled(false);
            return false;
        }

        if (rows < 1) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">There must be at least 1 ballot.</b>" +"</html>");
            viewBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            copyBtn.setEnabled(false);
            return false;
        }

        for (int i = 0; i < rows; i++) {
            boolean endOnNull = false;
            for (int j = 1; j < cols - 1; j++) {
                // check first option
                if (j == 1) {
                    if (dtm.getValueAt(i,j) == null) {
                        label1.setText("<html>" + "<b> Alert : </b>" +
                                "<br> <b style=\"color:RED;\">Ballot " + (i+1) + " does not have a first choice.</b>" +"</html>");
                        viewBtn.setEnabled(false);
                        exportBtn.setEnabled(false);
                        copyBtn.setEnabled(false);
                        return false;
                    }
                }

                // check the rest for skips

                // null found AND current element not null, so the ballot skips choices
                if (dtm.getValueAt(i,j) != null && endOnNull) {
                    label1.setText("<html>" + "<b> Alert : </b>" +
                            "<br> <b style=\"color:RED;\">Ballot " + (i+1) + " skips choices.</b>" +"</html>");
                    viewBtn.setEnabled(false);
                    exportBtn.setEnabled(false);
                    copyBtn.setEnabled(false);
                    return false;
                }

                // null found
                if (dtm.getValueAt(i,j) == null)
                    endOnNull = true;
            }
        }

        for (int i = 0; i < rows; i ++) {
            if (dtm.getValueAt(i, cols-1) == null) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Multiplier " + (i+1) + " is empty.</b>" +"</html>");
                viewBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                copyBtn.setEnabled(false);
                return false;
            }
        }

        label1.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");
        viewBtn.setEnabled(true);
        exportBtn.setEnabled(true);
        copyBtn.setEnabled(true);

        return true;
    }

    public String saveChanges() throws OpenDataException {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        if (!updateStatus()) {
            JOptionPane.showMessageDialog(this, "Cannot save changes, because there are active alerts.", "Error", JOptionPane.OK_OPTION);
            throw new OpenDataException();
        }

        if (!Main.checkConfig())
            return null;

        try {
            String workDir = Main.getWorkDir();

            String fileSeperator = FileSystems.getDefault().getSeparator();
            String filePath = workDir + fileSeperator + scenarioTitleTxt.getText() + ".scenario";

            JSONObject scenario = new JSONObject();

            scenario.put("ScenarioTitle", scenarioTitleTxt.getText());
            scenario.put("ElectionTitle", electionTitle);
            JSONArray arr = new JSONArray();
            for (String s : candidates)
                arr.add(s);
            scenario.put("Candidates", arr);

            JSONArray choices = new JSONArray();

            DefaultTableModel dtm = (DefaultTableModel) table1.getModel();

            for (int i = 0; i < dtm.getRowCount(); i++) {
                JSONArray row = new JSONArray();
                for (int j = 1; j < dtm.getColumnCount(); j++) {
                    Object val = dtm.getValueAt(i, j);

                    if (val == null) {
                        val = dtm.getValueAt(i, dtm.getColumnCount()-1);
                        row.add(val);
                        break;
                    }

                    row.add(val);
                }
                choices.add(row);
            }

            scenario.put("Choices", choices);

            OutputStreamWriter file = new OutputStreamWriter(Files.newOutputStream(Paths.get(filePath)), StandardCharsets.UTF_8);
            file.write(scenario.toJSONString());
            file.close();

            unsaved = false;

            JOptionPane.showMessageDialog(null, "Scenario '" + scenarioTitleTxt.getText() + "' has been saved!");
        } catch (Exception e) {

        }

        return null;
    }


    private void exportBtn(ActionEvent e) {
        try {
            saveChanges();
        } catch (Exception ignored) {}
    }

    private void remBtn(ActionEvent e) {
        int numRows = table1.getSelectedRows().length;
        if (numRows != 0) {
            ballotCount -= numRows;
            DefaultTableModel m = (DefaultTableModel) table1.getModel();
            for (int i = 0; i < numRows; i++) {
                cbGroups.remove(table1.getSelectedRow());
                m.removeRow(table1.getSelectedRow());
            }
            for (int i = 0; i < m.getRowCount(); i++) {
                m.setValueAt(i+1, i, 0);
            }
            remBtn.setEnabled(false);
        }
    }

    private void table1MouseClicked(MouseEvent e) {
    }

    private void copyBtn(ActionEvent e) {
        table1.selectAll();
        Action copy = table1.getActionMap().get("copy");
        ActionEvent ae = new ActionEvent(table1, ActionEvent.ACTION_PERFORMED, "");
        copy.actionPerformed(ae);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        viewBtn = new JButton();
        addBtn = new JButton();
        customSeats = new JCheckBox();
        spinner1 = new JSpinner();
        label1 = new JLabel();
        exportBtn = new JButton();
        remBtn = new JButton();
        electionTitleTxt = new JLabel();
        label2 = new JLabel();
        scenarioTitleTxt = new JTextField();
        copyBtn = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- table1 ----
            table1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    table1MouseClicked(e);
                }
            });
            scrollPane1.setViewportView(table1);
        }

        //---- viewBtn ----
        viewBtn.setText("View results");
        viewBtn.addActionListener(e -> viewBtn(e));

        //---- addBtn ----
        addBtn.setText("Add +");
        addBtn.addActionListener(e -> addBtn(e));

        //---- customSeats ----
        customSeats.setText("Custom seats to be filled");
        customSeats.addActionListener(e -> customSeats(e));

        //---- spinner1 ----
        spinner1.addChangeListener(e -> spinner1StateChanged(e));

        //---- label1 ----
        label1.setText("tooltip");
        label1.setVerticalAlignment(SwingConstants.TOP);

        //---- exportBtn ----
        exportBtn.setText("Save scenario");
        exportBtn.addActionListener(e -> exportBtn(e));

        //---- remBtn ----
        remBtn.setText("Remove -");
        remBtn.addActionListener(e -> remBtn(e));

        //---- electionTitleTxt ----
        electionTitleTxt.setText("Election title");
        electionTitleTxt.setVerticalAlignment(SwingConstants.BOTTOM);
        electionTitleTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        electionTitleTxt.setFont(electionTitleTxt.getFont().deriveFont(electionTitleTxt.getFont().getSize() + 5f));

        //---- label2 ----
        label2.setText("Scenario title :");

        //---- copyBtn ----
        copyBtn.setText("Copy to clipboard");
        copyBtn.addActionListener(e -> copyBtn(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn)
                            .addGap(28, 28, 28)
                            .addComponent(customSeats)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(viewBtn, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exportBtn, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(copyBtn))
                        .addComponent(scrollPane1)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(electionTitleTxt, GroupLayout.PREFERRED_SIZE, 323, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label2)
                            .addGap(12, 12, 12)
                            .addComponent(scenarioTitleTxt)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(label1, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                        .addComponent(electionTitleTxt, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label2)
                        .addComponent(scenarioTitleTxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6, 6, 6)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn)
                        .addComponent(exportBtn)
                        .addComponent(viewBtn)
                        .addComponent(customSeats)
                        .addComponent(copyBtn)
                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton viewBtn;
    private JButton addBtn;
    private JCheckBox customSeats;
    private JSpinner spinner1;
    private JLabel label1;
    private JButton exportBtn;
    private JButton remBtn;
    private JLabel electionTitleTxt;
    private JLabel label2;
    private JTextField scenarioTitleTxt;
    private JButton copyBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
