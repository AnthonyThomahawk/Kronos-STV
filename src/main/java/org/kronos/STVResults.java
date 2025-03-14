package org.kronos;

public class STVResults {
    String[] elected;
    Float[] votes;
    public int lastRank;
    int ballotCount;
    String stvInput;

    private void parseResultString(String input) {
        String results = input.split("Results:"+System.lineSeparator())[1];
        String[] lines = results.split(System.lineSeparator());
        elected = new String[lines.length];
        votes = new Float[lines.length];
        for (int i = 0; i < lines.length; i++) {
            elected[i] = lines[i].split("'")[1].split("'")[0];

            String p1 = lines[i].split(",")[2];
            String p2 = p1.replace(" ", "");
            String p3 = p2.replace(")", "");

            votes[i] = Float.parseFloat(p3);
        }
        lastRank = lines.length;
    }

    public STVResults(String resultStr, int bc) {
        parseResultString(resultStr);
        stvInput = resultStr;
        ballotCount = bc;
    }

    public String getElected(int rank) {
        return elected[rank-1];
    }
    public Float getVotes(int rank) {
        return votes[rank-1];
    }
    public String getInput() {
        return stvInput;
    }
    public int getBallotCount() {
        return ballotCount;
    }
}
