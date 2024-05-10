package org.kronos;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static JFrame mainFrame;
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        mainFrame = new JFrame();
        mainForm m = new mainForm();
        mainFrame.setContentPane(m);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setTitle("Kronos Beta");
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
    }
}