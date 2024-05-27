/*
 * Created by JFormDesigner on Sat Apr 20 07:43:43 EEST 2024
 */

package org.kronos;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.TableView;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        unsaved = false;
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

        // candidate >= 1 count check
        if (rows <= 1) {
            label2.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">There must be more than 1 candidate.</b></html>");
            createBtn.setEnabled(false);
            return;
        }

        // candidate name not empty check
        for (int i = 0; i < rows; i++) {
            if (dtm.getValueAt(i, 1).equals("")) {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " does not have a name.</b>" +"</html>");
                createBtn.setEnabled(false);
                return;
            }
        }

        for (int i = 0; i < rows; i++) {
            String d = (String) dtm.getValueAt(i, 1);
            if (Character.isDigit(d.charAt(0))) {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " name cannot start with a digit.</b>" +"</html>");
                createBtn.setEnabled(false);
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
                    return;
                }
            }
        }

        label2.setText("<html><b> Status : </b><br> <b style=\"color:GREEN;\">OK</b></html>");
        createBtn.setEnabled(true);
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
        candidates = extractDataToString();

        try {
            success = true;
            mainForm.inputCandidatesDlg.dispose();
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

    public void saveChanges() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Text File","txt");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileAbsolutePath = file.getAbsolutePath();

            while (file.exists()) {
                int res = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "File Exists", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    break;
                }

                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    fileAbsolutePath = file.getAbsolutePath();
                } else {
                    return;
                }
            }

            try {
                OutputStream outputStream = Files.newOutputStream(Paths.get(fileAbsolutePath));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
                for (int i = 0; i < dtm.getRowCount(); i++) {
                    String val = (String) dtm.getValueAt(i, 1);
                    out.println(val);
                }

                out.flush();
                out.close();
            } catch (Exception e) {
                System.out.println(e);
            }

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

        //======== this ========

        //---- label1 ----
        label1.setText("Enter candidates");
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
        addBtn.setText("Add +");
        addBtn.addActionListener(e -> addBtn(e));

        //---- label2 ----
        label2.setText("tooltip");
        label2.setVerticalAlignment(SwingConstants.TOP);
        label2.setHorizontalAlignment(SwingConstants.LEFT);

        //---- remBtn ----
        remBtn.setText("Remove -");
        remBtn.addActionListener(e -> remBtn(e));

        //---- exportBtn ----
        exportBtn.setText("Export candidates");
        exportBtn.addActionListener(e -> exportBtn(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exportBtn, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addBtn)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(remBtn)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label1)
                        .addComponent(remBtn)
                        .addComponent(addBtn))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(createBtn, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                        .addComponent(exportBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
