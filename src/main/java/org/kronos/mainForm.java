/*
 * Created by JFormDesigner on Sat Apr 20 06:38:13 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public static void safeClose(JDialog form, Class formClass, Object formObj) {
        try {
            Field unsavedField = formClass.getDeclaredField("unsaved");
            boolean unsaved = unsavedField.getBoolean(null);
            if (!unsaved) {
                form.dispose();
                return;
            }
        } catch (Exception e) {
            return;
        }


        int res = JOptionPane.showConfirmDialog(null, "Save unsaved changes?", "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                Method saveChanges = formObj.getClass().getMethod("saveChanges");
                saveChanges.invoke(formObj);
            } catch (Exception e) {
                return;
            }
        } else if (res == JOptionPane.NO_OPTION){
            form.dispose();
        }
    }

    private void startElectionBtn(ActionEvent e) {
        openCandidatesForm(null);
    }

    private void loadElectionBtn(ActionEvent e) {
        JDialog j = new JDialog(Main.mainFrame, "Load election", true);
        electionLoad el = new electionLoad();
        j.setContentPane(el);
        j.pack();
        j.setLocationRelativeTo(null);
        j.setVisible(true);
    }

    public static void openCandidatesForm(File inFile) {
        inputCandidatesDlg = new JDialog(Main.mainFrame, "", true);

        inputCandidates f;
        if (inFile != null) f = new inputCandidates(inFile);
        else f = new inputCandidates();

        inputCandidatesDlg.setContentPane(f);
        inputCandidatesDlg.pack();
        inputCandidatesDlg.setLocationRelativeTo(null);
        inputCandidatesDlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                safeClose(inputCandidatesDlg, inputCandidates.class, f);
            }
        });
        inputCandidatesDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


        inputCandidatesDlg.setVisible(true); // BLOCKING CALL!!!
    }

    private void loadCandidatesBtn(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Text File","txt");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            openCandidatesForm(file);
        }
    }

    private void settingsBtn(ActionEvent e) {
        JDialog settings = new JDialog(Main.mainFrame, "", true);
        SettingsUI settingsPane = new SettingsUI();
        settings.setContentPane(settingsPane);
        settings.pack();
        settings.setLocationRelativeTo(null);
        settings.setVisible(true);
        settings.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        startElectionBtn = new JButton();
        loadElectionBtn = new JButton();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        loadCandidatesBtn = new JButton();
        label6 = new JLabel();
        label7 = new JLabel();
        settingsBtn = new JButton();

        //======== this ========

        //---- startElectionBtn ----
        startElectionBtn.setIcon(null);
        startElectionBtn.setText("+");
        startElectionBtn.setFont(startElectionBtn.getFont().deriveFont(startElectionBtn.getFont().getStyle() | Font.BOLD, startElectionBtn.getFont().getSize() + 15f));
        startElectionBtn.setVerticalAlignment(SwingConstants.TOP);
        startElectionBtn.addActionListener(e -> startElectionBtn(e));

        //---- loadElectionBtn ----
        loadElectionBtn.setText("\ud83d\udcc1");
        loadElectionBtn.setFont(loadElectionBtn.getFont().deriveFont(loadElectionBtn.getFont().getStyle() | Font.BOLD, loadElectionBtn.getFont().getSize() + 15f));
        loadElectionBtn.addActionListener(e -> loadElectionBtn(e));

        //---- label2 ----
        label2.setText("Start an election");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 13f));

        //---- label3 ----
        label3.setText("Load an election");
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 13f));

        //---- label4 ----
        label4.setText("<html> <p align=\"justify\">Create a virtual election in Kronos that can be then used to create scenarios</p></html>");
        label4.setHorizontalAlignment(SwingConstants.LEFT);
        label4.setVerticalAlignment(SwingConstants.TOP);
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 3f));

        //---- label5 ----
        label5.setText("<html> <p align=\"justify\"> Load a pre-made election, and then create scenarios based on it.</p> </html>");
        label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() + 3f));
        label5.setVerticalAlignment(SwingConstants.TOP);

        //---- loadCandidatesBtn ----
        loadCandidatesBtn.setText("\ud83d\udcc1");
        loadCandidatesBtn.setFont(loadCandidatesBtn.getFont().deriveFont(loadCandidatesBtn.getFont().getStyle() | Font.BOLD, loadCandidatesBtn.getFont().getSize() + 15f));
        loadCandidatesBtn.addActionListener(e -> loadCandidatesBtn(e));

        //---- label6 ----
        label6.setText("Load a scenario");
        label6.setFont(label6.getFont().deriveFont(label6.getFont().getSize() + 13f));

        //---- label7 ----
        label7.setText("<html> <p align=\"justify\"> Load a pre-made scenario and change it to your liking. </p> </html>");
        label7.setFont(label7.getFont().deriveFont(label7.getFont().getSize() + 3f));
        label7.setVerticalAlignment(SwingConstants.TOP);

        //---- settingsBtn ----
        settingsBtn.setText("\u2699");
        settingsBtn.setFont(settingsBtn.getFont().deriveFont(settingsBtn.getFont().getSize() + 11f));
        settingsBtn.addActionListener(e -> settingsBtn(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(settingsBtn))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(loadElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, 422, GroupLayout.PREFERRED_SIZE))
                                .addComponent(label5, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(startElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(loadCandidatesBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(label6, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(label7, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(startElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loadElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label3, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loadCandidatesBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label6, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addComponent(label7, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(settingsBtn)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JButton startElectionBtn;
    private JButton loadElectionBtn;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JButton loadCandidatesBtn;
    private JLabel label6;
    private JLabel label7;
    private JButton settingsBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
