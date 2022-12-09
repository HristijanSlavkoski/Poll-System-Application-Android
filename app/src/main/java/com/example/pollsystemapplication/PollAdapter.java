package com.example.pollsystemapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder>{
    private List<User> myList;
    private int rowLayout;
    private Context mContext;

    // Референца на views за секој податок
    // Комплексни податоци може да бараат повеќе views per item
    // Пристап до сите views за податок се дефинира во view holder

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView myName;
        //public ImageView Pic;

        public ViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.Name);
            //Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }

    // конструктор
    public PollAdapter(List<User> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    // Креирање нови views (повикано од layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    // Замена на содржината во view (повикано од layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        User entry = myList.get(i);
        viewHolder.myName.setText(entry.getEmail());
        viewHolder.myName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                Toast.makeText(mContext, tv.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        //viewHolder.Pic.setImageResource(R.drawable.main_page_background);
    }

    // Пресметка на големината на податочното множество (повикано од layout manager)
    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
