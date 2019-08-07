package com.example.android.letsvote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText loginIdView;
    private EditText passView;
    private Button loginBtn;
    private TextView signUpTxt;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginIdView = findViewById(R.id.login_id);
        passView = findViewById(R.id.login_pass);
        signUpTxt = findViewById(R.id.signup_txt);
        loginBtn = findViewById(R.id.login_btn);
        progressDialog = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginId = loginIdView.getText().toString().trim();
                String pass = passView.getText().toString().trim();

                if(TextUtils.isEmpty(loginId)) {
                    loginIdView.setError("Required..");
                    loginIdView.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    passView.setError("Required..");
                    passView.requestFocus();
                    return;
                }

                progressDialog.setMessage("Processing..");
                progressDialog.show();
//                startActivity(new Intent(getApplicationContext(), AdminViewActivity.class));
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

    }
}
