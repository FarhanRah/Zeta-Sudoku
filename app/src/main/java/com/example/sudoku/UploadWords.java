package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class UploadWords extends AppCompatActivity {
    private TextInputLayout englishInput;
    private TextInputLayout frenchInput;
    private EditText editTextEnglish;
    private EditText editTextFrench;
    private Button btnAdd;
    private Button btnStart;
    private WordViewModel wordViewModel;
    private ConstraintLayout mainLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_words);

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

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        WordAdapter adapter = new WordAdapter();
        recyclerView.setAdapter(adapter);

        Bundle b = getIntent().getExtras();
        int number = b.getInt("number");
        String listName = "List " + number;

        // config page values
        int language = b.getInt("language");
        int gridSize = b.getInt("gridSize");

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        wordViewModel.getListWords(listName).observe(this, adapter::setWords);
        Log.e("UpdateWords", "adapter:words: " + adapter.words);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (adapter.getItemCount() != 1) {
                    wordViewModel.delete(adapter.getWordAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(UploadWords.this, "Word deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        englishInput = findViewById(R.id.english_input);
        frenchInput = findViewById(R.id.french_input);
        editTextEnglish = findViewById(R.id.english_edit_text);
        editTextFrench = findViewById(R.id.french_edit_text);
        btnAdd = findViewById(R.id.btn_add);
        btnStart = findViewById(R.id.btn_start);

        englishInput.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        englishInput.setEndIconVisible(true);

        frenchInput.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        frenchInput.setEndIconVisible(true);

        btnAdd.setOnClickListener(view -> {
            String englishWord = editTextEnglish.getText().toString();
            String frenchWord = editTextFrench.getText().toString();

            if (englishWord.trim().isEmpty() || frenchWord.trim().isEmpty()) {
                Toast.makeText(UploadWords.this, "Please fill in words", Toast.LENGTH_SHORT).show();
            }
            else {
                Word word = new Word(englishWord, frenchWord, listName);
                wordViewModel.insert(word);
            }
        });

        btnStart.setOnClickListener(view -> {
            if (adapter.getItemCount() < 12) {
                Toast.makeText(this, "Please add more words", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(this, ConfigActivity.class);
                Bundle bun = new Bundle();
                bun.putString("name", listName);
                bun.putInt("language", language);
                bun.putInt("gridSize", gridSize);
                intent.putExtras(bun);
                startActivity(intent);
            }
        });

        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
    }

    public void goBack(View v) {
        Intent intent = new Intent(UploadWords.this, WordLists.class);
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
        popupWindow.showAsDropDown(view, -180, 25);

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
        mainLayout.setBackgroundDrawable(new ColorDrawable(Color.parseColor(v.getTag().toString())));
    }
}