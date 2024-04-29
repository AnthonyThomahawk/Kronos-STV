/*
 * Created by JFormDesigner on Sat Apr 20 07:43:43 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Locale;
import java.util.ResourceBundle;

public class inputCandidates extends JPanel {
    public static int candidateCount = 1;
    public static String[] candidates = {};
    public static boolean success = false;
    public inputCandidates() {
        candidateCount = 1;
        initComponents();
        //initLocale();
        initTable();
        success = false;
    }

    public static void setCellColorEmpty(JTable t, Color color, String columnName) {
        t.getColumn(columnName).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        setText(value.toString());
                        setHorizontalAlignment(CENTER);

                        if (value.toString().equals("")) {
                            setBackground(color);
                        } else {
                            setBackground(table.getBackground());
                        }
                        return this;
                    }
                }
        );
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
        table1.setRowSelectionAllowed(false);
        table1.getTableHeader().setReorderingAllowed(false);

        //setCellColor(table1, Color.RED, "Candidate name", 0, 1);
    }

    private void initLocale() {
        Locale currentLocale;

        currentLocale = Locale.ENGLISH;

        //currentLocale = new Locale("gr", "GR");

        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale, new UTF8Control());

        button1.setText(messages.getString("create"));
    }

    private boolean checkData() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
        int rows = dtm.getRowCount();
        for (int i = 0; i < rows; i++) {
            if (dtm.getValueAt(i, 1).equals("")) {
                setCellColorEmpty(table1, Color.RED, "Candidate name");
                table1.repaint();
                JOptionPane.showMessageDialog(null, "All candidates must have names.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (rows <= 1) {
            JOptionPane.showMessageDialog(null, "Candidates must be more than 1.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

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

    private void button1(ActionEvent e) {
        if (!checkData())
            return;

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
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        button1 = new JButton();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        button2 = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText("Enter candidates");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 12f));

        //---- button1 ----
        button1.setText("Create");
        button1.addActionListener(e -> button1(e));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- button2 ----
        button2.setText("Add +");
        button2.addActionListener(e -> button2(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(button1))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                            .addComponent(button2))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label1)
                        .addComponent(button2))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(button1)
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
