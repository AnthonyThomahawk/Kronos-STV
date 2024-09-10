/*
 * Created by JFormDesigner on Tue Jun 04 19:20:44 EEST 2024
 */

package org.kronos;

import javafx.stage.FileChooser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    public scenarioLoad() {
        initComponents();
        initList();

        loadElectionBtn.setText("<html><b>Load</b><html>");
    }

    private File[] scanWorkDirForScenarios() throws IOException {
        String workDir = Main.getWorkDir();
        File directory = new File(workDir);
        File[] allFiles = directory.listFiles();
        List<File> scenarioList = new ArrayList<>();

        for (File f : allFiles) {
            if (f.getName().endsWith(".scenario")) {
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

        JSONParser parser = new JSONParser();
        try {
            JSONObject election = (JSONObject) parser.parse(new InputStreamReader(Files.newInputStream(Paths.get(scenarioFiles[selectedIndex].toURI())), StandardCharsets.UTF_8));
            String electionTitle = (String) election.get("ElectionTitle");
            mainForm.openScenarioForm(scenarioFiles[selectedIndex], "Edit scenario - " + electionTitle);
        } catch (Exception x) {
            JOptionPane.showMessageDialog(null, "This scenario file has an invalid format and cannot be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void list1MouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();

            loadElectionBtn(null);
        }
    }

    private void importBtn(ActionEvent e) {
        JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileFilter filter = new FileNameExtensionFilter("Kronos export File","KRONOS");
        fc.setFileFilter(filter);

        int res = fc.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                String workDir = Main.getWorkDir();
                ArrayList<File> files =  Zip.decompressFiles(f.getAbsolutePath(), workDir);
                initList();
                String msg = "<html>Imported from : " + f.getPath() + "<br>";
                msg += "Files imported : <br>";
                for (File z : files) {
                    msg += "<b>" + z.getName() + "</b><br>";
                }
                msg += "</html>";
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
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
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
                    .addGap(8, 8, 8)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(importBtn)
                        .addComponent(loadElectionBtn, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
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
