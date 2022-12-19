package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    WordList words;
    Toast conflictToast;
    PrefilledGenerator prefilledGenerator;
    TextView chosenCell;
    TextView previousChosenCell;
    RelativeLayout rl;
    static ConstraintLayout pauseLayout;
    TableLayout table;
    TableLayout buttons;
    int language = -1;
    int wordCount = 12;
    String name;
    boolean isGamePaused = false;
    WordViewModel wordViewModel;
    List<Word> dbWords;
    boolean gridGenerated = false;
    boolean previousState;
    boolean valid;
    List<String> EnglishWordList = null;
    List<String> FrenchWordList = null;
    TextToSpeech t1;

    static int gridSize = 9;
    int cellHeight = 3;
    int cellWidth = 3;

    static int[][] grid;
    boolean[][] isRed;

    // on below line we are creating variable for the timer
    private Chronometer chronometer;
    private long lastPause;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list", grid);
        outState.putSerializable("is_red", isRed);
        outState.putSerializable("main_grid_prefilled", prefilledGenerator.prefilled);
        outState.putSerializable("main_grid_solution", prefilledGenerator.solution);
        List<String> EnglishWordList = new ArrayList<>();
        List<String> FrenchWordList = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        int lang = b.getInt("language");
        outState.putInt("prev_lang", lang);

        for (int i = 0; i < gridSize; i++) {
            EnglishWordList.add(words.getEnglishWord(i));
            FrenchWordList.add(words.getFrenchWord(i));
        }
        outState.putStringArrayList("EnglishWordList_", (ArrayList<String>) EnglishWordList);
        outState.putStringArrayList("FrenchWordList_", (ArrayList<String>) FrenchWordList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // immersive sticky mode
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        Bundle b = getIntent().getExtras();

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    if (b != null) {
                        language = b.getInt("language");
                    }
                    if (language == 20) {
                        t1.setLanguage(Locale.FRENCH);
                    }
                    else {
                        t1.setLanguage(Locale.ENGLISH);
                    }
                    t1.setSpeechRate(0.40f);
                }
            }
        });

        if (b != null) {
            language = b.getInt("language");
            name = b.getString("name");
            gridSize = b.getInt("gridSize");
            if (gridSize == 0) {
                gridSize = 4;
                cellHeight = 2;
                cellWidth = 2;
            } else if(gridSize == 1) {
                gridSize = 6;
                cellHeight = 2;
                cellWidth = 3;
            } else if(gridSize == 2) {
                gridSize = 9;
                cellHeight = 3;
                cellWidth = 3;
            } else if(gridSize == 3) {
                gridSize = 12;
                cellHeight = 3;
                cellWidth = 4;
            }
        }

        grid = new int[gridSize][gridSize];
        isRed = new boolean[gridSize][gridSize];

        valid = true;

        if (savedInstanceState != null && savedInstanceState.getInt("prev_lang") != language)
            valid = false;

        int[][] same_grid;
        previousState = false;
        if (savedInstanceState != null && valid) {
            previousState = true;
            same_grid = (int[][]) savedInstanceState.getSerializable("list");
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    grid[i][j] = same_grid[i][j];
                }
            }
            EnglishWordList = savedInstanceState.getStringArrayList("EnglishWordList_");
            FrenchWordList = savedInstanceState.getStringArrayList("FrenchWordList_");
        }

        // connect database
        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        // Create the grid
        table = (TableLayout) findViewById(R.id.grid);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int minCellSize = displayMetrics.widthPixels / gridSize + 1;

        for (int i = 0; i < gridSize; i++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams();

            for (int j = 0; j < gridSize; j++) {
                TextView textView = new TextView(this);

                textView.setWidth(translateDpToPixel(0, getApplicationContext()));
                textView.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

                if (((j + 1) % cellWidth == 0 && (j != gridSize - 1)) && ((i + 1) % cellHeight == 0 && (i != gridSize - 1))) {
                    textView.setBackgroundResource(R.drawable.table_border_cell_vertical_and_horizontal);
                } else if ((j + 1) % cellWidth == 0 && (j != gridSize - 1)) {
                    textView.setBackgroundResource(R.drawable.table_border_cell_vertical);
                } else if ((i + 1) % cellHeight == 0 && (i != gridSize - 1)) {
                    textView.setBackgroundResource(R.drawable.table_border_cell_horizontal);
                } else {
                    textView.setBackgroundResource(R.drawable.table_border_cell);
                }

                if (gridSize == 12) {
                    textView.setTextSize(10);
                }

                textView.setGravity(Gravity.CENTER);
                textView.setMinimumWidth(minCellSize);
                textView.setMinimumHeight(minCellSize);
                textView.setSingleLine(true);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextColor(Color.parseColor("#000000"));
                String tag = String.valueOf(i * gridSize + j);
                textView.setTag(tag);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseCell(view);
                    }
                });
                rowParams.weight = 1.0f;

                tableRow.addView(textView, rowParams);
            }

            rowParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            rowParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            table.addView(tableRow);
        }


        // Create the buttons
        buttons = (TableLayout) findViewById(R.id.buttons);

        for (int i = 0; i < cellHeight; i++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams();

            for (int j = 0; j < cellWidth; j++) {
                Button btn = new Button(this);

                btn.setWidth(translateDpToPixel(0, getApplicationContext()));
                btn.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                btn.setSingleLine(true);
                btn.setBackgroundResource(R.drawable.game_button);
                btn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                btn.setTextColor(Color.parseColor("#000000"));
                String tag = String.valueOf(i * cellWidth + j);
                btn.setTag(tag);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonClicked(view);
                    }
                });
                rowParams.weight = 1.0f;

                tableRow.addView(btn, rowParams);
            }

            rowParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            rowParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            rowParams.setMargins(2,2,2,2);

            buttons.addView(tableRow);
        }

        if (savedInstanceState != null && valid) {
            isRed = (boolean[][]) savedInstanceState.getSerializable("is_red");
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (isRed[i][j]) {
                        ((TextView)((TableRow)table.getChildAt(i)).getChildAt(j)).setTextColor(Color.RED);
                    }
                }
            }
        }

        if (name == null) {
            words = new WordList(wordCount);
            if (previousState && valid) {
                words.englishWordList = EnglishWordList;
                words.frenchWordList = FrenchWordList;
            } else {
                words.randomWords(this);
                // add to db
                for (int i = 0; i < wordCount; i++) {
                    Word currentWord = new Word(words.getEnglishWord(i), words.getFrenchWord(i), "RANDOMLY_GENERATED");
                    wordViewModel.insert(currentWord);
                }
            }

            // fill buttons
            TableLayout buttons = (TableLayout) findViewById(R.id.buttons);
            int index = 0;
            for (int i = 0; i < buttons.getChildCount(); i++) {
                TableRow tr = (TableRow) buttons.getChildAt(i);
                for (int j = 0; j < tr.getChildCount(); j++) {
                    Button btn = (Button) tr.getChildAt(j);
                    if (language == 0 || language == 20) {
                        btn.setText(words.getEnglishWord(index));
                    } else {
                        btn.setText(words.getFrenchWord(index));
                    }
                    index++;
                }
            }

            // fill grid
            prefilledGenerator = new PrefilledGenerator(gridSize, cellHeight, cellWidth);
            if (previousState && valid) {
                prefilledGenerator.prefilled = (int[][]) savedInstanceState.getSerializable("main_grid_prefilled");
                prefilledGenerator.solution = (int[][]) savedInstanceState.getSerializable("main_grid_solution");
            }

            for (int i = 0; i < table.getChildCount(); i++) {
                TableRow tr = (TableRow) table.getChildAt(i);
                for (int j = 0; j < tr.getChildCount(); j++) {
                    TextView tv = (TextView) tr.getChildAt(j);
                    int wordIndex = prefilledGenerator.getPrefilledAt(i, j);
                    if (wordIndex == -1) {
                        continue;
                    } else if (language == 0) {
                        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                        tv.setText(words.getFrenchWord(wordIndex));
                    }
                    else if (language == 20 || language == 21) {
                        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                        tv.setText(String.valueOf(wordIndex + 1));
                    }
                    else {
                        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                        tv.setText(words.getEnglishWord(wordIndex));
                    }
                    addToGrid(i, j, wordIndex);
                }
            }


            if (previousState) {
                for (int i = 0; i < table.getChildCount(); i++) {
                    TableRow tr = (TableRow) table.getChildAt(i);
                    for (int j = 0; j < tr.getChildCount(); j++) {
                        TextView tv = (TextView) tr.getChildAt(j);
                        if (grid[i][j] != 0 && tv.getText().toString().equals("")) {
                            if (language == 0) {
                                String FrenchWord = "";
                                for (int k = 0; k < FrenchWordList.size(); k++) {
                                    if (FrenchWordList.get(k) == words.getFrenchWord(grid[i][j] - 1))
                                        FrenchWord = FrenchWordList.get(k);
                                }
                                tv.setText(FrenchWord);
                            }
                            else if (language == 20 || language == 21) {
                                tv.setText(String.valueOf(grid[i][j]));
                            }
                            else {
                                String EnglishWord = "";
                                for (int k = 0; k < EnglishWordList.size(); k++) {
                                    if (EnglishWordList.get(k) == words.getEnglishWord(grid[i][j] - 1))
                                        EnglishWord = EnglishWordList.get(k);
                                }
                                tv.setText(EnglishWord);
                            }
                        }
                    }
                }
            }
        } else {
            words = new WordList(wordCount);
            wordViewModel.getListWords(name).observe(this, new Observer<List<Word>>() {
                @Override
                public void onChanged(List<Word> list) {
                    if (!gridGenerated) {
                        if (previousState && valid) {
                            words.englishWordList = EnglishWordList;
                            words.frenchWordList = FrenchWordList;
                        }
                        dbWords = list;
                        for (int i = 0; i < wordCount; i++) {
                            if (dbWords != null) {
                                words.englishWordList.add(dbWords.get(i).getEnglish());
                                words.frenchWordList.add(dbWords.get(i).getFrench());
                            }
                        }
                        // fill buttons
                        TableLayout buttons = (TableLayout) findViewById(R.id.buttons);
                        int index = 0;
                        for (int i = 0; i < buttons.getChildCount(); i++) {
                            TableRow tr = (TableRow) buttons.getChildAt(i);
                            for (int j = 0; j < tr.getChildCount(); j++) {
                                Button btn = (Button) tr.getChildAt(j);
                                if (language == 0 || language == 20) {
                                    btn.setText(words.getEnglishWord(index));
                                } else {
                                    btn.setText(words.getFrenchWord(index));
                                }
                                index++;
                            }
                        }

                        // fill grid
                        prefilledGenerator = new PrefilledGenerator(gridSize, cellHeight, cellWidth);
                        if (previousState && valid) {
                            prefilledGenerator.prefilled = (int[][]) savedInstanceState.getSerializable("main_grid_prefilled");
                            prefilledGenerator.solution = (int[][]) savedInstanceState.getSerializable("main_grid_solution");
                        }

                        for (int i = 0; i < table.getChildCount(); i++) {
                            TableRow tr = (TableRow) table.getChildAt(i);
                            for (int j = 0; j < tr.getChildCount(); j++) {
                                TextView tv = (TextView) tr.getChildAt(j);
                                int wordIndex = prefilledGenerator.getPrefilledAt(i, j);
                                if (wordIndex == -1) {
                                    continue;
                                } else if (language == 0) {
                                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                                    tv.setText(words.getFrenchWord(wordIndex));
                                }
                                else if (language == 20 || language == 21) {
                                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                                    tv.setText(String.valueOf(wordIndex + 1));
                                }
                                else {
                                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                                    tv.setText(words.getEnglishWord(wordIndex));
                                }
                                addToGrid(i, j, wordIndex);
                            }
                        }

                        if (previousState) {
                            for (int i = 0; i < table.getChildCount(); i++) {
                                TableRow tr = (TableRow) table.getChildAt(i);
                                for (int j = 0; j < tr.getChildCount(); j++) {
                                    TextView tv = (TextView) tr.getChildAt(j);
                                    if (grid[i][j] != 0 && tv.getText().toString().equals("")) {
                                        if (language == 0) {
                                            String FrenchWord = "";
                                            for (int k = 0; k < FrenchWordList.size(); k++) {
                                                if (FrenchWordList.get(k) == words.getFrenchWord(grid[i][j] - 1))
                                                    FrenchWord = FrenchWordList.get(k);
                                            }
                                            tv.setText(FrenchWord);
                                        }
                                        else if (language == 20 || language == 21) {
                                            tv.setText(String.valueOf(grid[i][j]));
                                        }
                                        else {
                                            String EnglishWord = "";
                                            for (int k = 0; k < EnglishWordList.size(); k++) {
                                                if (EnglishWordList.get(k) == words.getEnglishWord(grid[i][j] - 1))
                                                    EnglishWord = EnglishWordList.get(k);
                                            }
                                            tv.setText(EnglishWord);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    gridGenerated = true;
                }
            });
        }
        Log.e("GameActivity", "words size:" + words.length());
        // create toast for invalid value
        conflictToast = Toast.makeText(this, "Conflict in row!", Toast.LENGTH_SHORT);
        rl = (RelativeLayout) findViewById(R.id.mainLayout);
        pauseLayout = (ConstraintLayout) findViewById(R.id.pauseScreen);
        chronometer = findViewById(R.id.timer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    public static int translateDpToPixel(int sizeInDp, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, context.getResources()
                        .getDisplayMetrics());
    }

    public static int translateDpToPixel(float sizeInDp, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, context.getResources()
                        .getDisplayMetrics());
    }

    public void addToGrid(int row, int column, int value) {
        grid[row][column] = value + 1;
        if (game_ended()) {
            game_ended_ui();
            chronometer.stop();
        }
    }

    public void unhighlight() {
        int cellRow = getRow(previousChosenCell);
        int cellColumn = getColumn(previousChosenCell);

        // whole row
        for (int column = 0; column < gridSize; column++) {
            TextView cell = rl.findViewWithTag(String.valueOf(cellRow * gridSize + column));

            if (((column + 1) % cellWidth == 0 && (column != gridSize - 1)) && ((cellRow + 1) % cellHeight == 0 && (cellRow != gridSize - 1))) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical_and_horizontal);
            } else if ((column + 1) % cellWidth == 0 && (column != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical);
            } else if ((cellRow + 1) % cellHeight == 0 && (cellRow != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_horizontal);
            } else {
                cell.setBackgroundResource(R.drawable.table_border_cell);
            }
        }

        // whole column
        for (int row = 0; row < gridSize; row++) {
            TextView cell = rl.findViewWithTag(String.valueOf(row * gridSize + cellColumn));

            if (((cellColumn + 1) % cellWidth == 0 && (cellColumn != gridSize - 1)) && ((row + 1) % cellHeight == 0 && (row != gridSize - 1))) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical_and_horizontal);
            } else if ((cellColumn + 1) % cellWidth == 0 && (cellColumn != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical);
            } else if ((row + 1) % cellHeight == 0 && (row != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_horizontal);
            } else {
                cell.setBackgroundResource(R.drawable.table_border_cell);
            }
        }
    }

    public void highlight() {
        if (previousChosenCell != null) {
            unhighlight();
        }

        int cellRow = getRow(chosenCell);
        int cellColumn = getColumn(chosenCell);

        // highlight the whole row
        for (int column = 0; column < gridSize; column++) {
            TextView cell = rl.findViewWithTag(String.valueOf(cellRow * gridSize + column));

            if (((column + 1) % cellWidth == 0 && (column != gridSize - 1)) && ((cellRow + 1) % cellHeight == 0 && (cellRow != gridSize - 1))) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical_and_horizontal_highlighted_light);
            } else if ((column + 1) % cellWidth == 0 && (column != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical_highlighted_light);
            } else if ((cellRow + 1) % cellHeight == 0 && (cellRow != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_horizontal_highlighted_light);
            } else {
                cell.setBackgroundResource(R.drawable.table_border_cell_highlighted_light);
            }
        }

        // highlight the whole column
        for (int row = 0; row < gridSize; row++) {
            TextView cell = rl.findViewWithTag(String.valueOf(row * gridSize + getColumn(chosenCell)));

            if (((cellColumn + 1) % cellWidth == 0 && (cellColumn != gridSize - 1)) && ((row + 1) % cellHeight == 0 && (row != gridSize - 1))) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical_and_horizontal_highlighted_light);
            } else if ((cellColumn + 1) % cellWidth == 0 && (cellColumn != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_vertical_highlighted_light);
            } else if ((row + 1) % cellHeight == 0 && (row != gridSize - 1)) {
                cell.setBackgroundResource(R.drawable.table_border_cell_horizontal_highlighted_light);
            } else {
                cell.setBackgroundResource(R.drawable.table_border_cell_highlighted_light);
            }
        }

        // highlight the cell itself
        if (((cellColumn + 1) % cellWidth == 0 && (cellColumn != gridSize - 1)) && ((cellRow + 1) % cellHeight == 0 && (cellRow != gridSize - 1))) {
            chosenCell.setBackgroundResource(R.drawable.table_border_cell_vertical_and_horizontal_highlighted_light);
        } else if ((cellColumn + 1) % cellWidth == 0 && (cellColumn != gridSize - 1)) {
            chosenCell.setBackgroundResource(R.drawable.table_border_cell_vertical_highlighted_light);
        } else if ((cellRow + 1) % cellHeight == 0 && (cellRow != gridSize - 1)) {
            chosenCell.setBackgroundResource(R.drawable.table_border_cell_horizontal_highlighted_light);
        } else {
            chosenCell.setBackgroundResource(R.drawable.table_border_cell_highlighted_light);
        }
    }

    public void chooseCell(View view) {
        if (chosenCell != null) {
            previousChosenCell = chosenCell;
        }
        TextView textview = (TextView) view;
        chosenCell = textview;
        if (!textview.getText().toString().equals("") && language == 20) {
            String toSpeak = words.getFrenchWord(Integer.parseInt(chosenCell.getText().toString()) - 1);
            t1.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
        } else if (!textview.getText().toString().equals("") && language == 21) {
            String toSpeak = words.getEnglishWord(Integer.parseInt(chosenCell.getText().toString()) - 1);
            t1.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
        }
        highlight();
    }

    public static int getRow(TextView cell) {
        String rowAndColumn = cell.getTag().toString();
        int row = Integer.parseInt(rowAndColumn) / gridSize;

        return row;
    }

    public static int getColumn(TextView cell) {
        String rowAndColumn = cell.getTag().toString();
        int column = Integer.parseInt(rowAndColumn) % gridSize;

        return column;
    }

    public void buttonClicked(View v) {
        if (chosenCell != null) {
            Button chosenButton = (Button) v;
            int row = getRow(chosenCell);
            int column = getColumn(chosenCell);
            int index = Integer.parseInt(chosenButton.getTag().toString().split("")[0]);

            if (prefilledGenerator.getPrefilledAt(row, column) != -1)
                return;
            if (language == 0) {
                chosenCell.setText(words.getFrenchWord(index));
            }
            else if (language == 20 || language == 21) {
                chosenCell.setText(String.valueOf(index + 1));
            } else {
                chosenCell.setText(words.getEnglishWord(index));
            }

            int conflict = validValue(row, column, index + 1);
            //If the word filled is wrong
            if (conflict == 0) {
                isRed[row][column] = false;
                chosenCell.setTextColor(Color.BLACK);
            }
            else {
                isRed[row][column] = true;
                chosenCell.setTextColor(Color.RED);
                wordViewModel.incrementIncorrectCount(words.getEnglishWord(index));
                if (conflict == 1) {
                    conflictToast.setText("Conflict in row!");
                    conflictToast.show();
                } else if (conflict == 2) {
                    conflictToast.setText("Conflict in column!");
                    conflictToast.show();
                } else if (conflict == 3) {
                    conflictToast.setText("Conflict in cell!");
                    conflictToast.show();
                }
            }
            addToGrid(row, column, index);
        }
    }

    public int validValue(int row, int col, int num) {
        for (int d = 0; d < gridSize; d++) {
            if (d == col) {
                continue;
            }
            if (grid[row][d] == num) {
                return 1;
            }
        }

        // check for same value in column
        for (int r = 0; r < gridSize; r++) {
            if (r == row) {
                continue;
            }
            if (grid[r][col] == num) {
                return 2;
            }
        }

        // check for same value in rectangle
        int rowStart = row - row % cellHeight;
        int columnStart = col - col % cellWidth;

        for (int r = rowStart; r < rowStart + cellHeight; r++) {
            for (int d = columnStart; d < columnStart + cellWidth; d++) {
                if (r == row && d == col) {
                    continue;
                }
                if (grid[r][d] == num) {
                    return 3;
                }
            }
        }
        return 0;
    }

    public void game_ended_ui() {
        ConstraintLayout overlay = (ConstraintLayout) findViewById(R.id.overlayGameEnded);
        overlay.setVisibility(View.VISIBLE);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_ended, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setOutsideTouchable(false);

        View view = findViewById(R.id.buttons);
        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, -75);

        // setup all the buttons
        Button home = (Button) popupView.findViewById(R.id.homeBtn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                grid = new int[gridSize][gridSize];
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button playAgain = (Button) popupView.findViewById(R.id.playAgainBtn);
        playAgain.setOnClickListener(new View.OnClickListener() {
            final Intent i = getIntent();
            final int val = i.getIntExtra("language",0);

            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                grid = new int[gridSize][gridSize];
                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                Bundle b = new Bundle();
                b.putInt("language", val);
                intent.putExtras(b);
                startActivity((intent));
            }
        });
    }

    public boolean game_ended() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j] == 0 || isRed[i][j]){
                    return false;
                }
            }
        }
       return true;
    }


    public void pauseGame(View v) {
        if (isGamePaused) {
            pauseLayout.setVisibility(View.GONE);
            isGamePaused = false;
            chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            chronometer.start();
        } else {
            pauseLayout.setVisibility(View.VISIBLE);
            isGamePaused = true;
            lastPause = SystemClock.elapsedRealtime();
            chronometer.stop();
        }
    }

    public void goBack(View v) {
        // onBackPressed();
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void colorChanger(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.color_changer, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAsDropDown(view, -200, 25);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void changeColor(View v) {
        rl.setBackgroundDrawable(new ColorDrawable(Color.parseColor(v.getTag().toString())));
    }
}
