/*
 * Created by JFormDesigner on Mon Apr 29 15:57:32 EEST 2024
 */

package org.kronos;

import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * @author Enterprise
 */
public class resultForm extends JPanel {
    public resultForm() {
        initComponents();
        initTable();
    }

    private void initTable() {
        table1.setModel(new DefaultTableModel(new Object[][] {
                null
        }, new String[]{"Rank #", "Elected candidate", "Votes"})
        {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

        table1.setShowVerticalLines(true);
        table1.setShowHorizontalLines(true);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(false);
        table1.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );

        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        populateTable();
    }

    private void populateTable() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.removeRow(0);
        for (int i = 1; i <= createScenario.electionResults.lastRank; i++) {
            Object[] row = new Object[3];
            row[0] = i;
            row[1] = createScenario.electionResults.getElected(i);
            row[2] = createScenario.electionResults.getVotes(i);
            model.addRow(row);
        }

    }

    private void exportResultsCSV(File f) throws IOException {
        String path = f.getAbsolutePath();

        if (!path.endsWith(".csv"))
            path += ".csv";

        File x = new File(path);

        if (x.exists()) {
            int res = JOptionPane.showConfirmDialog(null, "The file you are trying to save already exists. Are you sure you want to overwrite it?", "Existing file", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                return;
            }
        }

        FileWriter out = new FileWriter(path);

        DefaultTableModel m = (DefaultTableModel) table1.getModel();

        for (int i = 0; i < m.getRowCount(); i++) {
            String []r = new String[3];

            r[0] = Integer.toString((Integer) m.getValueAt(i, 0));
            r[1] = (String) m.getValueAt(i, 1);
            r[2] = Float.toString((Float) m.getValueAt(i, 2));

            String line = r[0] + "," + r[1] + "," + r[2];
            out.write(line);
            out.write("\r\n");
        }

        out.flush();
        out.close();

        JOptionPane.showMessageDialog(null, "Results exported to : " + f.getAbsolutePath() + ".csv", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void button1(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("CSV File","csv");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                exportResultsCSV(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Cannot write to file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        button1 = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText("Election results");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 15f));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- button1 ----
        button1.setText("Export as CSV");
        button1.addActionListener(e -> button1(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                            .addComponent(button1))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label1)
                        .addComponent(button1))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
