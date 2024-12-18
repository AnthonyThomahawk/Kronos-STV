package org.kronos;

import org.json.simple.JSONArray;
import sun.awt.image.ImageWatched;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ScenarioGenerator {
    ArrayList<String> candidates;
    ArrayList<String[]> ballots;
    ArrayList<String> candidatesSubset;
    int ballotCount;
    int seats;

    public ScenarioGenerator(String candidatesFile, String patternsFile, int seats) {
        readCandidatesFile(candidatesFile);
        readPatternFile(patternsFile);
        this.seats = seats;
    }

    public ScenarioGenerator(ArrayList<String> candidates, ArrayList<String> patterns, int seats) {
        this.candidates = new ArrayList<>(candidates);
        this.seats = seats;
        ballots = new ArrayList<>();
        patterns.forEach(this::processLine);
    }

    void processCandidateLine(String line) {
        if (!line.isEmpty())
            candidates.add(line.trim());
    }

    public void readCandidatesFile(String fileName) {
        candidates = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(this::processCandidateLine);
        } catch (Exception e) {
            System.out.println("Failed to read file " + e);
        }
    }

    public String[] makeBallot(String pattern) {
        String[] patternTokens = pattern.split("\\|");
        String[] preparedBallot = new String[patternTokens.length];

        ArrayList<String> remainingCandidates = new ArrayList<>(candidates);
        ArrayList<String> localCandidateSubSet = null;

        if (candidatesSubset != null)
            localCandidateSubSet = new ArrayList<>(candidatesSubset);

        for (int i = 0; i < patternTokens.length; i++) {
            if (candidates.contains(patternTokens[i])) {
                if (!remainingCandidates.contains(patternTokens[i])) {
                    throw new NumberFormatException("Duplicate candidate in ballot!");
                }

                preparedBallot[i] = patternTokens[i];
                remainingCandidates.remove(preparedBallot[i]);
            } else if (patternTokens[i].equals("$")) {
                if (remainingCandidates.isEmpty())
                    throw new NumberFormatException("There are no more candidates in random set.");

                Random ra = new Random();
                preparedBallot[i] = remainingCandidates.get(ra.nextInt(remainingCandidates.size()));
                remainingCandidates.remove(preparedBallot[i]);
            } else if (patternTokens[i].equals("#") && localCandidateSubSet != null) {
                for (String c : candidates) {
                    if (!remainingCandidates.contains(c)) {
                        localCandidateSubSet.remove(c);
                    }
                }

                if (localCandidateSubSet.isEmpty())
                    throw new NumberFormatException("There are no more candidates in exclusive random set.");

                Random ra = new Random();
                preparedBallot[i] = localCandidateSubSet.get(ra.nextInt(localCandidateSubSet.size()));
                remainingCandidates.remove(preparedBallot[i]);
                localCandidateSubSet.remove(preparedBallot[i]);
            }
            else {
                throw new NumberFormatException("Value : " + patternTokens[i] + " is not an index nor a bound letter (R or r)");
            }
        }

        return preparedBallot;
    }

    public void ballotsToCSV(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            PrintWriter writer = new PrintWriter(osw);

            for (String[] b : ballots) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < b.length; i++) {
                    line.append(b[i]);
                    if (i != b.length-1)
                        line.append(",");
                }
                writer.println(line);
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("Failed writing file " + e);
        }
    }

    public JSONArray ballotsToJSON() {
        JSONArray choices = new JSONArray();
        LinkedHashMap<String[], Integer> consolidated = new LinkedHashMap<>();

        for (String[] b : ballots) {
            boolean found = false;

            List<String[]> keys = new ArrayList<>(consolidated.keySet());

            for (int i = 0; i < keys.size(); i++) {
                if (Arrays.equals(keys.get(i), b)) {
                    found = true;
                    consolidated.put(keys.get(i), consolidated.get(keys.get(i)) + 1);
                    break;
                }
            }

            if (!found) {
                consolidated.put(b, 1);
            }
        }

        for (String[] k : consolidated.keySet()) {
            JSONArray ballot = new JSONArray();

            ballot.addAll(Arrays.asList(k));

            ballot.add(consolidated.get(k));

            choices.add(ballot);
        }

        return choices;
    }

    void processLine(String line) {
        if (line.matches("[0-9]+")){
            ballotCount = Integer.parseInt(line);
        } else if (line.contains("#=")) {
            candidatesSubset = new ArrayList<>();
            String subSetStr = line.split("#=\\{")[1].split("}")[0];
            String[] elements = subSetStr.split(",");

            candidatesSubset.addAll(Arrays.asList(elements));
        }
        else {
            String[] split1 = line.split("\\*");
            int multiplier;
            if (split1[0].trim().equals("?")) {
                multiplier = ballotCount - ballots.size();
            } else {
                multiplier = Integer.parseInt(split1[0].trim());
            }

            if (multiplier <= 0) {
                throw new NumberFormatException("Multiplier is negative (no remaining votes)");
            }

            String pattern = split1[1].trim();

            for (int i = 0 ; i < multiplier; i++) {
                ballots.add(makeBallot(pattern));
            }
        }
    }

    public void readPatternFile(String fileName) {
        ballots = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(this::processLine);
        } catch (Exception e) {
            System.out.println("Failed reading file " + e);
            System.exit(-1);
        }
    }
}
