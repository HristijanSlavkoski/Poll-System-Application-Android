package com.example.pollsystemapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateNewPollActivity extends AppCompatActivity {

    Button addQuestionButton, finishCreatingPoll, setStartDate, setStartTime, setEndDate, setEndTime, goBack;
    AlertDialog dialogForQuestion, dialogForAnswer;
    LinearLayout questionContainer;
    EditText pollTitleText;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String startDate, startTime, endDate, endTime;
    Calendar startCalendar, endCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_poll);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addQuestionButton = findViewById(R.id.addQuestion);
        setStartDate = findViewById(R.id.setStartDate);
        setStartTime = findViewById(R.id.setStartTime);
        setEndDate = findViewById(R.id.setEndDate);
        setEndTime = findViewById(R.id.setEndTime);
        goBack = findViewById(R.id.goBack);
        finishCreatingPoll = findViewById(R.id.finishCreatingPoll);
        questionContainer = findViewById(R.id.questionContainer);
        pollTitleText = findViewById(R.id.pollTitleText);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        setStartTime.setEnabled(false);
        setEndTime.setEnabled(false);

        buildDialogForQuestion();
        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForQuestion.show();
            }
        });

        setStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartDate();
            }
        });

        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartTime();
            }
        });

        setEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndDate();
            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndTime();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewPollActivity.this, AdministratorHomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        finishCreatingPoll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please wait while we add the new poll");
                progressDialog.setTitle("New poll");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                try {
                    if (startDate == null) {
                        throw new Exception("Please set start date");
                    }
                    if (startTime == null) {
                        throw new Exception("Please set start time");
                    }
                    if (endDate == null) {
                        throw new Exception("Please set end date");
                    }
                    if (endTime == null) {
                        throw new Exception("Please set end time");
                    }
                    Date start;
                    Date end;
                    start = startCalendar.getTime();
                    end = endCalendar.getTime();

                    if (start.after(end)) {
                        throw new Exception("Start time of the poll cannot be after end time");
                    }
                    ArrayList<Question> questions = new ArrayList<>();
                    final int numberOfQuestions = questionContainer.getChildCount();
                    if (numberOfQuestions == 0) {
                        throw new Exception("Please add at least one question");
                    }
                    for (int i = 0; i < numberOfQuestions; i++) {
                        View questionView = questionContainer.getChildAt(i);
                        TextView questionName = questionView.findViewById(R.id.question);
                        LinearLayout answerLayout = questionView.findViewById(R.id.answerLayout);
                        ArrayList<String> answers = new ArrayList<>();
                        final int numberOfAnswers = answerLayout.getChildCount();
                        if (numberOfAnswers == 0) {
                            throw new Exception("Please add at least one choice to each question");
                        }
                        for (int j = 0; j < numberOfAnswers; j++) {
                            View answerView = answerLayout.getChildAt(j);
                            TextView answerName = answerView.findViewById(R.id.answer);
                            answers.add(answerName.getText().toString());
                        }
                        questions.add(new Question(questionName.getText().toString(), answers));
                    }
                    if (pollTitleText.getText().toString() == null || pollTitleText.getText().toString().equals("")) {
                        throw new Exception("Please add title of the poll");
                    }
                    //On create, all polls will be inactive, we can activate them later
                    Poll poll = new Poll(pollTitleText.getText().toString(), firebaseUser.getEmail(), questions, start, end);
                    databaseReference.child("poll").push().setValue(poll).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(CreateNewPollActivity.this, AdministratorHomePage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(CreateNewPollActivity.this, "New poll created successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(CreateNewPollActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateNewPollActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setStartDate() {
        Calendar calendar = Calendar.getInstance();
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                startDate = i2 + "-" + (i1 + 1) + "-" + i;
                startCalendar.set(i, i1, i2);
                setStartTime.setEnabled(true);
                setStartDate.setText(startDate);
            }
        }, startYear, startMonth, startDay);

        datePickerDialog.show();
    }

    private void setStartTime() {
        Calendar calendar = Calendar.getInstance();
        int startHour = calendar.get(Calendar.HOUR);
        int startMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                startTime = i + ":" + i1;
                setStartTime.setText(startTime);
                startCalendar.set(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), i, i1);
            }
        }, startHour, startMinute, true);

        timePickerDialog.show();
    }

    private void setEndDate() {
        Calendar calendar = Calendar.getInstance();
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                endDate = i2 + "-" + (i1 + 1) + "-" + i;
                setEndDate.setText(endDate);
                endCalendar.set(i, i1, i2);
                setEndTime.setEnabled(true);
            }
        }, startYear, startMonth, startDay);

        datePickerDialog.show();
    }

    private void setEndTime() {
        Calendar calendar = Calendar.getInstance();
        int startHour = calendar.get(Calendar.HOUR);
        int startMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                endTime = i + ":" + i1;
                setEndTime.setText(endTime);
                endCalendar.set(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), i, i1);
            }
        }, startHour, startMinute, true);

        timePickerDialog.show();
    }

    private void buildDialogForQuestion() {
        AlertDialog.Builder builderForQuestion = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_new_question_dialog, null);

        EditText questionName = view.findViewById(R.id.nameEdit);
        builderForQuestion.setView(view);
        builderForQuestion.setTitle("Enter new question")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addQuestion(questionName.getText().toString());
                        questionName.getText().clear();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialogForQuestion = builderForQuestion.create();
    }

    private void addQuestion(String questionName) {
        View view = getLayoutInflater().inflate(R.layout.activity_creating_questions_card, null);
        TextView nameView = view.findViewById(R.id.question);
        Button deleteQuestion = view.findViewById(R.id.deleteQuestion);
        Button addAnswers = view.findViewById(R.id.addAnswers);
        nameView.setText(questionName);
        LinearLayout answerContainer = view.findViewById(R.id.answerLayout);
        deleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContainer.removeView(view);
                questionContainer.forceLayout();
            }
        });
        addAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialogForAnswer(answerContainer);
                dialogForAnswer.show();
            }
        });
        questionContainer.addView(view);
    }

    private void buildDialogForAnswer(LinearLayout answerContainer) {
        AlertDialog.Builder builderForAnswer = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_new_answer_dialog, null);

        EditText answerName = view.findViewById(R.id.nameEditAnswer);

        builderForAnswer.setView(view);
        builderForAnswer.setTitle("Enter new choice")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addAnswer(answerContainer, answerName.getText().toString());
                        answerName.getText().clear();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialogForAnswer = builderForAnswer.create();
    }

    private void addAnswer(LinearLayout answerContainer, String answerName) {
        View view = getLayoutInflater().inflate(R.layout.activity_creating_answer_card, null);
        TextView nameView = view.findViewById(R.id.answer);
        Button deleteAnswer = view.findViewById(R.id.deleteAnswer);
        nameView.setText(answerName);

        deleteAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerContainer.removeView(view);
                answerContainer.forceLayout();
            }
        });
        answerContainer.addView(view);
    }
}