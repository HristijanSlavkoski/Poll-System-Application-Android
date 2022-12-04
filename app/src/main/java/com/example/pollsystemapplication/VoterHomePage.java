package com.example.pollsystemapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class VoterHomePage extends AppCompatActivity {

    RecyclerView mRecyclerView;
    PollAdapter pollAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_home_page);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        List<User> values = new ArrayList<User>();
        databaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    values.add((User) dataSnapshot.getValue(User.class));
                }
                pollAdapter = new PollAdapter(values, R.layout.poll_row, VoterHomePage.this);
                mRecyclerView.setAdapter(pollAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VoterHomePage.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<User> getUsers() throws InterruptedException {
        //We can't make listeners to work synchronous, so we
        //change the logic of the project
        List<User> users = new ArrayList<User>();
        Semaphore semaphore = new Semaphore(0);
        databaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    users.add((User) dataSnapshot.getValue(User.class));
                }
                semaphore.release(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VoterHomePage.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        /*databaseReference.child("user").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(VoterHomePage.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        users.add((User) dataSnapshot.getValue(User.class));
                    }
                    semaphore.release();
                }
            }
        });*/
        semaphore.acquire(0);
        return users;
    }
}