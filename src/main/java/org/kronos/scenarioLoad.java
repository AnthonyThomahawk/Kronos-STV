/*
 * Created by JFormDesigner on Tue Jun 04 19:20:44 EEST 2024
 */

package org.kronos;

import javafx.stage.FileChooser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * @author Worker
 */
public class scenarioLoad extends JPanel {
    private File[] scenarioFiles;
    private static JFileChooser importChooser;

    public String scenarioTitle;
    public File selectedFile;

    boolean t;

    public scenarioLoad(boolean template) {
        t = template;

        initComponents();
        initList();

        loadElectionBtn.setText("<html><b>Load</b><html>");
    }

    private File[] scanWorkDirForScenarios() throws IOException {
        String workDir = Main.getWorkDir();
        File directory = new File(workDir);
        File[] allFiles = directory.listFiles();
        List<File> scenarioList = new ArrayList<>();

        String extension;
        if (t) extension = ".buildTemplate"; else extension = ".scenario";

        for (File f : allFiles) {
            if (f.getName().endsWith(extension)) {
                scenarioList.add(f);
            }
        }

        return scenarioList.toArray(new File[0]);
    }



    private void initList() {
        try {
            scenarioFiles = scanWorkDirForScenarios();

            String[] fileNames = new String[scenarioFiles.length];

            for (int i = 0; i < fileNames.length; i++) {
                fileNames[i] = scenarioFiles[i].getName().replace(".scenario", "");
            }

            list1.setListData(fileNames);

            label1.setText("Available scenarios (" + fileNames.length + ") : ");

        } catch (IOException e) {
        }
    }

    private void loadElectionBtn(ActionEvent e) {
        int selectedIndex = list1.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(null, "Please select a file to load from the list.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedFile = scenarioFiles[selectedIndex];

        if (t) {
            JDialog x = (JDialog) this.getRootPane().getParent();
            x.dispose();
            return;
        }

        JSONParser parser = new JSONParser();
        try {
            JSONObject election = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(Paths.get(scenarioFiles[selectedIndex].toURI())), StandardCharsets.UTF_8));
            String electionTitle = (String) election.get("ElectionTitle");

            scenarioTitle = electionTitle;
            JDialog x = (JDialog) this.getRootPane().getParent();
            x.dispose();
        } catch (Exception x) {
            JOptionPane.showMessageDialog(null, "This scenario file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            JDialog z = (JDialog) this.getRootPane().getParent();
            z.dispose();
        }
    }

    private void list1MouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            loadElectionBtn(null);
        }
    }

    

    private void importBtn(ActionEvent e) {
        if (importChooser == null) {
            try {
                FileInputStream in = new FileInputStream("settings.xml");
                Properties saveProps = new Properties();
                saveProps.loadFromXML(in);
                String path = saveProps.getProperty("importDir");
                importChooser = new JFileChooser(path);
            } catch (Exception x) {
                importChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            }

            FileFilter filter = new FileNameExtensionFilter("Kronos export File","KRONOS");
            importChooser.setFileFilter(filter);
        }

        int res = importChooser.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            File f = importChooser.getSelectedFile();
            try {
                String workDir = Main.getWorkDir();

                ArrayList<String> archiveFileNames = Zip.listArchiveFiles(f.getAbsolutePath());
                ArrayList<String> existingFileNames = new ArrayList<>();

                for (String fileName : archiveFileNames) {
                    if (new File(workDir + File.separator + fileName).exists()) {
                        existingFileNames.add(fileName);
                    }
                }

                if (!existingFileNames.isEmpty()) {
                    String msg = "<html><b>Warning!</b><br>";
                    msg += "The following files already exist in your work folder : <br>";

                    for (String existingFile : existingFileNames) {
                        msg += "<b>" + existingFile + "</b><br>";
                    }

                    msg += "Do you want to <b>overwrite</b> them ?";

                    int ans = JOptionPane.showConfirmDialog(null, msg, "Warning", JOptionPane.YES_NO_OPTION);

                    if (ans == JOptionPane.NO_OPTION) return;
                }

                ArrayList<File> files = Zip.decompressFiles(f.getAbsolutePath(), workDir);
                initList();

                String msg = "<html>Imported from : " + f.getPath() + "<br>";
                msg += "Files imported : <br>";
                for (File z : files) {
                    msg += "<b>" + z.getName() + "</b><br>";
                }
                msg += "</html>";

                FileInputStream in = new FileInputStream("settings.xml");
                Properties saveProps = new Properties();
                saveProps.loadFromXML(in);
                saveProps.setProperty("importDir", importChooser.getCurrentDirectory().toString());
                saveProps.storeToXML(Files.newOutputStream(Paths.get("settings.xml")), "");

                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);

                for (File x : files) {
                    if (x.getName().endsWith(".scenario")) {
                        String sName = x.getName().replace(".scenario", "");
                        list1.setSelectedValue(sName, true);
                        list1.grabFocus();
                        break;
                    }
                }
            } catch (Exception x) {
                System.out.println(x);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        loadElectionBtn = new JButton();
        label1 = new JLabel();
        importBtn = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- list1 ----
            list1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    list1MouseClicked(e);
                }
            });
            scrollPane1.setViewportView(list1);
        }

        //---- loadElectionBtn ----
        loadElectionBtn.setText("Load scenario");
        loadElectionBtn.addActionListener(e -> loadElectionBtn(e));

        //---- label1 ----
        label1.setText("Available scenarios : ");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 3f));

        //---- importBtn ----
        importBtn.setText("Import");
        importBtn.addActionListener(e -> importBtn(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(label1)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(importBtn)
                                    .addGap(75, 75, 75)
                                    .addComponent(loadElectionBtn, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 150, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addComponent(loadElectionBtn, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(importBtn, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JList list1;
    private JButton loadElectionBtn;
    private JLabel label1;
    private JButton importBtn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
