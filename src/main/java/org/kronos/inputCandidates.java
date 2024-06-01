/*
 * Created by JFormDesigner on Sat Apr 20 07:43:43 EEST 2024
 */

package org.kronos;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

public class inputCandidates extends JPanel {
    public static int candidateCount = 1;
    public static String[] candidates = {};
    public static boolean success = false;
    public static boolean unsaved = false;
    public inputCandidates() {
        candidateCount = 1;
        candidates = new String[]{};
        initComponents();
        //initLocale();
        initTable();
        success = false;
    }

    public inputCandidates(File inFile) {
        initComponents();
        initTable();
        success = false;
        populateTableFromFile(inFile);
    }

    private void initTable() {
        remBtn.setEnabled(false);
        table1.setModel(new DefaultTableModel(new Object[][] {
                {"1", ""}
        }, new String[] {
                "Candidate number #", "Candidate name"
        })
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(true);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(true);
        table1.getTableHeader().setReorderingAllowed(false);

        updateStatus();

        table1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                unsaved = true;
                updateStatus();
            }
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
            JSONObject election = (JSONObject) parser.parse(new FileReader(inFile.getAbsolutePath()));

            electionNameBox.setText((String) election.get("Title"));

            JSONArray candidatesJSON = (JSONArray) election.get("Candidates");

            for (Object o : candidatesJSON) {
                candidateCount++;
                model.addRow(new Object[]{candidateCount, o});
            }

            unsaved = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLocale() {
        Locale currentLocale;

        currentLocale = Locale.ENGLISH;

        //currentLocale = new Locale("gr", "GR");

        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale, new UTF8Control());

        createBtn.setText(messages.getString("create"));
    }

    private void updateStatus() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();

        if (electionNameBox.getText().isEmpty()) {
            label2.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">Election must have a name.</b></html>");
            createBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            return;
        }

        // candidate >= 1 count check
        if (rows <= 1) {
            label2.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">There must be more than 1 candidate.</b></html>");
            createBtn.setEnabled(false);
            exportBtn.setEnabled(false);
            return;
        }

        // candidate name not empty check
        for (int i = 0; i < rows; i++) {
            if (dtm.getValueAt(i, 1).equals("")) {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " does not have a name.</b>" +"</html>");
                createBtn.setEnabled(false);
                exportBtn.setEnabled(false);
                return;
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
                return;
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
                return;
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
                    return;
                }
            }
        }

        label2.setText("<html><b> Status : </b><br> <b style=\"color:GREEN;\">OK</b></html>");
        createBtn.setEnabled(true);
        exportBtn.setEnabled(true);
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
            success = true;

            JDialog createBallotsDlg = new JDialog(mainForm.inputCandidatesDlg, "Create ballots", true);
            createScenario c = new createScenario(saveChanges(), false);
            createBallotsDlg.setContentPane(c);
            createBallotsDlg.pack();
            createBallotsDlg.setLocationRelativeTo(null);

            createBallotsDlg.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);

                    mainForm.safeClose(createBallotsDlg, createScenario.class, c);
                }
            });
            createBallotsDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            createBallotsDlg.setVisible(true); // BLOCKING CALL !!!
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

    public String saveChanges() {
        if (label2.getText().contains("Alert")) {
            JOptionPane.showMessageDialog(this, "Cannot save changes, because there are active alerts.", "Error", JOptionPane.OK_OPTION);
            return null;
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

            FileWriter file = new FileWriter(filePath);

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
        saveChanges();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
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

        //---- label1 ----
        label1.setText("Start election");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 9f));

        //---- createBtn ----
        createBtn.setText("Create scenario");
        createBtn.addActionListener(e -> createBtn(e));

        //======== scrollPane1 ========
        {

            //---- table1 ----
            table1.addPropertyChangeListener("table1", e -> table1PropertyChange(e));
            scrollPane1.setViewportView(table1);
        }

        //---- addBtn ----
        addBtn.setText("Add candidate +");
        addBtn.addActionListener(e -> addBtn(e));

        //---- label2 ----
        label2.setText("tooltip");
        label2.setVerticalAlignment(SwingConstants.TOP);
        label2.setHorizontalAlignment(SwingConstants.LEFT);

        //---- remBtn ----
        remBtn.setText("Remove candidate -");
        remBtn.addActionListener(e -> remBtn(e));

        //---- exportBtn ----
        exportBtn.setText("Save election");
        exportBtn.addActionListener(e -> exportBtn(e));

        //---- label3 ----
        label3.setText("Election name : ");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(label2, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                            .addGap(12, 12, 12)
                            .addComponent(exportBtn, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label3)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(electionNameBox, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(label1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(remBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label3)
                        .addComponent(electionNameBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 492, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                            .addComponent(exportBtn, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
                        .addComponent(label2, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
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
