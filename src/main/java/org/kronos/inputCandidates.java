/*
 * Created by JFormDesigner on Sat Apr 20 07:43:43 EEST 2024
 */

package org.kronos;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.awt.event.*;
import java.beans.*;
import javax.management.openmbean.OpenDataException;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class inputCandidates extends JPanel {
    public boolean failed = false;
    public static int candidateCount = 1;
    public static String[] candidates = {};
    public static boolean unsaved = false;
    private boolean departmental = false;
    private String[] departmentNames;
    private int[] departmentStrengths;
    private int instituteQuota = -1;
    private String instituteName = "";
    private ArrayList<JComboBox> departmentBoxes;
    private int[] candidateDepartments;

    public inputCandidates(boolean b, String dFile) {
        candidateCount = 1;
        candidates = new String[]{};
        initComponents();
        departmental = b;

        if (b && dFile != null) {
            try {
                parseDepartments(dFile);
            } catch (Exception x){
                System.out.print(x);
            }
        }

        initTable();
    }

    public inputCandidates(File inFile) {
        initComponents();

        try {
            JSONParser parser = new JSONParser();
            JSONObject election = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(inFile.getAbsoluteFile().toPath()), StandardCharsets.UTF_8));

            departmental = election.containsKey("InstituteName");
        } catch (Exception ignored) {}


        initTable();
        populateTableFromFile(inFile);
    }

    private void parseDepartments(String f) throws IOException, ParseException {
        File file = new File(f);
        JSONParser parser = new JSONParser();
        JSONObject depts = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(file.getAbsoluteFile().toPath()), StandardCharsets.UTF_8));

        JSONArray dArr = (JSONArray) depts.get("Departments");

        departmentNames = new String[dArr.size()];
        departmentStrengths = new int[dArr.size()];

        int i = 0;
        for (Object o : dArr) {
            JSONArray dept = (JSONArray) o;
            departmentNames[i] = (String) dept.get(0);
            Long l = (long) dept.get(1);
            departmentStrengths[i] = l.intValue();
            i++;
        }

        Long quota = (long) depts.get("Quota");
        instituteQuota = quota.intValue();

        instituteName = (String) depts.get("Name");
    }

    private ArrayList<JComboBox> createDeptBoxes(ArrayList<JComboBox> old, int size) {
        ArrayList<JComboBox> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (old != null && i < old.size()) {
                list.add(old.get(i));
            } else {
                if (departmentNames == null) {
                    list.add(new JComboBox(new String[]{"NULL"}));
                } else {
                    list.add(new JComboBox(departmentNames));
                }
                list.get(i).setSelectedIndex(-1);
            }
        }

        return list;
    }

    private void initTable() {
        createBtn.setText("<html> <b> New scenario </b> </html>");

        remBtn.setEnabled(false);

        Object[][] rows;
        Object[] cols;

        if (!departmental) {
            editWardsBtn.setVisible(false);
            rows = new Object[][] {{"1", ""}};
            cols = new String[] {"#", "Candidate name"};
        } else {
            rows = new Object[][] {{"1", "", null}};
            cols = new String[] {"#", "Candidate name", "Ward"};
        }

        if (departmental) {
            departmentBoxes = createDeptBoxes(null, 1);

            table1 = new JTable() {
                @Override
                public TableCellEditor getCellEditor(int row, int column) {
                    if (column == 2) {
                        return new DefaultCellEditor(departmentBoxes.get(row));
                    }
                    return super.getCellEditor(row, column);
                }
            };

            scrollPane1.setViewportView(table1);
        }


        table1.setModel(new DefaultTableModel(rows, cols)
        {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    default:
                        return Object.class;
                }
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

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (table1.getCellEditor() != null) {
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

        scrollPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table1.isEditing())
                    table1.getCellEditor().stopCellEditing();
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        if (departmental)
            table1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(true);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(true);
        table1.getTableHeader().setReorderingAllowed(false);

        table1.getColumnModel().getColumn(0).setMaxWidth(50);

        updateStatus();

        table1.getModel().addTableModelListener(e -> {
            unsaved = true;
            updateStatus();
        });

        electionNameBox.getDocument().addDocumentListener(new DocumentListener() {
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

    private void populateTableFromFile(File inFile) {
        try {
            DefaultTableModel model = (DefaultTableModel) table1.getModel();

            model.removeRow(0);
            candidateCount = 0;

            JSONParser parser = new JSONParser();
            JSONObject election = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(inFile.getAbsoluteFile().toPath()), StandardCharsets.UTF_8));

            electionNameBox.setText((String) election.get("Title"));

            JSONArray candidatesJSON = (JSONArray) election.get("Candidates");

            for (Object o : candidatesJSON) {
                candidateCount++;
                model.addRow(new Object[]{candidateCount, o});
            }

            if (departmental) {
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

                departmentBoxes = createDeptBoxes(null, candidateDepartments.length);

                for (int i = 0; i < departmentBoxes.size(); i++) {
                    departmentBoxes.get(i).setSelectedIndex(candidateDepartments[i]);
                    model.setValueAt(departmentNames[candidateDepartments[i]], i, 2);
                }
            }


            unsaved = false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "This election file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            failed = true;
        }
    }

    private boolean updateStatus() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();

        if (electionNameBox.getText().isEmpty()) {
            label2.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">Election must have a name.</b></html>");
            createBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            return false;
        }

        if (nameChecks.isFileNameValid(electionNameBox.getText())) {
            label2.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">Election name contains illegal characters.</b></html>");
            createBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            return false;
        }

        // candidate >= 1 count check
        if (rows <= 1) {
            label2.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">There must be more than 1 candidate.</b></html>");
            createBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            return false;
        }

        // candidate name not empty check
        for (int i = 0; i < rows; i++) {
            if (dtm.getValueAt(i, 1).equals("")) {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " does not have a name.</b>" +"</html>");
                createBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                return false;
            }
        }

        // name starts with digit check
        for (int i = 0; i < rows; i++) {
            String d = (String) dtm.getValueAt(i, 1);
            if (Character.isDigit(d.charAt(0))) {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " name cannot start with a digit.</b>" +"</html>");
                createBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                return false;
            }
        }

        // special character check
        for (int i = 0; i < rows; i++) {
            String d = (String) dtm.getValueAt(i, 1);

            if (nameChecks.isPersonNameValid(d))
            {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " name cannot contain special characters.</b>" +"</html>");
                createBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                return false;
            }
        }

        // duplicate name check
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                if (j == i)
                    continue;

                if (dtm.getValueAt(i,1).equals(dtm.getValueAt(j,1))) {
                    label2.setText("<html>" + "<b> Alert : </b>" +
                            "<br> <b style=\"color:RED;\">Candidates " + (i+1) + " and " + (j+1) + " cannot have the same name.</b>" +"</html>");
                    createBtn.setEnabled(false);
                    exportBtn.setEnabled(false);
                    return false;
                }
            }
        }

        // department check (if election is departmental)
        if (departmental) {
            for (int i = 0; i < rows; i++) {
                if (departmentBoxes.get(i).getSelectedIndex() == -1) {
                    label2.setText("<html>" + "<b> Alert : </b>" +
                            "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " is not assigned to a department.</b>" +"</html>");
                    createBtn.setEnabled(false);
                    exportBtn.setEnabled(false);
                    return false;
                }
            }

            ArrayList<String> unassignedDepartments = new ArrayList<>(Arrays.asList(departmentNames));

            for (int i = 0; i < rows; i++) {
                unassignedDepartments.remove(departmentNames[departmentBoxes.get(i).getSelectedIndex()]);
            }

            if (!unassignedDepartments.isEmpty()) {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Ward \"" + unassignedDepartments.get(0) + "\" is not assigned to any candidate.</b>" +"</html>");
                createBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                return false;
            }
        }

        label2.setText("<html><b> Status : </b><br> <b style=\"color:GREEN;\">OK</b></html>");
        createBtn.setEnabled(true);
        exportBtn.setEnabled(true);

        return true;
    }

    private String[] extractDataToString() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();

        String[] data = new String[rows];
        for (int i = 0; i < rows; i++) {
            data[i] = (String)dtm.getValueAt(i, 1);
        }

        return data;
    }

    private void createBtn(ActionEvent e) {
        try {
            mainForm.openScenarioForm(null, "New scenario - " + electionNameBox.getText());
        } catch (Exception ignored) {
        }

    }

    private void addBtn(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        candidateCount++;
        if (departmental)
            departmentBoxes = createDeptBoxes(departmentBoxes, model.getRowCount() + 1);
        model.addRow(new Object[]{candidateCount, "", null});
        table1.requestFocus();
        table1.editCellAt(candidateCount-1, 1);
    }

    private void table1PropertyChange(PropertyChangeEvent e) {

    }

    private void remBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        int numRows = table1.getSelectedRows().length;
        if (numRows != 0) {
            candidateCount -= numRows;
            DefaultTableModel m = (DefaultTableModel) table1.getModel();
            for (int i = 0; i < numRows; i++) {
                int r = table1.getSelectedRow();
                if (departmental)
                    departmentBoxes.remove(r);
                m.removeRow(r);
            }
            for (int i = 0; i < m.getRowCount(); i++) {
                m.setValueAt(i+1, i, 0);
            }
            remBtn.setEnabled(false);
        }
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
            String filePath = workDir + fileSeperator + electionNameBox.getText() + ".election";

            JSONObject election = new JSONObject();
            election.put("Title", electionNameBox.getText());
            JSONArray candidates = new JSONArray();
            candidates.addAll(Arrays.asList(extractDataToString()));
            election.put("Candidates", candidates);

            if (departmental) {
                election.put("InstituteName", instituteName);
                election.put("InstituteQuota", instituteQuota);
                JSONArray depts = new JSONArray();
                for (int i = 0; i < departmentNames.length; i++) {
                    JSONArray d = new JSONArray();
                    d.add(departmentNames[i]);
                    d.add(departmentStrengths[i]);
                    depts.add(d);
                }
                election.put("Departments", depts);

                JSONArray cDepts = new JSONArray();

                for (int i = 0; i < departmentBoxes.size(); i++) {
                   cDepts.add(departmentBoxes.get(i).getSelectedIndex());
                }

                election.put("CandidateDepartments", cDepts);
            }

            OutputStreamWriter file = new OutputStreamWriter(Files.newOutputStream(Paths.get(filePath)), StandardCharsets.UTF_8);

            file.write(election.toJSONString());
            file.flush();
            file.close();

            unsaved = false;
            return filePath;
        } catch (Exception x) {
            return null;
        }

    }

    private void exportBtn(ActionEvent e) {
        String filePath = null;
        try {
            filePath = saveChanges();
        } catch (Exception ignored){}


        if (filePath == null) {
            JOptionPane.showMessageDialog(null, "Error saving election.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Path p = Paths.get(filePath);
        String fileName = p.getFileName().toString();

        JOptionPane.showMessageDialog(null, "Election has been saved as " + fileName + " !", "Save successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void electionNameBoxMouseClicked(MouseEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();
    }

    private void refreshDepartments() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        String[] candidates = new String[dtm.getRowCount()];
        String[] oldDepts = new String[dtm.getRowCount()];
        for (int i = 0; i < dtm.getRowCount(); i++) {
            candidates[i] = (String) dtm.getValueAt(i, 1);
            oldDepts[i] = (String) dtm.getValueAt(i, 2);
        }

        departmentBoxes = createDeptBoxes(null, candidates.length);

        for (int i = 0; i < candidates.length; i++) {
            if (!Arrays.asList(departmentNames).contains(oldDepts[i])) {
                departmentBoxes.get(i).setSelectedIndex(-1);
                dtm.setValueAt(null, i, 2);
            } else {
                departmentBoxes.get(i).setSelectedIndex(Arrays.asList(departmentNames).indexOf(oldDepts[i]));
                dtm.setValueAt(oldDepts[i], i, 2);
            }
        }
    }

    private void editWardsBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        try {
            String fileSeperator = FileSystems.getDefault().getSeparator();
            String workDir = Main.getWorkDir();
            String filePath = workDir + fileSeperator + instituteName  + ".institution";
            if (!new File(filePath).exists()) {
                JOptionPane.showMessageDialog(null, "The institution file \"" + instituteName + ".institution\" does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mainForm.openInstitutionForm("Edit institution - " + instituteName, filePath);
            parseDepartments(filePath);
            refreshDepartments();
        } catch (Exception ignored) {}

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        createBtn = new JButton();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        addBtn = new JButton();
        label2 = new JLabel();
        remBtn = new JButton();
        exportBtn = new JButton();
        label3 = new JLabel();
        electionNameBox = new JTextField();
        editWardsBtn = new JButton();

        //======== this ========

        //---- createBtn ----
        createBtn.setText("<html><b>New Scenario</b></html>");
        createBtn.addActionListener(e -> createBtn(e));

        //======== scrollPane1 ========
        {

            //---- table1 ----
            table1.addPropertyChangeListener("table1", e -> table1PropertyChange(e));
            scrollPane1.setViewportView(table1);
        }

        //---- addBtn ----
        addBtn.setText("Add +");
        addBtn.addActionListener(e -> addBtn(e));

        //---- label2 ----
        label2.setText("tooltip");
        label2.setHorizontalAlignment(SwingConstants.LEFT);

        //---- remBtn ----
        remBtn.setText("Remove -");
        remBtn.addActionListener(e -> remBtn(e));

        //---- exportBtn ----
        exportBtn.setText("Save");
        exportBtn.addActionListener(e -> exportBtn(e));

        //---- label3 ----
        label3.setText("Election name : ");

        //---- electionNameBox ----
        electionNameBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                electionNameBoxMouseClicked(e);
            }
        });

        //---- editWardsBtn ----
        editWardsBtn.setText("<html><b>Edit wards</b> (for current institution)</html>");
        editWardsBtn.addActionListener(e -> editWardsBtn(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label3)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(electionNameBox))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exportBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 359, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(editWardsBtn)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(editWardsBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label3)
                        .addComponent(electionNameBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportBtn)
                        .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(9, 9, 9))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JButton createBtn;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton addBtn;
    private JLabel label2;
    private JButton remBtn;
    private JButton exportBtn;
    private JLabel label3;
    private JTextField electionNameBox;
    private JButton editWardsBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
