package com.example.android.letsvote;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent splashIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(splashIntent);
                    finish();
                }
            }, 2000);

        }

    }
}
