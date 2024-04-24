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
import com.formdev.flatlaf.extras.*;

/**
 * @author Enterprise
 */
public class mainForm extends JPanel {
    public static JDialog inputDlg;
    public mainForm() {
        initComponents();
        initLocale();
    }

    private void initLocale() {
        Locale currentLocale;

        currentLocale = Locale.ENGLISH;

        //currentLocale = new Locale("gr", "GR");

        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale, new UTF8Control());
        label4.setText("<html>" + messages.getString("createscenariobtn") + "</html>");
        label5.setText("<html>" + messages.getString("loadscenariobtn") + "</html>");
    }

    private void button1(ActionEvent e) {
        inputDlg = new JDialog(Main.mainFrame, "", true);
        int c1 = this.getWidth()/2;
        int c2 = this.getHeight()/2;
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width/2)-c1;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height/2)-c2;
        inputDlg.setLocation(x, y);
        inputScenario f = new inputScenario();
        inputDlg.setContentPane(f);
        inputDlg.pack();
        inputDlg.setVisible(true);
    }

    private void button2(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        button1 = new JButton();
        label4 = new JLabel();
        label5 = new JLabel();
        button2 = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText("Kronos STV");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 12f));

        //---- button1 ----
        button1.setIcon(null);
        button1.setText("+");
        button1.setFont(button1.getFont().deriveFont(button1.getFont().getStyle() | Font.BOLD, button1.getFont().getSize() + 50f));
        button1.setVerticalAlignment(SwingConstants.TOP);
        button1.addActionListener(e -> button1(e));

        //---- label4 ----
        label4.setText("create scenario");
        label4.setHorizontalAlignment(SwingConstants.CENTER);
        label4.setVerticalAlignment(SwingConstants.TOP);

        //---- label5 ----
        label5.setText("Load scenario");
        label5.setHorizontalAlignment(SwingConstants.CENTER);
        label5.setVerticalAlignment(SwingConstants.TOP);

        //---- button2 ----
        button2.setText("\ud83d\udcc1");
        button2.setFont(button2.getFont().deriveFont(button2.getFont().getSize() + 50f));
        button2.addActionListener(e -> button2(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(93, 93, 93)
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label1)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(button1, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(99, 99, 99)
                            .addComponent(button2, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(label5, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(label4, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(button1, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(button2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label5, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JButton button1;
    private JLabel label4;
    private JLabel label5;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
