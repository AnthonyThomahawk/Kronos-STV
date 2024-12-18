/*
 * Created by JFormDesigner on Thu Dec 05 21:25:11 EET 2024
 */

package org.kronos;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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


    public ScenarioBuilder(String[] candidates, String constituencyFile) {
        initComponents();

        options = new ArrayList<>();

        options.addAll(Arrays.asList(candidates));

        spinner1.setValue(1);
        spinner2.setValue(1);
        init();
    }

    public ScenarioBuilder(String file, boolean isScenario) {
        initComponents();

        if (!isScenario)
            parseElection(file);
        else
            parseScenario(file);

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

        updateStatus();

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

    private void buildBtn(ActionEvent e) {
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
        sg.ballotsToCSV(scenarioNameTxt.getText() + ".csv");

        JSONArray b = sg.ballotsToJSON();

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
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(remBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groupsBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buildBtn))
                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup()
                                            .addComponent(scenarioNameTxt, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(statusTxt))
                    .addContainerGap(8, Short.MAX_VALUE))
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
                                .addComponent(scenarioNameTxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label4)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label5)
                                    .addGap(6, 6, 6)
                                    .addComponent(spinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
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
