/*
 * Created by JFormDesigner on Sat Apr 20 07:25:22 EEST 2024
 */

package org.kronos;

import jdk.nashorn.internal.scripts.JD;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.omg.CORBA.Environment;

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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Enterprise
 */
public class createScenario extends JPanel {
    public boolean failed = false;
    private int ballotCount = 1;
    public static boolean unsaved;
    private STVResults electionResults;
    private ArrayList<String[]> loadedPermutations;
    private ArrayList<Integer> loadedPermutationsMult;
    private ArrayList<JComboBox[]> cbGroups;
    private String electionTitle;
    private String[] candidates;
    private ArrayList<String> groupNames;
    private ArrayList<Integer> groupCandidates;
    private int candidateCount;
    private String notes = "";
    private static JFileChooser exportChooser;
    private boolean departmental = false;
    private String instituteName = "";
    private int instituteQuota = -1;
    private String[] departmentNames;
    private int[] departmentStrengths;
    private int[] candidateDepartments;
    public static JDialog fe;

    public createScenario(String file, boolean scenario) {
        initComponents();
        spinner1.setEnabled(false);
        spinner1.setValue(1);

        if (!scenario)
            parseElection(file);
        else
            parseScenario(file);

        initTable();
        initDeselectAreas();
        viewBtn.setText("<html> <b> Evaluate </b> </html>");
    }

    private void initDeselectAreas() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (table1.isEditing()) {
                    table1.getCellEditor().stopCellEditing();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        scrollPane1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (table1.isEditing()) {
                    table1.getCellEditor().stopCellEditing();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        ((JSpinner.DefaultEditor)spinner1.getEditor()).getTextField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table1.isEditing())
                    table1.getCellEditor().stopCellEditing();
            }
        });

        spinner1.addChangeListener(e -> {
            if (table1.isEditing())
                table1.getCellEditor().stopCellEditing();
        });
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

            if (election.containsKey("InstituteName")) {
                departmental = true;
                instituteName = (String) election.get("InstituteName");
                Long l = (long) election.get("InstituteQuota");
                instituteQuota = l.intValue();
                JSONArray depts = (JSONArray) election.get("Departments");

                departmentNames = new String[depts.size()];
                departmentStrengths = new int[depts.size()];

                for (int i = 0; i < depts.size(); i++) {
                    JSONArray dept = (JSONArray) depts.get(i);
                    departmentNames[i] = (String) dept.get(0);
                    Long dS = (long) dept.get(1);
                    departmentStrengths[i] = dS.intValue();
                }

                JSONArray cDepts = (JSONArray) election.get("CandidateDepartments");
                candidateDepartments = new int[cDepts.size()];

                for (int i = 0; i < cDepts.size(); i++) {
                    Long x = (long) cDepts.get(i);
                    candidateDepartments[i] = x.intValue();
                }

            } else {
                departmental = false;
            }

            if (departmental) {
                label3.setText("<html> <b>Institution : </b>" + instituteName + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html> <b>Election :  </b>" + electionTitle + "</html>");
            } else {
                label3.setText("<html> (Independent) <b>Election : </b>" + electionTitle + "</html>");
            }

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

            int c = Math.toIntExact((Long) scenario.get("Seats"));
            boolean b = (boolean) scenario.get("EnforceSeats");
            spinner1.setValue(c);
            customSeats.setSelected(b);
            spinner1.setEnabled(b);

            notes = (String) scenario.get("Notes");

            scenarioTitleTxt.setText((String) scenario.get("ScenarioTitle"));

            if (scenario.containsKey("InstituteName")) {
                departmental = true;
                instituteName = (String) scenario.get("InstituteName");
                Long l = (long) scenario.get("InstituteQuota");
                instituteQuota = l.intValue();
                JSONArray depts = (JSONArray) scenario.get("Departments");

                departmentNames = new String[depts.size()];
                departmentStrengths = new int[depts.size()];

                for (int i = 0; i < depts.size(); i++) {
                    JSONArray dept = (JSONArray) depts.get(i);
                    departmentNames[i] = (String) dept.get(0);
                    Long dS = (long) dept.get(1);
                    departmentStrengths[i] = dS.intValue();
                }

                JSONArray cDepts = (JSONArray) scenario.get("CandidateDepartments");
                candidateDepartments = new int[cDepts.size()];

                for (int i = 0; i < cDepts.size(); i++) {
                    Long x = (long) cDepts.get(i);
                    candidateDepartments[i] = x.intValue();
                }

            } else {
                departmental = false;
            }

            if (departmental) {
                label3.setText("<html> <b>Institution : </b>" + instituteName + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html> <b>Election :  </b>" + electionTitle + "</html>");
            } else {
                label3.setText("<html> (Independent) <b>Election : </b>" + electionTitle + "</html>");
            }

            if (scenario.containsKey("GroupNames") && scenario.containsKey("GroupCandidates")) {
                groupNames = new ArrayList<>();
                groupCandidates = new ArrayList<>();

                JSONArray gN = (JSONArray) scenario.get("GroupNames");
                groupNames.addAll(gN);

                JSONArray gC = (JSONArray) scenario.get("GroupCandidates");
                for (Object o : gC) {
                    Long longIndex = (Long) o;
                    groupCandidates.add(longIndex.intValue());
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "This scenario file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            failed = true;
        }
    }

    private JComboBox[] createCBGroup() {
        JComboBox[] cbGroup = new JComboBox[candidateCount];
        for (int i = 0; i < cbGroup.length; i++) {
            String[] opts = Arrays.copyOf(candidates, candidateCount+1);
            opts[candidateCount] = "Clear option [x]";

            cbGroup[i] = new JComboBox(opts);

            // remove combobox button
            Component[] components = cbGroup[i].getComponents();
            for (Component component : components) {
                if (component instanceof AbstractButton) {
                    cbGroup[i].remove(component);
                }
            }

            cbGroup[i].setSelectedIndex(-1); // for some reason not all comboboxes had 0 as index and they need to be init with blank (-1)
            final int currentBox = i;
            AtomicInteger oldSel = new AtomicInteger(-1);
            AtomicBoolean isCursorInside = new AtomicBoolean(false);

            cbGroup[currentBox].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isCursorInside.get()) {
                        if (table1.getCellEditor() != null) {
                            table1.getCellEditor().stopCellEditing();
                            isCursorInside.set(false);
                        }
                    }

                    remBtn.setEnabled(true);
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    isCursorInside.set(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isCursorInside.set(false);
                }
            });

            cbGroup[currentBox].addItemListener(e -> { // CAUTION! use ITEM LISTENER instead of ACTION LISTENER to track combobox changes!

                if (cbGroup[currentBox].getSelectedIndex() == -1)
                    return;

                if (cbGroup[currentBox].getSelectedIndex() == candidateCount) {
                    cbGroup[currentBox].setSelectedIndex(-1);
                    oldSel.set(-1);
                    return;
                }

                for (int j = 0; j < cbGroup.length; j++) {
                    if (j == currentBox)
                        continue;

                    if (cbGroup[j].getSelectedIndex() == -1)
                        continue;

                    if (cbGroup[currentBox].getSelectedIndex() == cbGroup[j].getSelectedIndex()) {
                        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();

                        // swap elements
                        int newSel = cbGroup[currentBox].getSelectedIndex();
                        if (oldSel.get() == newSel) {
                            cbGroup[j].setSelectedIndex(-1);
                        } else {
                            cbGroup[j].setSelectedIndex(oldSel.get());
                        }
                        int ind = table1.getSelectedRow();
                        dtm.setValueAt(cbGroup[j].getSelectedItem(), ind, j+1);
                    }

                }

                oldSel.set(cbGroup[currentBox].getSelectedIndex());
            });
        }

        return cbGroup;
    }

    private void initTable() {
        if (groupNames == null)
            groupNames = new ArrayList<>();
        if (groupCandidates == null)
            groupCandidates = new ArrayList<>();

        remBtn.setEnabled(false);
        Class<?>[] columnTypes = new Class[candidateCount+2];
        columnTypes[0] = Integer.class;
        for (int i = 1; i < columnTypes.length-1; i++) {
            columnTypes[i] = Object.class;
        }
        columnTypes[columnTypes.length-1] = Integer.class;

        String[] tCol = new String[candidateCount+2];
        tCol[0] = "#";
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
        tCol[tCol.length-1] = "Counts";

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

        table1.getColumnModel().getColumn(0).setMaxWidth(50);
        table1.getColumnModel().getColumn(tCol.length-1).setMaxWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );

        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        if (loadedPermutations != null) {
            populateTableFromFile();
        }

        updateStatus();

        table1.getModel().addTableModelListener(e -> {
            unsaved = true;
            updateStatus();
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

        unsaved = false;
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
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

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

    private int getBallotCount() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int ballots = 0;
        for (int i = 0; i < dtm.getRowCount(); i++) {
            ballots += (Integer)dtm.getValueAt(i, candidateCount+1);
        }

        return ballots;
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

    private void generateConstituencyFile(String filename) {
        try {
            OutputStream outputStream = Files.newOutputStream(Paths.get(filename));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            for (int i = 0; i < departmentNames.length; i++) {
                String line = departmentNames[i] + ", " + departmentStrengths[i];

                for (int j = 0; j < candidates.length; j++) {
                    if (i == candidateDepartments[j]) {
                        line += ", ";
                        line += candidates[j];
                    }
                }

                out.println(line);
            }

            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void deleteFile(String filename) {
        File f = new File(filename);
        f.delete();
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

    private String generateOutput(String ballotsFile, String constituenciesFile) {
        STVpy stv = new STVpy();
        String stvOutput;
        try {
            if (customSeats.isSelected()) {
                stvOutput = stv.callSTV(ballotsFile, (Integer)spinner1.getValue(), constituenciesFile, instituteQuota);
            } else {
                stvOutput = stv.callSTV(ballotsFile, constituenciesFile, instituteQuota);
            }
        } catch (Exception x) {
            return null;
        }

        return stvOutput;
    }

    public static String generateUniqueFileName(String directory, String baseName, String extension) {
        String newName = baseName + extension;
        File file = new File(directory, newName);
        int counter = 1;

        while (file.exists()) {
            newName = baseName + "_" + counter + extension;
            file = new File(directory, newName);
            counter++;
        }

        return newName;
    }

    private void viewBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();


        if (!updateStatus()) {
            JOptionPane.showMessageDialog(this, "Cannot view results, because there are active alerts.", "Error", JOptionPane.OK_OPTION);
            return;
        }

        generateBallotFile("b1.csv");

        if (departmental)
            generateConstituencyFile("c1.csv");

        boolean failed = false;

        Main.checkSTV();

        String output = "";

        try {
            if (departmental)
                output = generateOutput("b1.csv", "c1.csv");
            else
                output = generateOutput("b1.csv");

            electionResults = new STVResults(output, ballotCount);
        } catch (Exception x) {
            System.out.println(x);
            failed = true;
        }

       deleteFile("b1.csv");

       if (departmental)
           deleteFile("c1.csv");

       if (failed) {
           parseErrorForm f = new parseErrorForm(output);
           fe = new JDialog(Main.mainFrame, "Error", true);
           fe.setContentPane(f);
           fe.pack();
           fe.setLocationRelativeTo(null);
           fe.setVisible(true);
           return;
       }

       JDialog j = new JDialog(Main.mainFrame, "Results", true);

        try {
            String evalExport = getFullCSV(",");
            evalExport += System.lineSeparator() + System.lineSeparator();

            for (String s : electionResults.stvInput.split(System.lineSeparator())) {
                evalExport += "\"" + s + "\"" + System.lineSeparator();
            }

            // not needed for now, may use it later
//            String eEName = generateUniqueFileName(Main.getWorkDir(), scenarioTitleTxt.getText() + "_eval", ".csv");
//            File f = new File(Main.getWorkDir(), eEName);
//            OutputStreamWriter fStream = new OutputStreamWriter(Files.newOutputStream(f.toPath()), StandardCharsets.UTF_8);
//            fStream.write(evalExport);
//            fStream.flush();
//            fStream.close();

        } catch (Exception x) {
            System.out.println(x);
    };

    resultForm x = new resultForm(scenarioTitleTxt.getText(), electionResults, groupNames, groupCandidates, candidates);
    j.setContentPane(x);
    j.pack();
    j.setLocationRelativeTo(null);
    j.setVisible(true);
    }

    private void customSeats(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        spinner1.setEnabled(customSeats.isSelected());
        unsaved = true;
    }

    private void spinner1StateChanged(ChangeEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        if ((Integer)spinner1.getValue() < 1) {
            spinner1.setValue(1);
        }
        unsaved = true;
    }

    private boolean updateStatus() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();
        int cols = dtm.getColumnCount();

        voteCountTxt.setText("Total : " + getBallotCount());

        remBtn.setEnabled(table1.getSelectedRows().length != 0);

        if (getBallotCount() == 0) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">Total ballot count cannot be 0.</b>" +"</html>");
            viewBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            copyBtn.setEnabled(false);
            exportFileBtn.setEnabled(false);
            return false;
        }

        if (scenarioTitleTxt.getText().isEmpty()) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">Scenario must have a title.</b>" +"</html>");
            viewBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            copyBtn.setEnabled(false);
            exportFileBtn.setEnabled(false);
            return false;
        }

        if (nameChecks.isFileNameValid(scenarioTitleTxt.getText())) {
            label1.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">Scenario name contains illegal characters.</b></html>");
            viewBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            copyBtn.setEnabled(false);
            exportFileBtn.setEnabled(false);
            return false;
        }

        if (rows < 1) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">There must be at least 1 ballot.</b>" +"</html>");
            viewBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            copyBtn.setEnabled(false);
            exportFileBtn.setEnabled(false);
            return false;
        }

        for (int i = 0; i < rows; i++) {
            boolean endOnNull = false;
            for (int j = 1; j < cols - 1; j++) {
                // check first option
                if (j == 1) {
                    if (dtm.getValueAt(i,j) == null) {
                        label1.setText("<html>" + "<b> Alert : </b>" +
                                "<br> <b style=\"color:RED;\">#" + (i+1) + ": does not have a first choice.</b>" +"</html>");
                        viewBtn.setEnabled(false);
                        exportBtn.setEnabled(false);
                        copyBtn.setEnabled(false);
                        exportFileBtn.setEnabled(false);
                        return false;
                    }
                }

                // check the rest for skips

                // null found AND current element not null, so the ballot skips choices
                if (dtm.getValueAt(i,j) != null && endOnNull) {
                    label1.setText("<html>" + "<b> Alert : </b>" +
                            "<br> <b style=\"color:RED;\">#" + (i+1) + ": skips choices.</b>" +"</html>");
                    viewBtn.setEnabled(false);
                    exportBtn.setEnabled(false);
                    copyBtn.setEnabled(false);
                    exportFileBtn.setEnabled(false);
                    return false;
                }

                // null found
                if (dtm.getValueAt(i,j) == null)
                    endOnNull = true;
            }
        }

        for (int i = 0; i < rows; i++) {
            if (dtm.getValueAt(i, cols-1) == null) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">#" + (i+1) + ": Count is empty.</b>" +"</html>");
                viewBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                copyBtn.setEnabled(false);
                exportFileBtn.setEnabled(false);
                return false;
            } else if ((int) dtm.getValueAt(i, cols-1) < 0) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">#" + (i+1) + ": Count must not be negative.</b>" +"</html>");
                viewBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                copyBtn.setEnabled(false);
                exportFileBtn.setEnabled(false);
                return false;
            }
        }

        label1.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");
        viewBtn.setEnabled(true);
        exportBtn.setEnabled(true);
        copyBtn.setEnabled(true);
        exportFileBtn.setEnabled(true);

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
            scenario.put("Seats", spinner1.getValue());
            scenario.put("EnforceSeats", customSeats.isSelected());
            scenario.put("Notes", notes);

            if (departmental) {
                scenario.put("InstituteName", instituteName);
                scenario.put("InstituteQuota", instituteQuota);

                JSONArray depts = new JSONArray();

                for (int i = 0; i < departmentNames.length; i++) {
                    JSONArray dept = new JSONArray();
                    dept.add(departmentNames[i]);
                    dept.add(departmentStrengths[i]);
                    depts.add(dept);
                }

                scenario.put("Departments", depts);

                JSONArray cDepts = new JSONArray();

                for (int candidateDepartment : candidateDepartments) {
                    cDepts.add(candidateDepartment);
                }

                scenario.put("CandidateDepartments", cDepts);
            }

            if (groupNames != null && groupCandidates != null) {
                if (!groupNames.isEmpty() && !groupCandidates.isEmpty()) {
                    JSONArray groups = new JSONArray();
                    groups.addAll(groupNames);
                    JSONArray groupIndexes = new JSONArray();
                    groupIndexes.addAll(groupCandidates);

                    scenario.put("GroupNames", groups);
                    scenario.put("GroupCandidates", groupIndexes);
                }
            }



            OutputStreamWriter file = new OutputStreamWriter(Files.newOutputStream(Paths.get(filePath)), StandardCharsets.UTF_8);
            file.write(scenario.toJSONString());
            file.close();

            unsaved = false;

            return filePath;
        } catch (Exception x) {
            System.out.println(x);
            JOptionPane.showMessageDialog(null, "Error saving scenario", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }


    private void exportBtn(ActionEvent e) {
        try {
            String x = saveChanges();
            JOptionPane.showMessageDialog(null, "Scenario '" + x + "' has been saved!");
        } catch (Exception ignored) {}
    }

    private void remBtn(ActionEvent e) {
        int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove ?", "Confirmation needed", JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.NO_OPTION)
            return;

        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

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

    private String getCSVString(String delim) {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        String data = "";
        for (int i = 1; i < dtm.getColumnCount(); i++) {
            if (i == 1) {
                data += "1st Choice" + delim;
            } else if (i == 2) {
                data += "2nd Choice" + delim;
            } else if (i == 3) {
                data += "3rd Choice" + delim;
            } else if (i != dtm.getColumnCount()-1) {
                data += i + "th Choice" + delim;
            } else {
                data += "Counts";
            }
        }
        data += System.lineSeparator();;

        for (int i = 0; i < dtm.getRowCount(); i++) {
            for (int j = 1; j < dtm.getColumnCount(); j++) {
                String val;
                boolean dFlag;
                if (j == dtm.getColumnCount()-1) {
                    val = Integer.toString((int)dtm.getValueAt(i,j));
                    dFlag = false;
                } else {
                    val = (String) dtm.getValueAt(i,j);
                    dFlag = true;
                }

                if (val == null) {
                    val = "";
                }

                data += val;

                if (dFlag)
                    data += delim;
            }

            data += System.lineSeparator();
        }

        return data;
    }

    private void copyBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        if (!updateStatus()) {
            JOptionPane.showMessageDialog(null, "Cannot copy to clipboard, because there are active alerts.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String data = getCSVString("\t");

        data = "Election : " + electionTitle + System.lineSeparator() + data;
        data = "Scenario : " + scenarioTitleTxt.getText() + System.lineSeparator() + data;

        StringSelection selection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private void viewNotesBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        String oldNotes = notes;
        scenarioNotes sc = new scenarioNotes(notes);
        JDialog d = new JDialog(Main.mainFrame, "Notes - " + scenarioTitleTxt.getText(), true);
        d.setContentPane(sc);
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
        notes = sc.getNotes();
        if (!(notes.equals(oldNotes))) {
            unsaved = true;
        }
    }

    private String getFullCSV(String delim) {
        String data = "";
        for (int i = 1; i<= candidateCount; i++) {
            data += "Candidate " + i;
            if (i != candidateCount)
                data += delim;
        }

        data += System.lineSeparator();

        for (int i = 0; i < candidates.length; i++) {
            data += candidates[i];
            if (i != candidates.length-1) {
                data += delim;
            }
        }

        data += System.lineSeparator() + System.lineSeparator();

        data += getCSVString(delim);

        data += System.lineSeparator() + System.lineSeparator();

        data += "\"Election : " + electionTitle + "\"" + System.lineSeparator();
        data += "\"Scenario : " + scenarioTitleTxt.getText() + "\"" + System.lineSeparator();
        data += "\"Seats : " + spinner1.getValue() + " " + customSeats.isSelected() + "\"" + System.lineSeparator();

        String[] lines = notes.split("\n");
        for (String line : lines) {
            data += "\"" + line + "\"" + System.lineSeparator();
        }

        return data;
    }

    private void exportFileBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        String scenarioFile;
        try {
            scenarioFile = saveChanges();
        } catch (Exception z) {
            System.out.println(z);
            return;
        }


        if (exportChooser == null) {
            try {
                FileInputStream in = new FileInputStream("settings.xml");
                Properties saveProps = new Properties();
                saveProps.loadFromXML(in);
                String path = saveProps.getProperty("exportDir");
                exportChooser = new JFileChooser(path);
            } catch (Exception x) {
                exportChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            }

            FileFilter filter = new FileNameExtensionFilter("Kronos export File","KRONOS");
            exportChooser.setFileFilter(filter);
        }

        exportChooser.setSelectedFile(new File(scenarioTitleTxt.getText() + ".KRONOS"));
        int res = exportChooser.showSaveDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            File selectedFile = exportChooser.getSelectedFile();

            if(!exportChooser.getSelectedFile().getAbsolutePath().endsWith(".KRONOS")){
                selectedFile = new File(exportChooser.getSelectedFile() + ".KRONOS");
            }

            try {
                ArrayList<String> files = new ArrayList<>();
                String workDir = Main.getWorkDir();
                String fileSeperator = FileSystems.getDefault().getSeparator();
                files.add(scenarioFile);

                String electionFile = workDir + fileSeperator + electionTitle + ".election";

                if (new File(electionFile).exists() && new File(electionFile).isFile()) {
                    files.add(electionFile);
                } else {
                    JOptionPane.showMessageDialog(null, "The election file : " + electionFile + " for this scenario was not found. The file will be omitted.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (!instituteName.isEmpty()) {
                    String institutionFile = workDir + fileSeperator + instituteName + ".institution";

                    if (new File(institutionFile).exists() && new File(institutionFile).isFile()) {
                        files.add(institutionFile);
                    } else {
                        JOptionPane.showMessageDialog(null, "The institute file : " + electionFile + " for this scenario was not found. The file will be omitted.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }

                Zip.compressFiles(files, selectedFile.getPath());

                String msg = "<html>Export completed<br>Files included in export <b>" + selectedFile.getName() + "</b> are :<br>";

                for (String f : files) {
                    msg += "<b>" + new File(f).getName() + "</b><br>";
                }

                msg += "</html>";

                JOptionPane.showMessageDialog(null,  msg, "Success", JOptionPane.INFORMATION_MESSAGE);

                FileInputStream in = new FileInputStream("settings.xml");
                Properties saveProps = new Properties();
                saveProps.loadFromXML(in);
                saveProps.setProperty("exportDir", exportChooser.getCurrentDirectory().toString());
                saveProps.storeToXML(Files.newOutputStream(Paths.get("settings.xml")), "");

            } catch (Exception ex) {
                System.out.println(ex);
                JOptionPane.showMessageDialog(null, "Error exporting scenario to file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void scenarioTitleTxtMouseClicked(MouseEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();
    }

    private void groupsBtn(ActionEvent e) {
        JDialog d = new JDialog(Main.mainFrame, "Manage candidate groups", true);
        createGroups g = new createGroups(groupNames, groupCandidates, candidates);

        d.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (!g.status) {
                    JOptionPane.showMessageDialog(null, "Groups are invalid. Either remove ALL groups, or use appropriate names.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    d.dispose();
                }
            }
        });

        d.setContentPane(g);
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);

        if (!groupNames.equals(g.groupNames) || !groupCandidates.equals(g.groupCandidates)) {
            groupNames = new ArrayList<>();
            groupNames.addAll(g.groupNames);

            groupCandidates = new ArrayList<>();
            groupCandidates.addAll(g.groupCandidates);

            unsaved = true;
        }
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
        voteCountTxt = new JLabel();
        label2 = new JLabel();
        scenarioTitleTxt = new JTextField();
        copyBtn = new JButton();
        viewNotesBtn = new JButton();
        exportFileBtn = new JButton();
        label3 = new JLabel();
        groupsBtn = new JButton();

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
        viewBtn.setText("Evaluate");
        viewBtn.addActionListener(e -> viewBtn(e));

        //---- addBtn ----
        addBtn.setText("Add +");
        addBtn.addActionListener(e -> addBtn(e));

        //---- customSeats ----
        customSeats.setText("Seats");
        customSeats.addActionListener(e -> {
			customSeats(e);
			customSeats(e);
		});

        //---- spinner1 ----
        spinner1.addChangeListener(e -> spinner1StateChanged(e));

        //---- label1 ----
        label1.setText("tooltip");
        label1.setVerticalAlignment(SwingConstants.TOP);

        //---- exportBtn ----
        exportBtn.setText("Save");
        exportBtn.addActionListener(e -> exportBtn(e));

        //---- remBtn ----
        remBtn.setText("Remove -");
        remBtn.addActionListener(e -> remBtn(e));

        //---- voteCountTxt ----
        voteCountTxt.setText("VoteCount");
        voteCountTxt.setVerticalAlignment(SwingConstants.BOTTOM);
        voteCountTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        voteCountTxt.setFont(voteCountTxt.getFont().deriveFont(voteCountTxt.getFont().getSize() + 3f));

        //---- label2 ----
        label2.setText("Scenario title :");

        //---- scenarioTitleTxt ----
        scenarioTitleTxt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                scenarioTitleTxtMouseClicked(e);
            }
        });

        //---- copyBtn ----
        copyBtn.setText("Copy");
        copyBtn.addActionListener(e -> copyBtn(e));

        //---- viewNotesBtn ----
        viewNotesBtn.setText("Notes");
        viewNotesBtn.addActionListener(e -> viewNotesBtn(e));

        //---- exportFileBtn ----
        exportFileBtn.setText("Export");
        exportFileBtn.addActionListener(e -> exportFileBtn(e));

        //---- label3 ----
        label3.setText("text");
        label3.setVerticalAlignment(SwingConstants.BOTTOM);
        label3.setHorizontalAlignment(SwingConstants.RIGHT);
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 3f));

        //---- groupsBtn ----
        groupsBtn.setText("Groups");
        groupsBtn.addActionListener(e -> groupsBtn(e));

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
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(viewNotesBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exportBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exportFileBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(copyBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(groupsBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
                            .addComponent(customSeats)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(viewBtn, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollPane1)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label2)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(scenarioTitleTxt))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 504, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label3, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                            .addComponent(voteCountTxt, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 2, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label3, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                .addComponent(voteCountTxt, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label2)
                        .addComponent(scenarioTitleTxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(7, 7, 7)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn)
                        .addComponent(viewNotesBtn)
                        .addComponent(exportBtn)
                        .addComponent(viewBtn)
                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(customSeats)
                        .addComponent(exportFileBtn)
                        .addComponent(copyBtn)
                        .addComponent(groupsBtn))
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
    private JLabel voteCountTxt;
    private JLabel label2;
    private JTextField scenarioTitleTxt;
    private JButton copyBtn;
    private JButton viewNotesBtn;
    private JButton exportFileBtn;
    private JLabel label3;
    private JButton groupsBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
