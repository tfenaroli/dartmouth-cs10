import java.io.*;
import java.util.*;

/**
 * pattern recognition using Viterbi decoding
 * @author Thomas Fenaroli, Spring 2021, CS10
 */
public class PatternRecognition {

    public Map<String, Map<String, Double>> transitionsMap = new HashMap<>();
    public Map<String, Map<String, Double>> observationsMap = new HashMap<>();
    public Map<String, Map<String, Integer>> transitionsCount = new HashMap<>();
    public Map<String, Map<String, Integer>> observationsCount = new HashMap<>();

    public int unseen = -100;

    /**
     * performs Viterbi decoding
     * @param observationList   array of observations
     * @return  path of tags
     */
    public ArrayList<String> Viterbi(String[] observationList) {
        ArrayList<String> tagPath = new ArrayList<>();
        ArrayList<Map<String, String>> backTrack = new ArrayList<>();
        Set<String> currStates = new HashSet<>();
        Map<String, Double> currScores = new HashMap<>();

        currStates.add("#");
        currScores.put("#", 0.0);

        for (int i = 0; i < observationList.length; i++) {
            Set<String> nextStates = new HashSet<>();
            Map<String, Double> nextScores = new HashMap<>();
            for (String currState : currStates) {
                if (transitionsMap.get(currState) != null) {
                    for (String nextState : transitionsMap.get(currState).keySet()) {
                        nextStates.add(nextState);
                        double currentScore = currScores.get(currState);
                        double transitionScore = transitionsMap.get(currState).get(nextState);
                        double nextScore;
                        if (observationsMap.get(nextState).containsKey(observationList[i])) {
                            double observationScore = observationsMap.get(nextState).get(observationList[i]);
                            nextScore = currentScore + transitionScore + observationScore;
                        }
                        else {
                            nextScore = currentScore + transitionScore + unseen;
                        }
                        if (!nextScores.keySet().contains(nextState) || nextScore > nextScores.get(nextState)) {
                            nextScores.put(nextState, nextScore);
                            Map<String, String> step = new HashMap<>();
                            if (backTrack.size() <= i) {
                                backTrack.add(step);
                            }
                            backTrack.get(i).put(nextState, currState);
                        }
                    }
                }
            }
            currStates = nextStates;
            currScores = nextScores;
        }

        fillTagPath(currScores, tagPath, backTrack);
        Collections.reverse(tagPath);

        return tagPath;
    }

    /**
     * fills tagPath with best matches for every word
     * @param currScores    current scores at end of observations
     * @param tagPath   empty tagPath
     * @param backTrack     empty backTrack
     */
    public void fillTagPath(Map<String, Double> currScores, ArrayList<String> tagPath, ArrayList<Map<String, String>> backTrack) {
        String bestMatch = backTrack.get(backTrack.size() - 1).keySet().iterator().next();

        for (String POS : backTrack.get(backTrack.size() - 1).keySet()) {
            if (currScores.get(POS) > currScores.get(bestMatch)) {
                bestMatch = POS;
            }
        }

        int i = backTrack.size() - 1;
        String current = bestMatch;
        while (i >= 0) {
            tagPath.add(current);
            current = backTrack.get(i).get(current);
            i--;
        }
    }

    /**
     * trains and creates Markov model
     * @param wordsFileName     file name of file containing training words
     * @param tagsFileName      file name of file containing training tags
     * @throws IOException  if file not found
     */
    public void train(String wordsFileName, String tagsFileName) throws IOException {
        BufferedReader wordsReader = new BufferedReader(new FileReader(wordsFileName));
        BufferedReader tagsReader = new BufferedReader(new FileReader(tagsFileName));

        ArrayList<ArrayList<String>> sentencesWords = new ArrayList<>();
        ArrayList<ArrayList<String>> sentencesTags = new ArrayList<>();

        String line;
        while ((line = wordsReader.readLine()) != null) {
            String[] lineWords = line.split(" ");
            ArrayList<String> sentenceWords = new ArrayList<>();
            for (String word : lineWords) {
                sentenceWords.add(word);
            }
            sentencesWords.add(sentenceWords);
        }
        while ((line = tagsReader.readLine()) != null) {
            String[] lineTags = line.split(" ");
            ArrayList<String> sentenceTags = new ArrayList<>();
            for (String tag : lineTags) {
                sentenceTags.add(tag);
            }
            sentencesTags.add(sentenceTags);
        }

        fillTransitionsCount(sentencesWords, sentencesTags);
        fillObservationsCount(sentencesWords, sentencesTags);

        for (String state : transitionsCount.keySet()) {
            int total = 0;
            for (String next : transitionsCount.get(state).keySet()) {
                total += transitionsCount.get(state).get(next);
            }

            for (String next : transitionsCount.get(state).keySet()) {
                if (transitionsCount.get(state).get(next) != 0) {
                    if (!transitionsMap.containsKey(state)) {
                        transitionsMap.put(state, new HashMap<>());
                    }
                    transitionsMap.get(state).put(next, Math.log(transitionsCount.get(state).get(next) / (double)total));
                }
            }
        }

        for (String state : observationsCount.keySet()) {
            int total = 0;
            for (String word : observationsCount.get(state).keySet()) {
                total += observationsCount.get(state).get(word);
            }

            observationsMap.put(state, new HashMap<>());
            for (String word : observationsCount.get(state).keySet()) {
                if (observationsCount.get(state).get(word) != 0) {
                    if (!observationsMap.containsKey(state)) {
                        observationsMap.put(state, new HashMap<>());
                    }
                    observationsMap.get(state).put(word, Math.log(observationsCount.get(state).get(word) / (double)total));
                }
            }
        }
    }

    /**
     * fills map containing counts of observations
     * @param sentencesWords    list of list of words
     * @param sentencesTags     list of list of tags
     */
    public void fillObservationsCount(ArrayList<ArrayList<String>> sentencesWords, ArrayList<ArrayList<String>> sentencesTags) {
        int sentences = sentencesTags.size();
        for (int i = 0; i < sentences; i++) {
            int words = sentencesWords.get(i).size();
            for (int j = 0; j < words; j++) {
                String currentPOS = sentencesTags.get(i).get(j);
                String currentWord = sentencesWords.get(i).get(j);

                if (!observationsCount.containsKey(currentPOS)) {
                    observationsCount.put(currentPOS, new HashMap<>());
                }

                if (!observationsCount.get(currentPOS).containsKey(currentWord)) {
                    observationsCount.get(currentPOS).put(currentWord, 1);
                }

                else {
                    int currCount = observationsCount.get(currentPOS).get(currentWord);
                    observationsCount.get(currentPOS).put(currentWord, currCount + 1);
                }
            }
        }
    }

    /**
     * fills map containing counts of transitions
     * @param sentencesWords    list of list of words
     * @param sentencesTags     list of list of tags
     */
    public void fillTransitionsCount(ArrayList<ArrayList<String>> sentencesWords, ArrayList<ArrayList<String>> sentencesTags) {
        int sentences = sentencesWords.size();
        for (int i = 0; i < sentences; i++) {
            int words = sentencesWords.get(i).size();
            for (int j = 0; j < words - 1; j++) {
                String currentPOS = sentencesTags.get(i).get(j);
                if (j == 0) {
                    currentPOS = "#";
                }

                String nextPOS = sentencesTags.get(i).get(j + 1);

                if (!transitionsCount.containsKey(currentPOS)) {
                    transitionsCount.put(currentPOS, new HashMap<>());
                }

                if (!transitionsCount.get(currentPOS).containsKey(nextPOS)) {
                    transitionsCount.get(currentPOS).put(nextPOS, 1);
                }

                else {
                    transitionsCount.get(currentPOS).put(nextPOS, transitionsCount.get(currentPOS).get(nextPOS) + 1);
                }
            }
        }
    }

    /**
     * tests accuracy of pattern recognition using test files
     * @param testWordsFileName     file name of file containing test words
     * @param testTagsFileName      file name of file containing test tags
     * @throws IOException  if file not found
     */
    public void testAccuracy(String testWordsFileName, String testTagsFileName) throws IOException {
        int totalCorrect = 0;
        int totalIncorrect = 0;
        BufferedReader wordsReader = new BufferedReader(new FileReader(testWordsFileName));
        BufferedReader tagsReader = new BufferedReader(new FileReader(testTagsFileName));

        ArrayList<ArrayList<String>> testResults = new ArrayList<>();
        ArrayList<ArrayList<String>> correctResults = new ArrayList<>();

        String line;
        while ((line = wordsReader.readLine()) != null) {
            String[] lineWords = line.split(" ");
            testResults.add(Viterbi(lineWords));
        }

        while ((line = tagsReader.readLine()) != null) {
            String[] lineWords = line.split(" ");
            ArrayList<String> answers = new ArrayList<>();
            for (String POS : lineWords) {
                answers.add(POS);
            }
            correctResults.add(answers);
        }

        for (int i = 0; i < testResults.size(); i++) {
            for (int j = 0; j < testResults.get(i).size(); j++) {
                if (testResults.get(i).get(j).equals(correctResults.get(i).get(j))) {totalCorrect++;}
                else {totalIncorrect++;}
            }
        }
        System.out.println("The algorithm got " + (totalCorrect) + " out of " + (totalCorrect + totalIncorrect) + " correct using an unseen value of " + unseen);
    }

    public void displayResults(String[] parsedInput, List<String> path) {
        String result = "";
        for (int i = 0; i < parsedInput.length; i++) {
            result += parsedInput[i] + "/" + path.get(i) + " ";
        }
        System.out.println("Tagging result: " + result);
    }

    /**
     * fills maps for hardcoded test 1
     */
    public void fillMaps1() {

        transitionsMap.put("#", new HashMap<>());
        transitionsMap.get("#").put("NP", -1.6);
        transitionsMap.get("#").put("DET", -0.9);
        transitionsMap.get("#").put("PRO", -1.2);
        transitionsMap.get("#").put("MOD", -2.3);

        transitionsMap.put("NP", new HashMap<>());
        transitionsMap.get("NP").put("V", -0.7);
        transitionsMap.get("NP").put("VD", -0.7);

        transitionsMap.put("DET", new HashMap<>());
        transitionsMap.get("DET").put("N", 0.0);

        transitionsMap.put("PRO", new HashMap<>());
        transitionsMap.get("PRO").put("MOD", -1.6);
        transitionsMap.get("PRO").put("VD", -1.6);
        transitionsMap.get("PRO").put("V", -0.5);

        transitionsMap.put("MOD", new HashMap<>());
        transitionsMap.get("MOD").put("PRO", -0.7);
        transitionsMap.get("MOD").put("V", -0.7);

        transitionsMap.put("VD", new HashMap<>());
        transitionsMap.get("VD").put("DET", -1.1);
        transitionsMap.get("VD").put("PRO", -0.4);

        transitionsMap.put("N", new HashMap<>());
        transitionsMap.get("N").put("VD", -1.4);
        transitionsMap.get("N").put("V", -0.3);

        transitionsMap.put("V", new HashMap<>());
        transitionsMap.get("V").put("DET", -0.2);
        transitionsMap.get("V").put("PRO", -1.9);

        observationsMap.put("#", new HashMap<>());

        observationsMap.put("NP", new HashMap<>());
        observationsMap.get("NP").put("Jobs", -0.7);
        observationsMap.get("NP").put("will", -0.7);

        observationsMap.put("DET", new HashMap<>());
        observationsMap.get("DET").put("a", -1.3);
        observationsMap.get("DET").put("many", -1.7);
        observationsMap.get("DET").put("one", -1.7);
        observationsMap.get("DET").put("the", -1.0);

        observationsMap.put("VD", new HashMap<>());
        observationsMap.get("VD").put("saw", -1.1);
        observationsMap.get("VD").put("were", -1.1);
        observationsMap.get("VD").put("wore", -1.1);

        observationsMap.put("N", new HashMap<>());
        observationsMap.get("N").put("color", -2.4);
        observationsMap.get("N").put("cook", -2.4);
        observationsMap.get("N").put("fish", -1.0);
        observationsMap.get("N").put("jobs", -2.4);
        observationsMap.get("N").put("mine", -2.4);
        observationsMap.get("N").put("saw", -1.7);
        observationsMap.get("N").put("uses", -2.4);

        observationsMap.put("PRO", new HashMap<>());
        observationsMap.get("PRO").put("I", -1.9);
        observationsMap.get("PRO").put("many", -1.9);
        observationsMap.get("PRO").put("me", -1.9);
        observationsMap.get("PRO").put("mine", -1.9);
        observationsMap.get("PRO").put("you", -0.8);

        observationsMap.put("V", new HashMap<>());
        observationsMap.get("V").put("color", -2.1);
        observationsMap.get("V").put("cook", -1.4);
        observationsMap.get("V").put("eats", -2.1);
        observationsMap.get("V").put("fish", -2.1);
        observationsMap.get("V").put("has", -1.4);
        observationsMap.get("V").put("uses", -2.1);

        observationsMap.put("MOD", new HashMap<>());
        observationsMap.get("MOD").put("can", -0.7);
        observationsMap.get("MOD").put("will", -0.7);
    }

    /**
     * fills maps for hardcoded test 2
     */
    public void fillMaps2() {

        transitionsMap.put("#", new HashMap<>());
        transitionsMap.get("#").put("PRO", -0.69);
        transitionsMap.get("#").put("N", -0.69);

        transitionsMap.put("PRO", new HashMap<>());
        transitionsMap.get("PRO").put("V", 0.0);

        transitionsMap.put("N", new HashMap<>());
        transitionsMap.get("N").put("V", 0.0);

        transitionsMap.put("V", new HashMap<>());
        transitionsMap.get("V").put("N", -0.69);
        transitionsMap.get("V").put("ADJ", -0.69);

        observationsMap.put("#", new HashMap<>());

        observationsMap.put("PRO", new HashMap<>());
        observationsMap.get("PRO").put("he", 0.0);

        observationsMap.put("N", new HashMap<>());
        observationsMap.get("N").put("dogs", 0.0);

        observationsMap.put("V", new HashMap<>());
        observationsMap.get("V").put("likes", -0.69);
        observationsMap.get("V").put("are", -0.69);

        observationsMap.put("ADJ", new HashMap<>());
        observationsMap.get("ADJ").put("cool", 0.0);
    }

    /**
     * creates and runs pattern recognition
     * @param args
     * @throws IOException  if file not found
     */
    public static void main(String[] args) throws IOException {
        PatternRecognition test = new PatternRecognition();
        test.train("PS5/brown-train-sentences.txt", "PS5/brown-train-tags.txt");
        //test.fillMaps2();

        System.out.println("Type a sentence");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] parsedInput = input.split(" ");

        List<String> path = test.Viterbi(parsedInput);

        test.displayResults(parsedInput, path);

        test.testAccuracy("PS5/brown-test-sentences.txt", "PS5/brown-test-tags.txt");
    }
}