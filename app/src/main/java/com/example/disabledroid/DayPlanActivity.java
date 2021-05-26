package com.example.disabledroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disabledroid.SQLiteDatabase.DatabaseDayPlan;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class DayPlanActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tv_show_date;
    private TextInputLayout add_plan_et;

    private DatabaseDayPlan databaseDayPlan;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_plan);

        calendarView = findViewById(R.id.calenderView);
        tv_show_date = findViewById(R.id.show_date_tv);
        add_plan_et = findViewById(R.id.day_planner_et);

        databaseDayPlan = new DatabaseDayPlan(this);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Language is supported", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                tv_show_date.setText(dayOfMonth+"/"+month+"/"+year);
                tv_show_date.setTextColor(Color.GREEN);
                tv_show_date.setTextSize(20.0f);
                add_plan_et.setVisibility(View.VISIBLE);
            }
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
            return;
        } else {
            if (add_plan_et.getEditText().getText().toString().isEmpty()) {
                textToSpeech.speak("Please add a plan to that date", TextToSpeech.QUEUE_FLUSH, null);
                add_plan_et.setError("Please add a plan to that date");
                return;
            } else {
                String plan = add_plan_et.getEditText().getText().toString();
                String date = tv_show_date.getText().toString();

                String msg = "You have selected the date " + date + " with the plan " + plan;
                textToSpeech.speak(msg.toString(), TextToSpeech.QUEUE_FLUSH, null);

                boolean checkInsertion = databaseDayPlan.insertPlan(date, plan);
                if (checkInsertion == true)
                    Toast.makeText(this, "Plan is saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Plan is not been saved", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), SavedPlanActivity.class));
                    }
                }, 2500);
            }
        }
    }

    public void showPlans(View view) {
        startActivity(new Intent(getApplicationContext(), SavedPlanActivity.class));
    }
}