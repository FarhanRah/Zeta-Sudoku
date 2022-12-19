package com.example.sudoku;

import static org.junit.Assert.*;

import org.junit.Test;

public class WordTest {
    @Test
    public void getEnglish_isCorrect() {
        Word word = new Word("farhan", "faaan", "test");
        String actual = word.getEnglish();

        assertEquals("farhan", actual);
    }

    @Test
    public void getEnglish_isWrong() {
        Word word = new Word("rahmoon", "ramoon", "test2");
        String actual = word.getEnglish();

        assertNotEquals("farhan", actual);
    }

    @Test
    public void getFrench_isCorrect() {
        Word word = new Word("farhan", "faaan", "test");
        String actual = word.getFrench();

        assertEquals("faaan", actual);
    }

    @Test
    public void getFrench_isWrong() {
        Word word = new Word("rahmoon", "ramoon", "test2");
        String actual = word.getFrench();

        assertNotEquals("faaan", actual);
    }

    @Test
    public void getName_isCorrect() {
        Word word = new Word("farhan", "faaan", "test");
        String actual = word.getName();

        assertEquals("test", actual);
    }

    @Test
    public void getName_isWrong() {
        Word word = new Word("rahmoon", "ramoon", "test2");
        String actual = word.getName();

        assertNotEquals("test", actual);
    }

    @Test
    public void getId_isZeroWhenNotInitialized() {
        Word word = new Word("farhan", "faaan", "test");
        int actual = word.getId();

        assertEquals(0, actual);
    }

    @Test
    public void setId_isCorrect() {
        Word word = new Word("farhan", "faaan", "test");
        word.setId(21);
        int actual = word.getId();

        assertEquals(21, actual);
    }

    @Test
    public void setId_isWrong() {
        Word word = new Word("farhan", "faaan", "test");
        word.setId(21);
        int actual = word.getId();

        assertNotEquals(12, actual);
    }

    @Test
    public void getIncorrectCount_isZeroByDefault() {
        Word word = new Word("farhan", "faaan", "test");
        int actual = word.getIncorrectCount();

        assertEquals(0, actual);
    }

    @Test
    public void setIncorrectCount_isCorrect() {
        Word word = new Word("farhan", "faaan", "test");
        word.setIncorrectCount(20);
        int actual = word.getIncorrectCount();

        assertEquals(20, actual);
    }

    @Test
    public void setIncorrectCount_isWrong() {
        Word word = new Word("farhan", "faaan", "test");
        word.setIncorrectCount(20);
        int actual = word.getIncorrectCount();

        assertNotEquals(2, actual);
    }
}
