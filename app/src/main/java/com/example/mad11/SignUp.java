package com.example.mad11;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userID;

    TextInputEditText signUpName, signUpEmail, signUpPassword;
    Button signup;
    ProgressBar progressBar;
    TextView loginNow;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signUpName = findViewById(R.id.name);
        signUpEmail = findViewById(R.id.email);
        signUpPassword = findViewById(R.id.password);
        signup = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progress_bar);
        loginNow = findViewById(R.id.loginNow);

        //REDIRECT TO LOGIN
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        //CREATE NEW USER AUTHENTICATION + USER PROFILE IN DATABASE
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name, email, password;
                name = String.valueOf(signUpName.getText());
                email = String.valueOf(signUpEmail.getText());
                password = String.valueOf(signUpPassword.getText());

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignUp.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUp.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUp.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                                    userID = auth.getCurrentUser().getUid();

                                    DocumentReference userDoc = db.collection("users").document(userID);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("email", email);
                                    userDoc.set(user).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("app", e.toString());
                                        }
                                    });

                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(SignUp.this, "Failed to create a new user.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}