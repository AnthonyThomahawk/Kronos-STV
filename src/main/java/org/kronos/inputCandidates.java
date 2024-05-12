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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.TableView;
import java.util.Locale;
import java.util.ResourceBundle;

public class inputCandidates extends JPanel {
    public static int candidateCount = 1;
    public static String[] candidates = {};
    public static boolean success = false;
    public inputCandidates() {
        candidateCount = 1;
        candidates = new String[]{};
        initComponents();
        //initLocale();
        initTable();
        success = false;
    }

    private void initTable() {
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
                updateStatus();
            }
        });
    }

    private void initLocale() {
        Locale currentLocale;

        currentLocale = Locale.ENGLISH;

        //currentLocale = new Locale("gr", "GR");

        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale, new UTF8Control());

        button1.setText(messages.getString("create"));
    }

    private void updateStatus() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();

        // candidate >= 1 count check
        if (rows <= 1) {
            label2.setText("<html><b> Alert : </b><br> <b style=\"color:RED;\">There must be more than 1 candidate.</b></html>");
            button1.setEnabled(false);
            return;
        }

        // candidate name not empty check
        for (int i = 0; i < rows; i++) {
            if (dtm.getValueAt(i, 1).equals("")) {
                label2.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Candidate " + (i+1) + " does not have a name.</b>" +"</html>");
                button1.setEnabled(false);
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
                    button1.setEnabled(false);
                    return;
                }
            }
        }

        label2.setText("<html><b> Status : </b><br> <b style=\"color:GREEN;\">OK</b></html>");
        button1.setEnabled(true);
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

    private void button1(ActionEvent e) {
        candidates = extractDataToString();

        try {
            success = true;
            mainForm.inputCandidatesDlg.dispose();
        } catch (Exception ignored) {
        }

    }

    private void button2(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        candidateCount++;
        model.addRow(new Object[]{candidateCount, ""});
        table1.requestFocus();
        table1.editCellAt(candidateCount-1, 1);
    }

    private void table1PropertyChange(PropertyChangeEvent e) {

    }

    private void button3(ActionEvent e) {
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
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        button1 = new JButton();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        button2 = new JButton();
        label2 = new JLabel();
        button3 = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText("Enter candidates");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 9f));

        //---- button1 ----
        button1.setText("Create");
        button1.addActionListener(e -> button1(e));

        //======== scrollPane1 ========
        {

            //---- table1 ----
            table1.addPropertyChangeListener("table1", e -> table1PropertyChange(e));
            scrollPane1.setViewportView(table1);
        }

        //---- button2 ----
        button2.setText("Add +");
        button2.addActionListener(e -> button2(e));

        //---- label2 ----
        label2.setText("tooltip");
        label2.setVerticalAlignment(SwingConstants.TOP);

        //---- button3 ----
        button3.setText("Remove -");
        button3.addActionListener(e -> button3(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(0, 2, Short.MAX_VALUE)
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 363, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button1))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                            .addComponent(button3)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button2)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label1)
                        .addComponent(button2)
                        .addComponent(button3))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(button1, GroupLayout.Alignment.TRAILING)
                        .addComponent(label2, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JButton button1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton button2;
    private JLabel label2;
    private JButton button3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
