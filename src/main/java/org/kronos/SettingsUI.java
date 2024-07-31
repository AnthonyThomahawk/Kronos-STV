/*
 * Created by JFormDesigner on Fri May 31 01:01:17 EEST 2024
 */

package org.kronos;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Enterprise
 */
public class SettingsUI extends JPanel {
    String theme = "dark";
    public SettingsUI() {
        initComponents();
        loadSettings();
        clearWorkDirectory.setVisible(false); // hidden for now
    }

    void loadSettings() {
        if (new File("settings.xml").exists()) {
            try {
                Properties loadProps = new Properties();
                loadProps.loadFromXML(Files.newInputStream(Paths.get("settings.xml")));
                textField1.setText(loadProps.getProperty("workDir"));

                theme = loadProps.getProperty("theme");

                if (theme.equals("dark")) {
                    darkThemeBtn.setSelected(true);
                    lightThemeBtn.setSelected(false);
                    followSystemBtn.setSelected(false);
                }
                if (theme.equals("light")) {
                    lightThemeBtn.setSelected(true);
                    darkThemeBtn.setSelected(false);
                    followSystemBtn.setSelected(false);
                }
                if (theme.equals("system")) {
                    lightThemeBtn.setSelected(false);
                    darkThemeBtn.setSelected(false);
                    followSystemBtn.setSelected(true);
                }

            } catch (Exception x) {
                System.out.println(x);
                theme = "dark";
                darkThemeBtn.setSelected(true);
                lightThemeBtn.setSelected(false);
                followSystemBtn.setSelected(false);
                JOptionPane.showMessageDialog(null, "Failed to load settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            darkThemeBtn.setSelected(false);
            lightThemeBtn.setSelected(false);
            followSystemBtn.setSelected(true);
            theme = "system";
        }
    }

    private void darkThemeBtn(ActionEvent e) {
        if (theme.equals("dark")) {
            darkThemeBtn.setSelected(true);
            lightThemeBtn.setSelected(false);
            followSystemBtn.setSelected(false);
            return;
        }
        try {
            lightThemeBtn.setSelected(false);
            followSystemBtn.setSelected(false);
            theme = "dark";
        } catch (Exception ignored) {}
    }

    private void lightThemeBtn(ActionEvent e) {
        if (theme.equals("light")) {
            lightThemeBtn.setSelected(true);
            darkThemeBtn.setSelected(false);
            followSystemBtn.setSelected(false);
            return;
        }
        try {
            darkThemeBtn.setSelected(false);
            followSystemBtn.setSelected(false);
            theme = "light";
        } catch (Exception ignored) {}
    }

    private void dirPickerbtn(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Kronos work directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textField1.setText(String.valueOf(chooser.getSelectedFile()));
        }
    }

    private void saveBtn(ActionEvent e) {
        if (!new File(textField1.getText()).exists()) {
            JOptionPane.showMessageDialog(null, "Directory does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            FileInputStream in = new FileInputStream("settings.xml");
            Properties saveProps = new Properties();
            saveProps.loadFromXML(in);
            saveProps.setProperty("workDir", textField1.getText());
            saveProps.setProperty("theme", theme);
            saveProps.storeToXML(Files.newOutputStream(Paths.get("settings.xml")), "");

            if (theme.equals("dark")) {
                javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            } else if (theme.equals("light")) {
                javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } else {
                if (DarkModeDetector.isDarkMode()) {
                    javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
                } else {
                    javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
                }
            }
            com.formdev.flatlaf.FlatLaf.updateUI();

            if (Main.mainFormObj != null)
                Main.mainFormObj.setIcons();

        } catch (Exception x) {
            System.out.println(x);
            JOptionPane.showMessageDialog(null, "Error writing settings!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        JOptionPane.showMessageDialog(null, "Settings saved.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void followSystemBtn(ActionEvent e) {
        if (theme.equals("system")) {
            lightThemeBtn.setSelected(false);
            darkThemeBtn.setSelected(false);
            followSystemBtn.setSelected(true);
            return;
        }
        try {
            darkThemeBtn.setSelected(false);
            lightThemeBtn.setSelected(false);
            theme = "system";
        } catch (Exception ignored) {}
    }

    private void clearWorkDirectory(ActionEvent e) {
        int res = JOptionPane.showConfirmDialog(this, "<html> <b><i><u> WARNING : </b></i></u> This action will <b> DELETE ALL Kronos data (elections, scenarios, etc.)</b> \n Are you sure you want to continue?", "WARNING", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                String workDir = Main.getWorkDir();
                File wDir = new File(workDir);
                File[] kronosFiles = wDir.listFiles();
                if (kronosFiles != null) {
                    for (File f : kronosFiles) {
                        if (f.getAbsolutePath().endsWith(".scenario") || f.getAbsolutePath().endsWith(".election") || f.getAbsolutePath().endsWith(".institution"))
                            f.delete();
                    }
                }
                JOptionPane.showMessageDialog(this, "All Kronos data has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting files in work directory.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        label2 = new JLabel();
        textField1 = new JTextField();
        dirPickerbtn = new JButton();
        label3 = new JLabel();
        darkThemeBtn = new JRadioButton();
        lightThemeBtn = new JRadioButton();
        followSystemBtn = new JRadioButton();
        saveBtn = new JButton();
        clearWorkDirectory = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText("Settings");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getStyle() | Font.BOLD, label1.getFont().getSize() + 5f));

        //---- label2 ----
        label2.setText("Work directory");

        //---- dirPickerbtn ----
        dirPickerbtn.setText("Choose directory");
        dirPickerbtn.addActionListener(e -> dirPickerbtn(e));

        //---- label3 ----
        label3.setText("App theme");

        //---- darkThemeBtn ----
        darkThemeBtn.setText("Dark theme");
        darkThemeBtn.addActionListener(e -> darkThemeBtn(e));

        //---- lightThemeBtn ----
        lightThemeBtn.setText("Light theme");
        lightThemeBtn.addActionListener(e -> lightThemeBtn(e));

        //---- followSystemBtn ----
        followSystemBtn.setText("Follow system");
        followSystemBtn.addActionListener(e -> followSystemBtn(e));

        //---- saveBtn ----
        saveBtn.setText("Save");
        saveBtn.addActionListener(e -> saveBtn(e));

        //---- clearWorkDirectory ----
        clearWorkDirectory.setText("\u26a0 Clear Work directory \u26a0");
        clearWorkDirectory.addActionListener(e -> clearWorkDirectory(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 316, Short.MAX_VALUE)
                            .addComponent(saveBtn))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label1)
                                .addComponent(label2)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 252, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dirPickerbtn))
                                .addComponent(clearWorkDirectory)
                                .addComponent(label3)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(darkThemeBtn)
                                    .addGap(18, 18, 18)
                                    .addComponent(lightThemeBtn)
                                    .addGap(18, 18, 18)
                                    .addComponent(followSystemBtn)))
                            .addGap(0, 26, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(label2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(dirPickerbtn))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(clearWorkDirectory)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(label3)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(darkThemeBtn)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lightThemeBtn)
                            .addComponent(followSystemBtn)))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                    .addComponent(saveBtn)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JLabel label2;
    private JTextField textField1;
    private JButton dirPickerbtn;
    private JLabel label3;
    private JRadioButton darkThemeBtn;
    private JRadioButton lightThemeBtn;
    private JRadioButton followSystemBtn;
    private JButton saveBtn;
    private JButton clearWorkDirectory;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
