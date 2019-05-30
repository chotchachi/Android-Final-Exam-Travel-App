package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public final class WelcomScreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
        String user = sharedPreferences.TraSP("usernamedaluu", this);

        if (user.isEmpty()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(WelcomScreen.this, Login.class);
                    startActivity(i);
                    finish();
                }
            }, 1500);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(WelcomScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1500);
        }
    }
}