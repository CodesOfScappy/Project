package com.example.messangertest.view.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.messangertest.R;
import com.example.messangertest.databinding.ActivityProfileBinding;
import com.example.messangertest.view.display.ViewImageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.messangertest.view.startup.SplashScreenActivity;
import com.example.messangertest.view.settings.SettingsActivity;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private BottomSheetDialog bottomSheetDialog, bsDialogEditName;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private CircleImageView userProfileImage;
    //Test
    private int IMAGE_GALLERY_REQUEST = 2;
    private Uri selectedImageUri;
    int SELECT_PICTURE = 200;
    private ProgressDialog progressDialog;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //back test
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,SettingsActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);


        userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);

         // Instanzen fon Firestore
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
       // StorageReference profileImageRef = storage.getReference();

        if (firebaseUser !=null) {
            getInfo();
        }

        initActionClick();

    }
    // wenn man Plus neben ImagePhoto drückt
    private void initActionClick() {
        binding.button.setOnClickListener(v -> {
            // öffne Fenstenter mit Gallery und Cammera Bild zur Auswahl
            showBottomSheetPickPhoto();
        });
         // Profile Image Bearbeitungs Ansicht
        binding.visitProfileImage.setOnClickListener(v -> {
            binding.visitProfileImage.invalidate();
            Drawable dr = binding.visitProfileImage.getDrawable();
            Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, binding.visitProfileImage, "image");
            Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
            startActivity(intent, activityOptionsCompat.toBundle());
        });
        // Ändern vom Usernamen Eingabe Activity
        binding.lnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fenter
                showBottomSheetEditName();
            }
        });

        binding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialoSignOut();

            }
        });
    }

    // Methode zum Anzeigen der Ansicht --> bottom_sheet_pick.xml
    private void showBottomSheetPickPhoto() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick, null);

        ((View) view.findViewById(R.id.btn_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
               bottomSheetDialog.dismiss();
            }
        });
        //TODO Camera Image upload and Download
        ((View) view.findViewById(R.id.btn_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Camera not using", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });

        bottomSheetDialog.show();
    }

    private void showBottomSheetEditName() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name, null);

        ((View) view.findViewById(R.id.btn_cancel)).setOnClickListener(v -> {
            bsDialogEditName.dismiss();
        });

        EditText edUserName = view.findViewById(R.id.ed_username);
        ((View) view.findViewById(R.id.btn_save)).setOnClickListener(v -> {
            if (TextUtils.isEmpty(edUserName.getText().toString())) {
                Toast.makeText(getApplicationContext(), "can´e be empty", Toast.LENGTH_SHORT).show();
            } else {
                updateName(edUserName.getText().toString());

                bsDialogEditName.dismiss();
            }

        });

        bsDialogEditName = new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bsDialogEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bsDialogEditName=null;
            }
        });

        bsDialogEditName.show();
    }


    //Abrufen der Werte vom Firestore Document
    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            String userName = documentSnapshot.getString("userName");
            String userPhone = documentSnapshot.getString("userPhone");
            String imageProfile = documentSnapshot.getString("imageProfile");

            //Die Werte vom User bei der Regestrierung
            binding.tvUserName.setText(userName);
            binding.tvPhone.setText(userPhone);
            Glide.with(ProfileActivity.this).load(imageProfile).into(binding.visitProfileImage);

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void openGallery() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
               //.. Uri selectedImageUri = data.getData();
                selectedImageUri = data.getData();

                uploadToFirebase();

            }
        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadToFirebase() {
        if (selectedImageUri!=null);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("image");
        profileImageRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();

                final String sdownload_url = String.valueOf(downloadUrl);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageProfile", sdownload_url);

                progressDialog.dismiss();
                firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();

                                getInfo();
                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "upload failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
    }

    private void updateName(String newName) {
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                getInfo();
            }
        });
    }

    private void showDialoSignOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Do you want to sign out?");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, SplashScreenActivity.class));

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}