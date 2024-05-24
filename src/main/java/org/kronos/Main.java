package org.kronos;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static JFrame mainFrame;

    private static void checkSTV() {
        File l = new File("loader.py");
        if (!l.exists()) {
            InputStream loader = Main.class.getClassLoader().getResourceAsStream("loader.py");
            try {
                Files.copy(loader, Paths.get("loader.py"));
            } catch (Exception e) {
                System.out.println(e);
                JOptionPane.showMessageDialog(null, "Cannot write loader.py, terminating.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        File f = new File("stv.py");
        if (!f.exists()) {
            InputStream stvCore = Main.class.getClassLoader().getResourceAsStream("stv.py");
            try {
                Files.copy(stvCore, Paths.get("stv.py"));
            } catch (Exception e) {
                System.out.println(e);
                JOptionPane.showMessageDialog(null, "Cannot write stv.py, terminating.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    private static void checkPython() throws IOException {
        try {
            Runtime.getRuntime().exec("python --version");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "This software requires Python 3.6 or newer to function.", "Python not found", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        }

        String[] args = new String[2];
        args[0] = "python";
        args[1] = "--version";

        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        InputStream standardInput = process.getInputStream();
        String output;
        try (Scanner scanner = new Scanner(standardInput, StandardCharsets.UTF_8.name())) {
            output = scanner.useDelimiter("\\A").next();
        }

        String verStr = output.split("Python ")[1];
        verStr = verStr.replace(System.lineSeparator(), "");

        String[] pythonVer = verStr.split("\\.");

        if (!(Integer.parseInt(pythonVer[0]) == 3 && Integer.parseInt(pythonVer[1]) >= 6))
        {
            JOptionPane.showMessageDialog(null, "Python 3.6 or newer is required." + System.lineSeparator() + "Version found : " + verStr + System.lineSeparator() + "Required : 3.6.0 or newer", "Outdated python version", JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        }
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        try{
            checkPython();
        } catch (Exception ignored){}

        checkSTV();
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