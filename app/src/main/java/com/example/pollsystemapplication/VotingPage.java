package com.example.pollsystemapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VotingPage extends AppCompatActivity {
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    long timeLeftInMillis;
    TextView timerTextView, pollTitleText;
    CountDownTimer countDownTimer;
    Button goBack, submitVote;
    LinearLayout questionContainer;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LocationManager locationManager;
    long MIN_TIME_INTERVAL = 1000; // 1 second
    float MIN_DISTANCE = 100; // 100 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        progressDialog = new ProgressDialog(this);
        Bundle extras = getIntent().getExtras();
        Poll poll = (Poll) extras.getSerializable("poll");
        String pollId = extras.getString("pollId");
        Date current = new Date();
        Date end = poll.getEnd();
        timeLeftInMillis = end.getTime() - current.getTime();
        timerTextView = findViewById(R.id.timer);
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                long milliseconds = timeLeftInMillis;
                long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
                milliseconds -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
                milliseconds -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
                timerTextView.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                Toast.makeText(VotingPage.this, "The voting is over.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VotingPage.this, VoterHomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }.start();

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle the updated location
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
            }
        };

        goBack = findViewById(R.id.goBack);
        submitVote = findViewById(R.id.submitVote);

        pollTitleText = findViewById(R.id.pollTitleText);
        pollTitleText.setText(poll.getTitle());
        questionContainer = findViewById(R.id.questionContainer);
        final int numberOfQuestions = poll.getQuestions().size();
        for (int i = 0; i < numberOfQuestions; i++) {
            View viewQuestion = getLayoutInflater().inflate(R.layout.display_question_card_on_voting_page, null);
            TextView nameQuestion = viewQuestion.findViewById(R.id.question);
            RadioGroup radioGroup = viewQuestion.findViewById(R.id.radioGroup);
            nameQuestion.setText(poll.getQuestions().get(i).getQuestion());
            final int numberOfAnswers = poll.getQuestions().get(i).options.size();
            for (int j = 0; j < numberOfAnswers; j++) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(poll.getQuestions().get(i).getOptions().get(j));
                radioGroup.addView(radioButton);
            }
            questionContainer.addView(viewQuestion);
        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VotingPage.this, VoterHomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        submitVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please wait while submit your answers");
                progressDialog.setTitle("New vote");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                try {
                    ArrayList<String> answers = new ArrayList<>();
                    int questions = questionContainer.getChildCount();
                    for (int i = 0; i < questions; i++) {
                        View questionView = questionContainer.getChildAt(i);
                        RadioGroup radioGroup = questionView.findViewById(R.id.radioGroup);
                        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                        if (selectedRadioButtonId == -1) {
                            throw new Exception("Please select answer to each question");
                        }
                        RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonId);
                        String selectedRadioButtonText = selectedRadioButton.getText().toString();
                        answers.add(selectedRadioButtonText);
                    }
                    Location currentLocation = null;
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    // Check if the app has permission to access the device's location
                    if (ActivityCompat.checkSelfPermission((Activity) v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // If the app doesn't have permission, request permission
                        ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    } else {
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            // If the location provider is not enabled, prompt the user to enable it
                            Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(enableLocationIntent);
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE, locationListener);
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (currentLocation == null) {
                            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        if (currentLocation == null) {
                            currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        }
                    }
                    if (currentLocation == null) {
                        throw new Exception("Please allow location so we can submit your vote");
                    }
                    CustomLocation location = new CustomLocation();
                    location.setLongitude(currentLocation.getLongitude());
                    location.setLatitude(currentLocation.getLatitude());
                    Date now = new Date();
                    Vote vote = new Vote(answers, now, location);

                    databaseReference.child("vote").child(pollId).child(firebaseUser.getUid()).setValue(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(VotingPage.this, VoterHomePage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(VotingPage.this, "Your vote is submitted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(VotingPage.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(VotingPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}