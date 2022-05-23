package com.example.messangertest.view.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.messangertest.R;
import com.example.messangertest.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    ActivityResultLauncher<String> launcher;

    FirebaseDatabase database;
    FirebaseStorage storage;


    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private CircleImageView userProfileImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Instanzen von Database und Storage
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();




        database.getReference()
                .child("image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String image = snapshot.getValue(String.class);
                        Glide.with(ProfileActivity.this)
                                .load(image)
                                .into(binding.visitProfileImage);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

         // Instanzen fon Firestore
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser !=null) {
            getInfo();
        }

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        binding.visitProfileImage.setImageURI(uri);


                        final StorageReference reference = storage.getReference()
                                .child("image");

                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        database.getReference().child("image")
                                                .setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                    }
                                });

                            }
                        });
                    }
                });


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");

            }
        });





    }


    //Abrufen der Werte vom Firestore Document
    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            String userName = documentSnapshot.getString("userName");
            String userPhone = documentSnapshot.getString("userPhone");


            //Die Werte vom User bei der Regestrierung
            binding.tvUserName.setText(userName);
            binding.tvPhone.setText(userPhone);


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}