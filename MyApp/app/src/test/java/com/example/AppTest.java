package com.example;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class AppTest {

    // region buildPrefixMappings
    @Test
    @DisplayName("buildPrefixMappings: all prefixes exist for input words")
    void buildPrefixMappings_succeeds() {
        String[] words = new String[]{"rose", "oven"};
        Map<String, List<String>> prefixMappings = App.buildPrefixMappings(words);

        assertNotNull(prefixMappings.get(""));
        assertNotNull(prefixMappings.get("r"));
        assertNotNull(prefixMappings.get("ro"));
        assertNotNull(prefixMappings.get("ros"));
        assertNotNull(prefixMappings.get("rose"));
        assertNotNull(prefixMappings.get("o"));
        assertNotNull(prefixMappings.get("ov"));
        assertNotNull(prefixMappings.get("ove"));
        assertNotNull(prefixMappings.get("oven"));
    }

    @Test
    @DisplayName("buildPrefixMappings: fails for non-existent prefix")
    void buildPrefixMappings_fails() {
        String[] words = new String[]{"rose", "oven"};
        Map<String, List<String>> prefixMappings = App.buildPrefixMappings(words);

        assertNull(prefixMappings.get("abc"));
    }
    // endregion

    // region Input validation
    @Test
    @DisplayName("inputValidation: valid square size and input letters")
    void inputValidation_succeeds() {
        boolean ok = App.inputValidation("2", "abcd");
        assertTrue(ok);
    }

    @Test
    @DisplayName("inputValidation: valid square size and input letters (contains capitals)")
    void inputValidation_capitalLetters_succeeds() {
        boolean ok = App.inputValidation("2", "ABcd");
        assertTrue(ok);
    }

    @Test
    @DisplayName("inputValidation: square size not a valid int")
    void inputValidation_squareSizeNAN_fails() {
        boolean ok = App.inputValidation("a", "abcd");
        assertFalse(ok);
    }

    @Test
    @DisplayName("inputValidation: square size smaller than two")
    void inputValidation_squareSizeTooSmall_fails() {
        boolean ok = App.inputValidation("1", "a");
        assertFalse(ok);
    }

    @Test
    @DisplayName("inputValidation: input letters count not equal to squareSize^2")
    void inputValidation_lengthMismatch_fails() {
        boolean ok = App.inputValidation("2", "aabbccdd");
        assertFalse(ok);
    }

    @Test
    @DisplayName("inputValidation: input letters must be letters")
    void inputValidation_nonLetters_fails() {
        boolean ok = App.inputValidation("2", "abc!");
        assertFalse(ok);
    }
    // endregion

    // region stringToMultiset
    @Test
    @DisplayName("stringToMultiset: multiset counts characters correctly")
    void stringToMultiset_countsRemainingLettersCorrectly_succeeds() {
        Multiset<Character> ms = App.stringToMultiset("aaabc");
        assertEquals(3, ms.count('a'));
        assertEquals(1, ms.count('b'));
        assertEquals(1, ms.count('c'));
        assertEquals(5, ms.size());
    }
    // endregion

    // region canWordBeMadeFromRemainingLetters
    @Test
    @DisplayName("canWordBeMadeFromRemainingLetters: succeeds when letters supplied correctly")
    void canWordBeMade_succeeds() {
        Multiset<Character> remaining = HashMultiset.create();
        remaining.add('a', 2);
        remaining.add('b', 1);
        remaining.add('c', 1);

        boolean ok = App.canWordBeMadeFromRemainingLetters("aabc", remaining);
        assertTrue(ok);
    }

    @Test
    @DisplayName("canWordBeMadeFromRemainingLetters: fails when there aren't enough letters supplied")
    void canWordBeMade_fails() {
        Multiset<Character> remaining = HashMultiset.create();
        remaining.add('a', 1);
        remaining.add('b', 1);

        boolean ok = App.canWordBeMadeFromRemainingLetters("aab", remaining);
        assertFalse(ok);
    }
    // endregion

    // region correctSizeAndLetters
    @Test
    @DisplayName("correctSizeAndLetters: succeeds when squareSize is equal to input word, and multiset contains correct letter supply")
    void correctSizeAndLetters_succeeds() {
        Multiset<Character> multiset = HashMultiset.create();
        multiset.add('a');
        multiset.add('b');

        boolean ok = App.correctSizeAndLetters(2, multiset, "ab");
        assertTrue(ok);
    }

    @Test
    @DisplayName("correctSizeAndLetters: fails when squareSize is not equal to input word length")
    void correctSizeAndLetters_incorrectSize_fails() {
        Multiset<Character> multiset = HashMultiset.create();
        multiset.add('a');
        multiset.add('b');

        boolean ok = App.correctSizeAndLetters(3, multiset, "ab");
        assertFalse(ok);
    }

      @Test
    @DisplayName("correctSizeAndLetters: fails when the multiset doesn't have the right letters/quantities")
    void correctSizeAndLetters_multiSetNotFullySupplied_fails() {
        Multiset<Character> multiset = HashMultiset.create();
        multiset.add('c');
        multiset.add('d');

        // correct letters but not enough
        boolean ok1 = App.correctSizeAndLetters(2, multiset, "cc");
        assertFalse(ok1);

        // multiset doesn't contain the right letters
        boolean ok2 = App.correctSizeAndLetters(2, multiset, "ab" );
        assertFalse(ok2);
    }
    // endregion

    // region getNextPossibleWords
    @Test
    @DisplayName("getNextPossibleWords: returns words that match prefix and available letters")
    void getNextPossibleWords_succeeds() {
        String[] words = new String[]{"oven", "send", "rose","ends"};
        Map<String, List<String>> prefixMappings = App.buildPrefixMappings(words);

        Multiset<Character> multiset = HashMultiset.create();
         for (char character : "eeeeddoonnnsssrv".toCharArray()) {
            multiset.add(character);
        }

        // succeeds, ro is prefix of rose
        List<String> result = App.getNextPossibleWords("ro", prefixMappings, multiset);
        assertNotNull(result);
        assertEquals(
            Set.of("rose"),
            result.stream().collect(Collectors.toSet())
        );

        // fails, xy is not a prefix for any of the words in prefixMapping
        List<String> none = App.getNextPossibleWords("xy", prefixMappings, multiset);
        assertNull(none);
    }
    // endregion

    // region solveSquare
    @Test
    @DisplayName("solveSquare: succeeds using first example from spec")
    void solveSquare_succeeds() {
        String[] words = new String[]{"oven", "send", "rose","ends"};
        Map<String, List<String>> prefixMappings = App.buildPrefixMappings(words);

        Multiset<Character> multiset = HashMultiset.create();
        for (char character : "eeeeddoonnnsssrv".toCharArray()) {
            multiset.add(character);
        }

        List<String> partialSquare = new ArrayList<String>();
        boolean solved = App.solveSquare(partialSquare, 4, prefixMappings, multiset);

        assertTrue(solved);
        assertEquals(4, partialSquare.size());
        assertEquals(Set.of("rose", "oven", "ends", "send"), new HashSet<>(partialSquare));
    }

    @Test
    @DisplayName("solveSquare: no solution so fails")
    void solveSquare_fails() {
        String[] words = new String[]{"ab", "cd"};
        Map<String, List<String>> prefixMappings = App.buildPrefixMappings(words);

        Multiset<Character> multiset = HashMultiset.create();
        multiset.add('a');
        multiset.add('b');
        multiset.add('c');
        multiset.add('d');

        List<String> partialSquare = new ArrayList<String>();
        boolean solved = App.solveSquare(partialSquare, 2, prefixMappings, multiset);

        assertFalse(solved);
        assertTrue(partialSquare.isEmpty());
    }
    // endregion
}
