package org.kronos;

public class STVpy {
    public String coreScript = "loader.py";

    public String callSTV(String ballotFile, int seats) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG", "-s", Integer.toString(seats));
        return py.run();
    }
    public String callSTV(String ballotFile, int seats, String randomSeed) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG", "-s", Integer.toString(seats), "-r", randomSeed);
        return py.run();
    }
    public String callSTV(String ballotFile) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG");
        return py.run();
    }
    public String callSTV(String ballotFile, String constituencyFile) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG", "-c", constituencyFile);
        return py.run();
    }
}