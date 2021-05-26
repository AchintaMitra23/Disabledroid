package com.example.disabledroid.JavaClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.disabledroid.MainActivity;
import com.example.disabledroid.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    public Context context;
    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        this.update("AUTHENTICATION ERROR :-  " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        this.update("AUTHENTICATION FAILED", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        this.update(String.valueOf(helpString).toUpperCase(), false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        this.update("AUTHENTICATION DONE", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
                ((Activity)context).finish();
            }
        }, 1000);
    }

    private void update(String s, boolean b) {
        TextView status = ((Activity)context).findViewById(R.id.status);
        ImageView finger = ((Activity)context).findViewById(R.id.fingerprint);
        ProgressBar progressBar = ((Activity)context).findViewById(R.id.progress);
        TextView goStatus = ((Activity)context).findViewById(R.id.foot);
        status.setText(s);
        if (b == false) {
            status.setTextColor(Color.RED);
            finger.setImageResource(R.mipmap.fail);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            goStatus.setVisibility(View.VISIBLE);
            status.setTextColor(Color.GREEN);
            finger.setImageResource(R.mipmap.done);
        }
    }
}
