package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private Button startGame;
    private Button uploadWords;
    private Button statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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

        startGame = (Button) findViewById(R.id.btnStartGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStartGameActivity();
            }
        });

        uploadWords = (Button) findViewById(R.id.btnUpload);
        uploadWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWordListsActivity();
            }
        });

        statistics = (Button) findViewById(R.id.btnStatistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStatisticsActivity();
            }
        });
    }

    public void openStartGameActivity() {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }

    public void openWordListsActivity() {
        Intent intent = new Intent(this, WordLists.class);
        startActivity(intent);
    }

    public void openStatisticsActivity() {
        Intent intent = new Intent(this, DifficultWords.class);
        startActivity(intent);
    }
}