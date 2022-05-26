package com.example.messangertest.view.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.messangertest.R;
import com.example.messangertest.databinding.ActivitySettingsBinding;
import com.example.messangertest.view.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private  ActivitySettingsBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;


    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings);

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


             //  database.getReference()
             //          .child("image").addValueEventListener(new ValueEventListener() {
             //              @Override
             //              public void onDataChange(@NonNull DataSnapshot snapshot) {
             //                  String image = snapshot.getValue(String.class);
             //                  Glide.with(SettingsActivity.this)
             //                          .load(image)
             //                          .into(binding.userImage);


             //              }

             //              @Override
             //              public void onCancelled(@NonNull DatabaseError error) {

             //              }
             //          });




        if (firebaseUser !=null) {
            getInfo();
        }
        initClickAction();


    }

    private void initClickAction() {
        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (SettingsActivity.this,ProfileActivity.class));
            }
        });
    }

    // Firestore Datenbank Eintarg setzten: (Sammlung)--> Users (Dokument)-->UID-->(Dokumenten-Felder)-->(userName)---Admin David
    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
                String imageProfile = documentSnapshot.getString("imageProfile");


                binding.tvUsername.setText(userName);
                Glide.with(SettingsActivity.this).load(imageProfile).into(binding.userImage);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Get Data","onFailure: "+e.getMessage());

            }
        });
    }
}