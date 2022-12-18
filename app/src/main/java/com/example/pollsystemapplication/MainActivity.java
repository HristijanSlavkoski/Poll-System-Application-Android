package com.example.pollsystemapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    TextView createNewAccount, forgotPassword;
    EditText inputEmail, inputPassword;
    Button buttonLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNewAccount = findViewById(R.id.createNewAccount);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateLogin();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
    }

    private void ValidateLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter correct Email");
        } else if (password.isEmpty() || password.length() < 8) {
            inputPassword.setError("The password must be at least 8 characters");
        } else {
            progressDialog.setMessage("Please wait while we confirm the login");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        firebaseUser = firebaseAuth.getCurrentUser();
                        databaseReference.child("user").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    task.getResult().getValue();
                                    FirebaseMessaging.getInstance().subscribeToTopic("all")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    String msg = "Subscribed";
                                                    if (!task.isSuccessful()) {
                                                        msg = "Subscribe failed";
                                                    }
                                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    if (task.getResult().getValue(User.class).role.equals("voter")) {
                                        // Voter
                                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, VoterHomePage.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        // Administrator
                                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, AdministratorHomePage.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        /*
        IF WE USED AUTHENTICATION DATABASE WITH DISPLAYNAME
        INSTEAD OF NEW TABLE IN REALTIME DATABASE
        WE WOULD CHECK:
         if(firebaseUser.getDisplayName() != "voter")
         */
        firebaseUser = firebaseAuth.getCurrentUser();
        /*
        With Event Listener
        databaseReference.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.child(firebaseUser.getUid()).getValue(User.class);
                if (user.role.equals("voter")) {
                    // Voter
                    Intent intent = new Intent(MainActivity.this, VoterHomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Administrator
                    Intent intent = new Intent(MainActivity.this, AdministratorHomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
         */
        //With get only once
        databaseReference.child("user").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    task.getResult().getValue();
                    if (task.getResult().getValue(User.class).role.equals("voter")) {
                        // Voter
                        Intent intent = new Intent(MainActivity.this, VoterHomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // Administrator
                        Intent intent = new Intent(MainActivity.this, AdministratorHomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void forgotPassword() {
        String email = inputEmail.getText().toString();
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter correct Email");
        } else {
            progressDialog.setMessage("Please wait while we confirm the email");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(MainActivity.this, "Password reset link was sent to your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}