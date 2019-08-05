package com.example.android.letsvote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SignupActivity extends AppCompatActivity {

    private EditText signupNameView;
    private EditText signupIdView;
    private EditText passView;
    private EditText repassView;
    private Button signupBtn;
    private TextView loginTxt;

    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupNameView = findViewById(R.id.signup_name);
        signupIdView = findViewById(R.id.signup_id);
        passView = findViewById(R.id.signup_pass);
        repassView = findViewById(R.id.signup_repass);
        signupBtn = findViewById(R.id.signup_btn);
        loginTxt = findViewById(R.id.login_txt);

        dialog = new ProgressDialog(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signupName = signupNameView.getText().toString().trim();
                String signupId = signupIdView.getText().toString().trim();
                String pass = passView.getText().toString().trim();
                String repass = repassView.getText().toString().trim();

                if(TextUtils.isEmpty(signupName)) {
                    signupNameView.setError("Required..");
                    return;
                } if(TextUtils.isEmpty(signupId)) {
                    signupIdView.setError("Required..");
                    return;
                } if(TextUtils.isEmpty(pass)) {
                    passView.setError("Required..");
                    return;
                } if(TextUtils.isEmpty(repass)) {
                    repassView.setError("Required..");
                    return;
                } else if (!pass.equals(repass)) {
                    repassView.setError("Password Does not match");
                    return;
                }
                dialog.setMessage("Processing..");
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                dialog.show();
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
