/*
 * Created by JFormDesigner on Sat Apr 20 06:38:13 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

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

    private void button2() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("CSV File","csv");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileAbsolutePath = file.getAbsolutePath();

            createBallotsDlg = new JDialog(Main.mainFrame, "Create ballots", true);
            createScenario c = new createScenario(fileAbsolutePath);
            createBallotsDlg.setContentPane(c);
            createBallotsDlg.pack();
            createBallotsDlg.setLocationRelativeTo(null);
            createBallotsDlg.setVisible(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        button1 = new JButton();
        button2 = new JButton();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();

        //======== this ========

        //---- button1 ----
        button1.setIcon(null);
        button1.setText("+");
        button1.setFont(button1.getFont().deriveFont(button1.getFont().getStyle() | Font.BOLD, button1.getFont().getSize() + 15f));
        button1.setVerticalAlignment(SwingConstants.TOP);
        button1.addActionListener(e -> button1(e));

        //---- button2 ----
        button2.setText("\ud83d\udcc1");
        button2.setFont(button2.getFont().deriveFont(button2.getFont().getStyle() | Font.BOLD, button2.getFont().getSize() + 15f));
        button2.addActionListener(e -> {
			button2();
		});

        //---- label2 ----
        label2.setText("Create a voting scenario");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 13f));

        //---- label3 ----
        label3.setText("Load scenario");
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 13f));

        //---- label4 ----
        label4.setText("<html> <p align=\"justify\"> Create a voting scenario with kronos by entering your desired candidates, and then create virtual ballots to determine the winner. </p></html>");
        label4.setHorizontalAlignment(SwingConstants.LEFT);
        label4.setVerticalAlignment(SwingConstants.TOP);
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 3f));

        //---- label5 ----
        label5.setText("<html> <p align=\"justify\"> Load a pre-made scenario that contains candidates and their virtual ballots. Scenarios have .CSV and .XLSX formats. </p> </html>");
        label5.setVerticalAlignment(SwingConstants.TOP);
        label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() + 3f));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(label4, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(button1, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(button2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label3, GroupLayout.PREFERRED_SIZE, 422, GroupLayout.PREFERRED_SIZE))
                        .addComponent(label5, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(button1, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(button2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(27, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JButton button1;
    private JButton button2;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
