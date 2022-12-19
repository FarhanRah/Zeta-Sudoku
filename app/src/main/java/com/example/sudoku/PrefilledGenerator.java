package com.example.sudoku;

import java.util.Arrays;
import java.util.Random;

public class PrefilledGenerator {
    // height and width of square grid
    public final int gridSize;

    // height and width of cell, normally 3x3
    public final int cellHeight;
    public final int cellWidth;

    // grid of solved sudoku
    public int[][] solution;

    // grid with values removed, -1 indicating removed
    public int[][] prefilled;

    /**
     * Get value in solution array at row and col
     *
     * @param row row
     * @param col col
     * @return value at [row, col]
     */
    public int getSolutionAt(int row, int col) {
        return solution[row][col];
    }

    /**
     * Get value in prefilled array at row and col
     *
     * @param row row
     * @param col col
     * @return value at [row, col]
     */
    public int getPrefilledAt(int row, int col) {
        return prefilled[row][col];
    }

    /**
     * Constructor for Prefilled Generator:
     * Call getSolutionAt(r, c) for solution value,
     * call getPrefilledAt(r, c) for grid value with removed values,
     *
     * @param gridSize height/width of grid
     * @param cellHeight height of cell
     * @param cellWidth width of cell
     */
    public PrefilledGenerator(int gridSize, int cellHeight, int cellWidth) {
        this.gridSize = gridSize;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;

        int[][] seed = generateSeed();
        solution = mixSeed(seed);
        prefilled = removeValues(solution);
    }

    /**
     * Generates a generic sudoku grid to be shuffled
     *
     * @return array representing valid sudoku solution
     */
    public int[][] generateSeed() {
        int[][] seed = new int[gridSize][gridSize];
        for (int row = 0; row < cellWidth; row++) {
            for (int i = 0; i < cellHeight; i++) {
                for (int j = 0; j < gridSize; j++) {
                    seed[row * cellHeight + i][j] = (j + i * cellWidth + row) % gridSize;
                }
            }
        }
        return seed;
    }

    /**
     * Mix up seed to generate random sudoku grid
     *
     * @param seed sudoku grid to be mixed
     * @return mixed up array representing sudoku solution
     */
    private int[][] mixSeed(int[][] seed) {
        int[][] mixedSeed = deepCopy(seed);

        Random rand = new Random();

        // swap rows 10 times
        for (int i = 0; i < 10; i++) {
            int first = (rand.nextInt(gridSize / cellHeight)) * cellHeight;
            int second = first + rand.nextInt(cellHeight - 1) + 1;
            mixedSeed = swapRows(mixedSeed, first, second);
        }

        // swap columns 10 times
        for (int i = 0; i < 10; i++) {
            int first = (rand.nextInt(gridSize / cellWidth)) * cellWidth;
            int second = first + rand.nextInt(cellWidth - 1) + 1;
            mixedSeed = swapColumns(mixedSeed, first, second);
        }

        // transpose grid 0 to 1 times if cell is square
        if (cellWidth == cellHeight) {
            int num = rand.nextInt(2);
            for (int i = 0; i < num; i++) {
                mixedSeed = transpose(mixedSeed);
            }
        }

        return mixedSeed;
    }

    /**
     * Remove values from grid and check whether grid still
     * has a unique solution
     *
     * @param grid sudoku grid
     * @return array representing sudoku grid with removed values
     */
    public int[][] removeValues(int[][] grid) {
        int[][] finalGrid = deepCopy(grid);

        Random rand = new Random();

        // decide how many values to remove
        int cellCount = gridSize * gridSize;
        int counter = cellCount / 2 + rand.nextInt(cellCount / 4);

        // prevent infinite loop
        int limit = cellCount;

        // start removing values
        while (counter > 0 && limit > 0) {
            int row = rand.nextInt(gridSize);
            int column = rand.nextInt(gridSize);

            // grid to test unique value
            int[][] testGrid = deepCopy(finalGrid);
            testGrid[row][column] = -1;

            // if solution is no longer unique, skip, set finalGrid to testGrid
            if (!solveSudoku(testGrid, row, column, finalGrid[row][column])) {
                counter--;
                finalGrid = testGrid;
            }
            limit--;
        }

        return finalGrid;
    }

    /**
     * Swaps two rows in array
     *
     * @param input array to swap rows
     * @param first index of first row to swap
     * @param second index of second row to swap
     * @return array with swapped rows
     */
    public int[][] swapRows(int[][] input, int first, int second) {
        int[][] ret = deepCopy(input);

        int[] tempFirst = ret[first];
        int[] tempSecond = ret[second];

        ret[first] = tempSecond;
        ret[second] = tempFirst;

        return ret;
    }

    /**
     * Swaps two columns in array
     *
     * @param input array to swap columns
     * @param first index of first column to swap
     * @param second index of second column to swap
     * @return array with swapped columns
     */
    public int[][] swapColumns(int[][] input, int first, int second) {
        int[][] ret = deepCopy(input);

        for (int i = 0; i < gridSize; i++) {
            int tempFirst = ret[i][first];
            int tempSecond = ret[i][second];

            ret[i][first] = tempSecond;
            ret[i][second] = tempFirst;
        }

        return ret;
    }

    /**
     * Transpose the grid of integers
     *
     * @param grid grid to be transposed
     * @return transposed integer array
     */
    public int[][] transpose(int[][] grid) {
        int[][] transposedGrid = deepCopy(grid);

        for (int i = 0; i < gridSize; i++) {
            for (int j = i + 1; j < gridSize; j++) {
                int temp = transposedGrid[i][j];
                transposedGrid[i][j] = transposedGrid[j][i];
                transposedGrid[j][i] = temp;
            }
        }

        return transposedGrid;
    }

    /**
     * deep copy array
     *
     * @param original original array
     * @return copy
     */
    public static int[][] deepCopy(int[][] original) {
        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }

    /**
     * Used in SolveSudoku(), determines if a value can
     * be placed in the sudoku grid with no conflict
     *
     * @param board sudoku grid
     * @param row row of checked value
     * @param col column of checked value
     * @param num value of checked value
     * @return true if value is valid
     */
    public boolean isSafe(int[][] board, int row, int col, int num) {
        // check row for same value in row
        for (int d = 0; d < gridSize; d++) {
            if (board[row][d] == num) {
                return false;
            }
        }

        // check for same value in column
        for (int r = 0; r < gridSize; r++) {
            if (board[r][col] == num) {
                return false;
            }
        }

        // check for same value in rectangle
        int rowStart = row - row % cellHeight;
        int columnStart = col - col % cellWidth;

        for (int r = rowStart; r < rowStart + cellHeight; r++) {
            for (int d = columnStart; d < columnStart + cellWidth; d++) {
                if (board[r][d] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determines is sudoku grid is solvable with excluded cell value
     *
     * @param board sudoku grid to be solved
     * @param rowExclude row of excluded value
     * @param columnExclude column of excluded value
     * @param exclude value to be excluded
     * @return true sudoku grid has solution where
     * the value at a (row, column)
     * is not a certain value
     */
    private boolean solveSudoku(int[][] board, int rowExclude, int columnExclude, int exclude) {
        int row = -1;
        int col = -1;

        // check if grid has empty values
        boolean isFull = true;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (board[i][j] == -1) {
                    row = i;
                    col = j;
                    isFull = false;
                    break;
                }
            }
            if (!isFull) {
                break;
            }
        }

        // if grid is full then a solution was found, return true
        if (isFull) {
            return true;
        }

        // bruteforce backtrack rows to find alternate solution
        for (int num = 0; num < gridSize; num++) {

            // skip if currently attempted number has the same value and grid position of the removed number
            if (row == rowExclude && col == columnExclude && num == exclude) {
                continue;
            }

            // backtrack rows
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                if (solveSudoku(board, rowExclude, columnExclude, exclude)) {
                    return true;
                } else {
                    board[row][col] = -1;
                }
            }
        }

        return false;
    }
}
