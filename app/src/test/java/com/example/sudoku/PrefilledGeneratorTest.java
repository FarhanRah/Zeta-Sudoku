package com.example.sudoku;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;

public class PrefilledGeneratorTest {
    @Test
    public void getSolutionAt_isCorrect() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int expected = prefilledGenerator.solution[0][0];
        int actual = prefilledGenerator.getSolutionAt(0, 0);

        assertEquals(expected, actual);
    }

    @Test
    public void getPrefilledAt_isCorrect() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int expected = prefilledGenerator.prefilled[0][0];
        int actual = prefilledGenerator.getPrefilledAt(0, 0);

        assertEquals(expected, actual);
    }

    @Test
    public void generateSeed_correctlyShufflesTheGrid() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int[][] generatedSeed = prefilledGenerator.generateSeed();
        int[][] unShuffled = new int[9][9];

        boolean hasSeedBeenShuffled = true;

        if (generatedSeed == unShuffled) {
            hasSeedBeenShuffled = false;
        }

        assertTrue(hasSeedBeenShuffled);
    }

    @Test
    public void swapRows_correctlySwapsTheRows() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int[][] input = {{1, 2}, {2, 1}};
        int[][] swappedInput = prefilledGenerator.swapRows(input, 0, 1);

        assertEquals(input[1][0], swappedInput[0][0]);
        assertEquals(input[1][1], swappedInput[0][1]);
    }

    @Test
    public void swapColumns_correctlySwapsTheColumns() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int[][] input = {{1, 2}, {2, 1}, {2, 1}, {2, 1}, {2, 1}, {2, 1}, {2, 1}, {2, 1}, {2, 1}};
        int[][] swappedInput = prefilledGenerator.swapColumns(input, 0, 1);

        assertEquals(input[1][0], swappedInput[0][0]);
        assertEquals(input[1][1], swappedInput[0][1]);
    }

    @Test
    public void transpose_isCorrect() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int[][] matrix = {
                {0, 1, 2, 3, 4, 5, 6, 7, 8},
                {9, 10, 11, 12, 13, 14, 15, 16, 17},
                {18, 19, 20, 21, 22, 23, 24, 25, 26},
                {27, 28, 29, 30, 31, 32, 33, 34, 35},
                {36, 37, 38, 39, 40, 41, 42, 43, 44},
                {45, 46, 47, 48, 49, 50, 51, 52, 53},
                {54, 55, 56, 57, 58, 59, 60, 61, 62},
                {63, 64, 65, 66, 67, 68, 69, 70, 71},
                {72, 73, 74, 75, 76, 77, 78, 79, 80}
        };
        int[][] transposedMatrix = prefilledGenerator.transpose(matrix);
        int[][] expected = {
                {0, 9, 18, 27, 36, 45, 54, 63, 72},
                {1, 10, 19, 28, 37, 46, 55, 64, 73},
                {2, 11, 20, 29, 38, 47, 56, 65, 74},
                {3, 12, 21, 30, 39, 48, 57, 66, 75},
                {4, 13, 22, 31, 40, 49, 58, 67, 76},
                {5, 14, 23, 32, 41, 50, 59, 68, 77},
                {6, 15, 24, 33, 42, 51, 60, 69, 78},
                {7, 16, 25, 34, 43, 52, 61, 70, 79},
                {8, 17, 26, 35, 44, 53, 62, 71, 80}
        };
        int[] actualFirstRow = {0, 0, 0, 0, 0, 0, 0, 0, 0};

        assertEquals(expected, transposedMatrix);
    }

    @Test
    public void deepCopy_isCorrect() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int[][] expected = {{1, 2}, {2, 1}};
        int[][] actual = prefilledGenerator.deepCopy(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void prefilledGenerator_lengthIsCorrect() {
        PrefilledGenerator prefilledGenerator = new PrefilledGenerator(9, 3, 3);

        int expected = 9;
        int actual = prefilledGenerator.solution.length;

        assertEquals(expected, actual);
    }
}