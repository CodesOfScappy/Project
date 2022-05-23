package com.example.messangertest.view.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.messangertest.model.user.Users;
import com.example.messangertest.view.MainActivity;

import com.example.messangertest.R;
import com.example.messangertest.databinding.ActivitySetUserInfoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetUserInfoActivity extends AppCompatActivity {

    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_set_user_info);

        progressDialog = new ProgressDialog(this);
        initButtonClick();
    }

    private void initButtonClick() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.edName.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please input Username", Toast.LENGTH_SHORT).show();
                }
                doUpdate();

            }
        });
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //picImage
                //i will code it next
                Toast.makeText(getApplicationContext(), "This function is not ready to use", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doUpdate() {
        ////
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser !=null) {
            String userID = firebaseUser.getUid();
            Users users = new Users(userID,
                    binding.edName.getText().toString(),
                    firebaseUser.getPhoneNumber(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
           firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(users)
                   // Update("UserName",binding.edName.gettext().toString())
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           progressDialog.dismiss();
                           Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));

                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           progressDialog.dismiss();
                           Toast.makeText(getApplicationContext(), "Update Fail..."+e.getMessage(), Toast.LENGTH_SHORT).show();

                       }
                   });
        }else {
            Toast.makeText(getApplicationContext(), "you need to login first..", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }
}