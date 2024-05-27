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
    public boolean darktheme = true;
    public static JDialog createBallotsDlg;
    public mainForm() {
        initComponents();
        darkThemeBtn.setSelected(true);
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

    private void safeClose(JDialog form, Class formClass, Object formObj) {
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

    private void createScenarioBtn(ActionEvent e) {
        openCandidatesForm(null);
    }

    private void darkThemeBtn(ActionEvent e) {
        if (darktheme) {
            darkThemeBtn.setSelected(true);
            return;
        }
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            com.formdev.flatlaf.FlatLaf.updateUI();
            lightThemeBtn.setSelected(false);
            darktheme = true;
        } catch (Exception ignored) {}
    }

    private void lightThemeBtn(ActionEvent e) {
        if (!darktheme) {
            lightThemeBtn.setSelected(true);
            return;
        }
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            com.formdev.flatlaf.FlatLaf.updateUI();
            darkThemeBtn.setSelected(false);
            darktheme = false;
        } catch (Exception ignored) {}
    }

    private void loadScenarioBtn(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("CSV File","csv");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String fileAbsolutePath = file.getAbsolutePath();

            createBallotsDlg = new JDialog(Main.mainFrame, "Create ballots", true);
            createScenario c = new createScenario(fileAbsolutePath);
            createBallotsDlg.setContentPane(c);
            createBallotsDlg.pack();
            createBallotsDlg.setLocationRelativeTo(null);

            createBallotsDlg.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);

                    safeClose(createBallotsDlg, createScenario.class, c);
                }
            });
            createBallotsDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            createBallotsDlg.setVisible(true); // BLOCKING CALL
        }
    }

    private void openCandidatesForm(File inFile) {
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


        if (inputCandidates.success) {
            createBallotsDlg = new JDialog(Main.mainFrame, "Create ballots", true);
            createScenario c = new createScenario();
            createBallotsDlg.setContentPane(c);
            createBallotsDlg.pack();
            createBallotsDlg.setLocationRelativeTo(null);

            createBallotsDlg.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);

                    safeClose(createBallotsDlg, createScenario.class, c);
                }
            });
            createBallotsDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            createBallotsDlg.setVisible(true); // BLOCKING CALL !!!
        }
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        createScenarioBtn = new JButton();
        loadScenarioBtn = new JButton();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        darkThemeBtn = new JRadioButton();
        lightThemeBtn = new JRadioButton();
        loadCandidatesBtn = new JButton();
        label6 = new JLabel();
        label7 = new JLabel();

        //======== this ========

        //---- createScenarioBtn ----
        createScenarioBtn.setIcon(null);
        createScenarioBtn.setText("+");
        createScenarioBtn.setFont(createScenarioBtn.getFont().deriveFont(createScenarioBtn.getFont().getStyle() | Font.BOLD, createScenarioBtn.getFont().getSize() + 15f));
        createScenarioBtn.setVerticalAlignment(SwingConstants.TOP);
        createScenarioBtn.addActionListener(e -> createScenarioBtn(e));

        //---- loadScenarioBtn ----
        loadScenarioBtn.setText("\ud83d\udcc1");
        loadScenarioBtn.setFont(loadScenarioBtn.getFont().deriveFont(loadScenarioBtn.getFont().getStyle() | Font.BOLD, loadScenarioBtn.getFont().getSize() + 15f));
        loadScenarioBtn.addActionListener(e -> loadScenarioBtn(e));

        //---- label2 ----
        label2.setText("Create a voting scenario");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 13f));

        //---- label3 ----
        label3.setText("Load scenario");
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 13f));

        //---- label4 ----
        label4.setText("<html> <p align=\"justify\"> Create an election scenario with kronos by entering your desired candidates, and then create virtual ballots to determine the winner. </p></html>");
        label4.setHorizontalAlignment(SwingConstants.LEFT);
        label4.setVerticalAlignment(SwingConstants.TOP);
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 3f));

        //---- label5 ----
        label5.setText("<html> <p align=\"justify\"> Load a pre-made scenario that contains candidates and their virtual ballots. Scenarios have .CSV and .XLSX formats. </p> </html>");
        label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() + 3f));
        label5.setVerticalAlignment(SwingConstants.TOP);

        //---- darkThemeBtn ----
        darkThemeBtn.setText("Dark theme");
        darkThemeBtn.addActionListener(e -> darkThemeBtn(e));

        //---- lightThemeBtn ----
        lightThemeBtn.setText("Light theme");
        lightThemeBtn.addActionListener(e -> lightThemeBtn(e));

        //---- loadCandidatesBtn ----
        loadCandidatesBtn.setText("\ud83d\udcc1");
        loadCandidatesBtn.setFont(loadCandidatesBtn.getFont().deriveFont(loadCandidatesBtn.getFont().getStyle() | Font.BOLD, loadCandidatesBtn.getFont().getSize() + 15f));
        loadCandidatesBtn.addActionListener(e -> loadCandidatesBtn(e));

        //---- label6 ----
        label6.setText("Load candidate list");
        label6.setFont(label6.getFont().deriveFont(label6.getFont().getSize() + 13f));
        label6.setHorizontalAlignment(SwingConstants.LEFT);
        label6.setVerticalAlignment(SwingConstants.TOP);

        //---- label7 ----
        label7.setText("<html> <p align=\"justify\"> Load a pre-made list that contains the names of candidates. You can then use those names to create election scenarios. </p> </html>");
        label7.setFont(label7.getFont().deriveFont(label7.getFont().getSize() + 3f));
        label7.setVerticalAlignment(SwingConstants.TOP);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(loadScenarioBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label3, GroupLayout.PREFERRED_SIZE, 422, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(loadCandidatesBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(label6, GroupLayout.PREFERRED_SIZE, 422, GroupLayout.PREFERRED_SIZE))
                                .addComponent(label5, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(label4, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(createScenarioBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)))
                        .addComponent(label7, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(291, Short.MAX_VALUE)
                    .addComponent(darkThemeBtn)
                    .addGap(18, 18, 18)
                    .addComponent(lightThemeBtn)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(createScenarioBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loadScenarioBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label3, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label5, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(loadCandidatesBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addComponent(label6, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label7, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                    .addGap(17, 17, 17)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(darkThemeBtn)
                        .addComponent(lightThemeBtn))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JButton createScenarioBtn;
    private JButton loadScenarioBtn;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JRadioButton darkThemeBtn;
    private JRadioButton lightThemeBtn;
    private JButton loadCandidatesBtn;
    private JLabel label6;
    private JLabel label7;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
