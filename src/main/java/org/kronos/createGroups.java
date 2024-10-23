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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Vector;

/**
 * @author Worker
 */
public class createGroups extends JPanel {
    public ArrayList<String> groupNames;
    public ArrayList<Integer> groupCandidates;
    public String[] candidates;
    public ArrayList<JComboBox> groupBoxes;
    public boolean status;

    public createGroups(ArrayList<String> gN, ArrayList<Integer> gC, String[] c) {
        groupNames = gN;
        groupCandidates = gC;
        candidates = c;
        initComponents();
        initDeselectAreas();
        initTables();
    }

    private void initDeselectAreas() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (table1.isEditing()) {
                    table1.getCellEditor().stopCellEditing();
                }
                if (table2.isEditing()) {
                    table2.getCellEditor().stopCellEditing();
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

        scrollPane1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (table1.isEditing()) {
                    table1.getCellEditor().stopCellEditing();
                }
                if (table2.isEditing()) {
                    table2.getCellEditor().stopCellEditing();
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

        scrollPane2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (table1.isEditing()) {
                    table1.getCellEditor().stopCellEditing();
                }
                if (table2.isEditing()) {
                    table2.getCellEditor().stopCellEditing();
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
    }

    private Object[] makeOptsArr(ArrayList<String> gn) {
        Object[] opts;

        if (gn == null || gn.isEmpty()) {
            opts = new String[1];
            opts[0] = "No group";
        } else {
            opts = Arrays.copyOf(gn.toArray(), gn.size()+1);
            opts[gn.size()] = "No group";
        }

        return opts;
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

        Object[] opts = makeOptsArr(groupNames);

        groupBoxes = new ArrayList<>();
        for (int i = 0; i < candidates.length; i++) {
            groupBoxes.add(new JComboBox(opts));
            if (groupCandidates.isEmpty())
                groupBoxes.get(i).setSelectedItem("No group");
            else
                groupBoxes.get(i).setSelectedIndex(groupCandidates.get(i));
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

        for (int i = 0; i < candidates.length; i++) {
            if (groupCandidates.isEmpty() || groupNames.isEmpty() || groupCandidates.get(i) >= groupNames.size() || groupCandidates.get(i) == -1) {
                dtm2.addRow(new Object[]{candidates[i], "No group"});
            } else {
                dtm2.addRow(new Object[]{candidates[i], groupNames.get(groupCandidates.get(i))});
            }
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

        updateStatus();

        table1.getModel().addTableModelListener(e -> {
            status = updateStatus();
            if (status) {
                updateGroups();
                updateGroupCandidates();
            }
        });

        table2.getModel().addTableModelListener(e -> updateGroupCandidates());

        status = true;
    }

    private void updateGroupCandidates() {
        groupCandidates = new ArrayList<>();
        for (JComboBox j : groupBoxes) {
            if (j.getSelectedIndex() < groupNames.size())
                groupCandidates.add(j.getSelectedIndex());
            else
                groupCandidates.add(-1);
        }
    }

    private void updateGroups() {
        ArrayList<String> newGroups = new ArrayList<>();
        for (int i = 0; i < table1.getRowCount(); i++) {
            newGroups.add((String) table1.getValueAt(i, 0));
        }

        int[] oldSelIndexes = new int[candidates.length];

        for (int i = 0; i < candidates.length; i++) {
            oldSelIndexes[i] = groupBoxes.get(i).getSelectedIndex();
        }

        Object[] newOpts = makeOptsArr(newGroups);

        for (int i = 0; i < candidates.length; i++) {
            groupBoxes.set(i, new JComboBox(newOpts));
            try {
                if (newGroups.contains(groupNames.get(oldSelIndexes[i])))
                    groupBoxes.get(i).setSelectedItem(groupNames.get(oldSelIndexes[i]));
                else
                    groupBoxes.get(i).setSelectedItem("No group");
            } catch (IndexOutOfBoundsException iex) {
                groupBoxes.get(i).setSelectedItem("No group");
            }

            table2.setValueAt(groupBoxes.get(i).getSelectedItem(), i, 1);
        }

        groupNames = new ArrayList<>();

        groupNames.addAll(newGroups);
    }

    private boolean updateStatus() {
        if (table2.isEditing()) {
            table2.getCellEditor().stopCellEditing();
        }

        if (table1.getRowCount() == 0) {
            label1.setText("<html>" + "<b> Status : </b>" +
                    "<br> <b style=\"color:BLUE;\">There are no Groups.</b>" +"</html>");
            table2.setEnabled(false);
            table2.setBorder(null);
            return true;
        }

        DefaultTableModel dtm1 = (DefaultTableModel) table1.getModel();

        for (int i = 0; i < dtm1.getRowCount(); i++) {
            String s = (String) dtm1.getValueAt(i, 0);
            if (s == null || s.isEmpty()) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Group name " + (i+1) + " is empty.</b>" +"</html>");
                table2.setEnabled(false);
                table2.setBorder(BorderFactory.createLineBorder(Color.RED));
                return false;
            }
            if (nameChecks.isPersonNameValid(s)) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Group name " + (i+1) + " contains special characters.</b>" +"</html>");
                table2.setEnabled(false);
                table2.setBorder(BorderFactory.createLineBorder(Color.RED));
                return false;
            }
            if (s.toLowerCase().contains("no group")) {
                label1.setText("<html>" + "<b> Alert : </b>" +
                        "<br> <b style=\"color:RED;\">Group name " + (i+1) + " is not allowed.</b>" +"</html>");
                table2.setEnabled(false);
                table2.setBorder(BorderFactory.createLineBorder(Color.RED));
                return false;
            }
        }

        for (int i = 0; i < dtm1.getRowCount(); i++) {
            for (int j = 0; j < dtm1.getRowCount(); j++) {
                if (i == j)
                    continue;
                if (dtm1.getValueAt(i,0).equals(dtm1.getValueAt(j,0))) {
                    label1.setText("<html>" + "<b> Alert : </b>" +
                            "<br> <b style=\"color:RED;\">Group names " + (i+1) + " and " + (j+1) + " cannot be the same.</b>" +"</html>");
                    table2.setEnabled(false);
                    table2.setBorder(BorderFactory.createLineBorder(Color.RED));
                    return false;
                }
            }
        }

        label1.setText("<html>" + "<b> Status : </b>" +
                "<br> <b style=\"color:GREEN;\">OK</b>" +"</html>");

        table2.setEnabled(true);
        table2.setBorder(null);
        return true;
    }


    private void addBtn(ActionEvent e) {
        if (table1.isEditing())
            table1.getCellEditor().stopCellEditing();

        if (table2.isEditing())
            table2.getCellEditor().stopCellEditing();

        DefaultTableModel dtm1 = (DefaultTableModel) table1.getModel();
        dtm1.addRow((Vector) null);

        table1.requestFocus();
        table1.editCellAt(table1.getRowCount()-1, 0);
    }

    private void remBtn(ActionEvent e) {
        DefaultTableModel dtm1 = (DefaultTableModel) table1.getModel();
        for (int i : table1.getSelectedRows()) {
            dtm1.removeRow(i);
        }
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
        remBtn.addActionListener(e -> remBtn(e));

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
