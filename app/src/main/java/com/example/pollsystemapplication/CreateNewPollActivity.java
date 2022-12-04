package com.example.pollsystemapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateNewPollActivity extends AppCompatActivity {

    Button addQuestion, finishCreatingPoll;
    AlertDialog dialogForQuestion, dialogForAnswer;
    LinearLayout layout;
    EditText pollTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_poll);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addQuestion = findViewById(R.id.addQuestion);
        finishCreatingPoll = findViewById(R.id.finishCreatingPoll);
        layout = findViewById(R.id.containerLayout);
        pollTitleText = findViewById(R.id.pollTitleText);

        buildDialogForQuestion();
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForQuestion.show();
            }
        });

        finishCreatingPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OVDE SE ZACUVUVAT VO BAZA
            }
        });
    }

    private void buildDialogForQuestion() {
        AlertDialog.Builder builderForQuestion = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_new_question_dialog, null);

        EditText name = view.findViewById(R.id.nameEdit);
        builderForQuestion.setView(view);
        builderForQuestion.setTitle("Enter new question")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addCard(name.getText().toString());
                        name.getText().clear();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialogForQuestion = builderForQuestion.create();
    }

    private void addCard(String name) {
        View view = getLayoutInflater().inflate(R.layout.activity_creating_questions_card, null);
        TextView nameView = view.findViewById(R.id.question);
        Button deleteQuestion = view.findViewById(R.id.deleteQuestion);
        Button addAnswers = view.findViewById(R.id.addAnswers);
        nameView.setText(name);
        //
        LinearLayout answerContainer = view.findViewById(R.id.answerLayout);
        buildDialogForAnswer(answerContainer);
        //
        deleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(view);
                layout.forceLayout();
            }
        });
        addAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForAnswer.show();
            }
        });
        layout.addView(view);
    }

    private void buildDialogForAnswer(LinearLayout answerContainer) {
        AlertDialog.Builder builderForAnswer = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_new_answer_dialog, null);

        EditText name = view.findViewById(R.id.nameEditAnswer);
        builderForAnswer.setView(view);
        builderForAnswer.setTitle("Enter new choice")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addAnswer(answerContainer, name.getText().toString());
                        name.getText().clear();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialogForAnswer = builderForAnswer.create();
    }

    private void addAnswer(LinearLayout answerContainer, String name) {
        View view = getLayoutInflater().inflate(R.layout.activity_creating_answer_card, null);
        TextView nameView = view.findViewById(R.id.answer);
        Button deleteAnswer = view.findViewById(R.id.deleteAnswer);
        nameView.setText(name);

        deleteAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(view);
                layout.forceLayout();
            }
        });
        answerContainer.addView(view);
    }
}