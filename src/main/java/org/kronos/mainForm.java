/*
 * Created by JFormDesigner on Sat Apr 20 06:38:13 EEST 2024
 */

package org.kronos;

import jdk.nashorn.internal.scripts.JD;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Enterprise
 */
public class mainForm extends JPanel {
    static inputCandidates inputCandidatesObj;
    public mainForm() {
        initComponents();
        setIcons();
    }

    public void setIcons() {
        String[] iconLocations;

        String[] darkIcons = {"plusIconDark.png", "plusIconDark.png", "darkFolder.png", "darkFolder.png", "gearDark.png", "hammerDark.png"};
        String[] lightIcons = {"plusicon24.png", "plusicon24.png", "icons8-folder-24.png", "icons8-folder-24.png", "gearLight.png", "hammerDark.png"};

        if (Main.getTheme().equals("system")) {
            if (DarkModeDetector.isDarkMode()) {
                iconLocations = darkIcons;
            } else {
                iconLocations = lightIcons;
            }
        }
        else if (Main.getTheme().equals("dark")){
            iconLocations = darkIcons;
        } else {
            iconLocations = lightIcons;
        }

        JButton[] buttons = {newInstituteBtn, startElectionBtn, loadElectionBtn, loadScenarioBtn, settingsBtn, scenarioBuilderBtn};

        try {
            for (int i = 0; i < iconLocations.length; i++) {
                InputStream image = Main.class.getClassLoader().getResourceAsStream(iconLocations[i]);
                BufferedImage bf = ImageIO.read(image);
                buttons[i].setIcon(new ImageIcon(bf));
            }

        } catch(Exception ignored) {}
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


        int res = JOptionPane.showConfirmDialog(null, "Save changes?", "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
        if (res == JOptionPane.CANCEL_OPTION) return;
        if (res == JOptionPane.YES_OPTION) {
            try {
                Method saveChanges = formObj.getClass().getMethod("saveChanges");
                saveChanges.invoke(formObj);
                form.dispose();
            } catch (Exception e) {
                return;
            }
        } else {
            form.dispose();
        }

    }

    private void startElectionBtn(ActionEvent e) {
        String[] opts = {"for Institution (per ward quota)", "General election"};

        int sel = JOptionPane.showOptionDialog(null, "Select election type.", "Election type", 0, 3, null, opts, opts[0]);

        if (sel == 0) {
            JDialog x = new JDialog(Main.mainFrame, "Load institution", true);
            instituteLoad iL = new instituteLoad();
            x.setContentPane(iL);
            x.pack();
            x.setLocationRelativeTo(null);
            if (iL.failed) {
                x.dispose();
            } else {
                x.setVisible(true);
            }
        } else if (sel == 1) {
            openCandidatesForm(null, "New election");
        }
    }

    private void loadElectionBtn(ActionEvent e) {
        JDialog j = new JDialog(Main.mainFrame, "Load election", true);
        electionLoad el = new electionLoad();
        j.setContentPane(el);
        j.pack();
        j.setLocationRelativeTo(null);
        j.setVisible(true);

        if (el.selectedFile != null)
            mainForm.openCandidatesForm(el.selectedFile, "Edit election");
    }

    public static void openInstitutionForm(String title, String f2e) {
        JDialog instituteDlg = new JDialog(Main.mainFrame, title, true);

        instituteDlg.setContentPane(new createInstitute(f2e, instituteDlg));
        instituteDlg.pack();
        instituteDlg.setLocationRelativeTo(null);
        instituteDlg.setVisible(true);
    }

    public static void openCandidatesForm(File inFile, String title) {
        JDialog inputCandidatesDlg = new JDialog(Main.mainFrame, title, true);

        if (inFile != null) inputCandidatesObj = new inputCandidates(inFile);
        else inputCandidatesObj = new inputCandidates(false, null);

        inputCandidatesDlg.setContentPane(inputCandidatesObj);
        inputCandidatesDlg.pack();
        inputCandidatesDlg.setLocationRelativeTo(null);
        inputCandidatesDlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                safeClose(inputCandidatesDlg, inputCandidates.class, inputCandidatesObj);
            }
        });
        inputCandidatesDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        if (inputCandidatesObj.failed)
            inputCandidatesDlg.dispose();
        else
            inputCandidatesDlg.setVisible(true); // BLOCKING CALL!!!
    }

    public static void openDeptCandidatesForm(String dFile, String title) {
        JDialog inputCandidatesDlg = new JDialog(Main.mainFrame, title, true);

        inputCandidatesObj = new inputCandidates(true, dFile);

        inputCandidatesDlg.setContentPane(inputCandidatesObj);
        inputCandidatesDlg.pack();
        inputCandidatesDlg.setLocationRelativeTo(null);
        inputCandidatesDlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                safeClose(inputCandidatesDlg, inputCandidates.class, inputCandidatesObj);
            }
        });
        inputCandidatesDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        if (inputCandidatesObj.failed)
            inputCandidatesDlg.dispose();
        else
            inputCandidatesDlg.setVisible(true); // BLOCKING CALL!!!
    }



    public static void openScenarioForm(File inFile, String title) {
        JDialog createBallotsDlg = new JDialog(Main.mainFrame, title, true);
        createScenario c = null;
        if (inputCandidatesObj != null && inFile == null) {
            try{
                c = new createScenario(inputCandidatesObj.saveChanges(), false);
            } catch (Exception ignored){}

        } else {
            c = new createScenario(inFile.getAbsolutePath(), true);
        }
        createBallotsDlg.setContentPane(c);
        createBallotsDlg.pack();
        createBallotsDlg.setLocationRelativeTo(null);

        createScenario finalC = c;
        createBallotsDlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                mainForm.safeClose(createBallotsDlg, createScenario.class, finalC);
            }
        });
        createBallotsDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        if (c.failed) {
            createBallotsDlg.dispose();
        } else {
            createBallotsDlg.setVisible(true); // BLOCKING CALL !!!
        }
    }

    private void loadScenarioBtn(ActionEvent e) {
        JDialog j = new JDialog(Main.mainFrame, "Load scenario", true);
        scenarioLoad sl = new scenarioLoad(false);
        j.setContentPane(sl);
        j.pack();
        j.setLocationRelativeTo(null);
        j.setVisible(true);

        if (sl.selectedFile != null)
            mainForm.openScenarioForm(sl.selectedFile, "Edit scenario - " + sl.scenarioTitle);
    }

    private void settingsBtn(ActionEvent e) {
        JDialog settings = new JDialog(Main.mainFrame, "", true);
        SettingsUI settingsPane = new SettingsUI(settings);
        settings.setContentPane(settingsPane);
        settings.pack();
        settings.setLocationRelativeTo(null);
        settings.setVisible(true);
    }

    private void newInstituteBtn(ActionEvent e) {
        openInstitutionForm("New institution", null);
    }

    private void scenarioBuilderBtn(ActionEvent e) {
        String[] opts = {"Election", "Scenario", "Load template"};

        int sel = JOptionPane.showOptionDialog(null, "What do you want to build upon?", "Select type", 0, 3, null, opts, opts[0]);
        JDialog j = new JDialog(Main.mainFrame, "Load election", true);
        JDialog sb = new JDialog(Main.mainFrame, "Scenario Builder", true);
        ScenarioBuilder sbf;

        if (sel == -1)
            return;
        if (sel == 0) {
            j.setTitle("Load election");
            electionLoad el = new electionLoad();
            j.setContentPane(el);
            j.pack();
            j.setLocationRelativeTo(null);
            j.setVisible(true);

            if (el.selectedFile == null)
                return;

            sbf = new ScenarioBuilder(el.selectedFile.toString(), sel);
            sb.setContentPane(sbf);
            sb.pack();
            sb.setLocationRelativeTo(null);

        } else if (sel == 1) {
            j.setTitle("Load scenario");
            scenarioLoad sl = new scenarioLoad(false);
            j.setContentPane(sl);
            j.pack();
            j.setLocationRelativeTo(null);
            j.setVisible(true);

            if (sl.selectedFile == null)
                return;

            sbf = new ScenarioBuilder(sl.selectedFile.toString(), sel);
            sb.setContentPane(sbf);
            sb.pack();
            sb.setLocationRelativeTo(null);

        } else {
            j.setTitle("Load template");
            scenarioLoad sl = new scenarioLoad(true);
            j.setContentPane(sl);
            j.pack();
            j.setLocationRelativeTo(null);
            j.setVisible(true);

            if (sl.selectedFile == null)
                return;

            sbf = new ScenarioBuilder(sl.selectedFile.toString(), sel);
            sb.setContentPane(sbf);
            sb.pack();
            sb.setLocationRelativeTo(null);

        }

        sb.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                if (sbf.solveThread != null && sbf.solveThread.isAlive()) {
                    JOptionPane.showMessageDialog(null, "Cannot exit because solver is running. Please cancel before exiting!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                mainForm.safeClose(sb, ScenarioBuilder.class, sbf);
            }
        });
        sb.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        sb.setVisible(true);
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
        loadScenarioBtn = new JButton();
        label6 = new JLabel();
        label7 = new JLabel();
        settingsBtn = new JButton();
        newInstituteBtn = new JButton();
        label8 = new JLabel();
        label9 = new JLabel();
        label10 = new JLabel();
        label11 = new JLabel();
        scenarioBuilderBtn = new JButton();

        //======== this ========

        //---- startElectionBtn ----
        startElectionBtn.setIcon(null);
        startElectionBtn.setFont(startElectionBtn.getFont().deriveFont(startElectionBtn.getFont().getStyle() | Font.BOLD, startElectionBtn.getFont().getSize() + 15f));
        startElectionBtn.addActionListener(e -> startElectionBtn(e));

        //---- loadElectionBtn ----
        loadElectionBtn.setFont(loadElectionBtn.getFont().deriveFont(loadElectionBtn.getFont().getStyle() | Font.BOLD, loadElectionBtn.getFont().getSize() + 15f));
        loadElectionBtn.addActionListener(e -> loadElectionBtn(e));

        //---- label2 ----
        label2.setText("New election");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 13f));

        //---- label3 ----
        label3.setText("Load election");
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 13f));

        //---- label4 ----
        label4.setText("<html> <p align=\"justify\">Supply election title and list of candidates.</p></html>");
        label4.setHorizontalAlignment(SwingConstants.LEFT);
        label4.setVerticalAlignment(SwingConstants.TOP);
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 3f));

        //---- label5 ----
        label5.setText("<html> <p align=\"justify\"> Load an election to edit and create scenarios.</p> </html>");
        label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() + 3f));
        label5.setVerticalAlignment(SwingConstants.TOP);

        //---- loadScenarioBtn ----
        loadScenarioBtn.setFont(loadScenarioBtn.getFont().deriveFont(loadScenarioBtn.getFont().getStyle() | Font.BOLD, loadScenarioBtn.getFont().getSize() + 15f));
        loadScenarioBtn.addActionListener(e -> loadScenarioBtn(e));

        //---- label6 ----
        label6.setText("Load scenario");
        label6.setFont(label6.getFont().deriveFont(label6.getFont().getSize() + 13f));

        //---- label7 ----
        label7.setText("<html> <p align=\"justify\"> Load a scenario to edit and analyze. </p> </html>");
        label7.setFont(label7.getFont().deriveFont(label7.getFont().getSize() + 3f));
        label7.setVerticalAlignment(SwingConstants.TOP);

        //---- settingsBtn ----
        settingsBtn.setFont(settingsBtn.getFont().deriveFont(settingsBtn.getFont().getSize() + 11f));
        settingsBtn.addActionListener(e -> settingsBtn(e));

        //---- newInstituteBtn ----
        newInstituteBtn.setIcon(null);
        newInstituteBtn.setFont(newInstituteBtn.getFont().deriveFont(newInstituteBtn.getFont().getStyle() | Font.BOLD, newInstituteBtn.getFont().getSize() + 15f));
        newInstituteBtn.addActionListener(e -> newInstituteBtn(e));

        //---- label8 ----
        label8.setText("New institution");
        label8.setFont(label8.getFont().deriveFont(label8.getFont().getSize() + 13f));

        //---- label9 ----
        label9.setText("<html> <p align=\"justify\"> Supply Wards for elections with ward quotas.</p> </html>");
        label9.setFont(label9.getFont().deriveFont(label9.getFont().getSize() + 3f));
        label9.setVerticalAlignment(SwingConstants.TOP);

        //---- label10 ----
        label10.setText("<html> <p align=\"justify\"> Create scenarios dynamically. </p> </html>");
        label10.setFont(label10.getFont().deriveFont(label10.getFont().getSize() + 3f));
        label10.setVerticalAlignment(SwingConstants.TOP);

        //---- label11 ----
        label11.setText("Scenario Builder");
        label11.setFont(label11.getFont().deriveFont(label11.getFont().getSize() + 13f));

        //---- scenarioBuilderBtn ----
        scenarioBuilderBtn.setFont(scenarioBuilderBtn.getFont().deriveFont(scenarioBuilderBtn.getFont().getStyle() | Font.BOLD, scenarioBuilderBtn.getFont().getSize() + 15f));
        scenarioBuilderBtn.addActionListener(e -> scenarioBuilderBtn(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label9)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(startElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(loadElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label3, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE)))
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addGap(52, 52, 52))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(label5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(loadScenarioBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label6, GroupLayout.PREFERRED_SIZE, 286, GroupLayout.PREFERRED_SIZE))
                                .addComponent(label7, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(newInstituteBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label8, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE))
                                .addComponent(label4, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(scenarioBuilderBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label11, GroupLayout.PREFERRED_SIZE, 286, GroupLayout.PREFERRED_SIZE)
                                    .addGap(35, 35, 35))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(label10)
                                    .addGap(12, 12, 12)))
                            .addComponent(settingsBtn, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(newInstituteBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label8, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label9, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(startElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(loadElectionBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label5, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(loadScenarioBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label7, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scenarioBuilderBtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label11, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(settingsBtn, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label10, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
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
    private JButton loadScenarioBtn;
    private JLabel label6;
    private JLabel label7;
    private JButton settingsBtn;
    private JButton newInstituteBtn;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JLabel label11;
    private JButton scenarioBuilderBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
