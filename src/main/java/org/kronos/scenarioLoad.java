/*
 * Created by JFormDesigner on Tue Jun 04 19:20:44 EEST 2024
 */

package org.kronos;

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

/**
 * @author Worker
 */
public class scenarioLoad extends JPanel {
    private File[] scenarioFiles;

    public scenarioLoad() {
        initComponents();
        initList();
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        loadElectionBtn = new JButton();
        label1 = new JLabel();

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

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addGap(155, 155, 155)
                    .addComponent(loadElectionBtn, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(154, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(loadElectionBtn, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(8, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JScrollPane scrollPane1;
    private JList list1;
    private JButton loadElectionBtn;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
