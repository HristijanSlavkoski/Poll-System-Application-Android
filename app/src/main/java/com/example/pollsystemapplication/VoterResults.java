package com.example.pollsystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class VoterResults extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button goBack;
    LinearLayout pollContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_results);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        Bundle extras = getIntent().getExtras();
        Poll poll = (Poll) extras.getSerializable("poll");
        String pollId = extras.getString("pollId");
        goBack = findViewById(R.id.goBack);
        pollContainer = findViewById(R.id.pollContainer);

        databaseReference.child("vote").child(pollId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                View viewPoll = getLayoutInflater().inflate(R.layout.display_poll_card, null);
                Button pollInfo = viewPoll.findViewById(R.id.pollInfo);
                pollInfo.setVisibility(View.GONE);
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
                        for (DataSnapshot dataSnapshotVote : snapshot.getChildren()) {
                            ArrayList<String> answers = (ArrayList<String>) dataSnapshotVote.child("answers").getValue();
                            if (answers.contains(answerName.getText())) {
                                votes++;
                            }
                        }
                        numberOfVotes.setText((votes.toString()));
                        answerLayout.addView(viewAnswer);
                    }
                    questionContainer.addView(viewQuestion);
                }
                pollContainer.addView(viewPoll);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VoterResults.this, VoterHomePage.class));
            }
        });
    }
}