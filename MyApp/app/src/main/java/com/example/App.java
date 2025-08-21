package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.util.FileToArrayReader;
import com.example.util.ITextFileReader;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class App {
    public static void main(String[] args) {
        if (!inputValidation(args[0], args[1])) {
            return;
        }

        int squareSize = Integer.parseInt(args[0]);
        String letters = args[1].toLowerCase();
        String fileName = args[2];

        // pull in valid words
        ITextFileReader fr = new FileToArrayReader();
        String[] words = fr.ReadTextFile(fileName);

        Multiset<Character> remainingLetters = stringToMultiset(letters);

        // filter to only words of correct length and letter composition
        words = Arrays.stream(words)
            .filter(s -> correctSizeAndLetters(squareSize, remainingLetters, s))
            .map(s -> s.toLowerCase())
            .toArray(String[]::new);

        // create a dictionary of all word prefixes and their possible word mappings
        Map<String, List<String>> prefixMappings = buildPrefixMappings(words);

        List<String> partialSquare = new ArrayList<String>();
        if (solveSquare(partialSquare, squareSize, prefixMappings, remainingLetters)) {
            for (String word : partialSquare) {
                System.out.println(word);
            }
        }
        else {
            System.out.println("Not solvable");
        }
    }

    static Map<String, List<String>> buildPrefixMappings(String[] words) {
        Map<String, List<String>> prefixMappings = new HashMap<>();
           for (String word : words) {
            for (int letterPos = 0; letterPos <= word.length(); letterPos++) { 
                String substring = word.substring(0, letterPos);
                prefixMappings.computeIfAbsent(substring, a -> new ArrayList<>()).add(word);
            }
        }
        return prefixMappings;
    }

    static boolean correctSizeAndLetters(int size, Multiset<Character> multiset, String input) {
        return (input.length() == size && canWordBeMadeFromRemainingLetters(input, multiset));
    }

    static Multiset<Character> stringToMultiset(String word) {
        Multiset<Character> multiset = HashMultiset.create();
        for (char character : word.toCharArray()) {
            multiset.add(character);
        }
        return multiset;
    }

    static boolean canWordBeMadeFromRemainingLetters(String word, Multiset<Character> remainingLettersSet) {
        Multiset<Character> requiredSet = stringToMultiset(word);
        return Multisets.containsOccurrences(remainingLettersSet, requiredSet);
    }

    static boolean inputValidation(String squareSize, String letters) {
        try {
            int squareSizeInt = Integer.parseInt(squareSize);

            if (squareSizeInt < 2) {
                System.out.println("Square size must be at least two");
                return false;
            }

            if (Math.pow(squareSizeInt, 2) != letters.length()) {
                System.out.println("Input string must be as long as the size of the square, squared");
                return false;
            }

            for (char character: letters.toCharArray()) {
                if (!Character.isLetter(character)) {
                    System.out.println("Input string must contain only letters");
                    return false;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error parsing square size: " + e);
            return false;
        }

        return true;
    }

    static List<String> getNextPossibleWords(String mustStartWith, Map<String, List<String>> prefixMappings, Multiset<Character> remainingLetters) {
        // use the prefix mappings dictionary to quickly find any words that start with mustStartWith
        List<String> possibleWords = prefixMappings.get(mustStartWith);

        if (possibleWords != null) {
            // then filter out any words from this shortlist that can't be made using remaining letters
            return possibleWords
                .stream()
                .filter(word -> canWordBeMadeFromRemainingLetters(word, remainingLetters))
                .toList();
        }
        else {
            return null;
        }
    }

    static boolean solveSquare(List<String> partialSquare, int squareSize, Map<String, List<String>> prefixMappings, Multiset<Character> remainingLetters) {
        int partialSquareSize = partialSquare.size();

        if (partialSquare.size() < squareSize) {
            String mustStartWith = "";

            // find what the next word can/should start with, by looking at the letters in column [partialSquareSize] of preceding words
            // on first pass this will be an empty string
            for (int row = 0; row < partialSquareSize; row++) {
                mustStartWith += partialSquare.get(row).charAt(partialSquareSize);
            }

            // get all words that both start with the above, and that we still have letters for
            List<String> nextPossibleWords = getNextPossibleWords(mustStartWith, prefixMappings, remainingLetters);

            // try out each possible word by adding it to the partialSquare, and consuming the letters it's made up of, then continue recursion
            if (nextPossibleWords != null) {
                for (String word: nextPossibleWords) {

                    partialSquare.addLast(word);
                    for (char character : word.toCharArray()) {
                        remainingLetters.remove(character);
                    }

                    if (solveSquare(partialSquare, squareSize, prefixMappings, remainingLetters)) {
                        return true;
                    }

                    partialSquare.removeLast();
                    for (char character : word.toCharArray()) {
                        remainingLetters.add(character);
                    }
                }
            }

            return false;
        }

        return true;
    }

}
