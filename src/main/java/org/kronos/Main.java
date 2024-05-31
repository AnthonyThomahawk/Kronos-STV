package org.kronos;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.json.simple.parser.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.lang.model.element.Element;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static JFrame mainFrame;

    public static boolean checkConfig() {
        File s = new File("settings.xml");
        if (!s.exists()) {
            int input = JOptionPane.showOptionDialog(null, "Kronos detected this is the first time its running on this system.\n" +
                    "<html><b><i>In order to use Kronos, you must pick a Working folder in Settings.</b></i></html>", "First time running Kronos", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

            if (input == JOptionPane.CANCEL_OPTION) {
                System.exit(1);
            }

            return false;
        }

        try {
            Properties loadProps = new Properties();
            loadProps.loadFromXML(Files.newInputStream(Paths.get("settings.xml")));

            String workDir = loadProps.getProperty("workDir");
            if (workDir == null || !new File(workDir).exists()) {
                int input = JOptionPane.showOptionDialog(null, "<html><b><i>In order to use Kronos, you must pick a Working folder in Settings.</b></i></html>", "Invalid Work Directory", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                if (input == JOptionPane.CANCEL_OPTION) {
                    System.exit(1);
                }

                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

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
        String custominterp = System.getenv("PYTHON_CUSTOM");
        if (custominterp != null) {
            System.out.println("Using python : " + custominterp);
            CallPython.interpreterPath = custominterp;
            return;
        }

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
        if (DarkModeDetector.isDarkMode())
            FlatDarkLaf.setup();
        else
            FlatLightLaf.setup();

        mainFrame = new JFrame();

        try{
            checkPython();
        } catch (Exception ignored){}

        checkSTV();

        while (!checkConfig()) {
            JDialog settings = new JDialog(Main.mainFrame, "", true);
            SettingsUI settingsPane = new SettingsUI();
            settings.setContentPane(settingsPane);
            settings.pack();
            settings.setLocationRelativeTo(null);
            settings.setVisible(true);
            settings.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        if (new File("settings.xml").exists()) {
            try {
                Properties loadProps = new Properties();
                loadProps.loadFromXML(Files.newInputStream(Paths.get("settings.xml")));
                String theme = loadProps.getProperty("theme");

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

            } catch (Exception ignored) {}
        }

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