package com.example.pollsystemapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private List<String> myKeys;
    private List<Poll> myList;
    private int rowLayout;
    private Context mContext;

    public PollAdapter(List<String> keys, List<Poll> myList, int rowLayout, Context context) {
        this.myKeys = keys;
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        Poll entry = myList.get(i);
        String key = myKeys.get(i);
        viewHolder.questionTitle.setText(entry.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        viewHolder.start.setText("From: " + simpleDateFormat.format(entry.getStart()));
        viewHolder.end.setText(" To:  " + simpleDateFormat.format(entry.getEnd()));
        Date startDate = entry.getStart();
        Date endDate = entry.getEnd();
        if (startDate.after(new Date())) {
            viewHolder.voteButton.setText("Coming soon");
            viewHolder.voteButton.setEnabled(false);
            viewHolder.voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "This poll is not active yet.", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (endDate.before(new Date())) {
            viewHolder.voteButton.setText("Results");
            viewHolder.voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VoterResults.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("pollId", key);
                    intent.putExtra("poll", entry);
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            databaseReference.child("vote").child(key).child(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
                //Check if the user already voted on that poll
                if (task.getResult().exists()) {
                    viewHolder.voteButton.setText("Results");
                    viewHolder.voteButton.setEnabled(false);
                } else {
                    viewHolder.voteButton.setText("Vote");

                    viewHolder.voteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), VotingPage.class);
                            intent.putExtra("pollId", key);
                            intent.putExtra("poll", entry);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            v.getContext().startActivity(intent);
                        }
                    });
                }
            });
        }
        viewHolder.questionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                Toast.makeText(mContext, tv.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionTitle;
        public Button voteButton;
        public TextView start;
        public TextView end;

        public ViewHolder(View itemView) {
            super(itemView);
            questionTitle = (TextView) itemView.findViewById(R.id.questionTitle);
            voteButton = (Button) itemView.findViewById(R.id.voteButton);
            start = (TextView) itemView.findViewById(R.id.start);
            end = (TextView) itemView.findViewById(R.id.end);
        }
    }
}
