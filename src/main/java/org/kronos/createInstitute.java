/*
 * Created by JFormDesigner on Mon Jul 15 19:02:32 EEST 2024
 */

package org.kronos;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Worker
 */
public class createInstitute extends JPanel {
    public createInstitute() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        label2 = new JLabel();
        textField1 = new JTextField();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        label3 = new JLabel();
        spinner1 = new JSpinner();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        button4 = new JButton();
        label4 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText("alerts");

        //---- label2 ----
        label2.setText("Institute name :");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }

        //---- label3 ----
        label3.setText("Institute quota : ");

        //---- button1 ----
        button1.setText("Add +");

        //---- button2 ----
        button2.setText("Remove -");

        //---- button3 ----
        button3.setText("Save");

        //---- button4 ----
        button4.setText("<html><b>New election</b></html>");

        //---- label4 ----
        label4.setText("<html> <i> * Defines the max number of candidates for each department </i> </html>");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label2)
                                    .addGap(10, 10, 10))
                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(label3)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 82, Short.MAX_VALUE))
                                .addComponent(textField1, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(button1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button3)))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 443, Short.MAX_VALUE)
                            .addComponent(button4, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)))
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
                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label3)
                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 445, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(button1)
                        .addComponent(button2)
                        .addComponent(button3)
                        .addComponent(button4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JLabel label2;
    private JTextField textField1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JLabel label3;
    private JSpinner spinner1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
