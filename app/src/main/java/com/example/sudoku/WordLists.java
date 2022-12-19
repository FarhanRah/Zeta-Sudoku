package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class WordLists extends AppCompatActivity {
    Button btnAdd;
    private WordViewModel wordViewModel;
    private WordListAdapter adapter;
    private int language;
    private int gridSize;
    private ConstraintLayout mainLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        // config page values
        if (b != null) {
            language = b.getInt("language");
            gridSize = b.getInt("gridSize");
        }

        setContentView(R.layout.activity_word_lists);

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

        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(view -> openUploadActivity());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new WordListAdapter();
        recyclerView.setAdapter(adapter);

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        wordViewModel.getListNames().observe(this, adapter::setNames);

        adapter.setOnItemClickListener(new WordListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(WordLists.this, UploadWords.class);
                int number = position;
                Bundle b = new Bundle();
                b.putInt("number", number);
                b.putInt("language", language);
                b.putInt("gridSize", gridSize);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
    }

    public void openUploadActivity() {
        Intent intent = new Intent(this, UploadWords.class);
        int number = adapter.getItemCount() + 1;
        Bundle b = new Bundle();
        b.putInt("number", number);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void goBack(View v) {
        Intent intent = new Intent(WordLists.this, MainActivity.class);
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