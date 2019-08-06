package com.example.android.letsvote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class AdminViewActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lets Vote");
    }
}
