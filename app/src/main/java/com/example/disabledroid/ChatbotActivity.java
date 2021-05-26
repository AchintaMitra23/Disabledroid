package com.example.disabledroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disabledroid.JavaClasses.MessageAdapter;
import com.example.disabledroid.JavaClasses.ResponseMessage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatbotActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private TextInputLayout et_msg;
    private RecyclerView recycle;
    private List<ResponseMessage> responseMessageList;
    private MessageAdapter messageAdapter;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        et_msg = findViewById(R.id.et_ask);
        recycle = findViewById(R.id.recycle);

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

        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList);
        recycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycle.setAdapter(messageAdapter);

        et_msg.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    ResponseMessage msg1 = new ResponseMessage(et_msg.getEditText().getText().toString(), true);
                    responseMessageList.add(msg1);
                    ResponseMessage msg2 = new ResponseMessage(response(et_msg.getEditText().getText().toString()), false);
                    responseMessageList.add(msg2);
                    et_msg.getEditText().setText("");
                    messageAdapter.notifyDataSetChanged();

                    textToSpeech.speak(msg2.getText(), TextToSpeech.QUEUE_FLUSH, null);

                    if(!isVisible()) recycle.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                }
                return true;
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

    public boolean isVisible() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycle.getLayoutManager();
        int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int current = recycle.getAdapter().getItemCount();

        return (position>=current);
    }

    public String response(String ipStr) {
        String retStr = null;
        switch (ipStr) {
            case "Hi" :
            case "hi" :
            case "Hello" :
            case "hello" :
            case "Yo" :
            case "yo" :
                retStr = "Hello my friend!";
                break;
            case "How are you?" :
            case "how are you" :
                retStr = "I am fine, What about you my friend?";
                break;
            case "Bye" :
            case "bye" :
            case "Ok Thanks" :
            case "ok thanks" :
            case "See you later" :
            case "see you later" :
                retStr = "Have a nice day! Thank u :)";
                break;
            default :
                retStr = ipStr.toString();
        }
        return retStr;
    }

    public void float_add_chat(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e) {
            Toast.makeText(ChatbotActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                ResponseMessage msg1 = new ResponseMessage(result.get(0), true);
                responseMessageList.add(msg1);
                ResponseMessage msg2 = new ResponseMessage(response(result.get(0)), false);
                responseMessageList.add(msg2);
                messageAdapter.notifyDataSetChanged();

                textToSpeech.speak(msg2.getText(), TextToSpeech.QUEUE_FLUSH, null);

                if(!isVisible()) recycle.smoothScrollToPosition(messageAdapter.getItemCount()-1);
            }
        }
    }
}