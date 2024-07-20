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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class inputCandidates extends JPanel {
    public static int candidateCount = 1;
    public static String[] candidates = {};
    public static boolean unsaved = false;
    private boolean departamental = false;
    private String[] departmentNames;
    private int[] departmentStrengths;

    public inputCandidates(boolean b, String dFile) {
        candidateCount = 1;
        candidates = new String[]{};
        initComponents();
        departamental = b;

        if (b && dFile != null) {
            try {
                initDepartments(dFile);
            } catch (Exception x){
                System.out.print(x);
            }
        }

        initTable();
    }

    public inputCandidates(File inFile) {
        initComponents();
        initTable();
        populateTableFromFile(inFile);
    }

    private void initDepartments(String f) throws IOException, ParseException {
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
    }

    private void initTable() {
        createBtn.setText("<html> <b> New scenario </b> </html>");

        remBtn.setEnabled(false);

        Object[][] rows;
        Object[] cols;

        if (!departamental) {
            rows = new Object[][] {{"1", ""}};
            cols = new String[] {"#", "Candidate name"};
        } else {
            rows = new Object[][] {{"1", "", null}};
            cols = new String[] {"#", "Candidate name", "Department"};
        }


        table1.setModel(new DefaultTableModel(rows, cols)
        {

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

            unsaved = false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "This election file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            mainForm.stopLoadingForm = true;
        }
    }

    private void initLocale() {
        Locale currentLocale;

        currentLocale = Locale.ENGLISH;

        //currentLocale = new Locale("gr", "GR");

        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale, new UTF8Control());

        createBtn.setText(messages.getString("create"));
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

        if (electionNameBox.getText().matches(".*[\"*:|?<>/].*") || electionNameBox.getText().contains("\\")) {
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

            if (d.matches(".*[!@#$%^&*(),;'~`><?=-].*") || d.contains("\\") || d.contains("/") || d.contains("[") || d.contains("]"))
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
        model.addRow(new Object[]{candidateCount, ""});
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
                m.removeRow(table1.getSelectedRow());
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

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label3)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(electionNameBox, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exportBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 211, Short.MAX_VALUE)
                            .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 338, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label3)
                        .addComponent(electionNameBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 492, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportBtn)
                        .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
