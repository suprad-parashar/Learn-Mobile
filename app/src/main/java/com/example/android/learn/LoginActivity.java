package com.example.android.learn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity to Log in users. The First activity of the App.
 */
public class LoginActivity extends AppCompatActivity {

    //Firebase Auth Variable
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // Declare UI Variables
    EditText emailEditText, passwordEditText;
    Button loginButton, signUpButton;
    SignInButton googleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialise Views.
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        googleSignInButton = findViewById(R.id.google_sign_in_button);

        //Handle Click on Login Button.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                if (email.equals("")) {
                    emailEditText.setError("Enter Email Address");
                    emailEditText.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid Email Address");
                    emailEditText.requestFocus();
                } else {
                    //Sign In Using Email and Password.
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_LONG).show();
                                        auth.signOut();
                                        //TODO: Update UI.
                                    } else {
                                        passwordEditText.setError("Incorrect Credentials");
                                        passwordEditText.requestFocus();
                                    }
                                }
                            });
                }
            }
        });
    }
}
