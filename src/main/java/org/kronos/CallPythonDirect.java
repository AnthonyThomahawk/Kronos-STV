package org.kronos;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class CallPythonDirect {
    private String scriptPath = "";
    public static String interpreterPath = "";
    String input;
    // call python script without arguments
    public CallPythonDirect(String script) {
        scriptPath = script;
    }
    // call python script with arguments
    public CallPythonDirect(String script, String input) {
        scriptPath = script;
        this.input = input;
    }

    // run script and return result as String
    public String run() throws Exception {
        if (interpreterPath.isEmpty()) {
            interpreterPath = "python";
        }

        try {
            Runtime.getRuntime().exec(interpreterPath + " --version");
        } catch (Exception e) {
            throw new Exception("Python does not exist");
        }

        String []args;
        args = new String[2];
        args[0] = interpreterPath;
        args[1] = scriptPath;

        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Map<String, String> env = processBuilder.environment();
        env.put("PYTHONUTF8", "1");

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        OutputStream pStdIn = process.getOutputStream();

        Writer writer = new OutputStreamWriter(pStdIn, StandardCharsets.UTF_8);
        writer.write(input);
        writer.close(); // no need to flush, closing flushes as well

        InputStream standardInput = process.getInputStream();
        String output;
        try (Scanner scanner = new Scanner(standardInput, StandardCharsets.UTF_8.name())) {
            output = scanner.useDelimiter("\\A").next();
        }
        return output;
    }
}