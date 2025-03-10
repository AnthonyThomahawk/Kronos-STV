package org.kronos;

public class STVpy {
    public String coreScript = "loader.py";

    public String callSTV(String ballotFile, int seats) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG", "-s", Integer.toString(seats));
        return py.run();
    }
    public String callSTV(String ballotFile) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG");
        return py.run();
    }
    public String callSTV(String ballotFile, String constituencyFile, int quota) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG", "-c", constituencyFile, "-q", Integer.toString(quota));
        return py.run();
    }
    public String callSTV(String ballotFile, int seats, String constituencyFile, int quota) throws Exception {
        CallPython py = new CallPython(coreScript, "-b", ballotFile, "-l", "DEBUG", "-s", Integer.toString(seats), "-c", constituencyFile, "-q", Integer.toString(quota));
        return py.run();
    }

    public String callSTVDirect(String ballots, int seats) throws Exception {
        String input = ballots;
        input += "[{!ARGUMENT_DELIMITER!}]";
        input += "-s " + seats;
        CallPythonDirect py = new CallPythonDirect("stv_direct.py", input);
        return py.run();
    }

    public String callSTVDirect(String ballots, int seats, String constituencyFile, int quota) throws Exception {
        String input = ballots;
        input += "[{!ARGUMENT_DELIMITER!}]";
        input += "-s " + seats + " -c " + constituencyFile + " -q " + quota;
        CallPythonDirect py = new CallPythonDirect("stv_direct.py", input);
        return py.run();
    }
}