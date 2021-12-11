package com.example.disabledroid;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.disabledroid.SQLiteDatabase.DatabaseDayPlan;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SavedPlanActivity extends AppCompatActivity {

    private List<String> list;
    private ArrayAdapter<String> arrayAdapter;

    private DatabaseDayPlan databaseDayPlan;
    private AlertDialog.Builder builder;
    private TextToSpeech textToSpeech;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_plan);

        ListView saved_plan_listView = findViewById(R.id.saved_plan_listView);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.plan_layout, list);
        saved_plan_listView.setAdapter(arrayAdapter);

        databaseDayPlan = new DatabaseDayPlan(this);
        builder = new AlertDialog.Builder(this);

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

        saved_plan_listView.setOnItemLongClickListener((parent, view, position, id) -> {
            View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update_delete_plan_dialog, null);
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alert_background));

            Button update_plan = view1.findViewById(R.id.plan_update_btn);
            Button delete_plan = view1.findViewById(R.id.plan_delete_btn);

            update_plan.setOnClickListener(v -> {
                updatePlan(list.get(position), position);
                alertDialog.dismiss();
            });
            delete_plan.setOnClickListener(v -> {
                String[] strgs = list.get(position).split(" ");
                boolean checkDeletion = databaseDayPlan.deletePlan(strgs[1]);
                if (checkDeletion) {
                    textToSpeech.speak("Plan is deleted", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(SavedPlanActivity.this, "Plan is deleted", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SavedPlanActivity.this, "Plan is not deleted", Toast.LENGTH_SHORT).show();
                    textToSpeech.speak("Plan is not deleted", TextToSpeech.QUEUE_FLUSH, null);
                }
                alertDialog.dismiss();
            });
            alertDialog.show();
            return true;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updatePlan(String str, int position) {
        View view2 = LayoutInflater.from(this).inflate(R.layout.update_plan, null);
        builder.setView(view2);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alert_background));

        TextInputLayout et_update = view2.findViewById(R.id.et_plan);
        Button update = view2.findViewById(R.id.button_update_plan);

        update.setOnClickListener(v -> {
            String update_plan = Objects.requireNonNull(et_update.getEditText()).getText().toString();
            if (update_plan.isEmpty()) {
                et_update.setError("Please enter new plan");
            } else {
                String[] strgs = str.split(" ");
                boolean checkUpdation = databaseDayPlan.updatePlan(strgs[1], update_plan);
                if (checkUpdation) {
                    textToSpeech.speak("Plan is updated", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(SavedPlanActivity.this, "Plan is updated", Toast.LENGTH_SHORT).show();
                    String newPlan = databaseDayPlan.getParticularPlan(strgs[1]);
                    list.set(position, "DATE: " + strgs[1] + " \nPLAN: " + newPlan);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    textToSpeech.speak("Plan is not updated", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(SavedPlanActivity.this, "Plan is not updated", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Cursor cursor = databaseDayPlan.getPlan();
        while(cursor.moveToNext()) {
            list.add("DATE: " + cursor.getString(0) + " \nPLAN: " + cursor.getString(1));
            arrayAdapter.notifyDataSetChanged();
        }
    }
}