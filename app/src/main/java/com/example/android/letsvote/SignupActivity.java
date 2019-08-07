package com.example.android.letsvote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.letsvote.Model.Data;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private EditText signupNameView;
    private EditText signupIdView;
    private EditText emailView;
    private EditText aadharView;
    private Button signupBtn;
    private TextView loginTxt;
    private String role;

    private RadioGroup radioGroup;
    private RadioButton userRadio;
    private RadioButton adminRadio;

    private ProgressDialog dialog;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String codeSent;
    private String code;
    private AlertDialog.Builder mDialog;
    private AlertDialog otpDialog;
    private EditText otpField;
    private Button otpSubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupNameView = findViewById(R.id.signup_name);
        signupIdView = findViewById(R.id.signup_id);
        emailView = findViewById(R.id.email_id);
        aadharView = findViewById(R.id.aadhar_no);
        signupBtn = findViewById(R.id.signup_btn);
        loginTxt = findViewById(R.id.login_txt);

        radioGroup = findViewById(R.id.radio_group);
        userRadio = findViewById(R.id.user_role);
        adminRadio = findViewById(R.id.admin_rule);

        dialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        //Creating the otp view
        mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View otpView = inflater.inflate(R.layout.layout_otp, null);
        mDialog.setView(otpView);
        otpDialog = mDialog.create();
        otpField = otpView.findViewById(R.id.enter_otp);
        otpSubmitBtn = otpView.findViewById(R.id.submit_btn);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    signupIdView.setError("Invalid phone number.");

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                codeSent = verificationId;

            }
        };

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signupName = signupNameView.getText().toString().trim();
                String signupId = signupIdView.getText().toString().trim();
                String email = emailView.getText().toString().trim();
                String aadharNo = aadharView.getText().toString().trim();
                role = null;

                if(TextUtils.isEmpty(signupName)) {
                    signupNameView.setError("Required..");
                    signupNameView.requestFocus();
                    return;
                } if(TextUtils.isEmpty(signupId)) {
                    signupIdView.setError("Required..");
                    signupIdView.requestFocus();
                    return;
                } else if(signupId.length() < 10) {
                    signupIdView.setError("Enter a valid Mobno..");
                    signupIdView.requestFocus();
                    return;
                } if(TextUtils.isEmpty(email)) {
                    emailView.setError("Required..");
                    emailView.requestFocus();
                    return;
                } else if(email.matches("^(.+)@(.+)\\.(.+)")) {
                    emailView.setError("Please enter a valid email..");
                    emailView.requestFocus();
                    return;
                } if(TextUtils.isEmpty(aadharNo)) {
                        aadharView.setError("Required..");
                        aadharView.requestFocus();
                        return;
                }
                if(radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select your role...", Toast.LENGTH_SHORT).show();
                    return;
                } else if(userRadio.isChecked()) {
                    role = "User";
                } else {
                    role = "Admin";
                }

                Data data = new Data(signupId, signupName, email, role, aadharNo);
                dialog.setMessage("Processing..");
                dialog.show();

                sendVerificationCode(signupId);

            }
        });


        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void sendVerificationCode(String mobNo) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobNo, 60, TimeUnit.SECONDS, this, mCallbacks);
        otpDialog.show();

        otpSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

    }

    private void verifyOtp() {

        code = otpField.getText().toString().trim();

        if(TextUtils.isEmpty(code)) {
            otpField.setError("Required..");
            otpField.requestFocus();
            return;
        } else if(code.length() < 6) {
            otpField.setError("Enter valid otp..");
            otpField.requestFocus();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        otpDialog.dismiss();
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    if (role.equals("User")) {
                        startActivity(new Intent(getApplicationContext(), UserViewActivity.class));
                        dialog.dismiss();
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(), AdminViewActivity.class));
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Wrong credentials", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


}
