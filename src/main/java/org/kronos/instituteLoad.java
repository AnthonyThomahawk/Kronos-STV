/*
 * Created by JFormDesigner on Thu Jul 25 16:37:10 EEST 2024
 */

package org.kronos;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Worker
 */
public class instituteLoad extends JPanel {
    private String[] instituteFiles;
    private File[] instituteFilesTmp;
    public boolean failed = false;

    public instituteLoad() {
        try {
            instituteFilesTmp = scanWorkDirForInstitutions();
        } catch (Exception x) {}

        if (instituteFilesTmp.length == 0) {
            JOptionPane.showMessageDialog(null, "<html><b>No institutions found in work directory.</b><br> Create one by using the \"New institution\" option in the main menu. </html>", "Error", JOptionPane.ERROR_MESSAGE);
            failed = true;
        }

        initComponents();
        initList();
        loadInstituteBtn.setText("<html> <b> Load </b> </html");
    }

    private File[] scanWorkDirForInstitutions() throws IOException {
        String workDir = Main.getWorkDir();
        File directory = new File(workDir);
        File[] allFiles = directory.listFiles();
        List<File> electionFiles = new ArrayList<>();

        for (File f : allFiles) {
            if (f.getName().endsWith(".institution")) {
                electionFiles.add(f);
            }
        }

        return electionFiles.toArray(new File[0]);
    }

    private void initList() {
        String[] fileNames = new String[instituteFilesTmp.length];
        instituteFiles = new String[instituteFilesTmp.length];

        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = instituteFilesTmp[i].getName().replace(".institution", "");
            instituteFiles[i] = instituteFilesTmp[i].getAbsolutePath();
        }

        list1.setListData(fileNames);

        label1.setText("Available institutions (" + fileNames.length + ") : ");
    }
    private void list1MouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();

            int selectedIndex = list1.getSelectedIndex();

            mainForm.openDeptCandidatesForm(instituteFiles[selectedIndex], "New election");
        }
    }

    private void loadInstituteBtn(ActionEvent e) {
        int selectedIndex = list1.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(null, "Please select a file to load from the list.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainForm.openDeptCandidatesForm(instituteFiles[selectedIndex], "New election");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
        label1 = new JLabel();
        loadInstituteBtn = new JButton();
        scrollPane1 = new JScrollPane();
        list1 = new JList();

        //======== this ========

        //---- label1 ----
        label1.setText("Available institutions : ");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 3f));

        //---- loadInstituteBtn ----
        loadInstituteBtn.setText("Load institution");
        loadInstituteBtn.addActionListener(e -> loadInstituteBtn(e));

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

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(label1))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(150, 150, 150)
                                    .addComponent(loadInstituteBtn, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 153, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(loadInstituteBtn, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(9, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Anthony Thomakos (lolcc iojvnd)
    private JLabel label1;
    private JButton loadInstituteBtn;
    private JScrollPane scrollPane1;
    private JList list1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
