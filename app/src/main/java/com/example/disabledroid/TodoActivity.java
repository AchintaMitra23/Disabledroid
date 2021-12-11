package com.example.disabledroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.disabledroid.SQLiteDatabase.DatabaseTODO;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TodoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private List<String> list;
    private ArrayAdapter<String> arrayAdapter;

    private AlertDialog.Builder builder;
    private DatabaseTODO databaseTODO;
    private TextToSpeech textToSpeech;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        SearchView searchView = findViewById(R.id.searchView);
        ListView todoListView = findViewById(R.id.todo_list_view);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.task_layout, list);
        todoListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        builder = new AlertDialog.Builder(this);
        databaseTODO = new DatabaseTODO(this);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });

        todoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update_delete_dialogbox, null);
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alert_background));

            Button update = view1.findViewById(R.id.task_update_btn);
            Button delete = view1.findViewById(R.id.task_delete_btn);

            update.setOnClickListener(v -> {
                update_task_view(position);
                alertDialog.dismiss();
            });
            delete.setOnClickListener(v -> {
                boolean checkDeletion = databaseTODO.deleteTask(list.get(position));
                if (checkDeletion) {
                    textToSpeech.speak("Task is deleted", TextToSpeech.QUEUE_FLUSH, null);
                    //Toast.makeText(TodoActivity.this, "Task is deleted", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    textToSpeech.speak("Task is not deleted", TextToSpeech.QUEUE_FLUSH, null);
                    //Toast.makeText(TodoActivity.this, "Task is not deleted", Toast.LENGTH_SHORT).show();
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
        Cursor cursor = databaseTODO.getTask();
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void update_task_view(int position) {
        try {
            View view1 = LayoutInflater.from(this).inflate(R.layout.add_update_task, null);
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alert_background));

            TextInputLayout et_task = view1.findViewById(R.id.et_task);
            Button actionBtn = view1.findViewById(R.id.action_btn);

            actionBtn.setText("UPDATE TASK");
            actionBtn.setOnClickListener(v -> {
                String taskStr = Objects.requireNonNull(et_task.getEditText()).getText().toString();
                if (taskStr.isEmpty()) {
                    et_task.setError("Please enter the task");
                } else {
                    boolean checkUpdate = databaseTODO.updateTask(list.get(position), taskStr);
                    if (checkUpdate)
                        textToSpeech.speak("Task is updated", TextToSpeech.QUEUE_FLUSH, null);
                        //Toast.makeText(TodoActivity.this, "Task is updated", Toast.LENGTH_SHORT).show();
                    else
                        textToSpeech.speak("Task is not updated", TextToSpeech.QUEUE_FLUSH, null);
                        //Toast.makeText(TodoActivity.this, "Task is not updated", Toast.LENGTH_SHORT).show();
                    list.set(position, taskStr);
                    arrayAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void add_text_task(View view) {
        try {
            View view1 = LayoutInflater.from(this).inflate(R.layout.add_update_task, null);
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alert_background));

            TextInputLayout et_task = view1.findViewById(R.id.et_task);
            Button actionBtn = view1.findViewById(R.id.action_btn);

            actionBtn.setText("ADD TASK");
            actionBtn.setOnClickListener(v -> {
                String taskStr = Objects.requireNonNull(et_task.getEditText()).getText().toString();
                if (taskStr.isEmpty()) {
                    et_task.setError("Please enter the task");
                } else {
                    list.add(taskStr);
                    arrayAdapter.notifyDataSetChanged();
                    boolean checkInsertion = databaseTODO.insertTask(taskStr);
                    if (checkInsertion)
                        textToSpeech.speak("Task is saved", TextToSpeech.QUEUE_FLUSH, null);
                        //Toast.makeText(TodoActivity.this, "Task is saved", Toast.LENGTH_SHORT).show();
                    else
                        textToSpeech.speak("Task is not saved", TextToSpeech.QUEUE_FLUSH, null);
                        //Toast.makeText(TodoActivity.this, "Task is not saved", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void add_voice_task(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e) {
            Toast.makeText(TodoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                list.add(result.get(0));
                arrayAdapter.notifyDataSetChanged();
                boolean checkInsertion = databaseTODO.insertTask(result.get(0));
                if (checkInsertion)
                    textToSpeech.speak("Task is saved", TextToSpeech.QUEUE_FLUSH, null);
                    //Toast.makeText(TodoActivity.this, "Task is saved", Toast.LENGTH_SHORT).show();
                else
                    textToSpeech.speak("Task is not saved", TextToSpeech.QUEUE_FLUSH, null);
                    //Toast.makeText(TodoActivity.this, "Task is not saved", Toast.LENGTH_SHORT).show();
            }
        }
    }
}