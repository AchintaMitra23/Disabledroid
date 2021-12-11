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

import com.example.disabledroid.SQLiteDatabase.DatabaseLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavedLocationActivity extends AppCompatActivity {

    private List<String> list;
    private ArrayAdapter<String> arrayAdapter;

    private DatabaseLocation databaseLocation;
    private AlertDialog.Builder builder;
    private TextToSpeech textToSpeech;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_location);

        ListView saved_location_listView = findViewById(R.id.saved_location_listView);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.location_layout, list);
        saved_location_listView.setAdapter(arrayAdapter);

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

        databaseLocation = new DatabaseLocation(this);
        builder = new AlertDialog.Builder(this);

        saved_location_listView.setOnItemLongClickListener((parent, view, position, id) -> {
            View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.delete_location_dialog, null);
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alert_background));

            Button dlt_location = view1.findViewById(R.id.location_delete_btn);
            dlt_location.setOnClickListener(v -> {
                boolean checkDeletion = databaseLocation.deleteLocation(list.get(position));
                if (checkDeletion) {
                    list.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                    textToSpeech.speak("Location is deleted", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(SavedLocationActivity.this, "Location is deleted", Toast.LENGTH_SHORT).show();
                } else {
                    textToSpeech.speak("Location is not deleted", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(SavedLocationActivity.this, "Location is not deleted", Toast.LENGTH_SHORT).show();
                }

                alertDialog.dismiss();
            });
            alertDialog.show();
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Cursor cursor = databaseLocation.getLocation();

        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}