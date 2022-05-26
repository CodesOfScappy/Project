package com.example.messangertest.view.display;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.messangertest.R;
import com.example.messangertest.databinding.ActivityViewImageBinding;
import com.example.messangertest.view.profile.Common;
import com.example.messangertest.view.profile.ProfileActivity;

public class ViewImageActivity extends AppCompatActivity {

    private ImageButton btnBack;

    private ActivityViewImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_image);
        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);

        btnBack = (ImageButton)  findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewImageActivity.this,ProfileActivity.class));
            }
        });

    }




}