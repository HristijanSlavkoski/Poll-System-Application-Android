package com.example.pollsystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckMyVotesPage extends AppCompatActivity {

    RecyclerView checkMyVotesRecyclerView;
    PollAdapter pollAdapterForCheckMyVotes;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_check_my_votes);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        goBack = findViewById(R.id.goBack);
        checkMyVotesRecyclerView = findViewById(R.id.listOfMyVotes);
        checkMyVotesRecyclerView.setHasFixedSize(true);
        checkMyVotesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkMyVotesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        List<Poll> values = new ArrayList<Poll>();
        List<String> keys = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.child("vote").getChildren()) {
                    for (DataSnapshot dataSnapshotUser : dataSnapshot.getChildren()) {
                        if (dataSnapshotUser.getKey().equals(firebaseUser.getUid())) {
                            keys.add(dataSnapshot.getKey());
                        }
                    }
                }
                for (DataSnapshot dataSnapshot : snapshot.child("poll").getChildren()) {
                    if (keys.contains(dataSnapshot.getKey())) {
                        values.add(dataSnapshot.getValue(Poll.class));
                    }
                }
                pollAdapterForCheckMyVotes = new PollAdapter(keys, values, R.layout.list_all_active_polls_card, CheckMyVotesPage.this);
                checkMyVotesRecyclerView.setAdapter(pollAdapterForCheckMyVotes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckMyVotesPage.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckMyVotesPage.this, VoterHomePage.class));
            }
        });
    }
}