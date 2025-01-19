/*
 * Created by JFormDesigner on Sun Jan 19 05:42:42 EET 2025
 */

package org.kronos;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * @author Worker
 */
public class manageVars extends JPanel {
    public ArrayList<Boolean> isVariableFriendly;
    public ArrayList<String> variables;

    public manageVars(ArrayList<Boolean> in, ArrayList<String> in2) {
        isVariableFriendly = in;
        variables = in2;
        initComponents();

        if (isVariableFriendly == null || isVariableFriendly.isEmpty() || isVariableFriendly.size() != variables.size()) {
            isVariableFriendly = new ArrayList<>();

            for (int i = 0; i < variables.size(); i++) {
                isVariableFriendly.add(true);
            }
        }

        table1.setModel(new DefaultTableModel() {
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

        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();

        dtm.addColumn("Variable name");
        dtm.addColumn("Is friendly?");

        for (int i = 0; i < variables.size(); i++) {
            if (isVariableFriendly.get(i)) {
                dtm.addRow(new Object[]{variables.get(i), Boolean.TRUE});
            } else {
                dtm.addRow(new Object[]{variables.get(i), Boolean.FALSE});
            }
        }

        table1.setShowVerticalLines(true);
        table1.setShowHorizontalLines(true);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(false);
        table1.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );

        table1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    }

    public void updateVariables() {
        DefaultTableModel dtm = (DefaultTableModel) table1.getModel();

        for (int i = 0; i < dtm.getRowCount(); i++) {
            isVariableFriendly.set(i, (Boolean) dtm.getValueAt(i, 1));
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        table1 = new JTable();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 369, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(10, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JTable table1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
