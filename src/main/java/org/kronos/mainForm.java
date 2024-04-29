/*
 * Created by JFormDesigner on Sat Apr 20 06:38:13 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Enterprise
 */
public class mainForm extends JPanel {
    public static JDialog inputCandidatesDlg;
    public static JDialog createBallotsDlg;
    public mainForm() {
        initComponents();
        //initLocale();

        //label3.setText("<html>" + "Create a voting scenario with Kronos by entering all the available candidates, and then add the ballots of the voters. You will then be able to adjust all the ballot permutations and find out which candidate gets elected." + "</html>");
    }

    private void initLocale() {
        Locale currentLocale;

        currentLocale = Locale.ENGLISH;

        currentLocale = new Locale("gr", "GR");

        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale, new UTF8Control());
        //label4.setText("<html>" + messages.getString("createscenariobtn") + "</html>");
        //label5.setText("<html>" + messages.getString("loadscenariobtn") + "</html>");
    }

    private void button1(ActionEvent e) {
        inputCandidatesDlg = new JDialog(Main.mainFrame, "", true);
        inputCandidates f = new inputCandidates();
        inputCandidatesDlg.setContentPane(f);
        inputCandidatesDlg.pack();
        inputCandidatesDlg.setLocationRelativeTo(null);
        inputCandidatesDlg.setVisible(true);

        if (inputCandidates.success) {
            createBallotsDlg = new JDialog(Main.mainFrame, "Create ballots", true);
            createScenario c = new createScenario();
            createBallotsDlg.setContentPane(c);
            createBallotsDlg.pack();
            createBallotsDlg.setLocationRelativeTo(null);
            createBallotsDlg.setVisible(true);
        }
    }

    private void button2(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        button1 = new JButton();
        button2 = new JButton();
        label2 = new JLabel();
        label3 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText("Kronos STV");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 12f));

        //---- button1 ----
        button1.setIcon(null);
        button1.setText("+");
        button1.setFont(button1.getFont().deriveFont(button1.getFont().getStyle() | Font.BOLD, button1.getFont().getSize() + 9f));
        button1.addActionListener(e -> button1(e));

        //---- button2 ----
        button2.setText("\ud83d\udcc1");
        button2.setFont(button2.getFont().deriveFont(button2.getFont().getSize() + 9f));
        button2.addActionListener(e -> button2(e));

        //---- label2 ----
        label2.setText("Create a voting scenario");

        //---- label3 ----
        label3.setText("Load scenario");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(button1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label2, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label1)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(button2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 5, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(button1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(button2)
                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JButton button1;
    private JButton button2;
    private JLabel label2;
    private JLabel label3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
