/*
 * Created by JFormDesigner on Mon Jul 15 19:02:32 EEST 2024
 */

package org.kronos;

import jdk.nashorn.internal.scripts.JD;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * @author Worker
 */
public class createInstitute extends JPanel {
    private int dLength = 1;
    private String editFile = "";
    private boolean unsaved;
    JDialog parentForm;

    public createInstitute(String file2Edit, JDialog form) {
        initComponents();
        dQuota.setValue(2);
        remBtn.setEnabled(false);
        editFile = file2Edit;
        parentForm = form;
        initTable();
    }

    private void initTable() {
        Class<?>[] columnTypes = new Class[3];
        columnTypes[0] = Integer.class;
        columnTypes[1] = String.class;
        columnTypes[2] = Integer.class;

        table1.setModel(new DefaultTableModel(new Object[][] {
                {1, "", null}
        }, new String[] {
                "#", "Ward name (University department or Municipal district)", "Ward size"
        })
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(true);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(true);
        table1.getTableHeader().setReorderingAllowed(false);

        table1.getColumnModel().getColumn(0).setMaxWidth(50);
        table1.getColumnModel().getColumn(2).setMaxWidth(100);

        updateStatus();

        iName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table1.isEditing())
                    table1.getCellEditor().stopCellEditing();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table1.isEditing())
                    table1.getCellEditor().stopCellEditing();
            }
        });

        ((JSpinner.DefaultEditor)dQuota.getEditor()).getTextField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table1.isEditing())
                    table1.getCellEditor().stopCellEditing();
            }
        });

        dQuota.addChangeListener(e -> {
            if (table1.isEditing())
                table1.getCellEditor().stopCellEditing();
        });

        iName.getDocument().addDocumentListener(new DocumentListener() {
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

        table1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                unsaved = true;
                updateStatus();
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

        scrollPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table1.isEditing())
                    table1.getCellEditor().stopCellEditing();
            }
        });

        if (editFile != null && !editFile.isEmpty())
        {
            try {
                loadFileForEditing(editFile);
                iName.setEnabled(false);
                newElecBtn.setText("<html><b>Save and return to election</b></html>");
            } catch (Exception ignored){}
        }
    }

    private void loadFileForEditing(String filePath) throws IOException, ParseException {
        String[] departmentNames;
        int[] departmentStrengths;
        int instituteQuota = -1;
        String instituteName = "";

        File file = new File(filePath);
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

        iName.setText(instituteName);
        dQuota.setValue(instituteQuota);

        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();

        dtm.removeRow(0);

        dLength = 0;
        for (i = 0; i < departmentNames.length; i++) {
            Object[] row = new Object[3];
            dLength++;
            row[0] = dLength;
            row[1] = departmentNames[i];
            row[2] = departmentStrengths[i];
            dtm.addRow(row);
        }
    }

    private boolean updateStatus() {
        if (iName.getText().isEmpty()) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">Institution must have a name.</b>" +"</html>");
            saveBtn.setEnabled(false);
            newElecBtn.setEnabled(false);
            return false;
        }

        if (nameChecks.isFileNameValid(iName.getText())) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">Institution name contains illegal characters.</b>" +"</html>");
            saveBtn.setEnabled(false);
            newElecBtn.setEnabled(false);
            return false;
        }

        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();

        for (int i = 0; i < rows; i++) {
            String dName = (String) dtm.getValueAt(i, 1);
            if (dName.isEmpty()) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Ward #" + (i+1) +" does not have a name.</b>" +"</html>");

                saveBtn.setEnabled(false);
                newElecBtn.setEnabled(false);

                return false;
            }
            if (dName.contains(",")) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Ward #" + (i+1) +" name contains comma.</b>" +"</html>");

                saveBtn.setEnabled(false);
                newElecBtn.setEnabled(false);

                return false;
            }
        }

        for (int i = 0; i < rows; i++) {
            Object dStrength = dtm.getValueAt(i, 2);

            if (dStrength == null) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Ward #" + (i+1) +" does not have a strength number.</b>" +"</html>");

                saveBtn.setEnabled(false);
                newElecBtn.setEnabled(false);

                return false;
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                if (i == j)
                    continue;
                if (dtm.getValueAt(i, 1).equals(dtm.getValueAt(j, 1))) {
                    label1.setText("<html>" + "<b> Alert : </b>" +
                            "<br> <b style=\"color:RED;\">Ward #" + (i+1) + " and #" + (j+1) + " cannot have the same name.</b>" +"</html>");

                    saveBtn.setEnabled(false);
                    newElecBtn.setEnabled(false);

                    return false;
                }
            }
        }

        label1.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");

        addBtn.setEnabled(true);
        remBtn.setEnabled(table1.getSelectedRows().length != 0);
        saveBtn.setEnabled(true);
        newElecBtn.setEnabled(true);
        return true;
    }

    private void addBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        DefaultTableModel model = (DefaultTableModel) table1.getModel();

        Object[] row = new Object[3];
        dLength++;

        row[0] = dLength;
        row[1] = "";
        row[2] = null;

        model.addRow(row);
    }

    private void remBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        int numRows = table1.getSelectedRows().length;
        if (numRows != 0) {
            dLength -= numRows;
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

    private void dQuotaStateChanged(ChangeEvent e) {
        if ((Integer)dQuota.getValue() <= 0) {
            dQuota.setValue(1);
        }
    }

    private void table1MouseClicked(MouseEvent e) {
        remBtn.setEnabled(table1.getSelectedRows().length != 0);
    }

    public String saveChanges() {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        if (!updateStatus()) {
            JOptionPane.showMessageDialog(this, "Cannot save changes, because there are active alerts.", "Error", JOptionPane.OK_OPTION);
            return null;
        }

        if (!Main.checkConfig())
            return null;

        try {
            String workDir = Main.getWorkDir();

            String fileSeperator = FileSystems.getDefault().getSeparator();
            String filePath = workDir + fileSeperator + iName.getText() + ".institution";

            JSONObject institution = new JSONObject();
            institution.put("Name", iName.getText());
            institution.put("Quota", dQuota.getValue());

            JSONArray depts = new JSONArray();

            DefaultTableModel dtm = (DefaultTableModel) table1.getModel();

            for (int i = 0; i < dtm.getRowCount(); i++) {
                JSONArray dept = new JSONArray();

                dept.add(dtm.getValueAt(i, 1)); // Department name
                dept.add(dtm.getValueAt(i,2)); // Department strength

                depts.add(dept);
            }

            institution.put("Departments", depts);

            OutputStreamWriter file = new OutputStreamWriter(Files.newOutputStream(Paths.get(filePath)), StandardCharsets.UTF_8);
            file.write(institution.toJSONString());
            file.close();

            unsaved = false;

            return filePath;

        } catch (Exception x) {
            JOptionPane.showMessageDialog(null, "Error saving institution", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void saveBtn(ActionEvent e) {
        saveChanges();
        JOptionPane.showMessageDialog(null, "Institution '" + iName.getText() + "' has been saved!");
    }

    private void newElecBtn(ActionEvent e) {
        if (editFile != null) {
            saveChanges();
            parentForm.dispose();
            return;
        }

        mainForm.openDeptCandidatesForm(saveChanges(), "New scenario");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        label2 = new JLabel();
        iName = new JTextField();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        label3 = new JLabel();
        dQuota = new JSpinner();
        addBtn = new JButton();
        remBtn = new JButton();
        saveBtn = new JButton();
        newElecBtn = new JButton();
        label4 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText("alerts");

        //---- label2 ----
        label2.setText("Institution name :");

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

        //---- label3 ----
        label3.setText("Ward quota : ");

        //---- dQuota ----
        dQuota.addChangeListener(e -> dQuotaStateChanged(e));

        //---- addBtn ----
        addBtn.setText("Add +");
        addBtn.addActionListener(e -> addBtn(e));

        //---- remBtn ----
        remBtn.setText("Remove -");
        remBtn.addActionListener(e -> remBtn(e));

        //---- saveBtn ----
        saveBtn.setText("Save");
        saveBtn.addActionListener(e -> saveBtn(e));

        //---- newElecBtn ----
        newElecBtn.setText("<html><b>New election</b></html>");
        newElecBtn.addActionListener(e -> newElecBtn(e));

        //---- label4 ----
        label4.setText("<html> <i> * max number of electable candidates per ward </i> </html>");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(saveBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                            .addComponent(newElecBtn, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(iName))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup()
                                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(label3)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(dQuota, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                    .addGap(0, 0, Short.MAX_VALUE)))))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label2)
                        .addComponent(iName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label3)
                        .addComponent(dQuota, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .addGap(8, 8, 8)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn)
                        .addComponent(remBtn)
                        .addComponent(saveBtn)
                        .addComponent(newElecBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JLabel label2;
    private JTextField iName;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JLabel label3;
    private JSpinner dQuota;
    private JButton addBtn;
    private JButton remBtn;
    private JButton saveBtn;
    private JButton newElecBtn;
    private JLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
