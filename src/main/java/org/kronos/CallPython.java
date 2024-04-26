package org.kronos;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CallPython {
    private String scriptPath = "";
    private String interpreterPath = "";
    private String[] extraArgs;
    // call python script without arguments
    public CallPython(String script) {
        scriptPath = script;
    }
    // call python script with arguments
    public CallPython(String script, String... exargs) {
        scriptPath = script;
        extraArgs = exargs;
    }
    // only use this when sure that the interpreter will work
    public void setCustomInterp(String path) {
        interpreterPath = path;
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
        if (extraArgs != null) {
            args = new String[extraArgs.length + 2];
            args[0] = interpreterPath;
            args[1] = scriptPath;
            int i = 2;
            for (String extraArg : extraArgs) {
                args[i] = extraArg;
                i++;
            }
        } else {
            args = new String[2];
            args[0] = interpreterPath;
            args[1] = scriptPath;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        InputStream standardInput = process.getInputStream();
        String output;
        try (Scanner scanner = new Scanner(standardInput, StandardCharsets.UTF_8.name())) {
            output = scanner.useDelimiter("\\A").next();
        }
        return output;
    }
}