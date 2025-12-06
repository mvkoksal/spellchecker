package edu.grinnell.csc207.spellchecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A spellchecker maintains an efficient representation of a dictionary for
 * the purposes of checking spelling and provided suggested corrections.
 */
public class SpellChecker {
    /** The number of letters in the alphabet. */
    private static final int NUM_LETTERS = 26;

    /** The path to the dictionary file. */
    private static final String DICT_PATH = "words_alpha.txt";

    /**
     * @param filename the path to the dictionary file
     * @return a SpellChecker over the words found in the given file.
     */
    public static SpellChecker fromFile(String filename) throws IOException {
        return new SpellChecker(Files.readAllLines(Paths.get(filename)));
    }

    /** A Node of the SpellChecker structure. */
    private class Node {
        HashMap<Character, Node> chars;
        boolean isEnd;
        public Node() {
            this.chars = new HashMap<>();
            this.isEnd = false;
        }
    }

    /** The root of the SpellChecker */
    private Node root;

    public SpellChecker(List<String> dict) {
        for (int i = 0; i < dict.size(); i++) {
            add(dict.get(i));
        }
    }

    public void add(String word) {
        char currentChar = ' ';
        Node firstNode = this.root;
        for (int i = 0; i < word.length(); i++) {
            currentChar = word.charAt(i);
            if (firstNode == null) {
                firstNode = new Node();
            } 
            if (!(firstNode.chars.containsKey(currentChar))) {
                firstNode.chars.put(currentChar, new Node());
                firstNode = firstNode.chars.get(currentChar);
            } else {
                firstNode = firstNode.chars.get(currentChar);
            }
        }
        firstNode.isEnd = true;
    }

    public boolean isWord(String word) {
        char currentChar = ' ';
        Node firstNode = root;
        for (int i = 0; i < word.length(); i++) {
            currentChar = word.charAt(i);
            if (firstNode == null) {
                firstNode = new Node();
            } 
            if (firstNode.chars.containsKey(currentChar)) {
                firstNode = firstNode.chars.get(currentChar);
            } else {
                return false;
            }
        }
        return true;
    }

    public List<String> getOneCharCompletions(String word) {
        char currentChar = ' ';
        Node firstNode = root;
        for (int i = 0; i < word.length(); i++) {
            currentChar = word.charAt(i);
            if (firstNode == null) {
                firstNode = new Node();
            } 
            if (firstNode.chars.containsKey(currentChar)) {
                firstNode = firstNode.chars.get(currentChar);
            }
        }

        List<String> wordList = new ArrayList<>();
        for (char key: firstNode.chars.keySet()) {
            if (firstNode.chars.get(key).isEnd) {
                wordList.add(word+key);
            }
        }
        return wordList;
    }

    public List<String> getOneCharEndCorrections(String word) {
        String shorterWord = "";
        // Removes the last letter of the word
        for (int i = 0; i< word.length() - 1; i ++) {
            shorterWord += word.charAt(i);
        }
        return getOneCharCompletions(shorterWord);
    }

    public List<String> getOneCharCorrections(String word) {
        char currentChar = ' ';
        Node firstNode = root;
        int index = 0;
        for (int i = 0; i < word.length(); i++) {
            currentChar = word.charAt(i);
            if (firstNode == null) {
                firstNode = new Node();
            } 
            if (firstNode.chars.containsKey(currentChar)) {
                firstNode = firstNode.chars.get(currentChar);
            } else {
                index = i;
                break;
            }
        }
        List<String> wordList = new ArrayList<>();

        for (char key: firstNode.chars.keySet()) {
            String first = word.substring(0, index - 1);
            String last = word.substring(index + 1, word.length() - 1);
            String newWord = first + key + last;
            if (isWord(newWord)) {
                wordList.add(newWord);
            }
        }
        return wordList;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java SpellChecker <command> <word>");
            System.exit(1);
        } else {
            String command = args[0];
            String word = args[1];
            SpellChecker checker = SpellChecker.fromFile(DICT_PATH);
            switch (command) {
                case "check": {
                    System.out.println(checker.isWord(word) ? "correct" : "incorrect");
                    System.exit(0);
                }

                case "complete": {
                    List<String> completions = checker.getOneCharCompletions(word);
                    for (String completion : completions) {
                        System.out.println(completion);
                    }
                    System.exit(0);
                }

                case "correct": {
                    List<String> corrections = checker.getOneCharEndCorrections(word);
                    for (String correction : corrections) {
                        System.out.println(correction);
                    }
                    System.exit(0);
                }

                default: {
                    System.err.println("Unknown command: " + command);
                    System.exit(1);
                }
            }
        }
    }
}
