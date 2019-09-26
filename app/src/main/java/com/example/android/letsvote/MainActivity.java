package com.example.android.letsvote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText loginIdView;
    private Button loginBtn;
    private TextView signUpTxt;
    private boolean isThere = false;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

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
        setContentView(R.layout.activity_main);

        loginIdView = findViewById(R.id.login_id);
        signUpTxt = findViewById(R.id.signup_txt);
        loginBtn = findViewById(R.id.login_btn);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mDataBase.keepSynced(true);

        mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View otpView = inflater.inflate(R.layout.layout_otp, null);
        mDialog.setView(otpView);
        otpDialog = mDialog.create();
        otpField = otpView.findViewById(R.id.enter_otp);
        otpSubmitBtn = otpView.findViewById(R.id.submit_btn);

        if(mAuth.getCurrentUser() != null) {

            String userId = mAuth.getCurrentUser().getUid();
            progressDialog.setMessage("Processing..");
            progressDialog.show();
            startActivityOnLogin(userId);

        }

//        startActivityOnLogin("BlrHMJ9o6LNBSoDHv5pAHl3khtn2");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    progressDialog.dismiss();
                    otpDialog.dismiss();
                    loginIdView.setError("Invalid phone number.");

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    progressDialog.dismiss();
                    otpDialog.dismiss();
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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String loginId = loginIdView.getText().toString().trim();

                if(TextUtils.isEmpty(loginId)) {
                    loginIdView.setError("Required..");
                    loginIdView.requestFocus();
                    return;
                } else if(loginId.length() < 10) {
                    loginIdView.setError("Please enter valid Mobno..");
                    loginIdView.requestFocus();
                    return;
                }

                // Checking user already exists or not

                mDataBase.orderByChild("userId")
                        .equalTo(loginId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            isThere = true;
                            sendVerificationCode(loginId);
                        } else {
                            isThere = false;
                            startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

    }

    private void sendVerificationCode(final String mobNo) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobNo, 60, TimeUnit.SECONDS, this, mCallbacks);
        otpDialog.show();

        otpSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Processing..");
                progressDialog.show();
                verifyOtp();
            }
        });

    }

    private void verifyOtp() {

        code = otpField.getText().toString().trim();

        if(TextUtils.isEmpty(code)) {
            progressDialog.dismiss();
            otpField.setError("Required..");
            otpField.requestFocus();
            return;
        } else if(code.length() < 6) {
            progressDialog.dismiss();
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

                    String userId = mAuth.getCurrentUser().getUid();

                    startActivityOnLogin(userId);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Wrong credentials", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    protected void startActivityOnLogin(String userId) {
        mDataBase.child(userId).child("userRole")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().equals("User")) {
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), UserViewActivity.class));
                    return;
                } else {
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), AdminViewActivity.class));
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(MainActivity.this);
    }
}
