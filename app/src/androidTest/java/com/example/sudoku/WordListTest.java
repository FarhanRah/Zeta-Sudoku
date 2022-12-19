package com.example.sudoku;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class WordListTest {
    @Test
    public void getEnglishWord_returnsCorrectWord() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        String expected = wordList.englishWordList.get(0);
        String actual = wordList.getEnglishWord(0);

        assertEquals(expected, actual);
    }

    @Test
    public void getEnglishWord_returnsWrongWord() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        String expected = wordList.englishWordList.get(0);
        String actual = wordList.getEnglishWord(1);

        assertNotEquals(expected, actual);
    }

    @Test
    public void getFrenchWord_returnsCorrectWord() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        String expected = wordList.frenchWordList.get(0);
        String actual = wordList.getFrenchWord(0);

        assertEquals(expected, actual);
    }

    @Test
    public void getFrenchWord_returnsWrongWord() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        String expected = wordList.frenchWordList.get(0);
        String actual = wordList.getFrenchWord(1);

        assertNotEquals(expected, actual);
    }

    @Test
    public void length_returnsCorrectSize() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        int expected = 9;
        int actual = wordList.length();

        assertEquals(expected, actual);
    }

    @Test
    public void length_returnsWrongSize() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        int expected = 10;
        int actual = wordList.length();

        assertNotEquals(expected, actual);
    }

    @Test
    public void loadJSONFromAssets_returnsNotNull() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String actual = WordList.loadJSONFromAssets(appContext);

        assertNotNull(actual);
    }

    @Test
    public void wordList_isUniqueEnglishWords() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        boolean isUnique = true;
        List<String> englishWords = wordList.englishWordList;
        Set<String> lump = new HashSet<String>();

        for (String word : englishWords) {
            if (lump.contains(word)) {
                isUnique = false;
                break;
            }

            lump.add(word);
        }

        assertTrue(isUnique);
    }

    @Test
    public void wordList_isUniqueFrenchWords() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordList wordList = new WordList(9);
        wordList.randomWords(appContext);

        boolean isUnique = true;
        List<String> frenchWords = wordList.frenchWordList;
        Set<String> lump = new HashSet<String>();

        for (String word : frenchWords) {
            if (lump.contains(word)) {
                isUnique = false;
                break;
            }

            lump.add(word);
        }

        assertTrue(isUnique);
    }
}


