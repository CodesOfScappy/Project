package com.example.messangertest.view.chats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messangertest.R;
import com.example.messangertest.adapter.ChatsAdapter;
import com.example.messangertest.databinding.ActivityChatsBinding;
import com.example.messangertest.model.chat.Chats;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private ActivityChatsBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String receiverID;
    private ChatsAdapter adapter;
    private List<Chats>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chats);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        receiverID = intent.getStringExtra("userID");
        String userProfile = intent .getStringExtra("userProfile");



        if (receiverID != null) {
            binding.tvUsername.setText(userName);
            Glide.with(this).load(userProfile).into(binding.imageProfile);
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_keyboard_voice_24));
                }else{
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_send_24dp));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initBtnClick();


        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        readChats();
    }



    private void initBtnClick(){
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    sendTextMessage(binding.edMessage.getText().toString());

                    binding.edMessage.setText("");
                }
            }
        });

    }

    private void sendTextMessage(String text) {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm ");
        String currentTime = df.format(currentDateTime.getTime());

        Chats chats = new Chats(
                today + ", " + currentTime,
                text,
                "TEXT",
                firebaseUser.getUid(),
                receiverID
        );

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("send", "onSuccess:");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("send","onFailure:" + e.getMessage() );

            }
        });

        // add to ChatList (User Sender)
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        //  (User Reveiver)
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());

    }

    private void readChats()  {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Chats chats = snapshot.getValue(Chats.class);
                        if (chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)) {
                            list.add(chats);
                        }
                    }
                    if (adapter!=null){
                        adapter.notifyDataSetChanged();
                    }else{
                        adapter = new ChatsAdapter(list, ChatsActivity.this);
                        binding.recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}