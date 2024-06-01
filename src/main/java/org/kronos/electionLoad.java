/*
 * Created by JFormDesigner on Sat Jun 01 23:34:15 EEST 2024
 */

package org.kronos;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Enterprise
 */
public class electionLoad extends JPanel {
    private File[] electionFiles;

    public electionLoad() {
        initComponents();

        initList();
    }

    private File[] scanWorkDirForElections() throws IOException {
        String workDir = Main.getWorkDir();
        File directory = new File(workDir);
        File[] allFiles = directory.listFiles();
        List<File> electionFiles = new ArrayList<>();

        for (File f : allFiles) {
            if (f.getName().endsWith(".election")) {
                electionFiles.add(f);
            }
        }

        return electionFiles.toArray(new File[0]);
    }

    private void initList() {
        try {
            electionFiles = scanWorkDirForElections();

            String[] fileNames = new String[electionFiles.length];

            for (int i = 0; i < fileNames.length; i++) {
                fileNames[i] = electionFiles[i].getName().replace(".election", "");
            }

            list1.setListData(fileNames);

            label1.setText("Available elections (" + fileNames.length + ") : ");

        } catch (IOException e) {
        }
    }

    private void loadElectionBtn(ActionEvent e) {
        int selectedIndex = list1.getSelectedIndex();

        mainForm.openCandidatesForm(electionFiles[selectedIndex]);
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
            scrollPane1.setViewportView(list1);
        }

        //---- loadElectionBtn ----
        loadElectionBtn.setText("Load election");
        loadElectionBtn.addActionListener(e -> loadElectionBtn(e));

        //---- label1 ----
        label1.setText("Available elections : ");
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
