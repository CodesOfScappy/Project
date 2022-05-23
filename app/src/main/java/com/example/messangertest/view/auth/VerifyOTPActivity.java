package com.example.messangertest.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.messangertest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity
{
    private String number, id;
    private EditText otp;
    private AppCompatButton submit;
    private LinearProgressIndicator loader;
    private TextView resend;
    // Phone AuthProvider API
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    // Firebase APIs
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    // TAG Display Dialog API
    private static String TAG = "VerifyOTPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfiy_otpactivity);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        otp = findViewById(R.id.otp);
        submit = findViewById(R.id.submit);
        loader = findViewById(R.id.loader);
        resend = findViewById(R.id.resend);

        // Firebase Init
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser !=null) {
            startActivity(new Intent(this,SetUserInfoActivity.class));
        }



        number = getIntent().getStringExtra("number");

        //create a function to sen the OTP from firebase to device
        sendVerificationCode();

        //click listener on the submit button
        // if the otp is correct with the phone number then user will send to the main activity
        submit.setOnClickListener(view -> {

            if (TextUtils.isEmpty(otp.getText().toString())){

                Toast.makeText(VerifyOTPActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();

            } else if (otp.getText().toString().replace(" ", "").length() !=6){

                Toast.makeText(VerifyOTPActivity.this, "Enter right OTP", Toast.LENGTH_SHORT).show();

            }else {

                loader.setVisibility(View.VISIBLE);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString()
                        .replace(" ", ""));
                signInWithPhoneAuthCredential(credential);
            }
        });

        // if user take more then 60 seconds to enter OTP, the resend text appears
        resend.setOnClickListener(view -> sendVerificationCode());
    }

    private void sendVerificationCode() {

        //for resend button
        new CountDownTimer(60000, 1000){
            @Override
            public void onTick(long l) {
                resend.setText(""+ l/1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText(" Resend");
                resend.setEnabled(true);

            }
        }.start();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e)
                            {
                                Toast.makeText(VerifyOTPActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                VerifyOTPActivity.this.id = verificationId;

                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG,"signInWithCredentials: Success");
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(VerifyOTPActivity.this, SetUserInfoActivity.class));

                        } else {
                            Log.w(TAG,"signInWithCredential: Failure",task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.d(TAG,"onComplete: Error Code");

                            }


                        }
                    }
                });
    }
}