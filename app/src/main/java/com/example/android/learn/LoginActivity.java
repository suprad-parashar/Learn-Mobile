package com.example.android.learn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

/**
 * Activity to Log in users. The First activity of the App.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN_RC = 474;
    //Declare Firebase Variables
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // Declare UI Variables
    EditText emailEditText, passwordEditText;
    Button loginButton, signUpButton;
    SignInButton googleSignInButton;
    GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        //Initialise Views.
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        googleSignInButton = findViewById(R.id.google_sign_in_button);

        //Get Intent to check if a user returned after registration.
        boolean wasRegistering = getIntent().getBooleanExtra("registration", false);
        if (wasRegistering)
            Toast.makeText(this, "Successfully Registered. Check your email for verification.", Toast.LENGTH_LONG).show();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, gso);


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
                                        if (Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()) {
                                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_LONG).show();
                                            auth.signOut();
                                            //TODO: Update UI.
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Verify your email before logging in", Toast.LENGTH_LONG).show();
                                            auth.signOut();
                                        }
                                    } else {
                                        passwordEditText.setError("Incorrect Credentials");
                                        passwordEditText.requestFocus();
                                    }
                                }
                            });
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RC);
            }
        });
    }

    //TODO: Not Working
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_LONG).show();
                            auth.signOut();
                            //TODO: Update UI.
                        } else {
                            Toast.makeText(LoginActivity.this, "Alpha Beta Gamma", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_RC) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
