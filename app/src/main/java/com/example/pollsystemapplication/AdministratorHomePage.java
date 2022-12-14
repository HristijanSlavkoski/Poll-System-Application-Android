package com.example.pollsystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdministratorHomePage extends AppCompatActivity {

    Button buttonCreateNewPoll, logoutButton;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout pollContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_home_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        buttonCreateNewPoll = findViewById(R.id.buttonCreateNewPoll);
        logoutButton = findViewById(R.id.logoutButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pollContainer = findViewById(R.id.pollContainer);
                pollContainer.removeAllViews();
                for (DataSnapshot dataSnapshotPoll : snapshot.child("poll").getChildren()) {
                    Poll poll = dataSnapshotPoll.getValue(Poll.class);
                    if (poll.getCreator().equals(firebaseUser.getEmail())) {
                        View viewPoll = getLayoutInflater().inflate(R.layout.display_poll_card, null);
                        Button pollInfo = viewPoll.findViewById(R.id.pollInfo);
                        pollInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), MapPage.class);
                                intent.putExtra("pollId", dataSnapshotPoll.getKey());
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                v.getContext().startActivity(intent);
                            }
                        });
                        TextView namePoll = viewPoll.findViewById(R.id.poll);
                        namePoll.setText(poll.getTitle());
                        LinearLayout questionContainer = viewPoll.findViewById(R.id.questionContainer);
                        final int numberOfQuestions = poll.getQuestions().size();
                        for (int i = 0; i < numberOfQuestions; i++) {
                            View viewQuestion = getLayoutInflater().inflate(R.layout.display_question_card_on_admin_page, null);
                            TextView nameQuestion = viewQuestion.findViewById(R.id.question);
                            nameQuestion.setText(poll.getQuestions().get(i).getQuestion());
                            LinearLayout answerLayout = viewQuestion.findViewById(R.id.answerLayout);
                            final int numberOfAnswers = poll.getQuestions().get(i).getOptions().size();
                            for (int j = 0; j < numberOfAnswers; j++) {
                                View viewAnswer = getLayoutInflater().inflate(R.layout.display_answer_with_number_of_votes, null);
                                TextView answerName = viewAnswer.findViewById(R.id.answerName);
                                TextView numberOfVotes = viewAnswer.findViewById(R.id.numberOfVotes);
                                answerName.setText(poll.getQuestions().get(i).getOptions().get(j));
                                Integer votes = 0;
                                for (DataSnapshot dataSnapshotVote : snapshot.child("vote").getChildren()) {
                                    if (dataSnapshotVote.getKey().equals(dataSnapshotPoll.getKey())) {
                                        for (DataSnapshot dataSnapshotUser : dataSnapshotVote.getChildren()) {
                                            ArrayList<String> answers = (ArrayList<String>) dataSnapshotUser.child("answers").getValue();
                                            if (answers.contains(answerName.getText())) {
                                                votes++;
                                            }
                                        }
                                    }
                                }
                                numberOfVotes.setText((votes.toString()));
                                answerLayout.addView(viewAnswer);
                            }
                            questionContainer.addView(viewQuestion);
                        }
                        pollContainer.addView(viewPoll);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        buttonCreateNewPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdministratorHomePage.this, CreateNewPollActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(AdministratorHomePage.this, MainActivity.class));
            }
        });
    }
}