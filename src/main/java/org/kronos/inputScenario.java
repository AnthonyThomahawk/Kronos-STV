/*
 * Created by JFormDesigner on Sat Apr 20 07:43:43 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Enterprise
 */
public class inputScenario extends JPanel {
    private int candidates = 1;
    public inputScenario() {
        initComponents();
        initLocale();
        initTable();
        spinner1.setValue(1);
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
    }

    private void initLocale() {
        Locale currentLocale;

        currentLocale = Locale.ENGLISH;

        //currentLocale = new Locale("gr", "GR");

        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale, new UTF8Control());

        label1.setText(messages.getString("createscenariobtn"));
        label3.setText(messages.getString("votercount"));
        button1.setText(messages.getString("create"));
    }

    private void button1(ActionEvent e) {
        try {
            mainForm.inputDlg.dispose();
        } catch (Exception ignored) {
        }

    }

    private void button2(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        candidates++;
        model.addRow(new Object[]{candidates, ""});
    }

    private void spinner1StateChanged(ChangeEvent e) {
        if ((Integer)spinner1.getValue() == 0) {
            spinner1.setValue(1);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        label3 = new JLabel();
        button1 = new JButton();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        button2 = new JButton();
        spinner1 = new JSpinner();

        //======== this ========

        //---- label1 ----
        label1.setText("Create new scenario");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 12f));

        //---- label3 ----
        label3.setText("Voter count");

        //---- button1 ----
        button1.setText("Create");
        button1.addActionListener(e -> button1(e));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- button2 ----
        button2.setText("Add candidate");
        button2.addActionListener(e -> button2(e));

        //---- spinner1 ----
        spinner1.addChangeListener(e -> spinner1StateChanged(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(0, 110, Short.MAX_VALUE)
                            .addComponent(label3)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button2)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button1))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addGap(0, 218, Short.MAX_VALUE))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 445, GroupLayout.PREFERRED_SIZE)
                    .addGap(28, 28, 28)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button1)
                            .addComponent(button2))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JLabel label3;
    private JButton button1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton button2;
    private JSpinner spinner1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
