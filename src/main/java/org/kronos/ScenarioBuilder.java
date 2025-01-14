/*
 * Created by JFormDesigner on Thu Dec 05 21:25:11 EET 2024
 */

package org.kronos;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.awt.event.*;
import javax.management.openmbean.OpenDataException;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Worker
 */

class Pair<T, U> {
    public final T t;
    public final U u;

    public Pair(T t, U u) {
        this.t= t;
        this.u= u;
    }
}

public class ScenarioBuilder extends JPanel {
    public static boolean unsaved;
    ArrayList<ArrayList<JComboBox>> comboBoxGroups;
    ArrayList<String> options;
    ArrayList<String> patterns;
    String[] candidates;
    String electionTitle;
    int candidateCount;
    boolean departmental;
    String instituteName;
    int instituteQuota;
    String[] departmentNames;
    int[] departmentStrengths;
    int[] candidateDepartments;
    private ArrayList<String> groupNames;
    private ArrayList<Integer> groupCandidates;

    ArrayList<String> allExRandToInit;
    ArrayList<String> selectedExRandToInit;
    ArrayList<ArrayList<String>> pTableDataToInit;

    int seatsInit = -1;
    int ballotCountInit = -1;

    public Thread solveThread;


    public ScenarioBuilder(String[] candidates, String constituencyFile) {
        initComponents();

        options = new ArrayList<>();

        options.addAll(Arrays.asList(candidates));

        spinner1.setValue(1);
        spinner2.setValue(1);

        init();
    }

    public ScenarioBuilder(String file, int fileType) {
        initComponents();

        switch (fileType) {
            case 0:
                parseElection(file);
                break;
            case 1:
                parseScenario(file);
                break;
            case 2:
                parseBuildFile(file);
                break;
        }

        options = new ArrayList<>();

        options.addAll(Arrays.asList(candidates));

        spinner1.setValue(1);
        spinner2.setValue(1);
        minSeatsSpinner.setValue(1);

        init();
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

        } catch (Exception ez) {
            JOptionPane.showMessageDialog(null, "This election file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            JDialog x = (JDialog) this.getRootPane().getParent();
            x.dispose();
        }
    }

    private void parseBuildFile(String buildFilePath) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject buildFile = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(Paths.get(buildFilePath)), StandardCharsets.UTF_8));
            electionTitle = (String) buildFile.get("ElectionTitle");

            JSONArray jCandidates = (JSONArray) buildFile.get("Candidates");
            ArrayList<String> cList = new ArrayList<>();
            jCandidates.iterator().forEachRemaining((x) -> cList.add((String)x));
            candidates = cList.toArray(new String[0]);
            candidateCount = candidates.length;

            seatsInit = Math.toIntExact((Long) buildFile.get("Seats"));

            ballotCountInit = Math.toIntExact((Long) buildFile.get("BallotCount"));

            if (buildFile.containsKey("InstituteName")) {
                departmental = true;
                instituteName = (String) buildFile.get("InstituteName");
                Long l = (long) buildFile.get("InstituteQuota");
                instituteQuota = l.intValue();
                JSONArray depts = (JSONArray) buildFile.get("Departments");

                departmentNames = new String[depts.size()];
                departmentStrengths = new int[depts.size()];

                for (int i = 0; i < depts.size(); i++) {
                    JSONArray dept = (JSONArray) depts.get(i);
                    departmentNames[i] = (String) dept.get(0);
                    Long dS = (long) dept.get(1);
                    departmentStrengths[i] = dS.intValue();
                }

                JSONArray cDepts = (JSONArray) buildFile.get("CandidateDepartments");
                candidateDepartments = new int[cDepts.size()];

                for (int i = 0; i < cDepts.size(); i++) {
                    Long x = (long) cDepts.get(i);
                    candidateDepartments[i] = x.intValue();
                }

            } else {
                departmental = false;
            }

            if (buildFile.containsKey("GroupNames") && buildFile.containsKey("GroupCandidates")) {
                groupNames = new ArrayList<>();
                groupCandidates = new ArrayList<>();

                JSONArray gN = (JSONArray) buildFile.get("GroupNames");
                groupNames.addAll(gN);

                JSONArray gC = (JSONArray) buildFile.get("GroupCandidates");
                for (Object o : gC) {
                    Long longIndex = (Long) o;
                    groupCandidates.add(longIndex.intValue());
                }

            }

            JSONArray allXRand = (JSONArray) buildFile.get("ExRandomAll");
            allExRandToInit = new ArrayList<>();
            allExRandToInit.addAll(allXRand);

            JSONArray selectedXRand = (JSONArray) buildFile.get("ExRandomSelected");
            selectedExRandToInit = new ArrayList<>();
            selectedExRandToInit.addAll(selectedXRand);

            JSONArray tableData = (JSONArray) buildFile.get("PermTable");
            pTableDataToInit = new ArrayList<>();
            pTableDataToInit.addAll(tableData);

            scenarioNameTxt.setText(electionTitle + "_scen");


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "This scenario file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            JDialog x = (JDialog) this.getRootPane().getParent();
            x.dispose();
        }
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

            int c = Math.toIntExact((Long) scenario.get("Seats"));
            spinner2.setValue(c);

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

            pTableDataToInit = new ArrayList<>();
            JSONArray choices = (JSONArray) scenario.get("Choices");
            for (Object choice : choices) {
                List list = (List) choice;
                ArrayList<String> p = new ArrayList<>();
                p.add(String.valueOf(Math.toIntExact((long) list.get(list.size() - 1))));
                for (int i = 0; i < list.size() - 1; i++) {
                    p.add((String) list.get(i));
                }
                pTableDataToInit.add(p);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "This scenario file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            JDialog x = (JDialog) this.getRootPane().getParent();
            x.dispose();
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

    private ArrayList<String> getAllExRand() {
        DefaultTableModel dtm = (DefaultTableModel) exRandtable.getModel();
        ArrayList<String> exRandList = new ArrayList<>();

        for (int i = 0; i < dtm.getRowCount(); i++) {
            exRandList.add((String) dtm.getValueAt(i, 0));
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



    private boolean updateStatus() {
        if (scenarioNameTxt.getText().isEmpty()) {
            statusTxt.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">Scenario must have a title.</b>" +"</html>");
            buildBtn.setEnabled(false);
            return false;
        }

        DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();

        int totalCount = 0;
        boolean foundWildcard = false;
        boolean foundX = false;
        for (int i = 0; i < dtm.getRowCount(); i++) {
            boolean foundBlank = false;
            for (int j = 0; j < dtm.getColumnCount(); j++) {
                if (j == 0) {
                    String s = (String) dtm.getValueAt(i, 0);

                    if (!nameChecks.isInteger(s) && !s.equals("?") && !s.equals("x") && !s.equals("X")) {
                        statusTxt.setText("<html>" + "<b> Alert : </b>" +
                                "<br> <b style=\"color:RED;\">Invalid multiplier on row #" + (i+1) + ".</b>" +"</html>");
                        buildBtn.setEnabled(false);
                        return false;
                    }

                    if (nameChecks.isInteger(s)) {
                        totalCount += Integer.parseInt(s);
                    } else if (s.equals("?")) {
                        if (foundWildcard) {
                            statusTxt.setText("<html>" + "<b> Alert : </b>" +
                                    "<br> <b style=\"color:RED;\">Wildcard (?) cannot be used twice on row #" + (i+1) + ".</b>" +"</html>");
                            buildBtn.setEnabled(false);
                            return false;
                        }

                        foundWildcard = true;
                    } else {
                        if (foundX) {
                            statusTxt.setText("<html>" + "<b> Alert : </b>" +
                                    "<br> <b style=\"color:RED;\">Variable (X) cannot be used twice on row #" + (i+1) + ".</b>" +"</html>");
                            buildBtn.setEnabled(false);
                            return false;
                        }

                        foundX = true;
                    }
                } else {
                    String s = (String) dtm.getValueAt(i, j);

                    if (foundBlank) {
                        if (!(s == null || s.isEmpty())) {
                            statusTxt.setText("<html>" + "<b> Alert : </b>" +
                                    "<br> <b style=\"color:RED;\">Row #" + (i+1) + " is skipping choices.</b>" +"</html>");
                            buildBtn.setEnabled(false);
                            return false;
                        }
                    }

                    if (s == null || s.isEmpty()) {
                        foundBlank = true;
                    }

                    if (getExRand().isEmpty() && s != null && s.equals("EX-RANDOM")) {
                        statusTxt.setText("<html>" + "<b> Alert : </b>" +
                                "<br> <b style=\"color:RED;\">Row #" + (i+1) + " contains EX-RANDOM but no exclusive random candidates are selected.</b>" +"</html>");
                        buildBtn.setEnabled(false);
                        return false;
                    }
                }
            }
        }

        if (totalCount > (Integer) spinner1.getValue()) {
            statusTxt.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">Total votes are exceeded (" + totalCount + " > " + spinner1.getValue() + ") .</b>" +"</html>");
            buildBtn.setEnabled(false);
            return false;
        }

        if (solveThread != null && solveThread.isAlive() && foundX) {
            statusTxt.setText("<html>" + "<b> Warning : </b>" +
                    "<br> <b style=\"color:ORANGE;\">Solving for X, please wait...</b>" +"</html>");

            buildBtn.setEnabled(false);
            return false;
        }

        if (foundX) {
            statusTxt.setText("<html>" + "<b> Warning : </b>" +
                    "<br> <b style=\"color:ORANGE;\">Unsolved variable X</b>" +"</html>");

            buildBtn.setEnabled(false);
            return true;
        }

        statusTxt.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");

        buildBtn.setEnabled(true);
        return true;
    }

    private ArrayList<JComboBox> createComboBoxGroup(ArrayList<String> opts, int currentIndex) {
        ArrayList<JComboBox> arr = new ArrayList<>();
        for (int i = 0; i < permTable.getColumnCount() - 1; i++) {
            JComboBox x = new JComboBox();
            for (String s : opts) {
                x.addItem(s);
            }
            x.addItem("RANDOM");
            x.addItem("EX-RANDOM");
            x.addItem("Clear option [x]");

            ArrayList<String> newOpts = new ArrayList<>(opts);
            newOpts.add("RANDOM");
            newOpts.add("EX-RANDOM");

            int selection;
            String v;

            if (pTableDataToInit != null) {
                try {
                    v = pTableDataToInit.get(currentIndex).get(i+1);
                } catch (Exception e) {
                    v = null;
                }
            } else {
                v = null;
            }

            if (v != null && !v.isEmpty())
                selection = newOpts.indexOf(v);
            else
                selection = -1;

            x.setSelectedIndex(selection);

            AtomicInteger oldSel = new AtomicInteger(selection);

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
        if (groupNames == null)
            groupNames = new ArrayList<>();
        if (groupCandidates == null)
            groupCandidates = new ArrayList<>();

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

//        String multInfo = "<html>Multiplier values : <br>" +
//                "<b>Any number</b> - Number of votes<br>" +
//                "<b> ? </b> - Remaining votes";
//
//        label4.setText(multInfo);

        panel1.setBorder(BorderFactory.createLineBorder(Color.gray, 2));

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

        // temp change
        for (int i = 0; i < 6; i++) {
            dtm2.addColumn("Choice " + (i+1));
        }

//        for (int i = 0; i < options.size(); i++) {
//            dtm2.addColumn("Choice " + (i+1));
//        }

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

        permTable.getModel().addTableModelListener(e -> {
            unsaved = true;
            updateStatus();
        });

        exRandtable.getModel().addTableModelListener(e -> {
            unsaved = true;
            updateStatus();
        });

        scenarioNameTxt.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                unsaved = true;
                updateStatus();
            }
            public void removeUpdate(DocumentEvent e) {
                unsaved = true;
                updateStatus();
            }
            public void insertUpdate(DocumentEvent e) {
                unsaved = true;
                updateStatus();
            }
        });

        spinner1.addChangeListener(e -> updateStatus());

        spinner2.addChangeListener(e -> updateStatus());

        targetGroupBox.removeAllItems();
        for (String s : groupNames)
            targetGroupBox.addItem(s);

        methodBox.addItem("Variable step");
        methodBox.addItem("Linear scan");
        methodBox.setSelectedIndex(0);

        if (selectedExRandToInit != null) {
            for (String c : selectedExRandToInit) {
                dtm.setValueAt(Boolean.TRUE, options.indexOf(c), 1);
            }
        }

        if (pTableDataToInit != null) {
            for (int i = 0; i < pTableDataToInit.size(); i++) {
                comboBoxGroups.add(createComboBoxGroup(options, i));
                dtm2.addRow(pTableDataToInit.get(i).toArray());
            }
        }

        if (ballotCountInit != -1)
            spinner1.setValue(ballotCountInit);

        if (seatsInit != -1)
            spinner2.setValue(seatsInit);

        updateStatus();

        unsaved = false;
    }

    private void addBtn(ActionEvent e) {
        if (permTable.isEditing())
            permTable.getCellEditor().stopCellEditing();

        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();
        comboBoxGroups.add(createComboBoxGroup(options, comboBoxGroups.size()));
        dtm2.addRow(new Object[] {"0", null, null, null, null});
    }

    private void remBtn(ActionEvent e) {
        if (permTable.isEditing())
            permTable.getCellEditor().stopCellEditing();

        int[] rows = permTable.getSelectedRows();
        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();

        for (int i = rows.length - 1; i >= 0; i--) {
            comboBoxGroups.remove(rows[i]);
            dtm2.removeRow(rows[i]);
        }
    }

    private String generateOutputDirect(String ballots, String constituenciesFile) {
        STVpy stv = new STVpy();
        String stvOutput;
        try {
            stvOutput = stv.callSTVDirect(ballots, (Integer)spinner2.getValue(), constituenciesFile, instituteQuota);
        } catch (Exception x) {
            return null;
        }

        return stvOutput;
    }

    private String generateOutputDirect(String ballots) {
        STVpy stv = new STVpy();
        String stvOutput;
        try {
            stvOutput = stv.callSTVDirect(ballots, (Integer)spinner2.getValue());
        } catch (Exception x) {
            return null;
        }

        return stvOutput;
    }

    private String generateOutput(String inputFile) {
        STVpy stv = new STVpy();
        String stvOutput;
        try {
            stvOutput = stv.callSTV(inputFile, (Integer)spinner2.getValue());
        } catch (Exception x) {
            return null;
        }

        return stvOutput;
    }

    private String generateOutput(String ballotsFile, String constituenciesFile) {
        STVpy stv = new STVpy();
        String stvOutput;
        try {
            stvOutput = stv.callSTV(ballotsFile, (Integer)spinner2.getValue(), constituenciesFile, instituteQuota);
        } catch (Exception x) {
            return null;
        }

        return stvOutput;
    }

    void sortPermTable() {
        DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();

        int wildCardRow = -1;

        for (int i = 0; i < dtm.getRowCount(); i++) {
            String s = (String) dtm.getValueAt(i, 0);
            if (s.equals("?")) {
                wildCardRow = i;
                break;
            }
        }

        if (wildCardRow != -1 && wildCardRow < dtm.getRowCount() - 1) {
            dtm.moveRow(wildCardRow, wildCardRow, dtm.getRowCount() - 1);

            ArrayList<JComboBox> tmp = comboBoxGroups.get(wildCardRow);
            comboBoxGroups.set(wildCardRow, comboBoxGroups.get(comboBoxGroups.size() - 1));
            comboBoxGroups.set(comboBoxGroups.size() - 1, tmp);
        }
    }


    private void buildBtn(ActionEvent e) {
        if (permTable.isEditing())
            permTable.getCellEditor().stopCellEditing();

        if (exRandtable.isEditing())
            exRandtable.getCellEditor().stopCellEditing();

        sortPermTable();

        patterns = new ArrayList<>();

        patterns.add(Integer.toString((Integer) spinner1.getValue()));
        ArrayList<String> exRand = getExRand();
        if (!exRand.isEmpty()) {
            StringBuilder exRandStr = new StringBuilder("#={" + exRand.get(0));
            for (int i = 1; i < exRand.size(); i++) {
                exRandStr.append(",").append(exRand.get(i));
            }
            exRandStr.append("}");
            patterns.add(exRandStr.toString());
        }

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

        try {
            ScenarioGenerator sg = new ScenarioGenerator(options, patterns, (Integer) spinner2.getValue());

            String[] opts = {"Results", "Edit scenario"};

            int sel = JOptionPane.showOptionDialog(null, "Display results or edit generated scenario ?", "Select action", 0, 3, null, opts, opts[0]);

            if (sel == 0) {
                saveBuildFile();

                sg.ballotsToCSV(scenarioNameTxt.getText() + ".csv");

                if (departmental) {
                    generateConstituencyFile(scenarioNameTxt.getText() + "_const.csv");
                }

                String output;

                if (departmental)
                    output = generateOutput(scenarioNameTxt.getText() + ".csv", scenarioNameTxt.getText() + "_const.csv");
                else
                    output = generateOutput(scenarioNameTxt.getText() + ".csv");

                STVResults electionResults = new STVResults(output, (Integer) spinner1.getValue());

                JDialog j = new JDialog(Main.mainFrame, "Results", true);
                resultForm x = new resultForm(scenarioNameTxt.getText(), electionResults, null, null, candidates);

                if (!groupNames.isEmpty())
                    x = new resultForm(scenarioNameTxt.getText(), electionResults, groupNames, groupCandidates, candidates);

                j.setContentPane(x);
                j.pack();
                j.setLocationRelativeTo(null);
                j.setVisible(true);
            } else if (sel == 1) {
                String fileName = saveScenario(sg.ballotsToJSON());
                mainForm.openScenarioForm(new File(fileName), "Edit scenario - " + scenarioNameTxt.getText());
            }
        } catch (Exception ez) {
            JOptionPane.showMessageDialog(null, ez.getMessage(), "Build Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String saveBuildFile() {
        if (permTable.isEditing())
            permTable.getCellEditor().stopCellEditing();

        if (exRandtable.isEditing())
            exRandtable.getCellEditor().stopCellEditing();

        if (!Main.checkConfig())
            return null;

        try {
            String workDir = Main.getWorkDir();

            String fileSeperator = FileSystems.getDefault().getSeparator();
            String filePath = workDir + fileSeperator + scenarioNameTxt.getText() + ".template";

            JSONObject buildFile = new JSONObject();

            buildFile.put("ScenarioTitle", scenarioNameTxt.getText());
            buildFile.put("ElectionTitle", electionTitle);
            JSONArray arr = new JSONArray();
            for (String s : candidates)
                arr.add(s);
            buildFile.put("Candidates", arr);

            buildFile.put("Seats", spinner2.getValue());
            buildFile.put("BallotCount", spinner1.getValue());
            buildFile.put("EnforceSeats", true);
            buildFile.put("Notes", "");

            if (departmental) {
                buildFile.put("InstituteName", instituteName);
                buildFile.put("InstituteQuota", instituteQuota);

                JSONArray depts = new JSONArray();

                for (int i = 0; i < departmentNames.length; i++) {
                    JSONArray dept = new JSONArray();
                    dept.add(departmentNames[i]);
                    dept.add(departmentStrengths[i]);
                    depts.add(dept);
                }

                buildFile.put("Departments", depts);

                JSONArray cDepts = new JSONArray();

                for (int candidateDepartment : candidateDepartments) {
                    cDepts.add(candidateDepartment);
                }

                buildFile.put("CandidateDepartments", cDepts);
            }

            if (groupNames != null && groupCandidates != null) {
                if (!groupNames.isEmpty() && !groupCandidates.isEmpty()) {
                    JSONArray groups = new JSONArray();
                    groups.addAll(groupNames);
                    JSONArray groupIndexes = new JSONArray();
                    groupIndexes.addAll(groupCandidates);

                    buildFile.put("GroupNames", groups);
                    buildFile.put("GroupCandidates", groupIndexes);
                }
            }

            JSONArray XRandom = new JSONArray();
            XRandom.addAll(getAllExRand());

            buildFile.put("ExRandomAll", XRandom);

            JSONArray XRandomSelected = new JSONArray();
            XRandomSelected.addAll(getExRand());

            buildFile.put("ExRandomSelected", XRandomSelected);

            JSONArray ptable = new JSONArray();
            DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();

            for (int i = 0; i < dtm.getRowCount(); i++) {
                JSONArray row = new JSONArray();
                for (int j = 0; j < dtm.getColumnCount(); j++) {
                    row.add(dtm.getValueAt(i,j));
                }

                ptable.add(row);
            }

            buildFile.put("PermTable", ptable);

            OutputStreamWriter file = new OutputStreamWriter(Files.newOutputStream(Paths.get(filePath)), StandardCharsets.UTF_8);
            file.write(buildFile.toJSONString());
            file.close();

            return filePath;
        } catch (Exception x) {
            System.out.println(x);
            JOptionPane.showMessageDialog(null, "Error saving scenario", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public String saveScenario(JSONArray choices) {
        if (permTable.isEditing())
            permTable.getCellEditor().stopCellEditing();

        if (exRandtable.isEditing())
            exRandtable.getCellEditor().stopCellEditing();

        if (!Main.checkConfig())
            return null;

        try {
            String workDir = Main.getWorkDir();

            String fileSeperator = FileSystems.getDefault().getSeparator();
            String filePath = workDir + fileSeperator + scenarioNameTxt.getText() + ".scenario";

            JSONObject scenario = new JSONObject();

            scenario.put("ScenarioTitle", scenarioNameTxt.getText());
            scenario.put("ElectionTitle", electionTitle);
            JSONArray arr = new JSONArray();
            for (String s : candidates)
                arr.add(s);
            scenario.put("Candidates", arr);

            scenario.put("Choices", choices);
            scenario.put("Seats", spinner2.getValue());
            scenario.put("EnforceSeats", true);
            scenario.put("Notes", "");

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

            return filePath;
        } catch (Exception x) {
            System.out.println(x);
            JOptionPane.showMessageDialog(null, "Error saving scenario", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }

    public String saveChanges() throws OpenDataException {
        if (permTable.isEditing())
            permTable.getCellEditor().stopCellEditing();

        if (exRandtable.isEditing())
            exRandtable.getCellEditor().stopCellEditing();

        if (!updateStatus()) {
            JOptionPane.showMessageDialog(this, "Cannot save changes, because there are active alerts.", "Error", JOptionPane.OK_OPTION);
            throw new OpenDataException();
        }

        String fPath = saveBuildFile();
        JOptionPane.showMessageDialog(this, "Saved as : " + fPath + "\n (Scenario builder saves TEMPLATES by default!)", "Info", JOptionPane.INFORMATION_MESSAGE);


        return fPath;
    }


    private void spinner1StateChanged(ChangeEvent e) {
        if ((Integer) spinner1.getValue() < 1)
            spinner1.setValue(1);
    }

    private void spinner2StateChanged(ChangeEvent e) {
        if ((Integer) spinner2.getValue() < 1)
            spinner2.setValue(1);
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

            targetGroupBox.removeAllItems();
            for (String s : groupNames)
                targetGroupBox.addItem(s);
        }
    }

    private Pair<Integer, Boolean> testScenario(int i) {
        DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();
        int targetGroup = targetGroupBox.getSelectedIndex();

        patterns = new ArrayList<>();

        patterns.add(Integer.toString((Integer) spinner1.getValue()));
        ArrayList<String> exRand = getExRand();
        if (!exRand.isEmpty()) {
            StringBuilder exRandStr = new StringBuilder("#={" + exRand.get(0));
            for (int k = 1; k < exRand.size(); k++) {
                exRandStr.append(",").append(exRand.get(k));
            }
            exRandStr.append("}");
            patterns.add(exRandStr.toString());
        }

        for (int j = 0; j < dtm.getRowCount(); j++) {
            String first;

            if (dtm.getValueAt(j, 1).equals("EX-RANDOM"))
                first = "#";
            else if (dtm.getValueAt(j, 1).equals("RANDOM"))
                first = "$";
            else
                first = (String) dtm.getValueAt(j, 1);

            String v = (String) dtm.getValueAt(j, 0);
            StringBuilder line;
            if (v.equals("X") || v.equals("x")) {
                line = new StringBuilder(i + "*" + first);
            } else {
                line = new StringBuilder(dtm.getValueAt(j, 0) + "*" + first);
            }
            for (int l = 2; l < dtm.getColumnCount(); l++) {
                if (dtm.getValueAt(j,l) != null)
                    if (dtm.getValueAt(j,l).equals("EX-RANDOM"))
                        line.append("|").append("#");
                    else if (dtm.getValueAt(j,l).equals("RANDOM"))
                        line.append("|").append("$");
                    else
                        line.append("|").append(dtm.getValueAt(j,l));

            }
            patterns.add(line.toString());

        }

        ScenarioGenerator sg = new ScenarioGenerator(options, patterns, (Integer) spinner2.getValue());

//                sg.ballotsToCSV(scenarioNameTxt.getText() + ".csv");
//
//                String output;
//
//                if (departmental)
//                    output = generateOutput(scenarioNameTxt.getText() + ".csv", scenarioNameTxt.getText() + "_const.csv");
//                else
//                    output = generateOutput(scenarioNameTxt.getText() + ".csv");

        String inp = sg.ballotsToCSVString();

        String output;

        if (departmental)
            output = generateOutputDirect(inp, scenarioNameTxt.getText() + "_const.csv");
        else
            output = generateOutputDirect(inp);

        STVResults electionResults = new STVResults(output, (Integer) spinner1.getValue());
        int targetCount = 0;

        for (int x = 1; x <= electionResults.lastRank; x++) {
            String elec = electionResults.getElected(x);
            int ind = Arrays.asList(candidates).indexOf(elec);
            int groupCandidateIndex = groupCandidates.get(ind);

            if (groupCandidateIndex == targetGroup)
                targetCount++;
        }

        // old uncertain check
        //boolean uncertain = electionResults.stvInput.contains("RANDOM") || electionResults.stvInput.contains("random") || electionResults.stvInput.contains("RAND") || electionResults.stvInput.contains("rand");

        // check for scenarios where randomness occured from low votes, and ZEUS chose a random candidate from constituencies
        boolean uncertain = electionResults.stvInput.contains("xSHUFFLE");

        return new Pair<>(targetCount, uncertain);
    }

    private Pair<Integer, Boolean> testScenario(int i, int h) {
        DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();
        int targetGroup = targetGroupBox.getSelectedIndex();

        patterns = new ArrayList<>();

        patterns.add(Integer.toString((Integer) spinner1.getValue()));
        ArrayList<String> exRand = getExRand();
        if (!exRand.isEmpty()) {
            StringBuilder exRandStr = new StringBuilder("#={" + exRand.get(0));
            for (int k = 1; k < exRand.size(); k++) {
                exRandStr.append(",").append(exRand.get(k));
            }
            exRandStr.append("}");
            patterns.add(exRandStr.toString());
        }

        for (int j = 0; j < dtm.getRowCount(); j++) {
            String first;

            if (dtm.getValueAt(j, 1).equals("EX-RANDOM"))
                first = "#";
            else if (dtm.getValueAt(j, 1).equals("RANDOM"))
                first = "$";
            else
                first = (String) dtm.getValueAt(j, 1);

            String v = (String) dtm.getValueAt(j, 0);
            StringBuilder line;
            if (v.equals("X") || v.equals("x")) {
                line = new StringBuilder(i + "*" + first);
            } else if (v.equals("Y") || v.equals("y")) {
                line = new StringBuilder(h + "*" + first);
            }else {
                line = new StringBuilder(dtm.getValueAt(j, 0) + "*" + first);
            }
            for (int l = 2; l < dtm.getColumnCount(); l++) {
                if (dtm.getValueAt(j,l) != null)
                    if (dtm.getValueAt(j,l).equals("EX-RANDOM"))
                        line.append("|").append("#");
                    else if (dtm.getValueAt(j,l).equals("RANDOM"))
                        line.append("|").append("$");
                    else
                        line.append("|").append(dtm.getValueAt(j,l));

            }
            patterns.add(line.toString());

        }

        ScenarioGenerator sg = new ScenarioGenerator(options, patterns, (Integer) spinner2.getValue());

//                sg.ballotsToCSV(scenarioNameTxt.getText() + ".csv");
//
//                String output;
//
//                if (departmental)
//                    output = generateOutput(scenarioNameTxt.getText() + ".csv", scenarioNameTxt.getText() + "_const.csv");
//                else
//                    output = generateOutput(scenarioNameTxt.getText() + ".csv");

        String inp = sg.ballotsToCSVString();

        String output;

        if (departmental)
            output = generateOutputDirect(inp, scenarioNameTxt.getText() + "_const.csv");
        else
            output = generateOutputDirect(inp);

        STVResults electionResults = new STVResults(output, (Integer) spinner1.getValue());
        int targetCount = 0;

        for (int x = 1; x <= electionResults.lastRank; x++) {
            String elec = electionResults.getElected(x);
            int ind = Arrays.asList(candidates).indexOf(elec);
            int groupCandidateIndex = groupCandidates.get(ind);

            if (groupCandidateIndex == targetGroup)
                targetCount++;
        }

        // old uncertain check
        //boolean uncertain = electionResults.stvInput.contains("RANDOM") || electionResults.stvInput.contains("random") || electionResults.stvInput.contains("RAND") || electionResults.stvInput.contains("rand");

        // check for scenarios where randomness occured from low votes, and ZEUS chose a random candidate from constituencies
        boolean uncertain = electionResults.stvInput.contains("xSHUFFLE");

        return new Pair<>(targetCount, uncertain);
    }

    private void solveBtn(ActionEvent e) {
        if (permTable.isEditing())
            permTable.getCellEditor().stopCellEditing();

        solveBtn.setEnabled(false);

        solveThread = new Thread(() -> {
            int minSeats = (int) minSeatsSpinner.getValue();
            int total = (int) spinner1.getValue();
            int maxLimit;

            DefaultTableModel dtm = (DefaultTableModel) permTable.getModel();

            int restDefined = 0;
            int Xpos = -1;
            int Ypos = -1;
            int wildCardPos = -1;

            for (int i = 0; i < dtm.getRowCount(); i++) {
                String v = (String) dtm.getValueAt(i, 0);
                if (v.equals("?")) {
                    wildCardPos = i;
                } else if (nameChecks.isInteger(v)) {
                    restDefined += Integer.parseInt((String) dtm.getValueAt(i, 0));
                } else if (v.equals("x") || v.equals("X")) {
                    Xpos = i;
                } else if (v.equals("y") || v.equals("Y")) {
                    Ypos = i;
                }
                else {
                    JOptionPane.showMessageDialog(null, "Unknown symbol on row #" + i, "ERROR", JOptionPane.ERROR_MESSAGE);
                    solveBtn.setEnabled(true);
                    return;
                }
            }

            if (Xpos == -1) {
                JOptionPane.showMessageDialog(null, "Variable X not found.", "ERROR", JOptionPane.ERROR_MESSAGE);
                solveBtn.setEnabled(true);
                return;
            }

            if (wildCardPos == -1) {
                JOptionPane.showMessageDialog(null, "Wildcard \"?\" not found.", "ERROR", JOptionPane.ERROR_MESSAGE);
                solveBtn.setEnabled(true);
                return;
            }

            boolean foundRand = false;
            for (int i = 0; i < dtm.getRowCount(); i++) {
                for (int j = 1; j < dtm.getColumnCount(); j++) {
                    if (dtm.getValueAt(i,j) != null) {
                        if (dtm.getValueAt(i,j).equals("RANDOM") || dtm.getValueAt(i,j).equals("EX-RANDOM")) {
                            foundRand = true;
                            break;
                        }
                    }
                }
            }

            if (foundRand) {
                int res = JOptionPane.showConfirmDialog(null, "The template you are currently using contains RANDOM/EX-RANDOM.\nThis may cause any solution found to be incorrect.\nDo you want to continue?", "WARNING", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.NO_OPTION) {
                    solveBtn.setEnabled(true);
                    return;
                }
            }

            maxLimit = total - restDefined;
            int progress = 0;
            progressBar1.setMaximum(maxLimit);
            progressBar1.setValue(progress);

            label9.setText("Solving");

            updateStatus();

            permTable.setEnabled(false);
            exRandtable.setEnabled(false);
            addBtn.setEnabled(false);
            remBtn.setEnabled(false);

            cancelSolveBtn.setEnabled(true);

            boolean uncertain = false;

            if (departmental) {
                generateConstituencyFile(scenarioNameTxt.getText() + "_const.csv");
            }

            Pair<Integer, Boolean> results;


            // 1 var
            if (Ypos == -1) {
                int solution = -1;
                final long startTime = System.currentTimeMillis();

                if (methodBox.getSelectedIndex() == 0) {
                    // VARIABLE STEP
                    int step = maxLimit / 2;
                    int mid = maxLimit - step;
                    int lastmid = -1;
                    final int lastSteps = 5; // this is to provide the best possible solution by checking a backwards number of steps, to see if the best solution is skipped

                    while (true) {
                        progress++;
                        results = testScenario(mid);

                        boolean certaintyCheck = !results.u;

                        if (!skipUncertaintyBox.isSelected()) { // if box is unchecked, override the check
                            certaintyCheck = true;
                        }

                        if (results.t >= minSeats && certaintyCheck) {
                            solution = mid;
                            step /= 2;
                            mid -= step;
                        } else {
                            step /= 2;
                            mid += step;
                        }

                        if (Math.abs(lastmid - mid) <= 1) {
                            for (int n = mid; n > mid - lastSteps; n--) {
                                results = testScenario(n);

                                certaintyCheck = !results.u;

                                if (!skipUncertaintyBox.isSelected()) { // if box is unchecked, override the check
                                    certaintyCheck = true;
                                }

                                if (results.t >= minSeats && certaintyCheck) {
                                    solution = n;
                                }
                            }

                            break;
                        }

                        lastmid = mid;
                    }
                } else {
                    // LINEAR SCAN

                    for (int i = 1; i < maxLimit; i++) {
                        progress++;

                        progressBar1.setValue(progress);
                        int percent = (int) ((progress / (double) maxLimit) * 100);
                        label8.setText(percent + " %");

                        results = testScenario(i);

                        uncertain = results.u;
                        int targetCount = results.t;

                        if (skipUncertaintyBox.isSelected() && uncertain)
                            continue;

                        if (targetCount >= minSeats) {
                            solution = i;
                            break;
                        }
                    }
                }


                final long endTime = System.currentTimeMillis();

                if (solution != -1 && uncertain) {
                    int res = JOptionPane.showConfirmDialog(null, "Solution = " + solution + "\nSolution is uncertain, would you like to keep it anyway?\nTime taken : " + (endTime - (double)startTime) / 1000 + " seconds\nIterations : " + progress, "Uncertain solution", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION)
                        dtm.setValueAt(String.valueOf(solution), Xpos, 0);
                } else if (solution != -1) {
                    JOptionPane.showMessageDialog(null, "Solution = " + solution + "\nTime taken : " + (endTime - (double)startTime) / 1000 + " seconds\nIterations : " + progress, "Info", JOptionPane.INFORMATION_MESSAGE);
                    dtm.setValueAt(String.valueOf(solution), Xpos, 0);
                } else {
                    JOptionPane.showMessageDialog(null, "No solution was found.\nTime taken : " + (endTime - (double)startTime) / 1000 + " seconds\nIterations : " + progress, "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            // 2 var
            else {
                methodBox.setSelectedIndex(1);

                final long startTime = System.currentTimeMillis();

                int solutionX = -1;
                int solutionY = -1;

                int x = 1; // assuming X is friendly ballot
                int y = 0; // assuming Y is opposing ballot

                boolean lastAdd = true; // true = x , false = y

                int varLimit = maxLimit / 2;

                while (x < varLimit && y < varLimit) {
                    progress++;
                    progressBar1.setValue(progress);
                    int percent = (int) ((progress / (double) maxLimit) * 100);
                    label8.setText(percent + " %");

                    if (lastAdd) {
                        y++;
                        lastAdd = false;
                    } else {
                        x++;
                        lastAdd = true;
                    }

                    results = testScenario(x,y);

                    uncertain = results.u;
                    int targetCount = results.t;

                    if (skipUncertaintyBox.isSelected() && uncertain)
                            continue;

                    if (targetCount >= minSeats) {
                        solutionX = x;
                        solutionY = y;
                        break;
                    }

                }

                while (y < varLimit) {
                    progress++;
                    progressBar1.setValue(progress);
                    int percent = (int) ((progress / (double) maxLimit) * 100);
                    label8.setText(percent + " %");

                    y++;
                    results = testScenario(x,y);

                    uncertain = results.u;
                    int targetCount = results.t;

                    if (skipUncertaintyBox.isSelected() && uncertain) {
                        solutionY = y - 1;
                        uncertain = false;
                        break;
                    }

                    if (targetCount == minSeats - 1) {
                        solutionY = y - 1;
                        break;
                    }
                }

                final long endTime = System.currentTimeMillis();

                if (solutionX != -1 && uncertain) {
                    int res = JOptionPane.showConfirmDialog(null, "Solution\n X = " + solutionX + "\nY = " + solutionY + "\nSolution is uncertain, would you like to keep it anyway?\nTime taken : " + (endTime - (double)startTime) / 1000 + " seconds\nIterations : " + progress, "Uncertain solution", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) {
                        dtm.setValueAt(String.valueOf(solutionX), Xpos, 0);
                        dtm.setValueAt(String.valueOf(solutionY), Ypos, 0);
                    }
                } else if (solutionX != -1) {
                    JOptionPane.showMessageDialog(null, "Solution\nX = " + solutionX + "\nY = " + solutionY + "\nTime taken : " + (endTime - (double)startTime) / 1000 + " seconds\nIterations : " + progress, "Info", JOptionPane.INFORMATION_MESSAGE);
                    dtm.setValueAt(String.valueOf(solutionX), Xpos, 0);
                    dtm.setValueAt(String.valueOf(solutionY), Ypos, 0);
                } else {
                    JOptionPane.showMessageDialog(null, "No solution was found.\nTime taken : " + (endTime - (double)startTime) / 1000 + " seconds\nIterations : " + progress, "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }



            progressBar1.setValue(0);
            label9.setText("Idle");
            label8.setText("0 %");

            updateStatus();

            permTable.setEnabled(true);
            exRandtable.setEnabled(true);
            addBtn.setEnabled(true);
            remBtn.setEnabled(true);

            cancelSolveBtn.setEnabled(false);
            solveBtn.setEnabled(true);
        });

        solveThread.start();
    }

    private void minSeatsSpinnerStateChanged(ChangeEvent e) {
        if ((int) minSeatsSpinner.getValue() < 1) {
           minSeatsSpinner.setValue(1);
        }
    }

    private void cancelSolveBtn(ActionEvent e) {
        if (solveThread.isAlive())
            solveThread.stop();

        progressBar1.setValue(0);
        label9.setText("Idle");
        label8.setText("0 %");

        permTable.setEnabled(true);
        exRandtable.setEnabled(true);
        addBtn.setEnabled(true);
        remBtn.setEnabled(true);

        cancelSolveBtn.setEnabled(false);
        solveBtn.setEnabled(true);

        JOptionPane.showMessageDialog(null, "Operation aborted.", "Info", JOptionPane.INFORMATION_MESSAGE);

        updateStatus();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        scenarioNameTxt = new JTextField();
        label2 = new JLabel();
        spinner1 = new JSpinner();
        label3 = new JLabel();
        scrollPane2 = new JScrollPane();
        permTable = new JTable();
        scrollPane1 = new JScrollPane();
        exRandtable = new JTable();
        addBtn = new JButton();
        remBtn = new JButton();
        buildBtn = new JButton();
        label5 = new JLabel();
        spinner2 = new JSpinner();
        statusTxt = new JLabel();
        groupsBtn = new JButton();
        panel1 = new JPanel();
        targetGroupBox = new JComboBox();
        label4 = new JLabel();
        label6 = new JLabel();
        minSeatsSpinner = new JSpinner();
        solveBtn = new JButton();
        label7 = new JLabel();
        progressBar1 = new JProgressBar();
        label8 = new JLabel();
        label9 = new JLabel();
        cancelSolveBtn = new JButton();
        skipUncertaintyBox = new JCheckBox();
        methodBox = new JComboBox();
        label10 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText("Scenario title");

        //---- label2 ----
        label2.setText("Total votes");

        //---- spinner1 ----
        spinner1.addChangeListener(e -> spinner1StateChanged(e));

        //---- label3 ----
        label3.setText("EX-RANDOM candidates");

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

        //---- buildBtn ----
        buildBtn.setText("Build");
        buildBtn.addActionListener(e -> buildBtn(e));

        //---- label5 ----
        label5.setText("Seats");

        //---- spinner2 ----
        spinner2.addChangeListener(e -> spinner2StateChanged(e));

        //---- statusTxt ----
        statusTxt.setText("Status :");

        //---- groupsBtn ----
        groupsBtn.setText("Groups");
        groupsBtn.addActionListener(e -> groupsBtn(e));

        //======== panel1 ========
        {

            //---- label4 ----
            label4.setText("Target group");

            //---- label6 ----
            label6.setText("Min seats");

            //---- minSeatsSpinner ----
            minSeatsSpinner.addChangeListener(e -> minSeatsSpinnerStateChanged(e));

            //---- solveBtn ----
            solveBtn.setText("Solve for X");
            solveBtn.addActionListener(e -> solveBtn(e));

            //---- label7 ----
            label7.setText("Vote solver");
            label7.setFont(label7.getFont().deriveFont(label7.getFont().getStyle() | Font.BOLD, label7.getFont().getSize() + 4f));

            //---- label8 ----
            label8.setText("0%");
            label8.setHorizontalAlignment(SwingConstants.RIGHT);

            //---- label9 ----
            label9.setText("Idle");
            label9.setHorizontalAlignment(SwingConstants.RIGHT);

            //---- cancelSolveBtn ----
            cancelSolveBtn.setText("Cancel");
            cancelSolveBtn.setEnabled(false);
            cancelSolveBtn.addActionListener(e -> cancelSolveBtn(e));

            //---- skipUncertaintyBox ----
            skipUncertaintyBox.setText("Skip uncertain solutions");

            //---- label10 ----
            label10.setText("Method");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(label4)
                                    .addComponent(targetGroupBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(label6, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(minSeatsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(label9, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label8, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(skipUncertaintyBox, GroupLayout.Alignment.TRAILING)))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(solveBtn)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cancelSolveBtn)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(label7)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(label10)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(methodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(label7)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(label4)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(targetGroupBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(minSeatsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(label6)
                                        .addGap(28, 28, 28))))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(methodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label10))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(skipUncertaintyBox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label9)
                                .addGap(5, 5, 5)
                                .addComponent(label8)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(solveBtn)
                                .addComponent(cancelSolveBtn))
                            .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createParallelGroup()
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(remBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groupsBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buildBtn))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup()
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addGroup(layout.createParallelGroup()
                                            .addComponent(label1)
                                            .addComponent(scenarioNameTxt, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 330, GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup()
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(label5, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(spinner2, GroupLayout.Alignment.TRAILING)
                                            .addComponent(spinner1))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))))
                            .addComponent(scrollPane2))
                        .addComponent(statusTxt))
                    .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(statusTxt)
                    .addGap(26, 26, 26)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label1)
                        .addComponent(label3)
                        .addComponent(label2))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(scenarioNameTxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label5)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(spinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(102, 102, 102))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(5, 5, 5))))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn)
                        .addComponent(buildBtn)
                        .addComponent(groupsBtn))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JTextField scenarioNameTxt;
    private JLabel label2;
    private JSpinner spinner1;
    private JLabel label3;
    private JScrollPane scrollPane2;
    private JTable permTable;
    private JScrollPane scrollPane1;
    private JTable exRandtable;
    private JButton addBtn;
    private JButton remBtn;
    private JButton buildBtn;
    private JLabel label5;
    private JSpinner spinner2;
    private JLabel statusTxt;
    private JButton groupsBtn;
    private JPanel panel1;
    private JComboBox targetGroupBox;
    private JLabel label4;
    private JLabel label6;
    private JSpinner minSeatsSpinner;
    private JButton solveBtn;
    private JLabel label7;
    private JProgressBar progressBar1;
    private JLabel label8;
    private JLabel label9;
    private JButton cancelSolveBtn;
    private JCheckBox skipUncertaintyBox;
    private JComboBox methodBox;
    private JLabel label10;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
