package com.example.android.learn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * Activity to Log in users. The First activity of the App.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    //Constants
    private static final int GOOGLE_SIGN_IN_RC = 474;

    //Declare Firebase Variables
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // Declare UI Variables
    EditText emailEditText, passwordEditText;
    Button loginButton, signUpButton;
    SignInButton googleSignInButton;
    GoogleSignInClient client;
    TextView forgotPassword;
    ProgressBar load;

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
        forgotPassword = findViewById(R.id.forgot_password);
        load = findViewById(R.id.wait);

        //Disable Loading Icon.
        load.setVisibility(View.GONE);

        //Get Intent to check if a user returned after registration.
        boolean wasRegistering = getIntent().getBooleanExtra("registration", false);
        if (wasRegistering)
            Toast.makeText(this, "Successfully Registered. Check your email for verification.", Toast.LENGTH_LONG).show();

        //Handle Click on Login Button.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load.setVisibility(View.VISIBLE);
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
                                            insertDefaultUserProfileDataInFirebaseDatabase();
                                            load.setVisibility(View.GONE);
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.putExtra("signIn", true);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            load.setVisibility(View.GONE);
                                            Toast.makeText(LoginActivity.this, "Verify your email before logging in", Toast.LENGTH_LONG).show();
                                            auth.signOut();
                                        }
                                    } else {
                                        load.setVisibility(View.GONE);
                                        passwordEditText.setError("Incorrect Credentials");
                                        passwordEditText.requestFocus();
                                    }
                                }
                            });
                }
                load.setVisibility(View.GONE);
            }
        });

        //Handle Sign Up Button Clicks.
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        //Google Sign In Options for Google Sign In Authentication.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, gso);

        //Sign In Using Google.
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load.setVisibility(View.VISIBLE);
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RC);
            }
        });

        //Handle Forgot Password Clicks.
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString().trim();
                //Check if email address is valid.
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid Email Address");
                    emailEditText.requestFocus();
                } else {
                    //Check if email exists.
                    auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size() == 0) {
                                        emailEditText.setError("Email Address not Registered! Register now.");
                                        emailEditText.requestFocus();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Reset Link sent to mail.", Toast.LENGTH_LONG).show();
                                        auth.sendPasswordResetEmail(email);
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * Authenticate user using Firebase with a Google Account.
     *
     * @param acct The GoogleSignInAccount object which stores the User Data.
     */
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            insertDefaultUserProfileDataInFirebaseDatabase();
                            load.setVisibility(View.GONE);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("signIn", true);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_LONG).show();
                            load.setVisibility(View.GONE);
                        }
                    }
                });
        load.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * Inserts the default data for the User's Profile in Firebase Database.
     */
    private void insertDefaultUserProfileDataInFirebaseDatabase() {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference()
                .child("users").child(user.getUid()).child("data");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("points").exists()) {
                    reference.child("points").setValue(0);
                }
                if (!dataSnapshot.child("type").exists()) {
                    reference.child("type").setValue("Student");
                }
                if (!dataSnapshot.child("status").exists()) {
                    reference.child("status").setValue("Newbie");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WTF", databaseError.toString());
                Toast.makeText(LoginActivity.this, "Firebase Database Error Occurred.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
