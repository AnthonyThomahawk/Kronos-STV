/*
 * Created by JFormDesigner on Sun Oct 20 16:27:38 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;

/**
 * @author Worker
 */
public class createGroups extends JPanel {
    public ArrayList<String> groupNames;
    public ArrayList<Integer> groupCandidates;
    public String[] candidates;
    public ArrayList<JComboBox> groupBoxes;

    public createGroups(ArrayList<String> gN, ArrayList<Integer> gC, String[] c) {
        groupNames = gN;
        groupCandidates = gC;
        candidates = c;
        initComponents();
        initTables();
    }

    private void initTables() {
        if (groupNames == null) {
            groupNames = new ArrayList<>();
        }

        if (groupCandidates == null) {
            groupCandidates = new ArrayList<>();
        }

        DefaultTableModel dtm1 = (DefaultTableModel) table1.getModel();
        dtm1.addColumn("Group");

        if (!groupNames.isEmpty()) {
            for (String s : groupNames) {
                dtm1.addRow(new Object[]{s});
            }
        }

        String[] opts;

        if (groupNames == null || groupNames.isEmpty()) {
            opts = new String[1];
            opts[0] = "No group";
        } else {
            opts = (String[]) Arrays.copyOf(groupNames.toArray(), candidates.length+1);
            opts[candidates.length] = "No group";
        }

        groupBoxes = new ArrayList<>();
        int i = 0;
        for (String s : candidates) {
            groupBoxes.add(new JComboBox(opts));
            groupBoxes.get(i).setSelectedItem("No group");
            i++;
        }

        table2 = new JTable() {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 1){
                    return new DefaultCellEditor(groupBoxes.get(row));
                }
                return super.getCellEditor(row, column);
            }
            @Override
            public boolean editCellAt(int row, int column, EventObject e)
            {
                boolean result = super.editCellAt(row, column, e);
                final Component editor = getEditorComponent();

                if (editor instanceof JTextField) {
                    ((JTextField) editor).setText("");
                }

                return result;
            }
        };

        scrollPane2.setViewportView(table2);

        DefaultTableModel model = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return column != 0;
            }
        };

        table2.setModel(model);

        DefaultTableModel dtm2 = (DefaultTableModel) table2.getModel();
        dtm2.addColumn("Candidate");
        dtm2.addColumn("Group");

        for (String s : candidates) {
            dtm2.addRow(new Object[]{s, "No group"});
        }

        table1.setShowVerticalLines(true);
        table1.setShowHorizontalLines(true);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(true);
        table1.getTableHeader().setReorderingAllowed(false);

        table2.setShowVerticalLines(true);
        table2.setShowHorizontalLines(true);
        table2.setColumnSelectionAllowed(false);
        table2.setRowSelectionAllowed(true);
        table2.getTableHeader().setReorderingAllowed(false);
    }

    private boolean updateStatus() {
        if (table1.getRowCount() == 0) {
            label1.setText("<html>" + "<b> Alert : </b>" +
                    "<br> <b style=\"color:RED;\">There are no Groups.</b>" +"</html>");
            table2.setEnabled(false);
            return false;
        }

        DefaultTableModel dtm1 = (DefaultTableModel) table1.getModel();

        for (int i = 0; i < dtm1.getRowCount(); i++) {
            String s = (String) dtm1.getValueAt(i, 0);
            if (s.isEmpty()) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Group name " + i + " is empty.</b>" +"</html>");
                table2.setEnabled(false);
                return false;
            }
            if (nameChecks.isPersonNameValid(s)) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Group name " + i + " contains special characters.</b>" +"</html>");
                table2.setEnabled(false);
                return false;
            }
        }

        label1.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");

        table2.setEnabled(true);
        return true;
    }


    private void addBtn(ActionEvent e) {
        DefaultTableModel dtm1 = (DefaultTableModel) table1.getModel();
        dtm1.addRow(new Object[]{});
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        addBtn = new JButton();
        remBtn = new JButton();
        scrollPane2 = new JScrollPane();
        table2 = new JTable();
        label1 = new JLabel();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- addBtn ----
        addBtn.setText("Add group");
        addBtn.addActionListener(e -> addBtn(e));

        //---- remBtn ----
        remBtn.setText("Remove group(s)");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(table2);
        }

        //---- label1 ----
        label1.setText("Alerts");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(addBtn)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(remBtn))
                                .addComponent(label1))
                            .addGap(0, 259, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(remBtn)
                        .addComponent(addBtn, GroupLayout.Alignment.TRAILING))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 299, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton addBtn;
    private JButton remBtn;
    private JScrollPane scrollPane2;
    private JTable table2;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
