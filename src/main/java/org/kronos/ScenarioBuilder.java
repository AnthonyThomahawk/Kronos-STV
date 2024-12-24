/*
 * Created by JFormDesigner on Thu Dec 05 21:25:11 EET 2024
 */

package org.kronos;

import jdk.nashorn.internal.scripts.JD;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
public class ScenarioBuilder extends JPanel {
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
        for (int i = 0; i < dtm.getRowCount(); i++) {
            boolean foundBlank = false;
            for (int j = 0; j < dtm.getColumnCount(); j++) {
                if (j == 0) {
                    String s = (String) dtm.getValueAt(i, 0);

                    if (!nameChecks.isInteger(s) && !s.equals("?")) {
                        statusTxt.setText("<html>" + "<b> Alert : </b>" +
                                "<br> <b style=\"color:RED;\">Invalid multiplier on row #" + (i+1) + ".</b>" +"</html>");
                        buildBtn.setEnabled(false);
                        return false;
                    }

                    if (nameChecks.isInteger(s)) {
                        totalCount += Integer.parseInt(s);
                    } else {
                        if (foundWildcard) {
                            statusTxt.setText("<html>" + "<b> Alert : </b>" +
                                    "<br> <b style=\"color:RED;\">Wildcard (?) cannot be used twice on row #" + (i+1) + ".</b>" +"</html>");
                            buildBtn.setEnabled(false);
                            return false;
                        }

                        foundWildcard = true;
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
                    "<br> <b style=\"color:RED;\">Total votes exceed the ballot limit (" + totalCount + " > " + spinner1.getValue() + ") .</b>" +"</html>");
            buildBtn.setEnabled(false);
            return false;
        }

        statusTxt.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");

        buildBtn.setEnabled(true);
        return true;
    }

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

        for (int i = 0; i < options.size(); i++) {
            dtm2.addColumn("Choice " + (i+1));
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

        permTable.getModel().addTableModelListener(e -> updateStatus());

        exRandtable.getModel().addTableModelListener(e -> updateStatus());

        scenarioNameTxt.getDocument().addDocumentListener(new DocumentListener() {
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


        if (selectedExRandToInit != null) {
            for (String c : selectedExRandToInit) {
                dtm.setValueAt(Boolean.TRUE, options.indexOf(c), 1);
            }
        }

        if (pTableDataToInit != null) {
            for (ArrayList<String> row : pTableDataToInit) {
                comboBoxGroups.add(createComboBoxGroup(options));
                dtm2.addRow(row.toArray());
            }
        }

        if (ballotCountInit != -1)
            spinner1.setValue(ballotCountInit);

        if (seatsInit != -1)
            spinner2.setValue(seatsInit);

        updateStatus();
    }

    private void addBtn(ActionEvent e) {
        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();
        comboBoxGroups.add(createComboBoxGroup(options));
        dtm2.addRow(new Object[] {"0", null, null, null, null});
    }

    private void remBtn(ActionEvent e) {
        int[] rows = permTable.getSelectedRows();
        DefaultTableModel dtm2 = (DefaultTableModel) permTable.getModel();
        for (int r : rows) {
            comboBoxGroups.remove(r);
            dtm2.removeRow(r);
        }
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

        ScenarioGenerator sg = new ScenarioGenerator(options, patterns, (Integer)spinner2.getValue());


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
    }

    public String saveBuildFile() {
        if (!Main.checkConfig())
            return null;

        try {
            String workDir = Main.getWorkDir();

            String fileSeperator = FileSystems.getDefault().getSeparator();
            String filePath = workDir + fileSeperator + scenarioNameTxt.getText() + ".buildTemplate";

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
        }
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
        label4 = new JLabel();
        buildBtn = new JButton();
        label5 = new JLabel();
        spinner2 = new JSpinner();
        statusTxt = new JLabel();
        groupsBtn = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText("Scenario name");

        //---- label2 ----
        label2.setText("Ballot count");

        //---- spinner1 ----
        spinner1.addChangeListener(e -> spinner1StateChanged(e));

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

        //---- spinner2 ----
        spinner2.addChangeListener(e -> spinner2StateChanged(e));

        //---- statusTxt ----
        statusTxt.setText("Status :");

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
                        .addGroup(layout.createParallelGroup()
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(remBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groupsBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buildBtn))
                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup()
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup()
                                            .addComponent(scenarioNameTxt, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
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
                                .addGroup(layout.createParallelGroup()
                                    .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))))
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
                        .addComponent(label2)
                        .addComponent(label3))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(scenarioNameTxt)
                                .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label4)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label5)
                                    .addGap(6, 6, 6)
                                    .addComponent(spinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGap(14, 14, 14))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                    .addGap(17, 17, 17)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
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
    private JLabel label4;
    private JButton buildBtn;
    private JLabel label5;
    private JSpinner spinner2;
    private JLabel statusTxt;
    private JButton groupsBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
