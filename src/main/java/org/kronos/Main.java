package org.kronos;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static JFrame mainFrame;
    public static mainForm mainFormObj;

    public static String getWorkDir() throws IOException {
        Properties loadProps = new Properties();
        loadProps.loadFromXML(Files.newInputStream(Paths.get("settings.xml")));

        String workDir = loadProps.getProperty("workDir");

        if (!new File(workDir).exists())
            return null;

        return workDir;
    }

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
            String workDir = getWorkDir();

            if (workDir == null) {
                int input = JOptionPane.showOptionDialog(null, "<html><b><i>In order to use Kronos, you must pick a Working folder in Settings.</b></i></html>", "Invalid Work Directory", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                if (input == JOptionPane.CANCEL_OPTION) {
                    System.exit(1);
                }

                return false;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occured and Kronos cannot read settings.xml", "Error", JOptionPane.OK_OPTION);
            return false;
        }

        return true;
    }

    public static String getTheme() {
        if (new File("settings.xml").exists()) {
            try {
                Properties loadProps = new Properties();
                loadProps.loadFromXML(Files.newInputStream(Paths.get("settings.xml")));
                String theme = loadProps.getProperty("theme");

                return theme;
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    public static void checkSTV() {
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

    private static int[] getPythonVer(String target) throws IOException {
        try {
            Runtime.getRuntime().exec(target + " --version");
        } catch (Exception e) {
            return null;
        }

        String[] args = new String[2];
        args[0] = target;
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

        int[] res = new int[2];

        res[0] = Integer.parseInt(pythonVer[0]);
        res[1] = Integer.parseInt(pythonVer[1]);

        return res;
    }

    private static void checkPython() throws IOException {
        String custominterp = System.getenv("PYTHON_CUSTOM");
        if (custominterp != null) {
            int[] checkVer = getPythonVer(custominterp);

            if (checkVer == null) {
                JOptionPane.showMessageDialog(null, "The custom python interpreter you provided is not a valid python binary.", "Custom Python error", JOptionPane.ERROR_MESSAGE);
                System.exit(2);
            }

            if (!(checkVer[0] == 3 && checkVer[1] >= 8)) {
                JOptionPane.showMessageDialog(null, "The custom python interpreter you provided is outdated (must be python 3.8 or newer).", "Custom Python error", JOptionPane.ERROR_MESSAGE);
                System.exit(3);
            }

            System.out.println("Using python : " + custominterp);
            CallPython.interpreterPath = custominterp;
            return;
        }

        int[] pythonVer = getPythonVer("python");

        int error;

        if (pythonVer == null) {
            error = 2;
        } else if (!(pythonVer[0] == 3 && pythonVer[1] >= 8)) {
            error = 3;
        } else {
            error = 0;
        }

        if (error == 0) {
            CallPython.interpreterPath = "python";
            return;
        }

        int[] python3Ver = getPythonVer("python3");

        if (python3Ver == null) {
            error = 2;
        } else if (!(python3Ver[0] == 3 && python3Ver[1] >= 8)) {
            error = 3;
        } else {
            error = 0;
        }

        if (error == 2) {
            JOptionPane.showMessageDialog(null, "This software requires Python 3.8 or newer to function.", "Python not found", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        } else if (error == 3) {
            JOptionPane.showMessageDialog(null, "Python 3.8 or newer is required." + System.lineSeparator() + "Version found : " + pythonVer[0] + "." + pythonVer[1] + System.lineSeparator() + "Required : 3.8.0 or newer", "Outdated python version", JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        } else {
            CallPython.interpreterPath = "python3";
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

        String theme = getTheme();
        if (theme != null) {
            try {
                if (theme.equals("dark")) {
                    javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
                } else if (theme.equals("light")) {
                    javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
                }
            } catch (Exception ignored) {}

            com.formdev.flatlaf.FlatLaf.updateUI();
        }

        mainFormObj = new mainForm();
        mainFrame.setContentPane(mainFormObj);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setTitle("Kronos Beta");
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
    }
}