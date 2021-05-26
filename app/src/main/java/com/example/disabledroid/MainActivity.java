package com.example.disabledroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_settings :
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.navigation_chat_bot :
                startActivity(new Intent(getApplicationContext(), ChatbotActivity.class));
                break;
            case R.id.navigation_share :
                shareApp();
                break;
        }
        return true;
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String subject = "Share App";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            String text = "https://play.google.com/store/apps/details?id = " + getApplication().getPackageName() + "\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);

            startActivity(Intent.createChooser(shareIntent, "Share By :- "));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void todo(View view) {
        startActivity(new Intent(getApplicationContext(), TodoActivity.class));
    }

    public void day_planner(View view) {
        startActivity(new Intent(getApplicationContext(), DayPlanActivity.class));
    }

    public void location(View view) {
        startActivity(new Intent(getApplicationContext(), LocationActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.get(0).equals("open to do list")) {
                    View view = LayoutInflater.from(this).inflate(R.layout.activity_todo, null);
                    todo(view);
                } else if (result.get(0).equals("open current location")) {
                    View view = LayoutInflater.from(this).inflate(R.layout.activity_location, null);
                    location(view);
                } else if (result.get(0).equals("open day planner")) {
                    View view = LayoutInflater.from(this).inflate(R.layout.activity_day_plan, null);
                    day_planner(view);
                } else if (result.get(0).equals("open settings")) {
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                } else if (result.get(0).equals("open chatbot")) {
                    startActivity(new Intent(getApplicationContext(), ChatbotActivity.class));
                } else if (result.get(0).equals("shareit")) {
                    shareApp();
                }
            }
        }
    }
}