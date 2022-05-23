package com.example.messangertest.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messangertest.R;
import com.example.messangertest.model.Chatlist;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);
        List<Chatlist> list = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

         //list.add(new Chatlist("1","Dave","Administrator","19/05/2022","https://codeofscappy.px.media/workspace/pixxio/tt.php?src=%2FfileArchiv%2Fpd%2Fpdhw43HLjwaQK05XM6__1652796421_6068835.jpg&dataPath=%2Fpixxiodata%2Fsystems%2Fpx-vacant-con-62838abd7b851&w=1920"));
         //list.add(new Chatlist("2","Lynda","Test Benutzer","19/05/2022","https://codeofscappy.px.media/workspace/pixxio/tt.php?src=%2FfileArchiv%2Frz%2FrZoopMC6y3MOksLhOR__1652796422_5850622.jpg&dataPath=%2Fpixxiodata%2Fsystems%2Fpx-vacant-con-62838abd7b851&w=1920"));
         //
        // recyclerView.setAdapter(new ChatListAdapter(list,getContext()));

        return view;
    }



}