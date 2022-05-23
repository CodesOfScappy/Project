package com.example.messangertest.view.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.messangertest.R;
import com.hbb20.CountryCodePicker;

public class PhoneLoginActivity extends AppCompatActivity
{
    private CountryCodePicker countryCodePicker;
    private EditText number;
    private AppCompatButton next;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        countryCodePicker = findViewById(R.id.ccp);
        number = findViewById(R.id.editText_carrierNumber);
        next =findViewById(R.id.next);

        //eg: +49-1791234567
        countryCodePicker.registerCarrierNumberEditText(number);

        // click listener on the next button
        // first  we will check the entered number is Valid or not, then send the number to the next activity.
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (TextUtils.isEmpty(number.getText().toString()))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your Phone number",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(PhoneLoginActivity.this, VerifyOTPActivity.class);
                    intent.putExtra("number", countryCodePicker.getFullNumberWithPlus()
                            .replace(" ", ""));
                    startActivity(intent);
                    finish();
                }
            }

        });
    }
}


