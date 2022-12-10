package com.example.pollsystemapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> {
    private List<Poll> myList;
    private int rowLayout;
    private Context mContext;

    public PollAdapter(List<Poll> myList, int rowLayout, Context context) {
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
        Poll entry = myList.get(i);
        viewHolder.questionTitle.setText(entry.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        viewHolder.start.setText("From: " + simpleDateFormat.format(entry.getStart()));
        viewHolder.end.setText(" To:  " + simpleDateFormat.format(entry.getEnd()));
        Date startDate = entry.getStart();
        Date endDate = entry.getEnd();
        if (startDate.after(new Date())) {
            viewHolder.voteButton.setText("Coming soon");
            viewHolder.voteButton.setEnabled(false);
        } else if (endDate.before(new Date())) {
            viewHolder.voteButton.setText("Results");
        } else {
            viewHolder.voteButton.setText("Vote");
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
