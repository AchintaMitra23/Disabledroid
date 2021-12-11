package com.example.disabledroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.disabledroid.SQLiteDatabase.DatabaseDayPlan;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.Objects;

public class DayPlanActivity extends AppCompatActivity {

    private TextView tv_show_date;
    private TextInputLayout add_plan_et;

    private DatabaseDayPlan databaseDayPlan;
    private TextToSpeech textToSpeech;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_plan);

        CalendarView calendarView = findViewById(R.id.calenderView);
        tv_show_date = findViewById(R.id.show_date_tv);
        add_plan_et = findViewById(R.id.day_planner_et);

        databaseDayPlan = new DatabaseDayPlan(this);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Language is supported", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            tv_show_date.setText(dayOfMonth+"/"+month+"/"+year);
            tv_show_date.setTextColor(Color.GREEN);
            tv_show_date.setTextSize(20.0f);
            add_plan_et.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void addDayPlan(View view) {
        if (tv_show_date.getText().equals("Please Select a Date from Calender")) {
            textToSpeech.speak("Please select a date", TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
        } else {
            if (Objects.requireNonNull(add_plan_et.getEditText()).getText().toString().isEmpty()) {
                textToSpeech.speak("Please add a plan to that date", TextToSpeech.QUEUE_FLUSH, null);
                add_plan_et.setError("Please add a plan to that date");
            } else {
                String plan = add_plan_et.getEditText().getText().toString();
                String date = tv_show_date.getText().toString();

                String msg = "You have selected the date " + date + " with the plan " + plan;
                textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);

                boolean checkInsertion = databaseDayPlan.insertPlan(date, plan);
                if (checkInsertion)
                    Toast.makeText(this, "Plan is saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Plan is not been saved", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), SavedPlanActivity.class)), 2500);
            }
        }
    }

    public void showPlans(View view) {
        startActivity(new Intent(getApplicationContext(), SavedPlanActivity.class));
    }
}