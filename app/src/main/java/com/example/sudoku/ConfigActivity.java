package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ncorti.slidetoact.SlideToActView;

public class ConfigActivity extends AppCompatActivity {
    private CheckBox listeningComprehensionCheckBox;
    private Button englishBtn;
    private Button frenchBtn;
    private ConstraintLayout mainLayout;
    private SlideToActView slideToStart;
    private int selectedLanguage;
    private int selectedGridSize;
    private String name;
    private Button selectedGridBtn;
    private Button selectedLanguageBtn;
    private Button currentBtn;
    private TextView add;
    private TableRow gridSizeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);

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

        // default values
        selectedLanguage = 0;
        selectedGridSize = 2;

        gridSizeTable = (TableRow) findViewById(R.id.tableRow);
        englishBtn = (Button) findViewById(R.id.englishBtn);
        frenchBtn = (Button) findViewById(R.id.frenchBtn);
        listeningComprehensionCheckBox = findViewById(R.id.listeningCheckBox);
        add = (TextView) findViewById(R.id.textView17);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedLanguage = bundle.getInt("language");
            selectedGridSize = bundle.getInt("gridSize");
            name = bundle.getString("name");
        }

        if(selectedLanguage == 1 || selectedLanguage == 21) {
            highlightButton(frenchBtn);
        }
        else {
            highlightButton(englishBtn);
        }

        currentBtn = (Button) gridSizeTable.getChildAt(selectedGridSize);
        gridSizeButtonClicked(currentBtn);
        add.setText(name);

        slideToStart = (SlideToActView) findViewById(R.id.slideToStart);
        slideToStart.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                openGameActivity(selectedLanguage, selectedGridSize);
            }
        });

        listeningComprehensionCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listeningComprehensionCheckBox.isChecked()) {
                    if (selectedLanguage == 0) {
                        selectedLanguage = 20;
                    } else if (selectedLanguage == 1) {
                        selectedLanguage = 21;
                    }
                } else {
                    if (selectedLanguage == 20) {
                        selectedLanguage = 0;
                    } else if (selectedLanguage == 21) {
                        selectedLanguage = 1;
                    }
                }
            }
        });

        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listeningComprehensionCheckBox.isChecked()) {
                    selectedLanguage = 20;
                } else {
                    selectedLanguage = 0;
                }

                if (selectedLanguageBtn != null) {
                    unHighlightButton(selectedLanguageBtn);
                }

                highlightButton(englishBtn);
            }
        });

        frenchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listeningComprehensionCheckBox.isChecked()) {
                    selectedLanguage = 21;
                } else {
                    selectedLanguage = 1;
                }

                if (selectedLanguageBtn != null) {
                    unHighlightButton(selectedLanguageBtn);
                }

                highlightButton(frenchBtn);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigActivity.this, WordLists.class);
                Bundle bun = new Bundle();
                bun.putInt("language", selectedLanguage);
                bun.putInt("gridSize", selectedGridSize);
                intent.putExtras(bun);
                startActivity(intent);
            }
        });

        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
    }

    public void gridSizeButtonClicked(View view) {
        Button btn = (Button) view;
        selectedGridSize = Integer.parseInt(btn.getTag().toString());
        if (selectedGridBtn != null) {
            unHighlightButton(selectedGridBtn);
        }

        selectedGridBtn = btn;
        btn.setBackgroundResource(R.drawable.game_button_highlighted);
    }

    public void openGameActivity(int language, int gridSize) {
        Intent intent = new Intent(this, GameActivity.class);
        // variable to indicate language: 0 indicates English and 1 indicates French
        // variable to indicate language: 20 indicated listening with English and 21 is listening with French
        Bundle b = new Bundle();
        b.putInt("language", language);
        b.putInt("gridSize", gridSize);
        b.putString("name", name);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void unHighlightButton(Button btn) {
        btn.setBackgroundResource(R.drawable.game_button);
    }

    public void highlightButton(Button btn) {
        selectedLanguageBtn = btn;
        btn.setBackgroundResource(R.drawable.game_button_highlighted);
    }

    public void goBack(View v) {
        Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
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
        boolean focusable = true; // tap outside the popup also dismiss it
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