/*
 * Created by JFormDesigner on Thu Aug 15 11:58:09 EEST 2024
 */

package org.kronos;

import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Worker
 */
public class parseErrorForm extends JPanel {
    public parseErrorForm(String scriptOutput) {
        initComponents();
        textArea1.setText(scriptOutput);
    }

    private void deleteFile(String filename) {
        File f = new File(filename);
        f.delete();
    }

    private void repairBtn(ActionEvent e) {
        deleteFile("stv.py");
        deleteFile("loader.py");
        Main.checkSTV();

        JOptionPane.showMessageDialog(null, "Automatic repair completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
        createScenario.fe.dispose();
    }

    private void cancelBtn(ActionEvent e) {
        createScenario.fe.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();
        label1 = new JLabel();
        label2 = new JLabel();
        repairBtn = new JButton();
        cancelBtn = new JButton();
        label3 = new JLabel();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textArea1);
        }

        //---- label1 ----
        label1.setText("Parsing error");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 6f));

        //---- label2 ----
        label2.setText("<html>An error has occured when parsing the results of the election evaluation. Here is the output of the python backend (for debugging purposes) : </html>");

        //---- repairBtn ----
        repairBtn.setText("<html><b>Attempt auto-repair</b></html>");
        repairBtn.addActionListener(e -> repairBtn(e));

        //---- cancelBtn ----
        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(e -> cancelBtn(e));

        //---- label3 ----
        label3.setText("<html>Backend components may be corrupted. Do you want to attempt to automatically repair them?</html>");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane1))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 388, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(76, 76, 76)
                                    .addComponent(repairBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(cancelBtn))
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, 388, GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addGap(140, 140, 140)
                    .addComponent(label1)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelBtn)
                        .addComponent(repairBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JLabel label1;
    private JLabel label2;
    private JButton repairBtn;
    private JButton cancelBtn;
    private JLabel label3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
