package com.example.android.learn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    //Declare Firebase Variables
    FirebaseAuth auth = FirebaseAuth.getInstance();

    //Declare UI Variables
    EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Alter Toolbar
        ActionBar titleBar = getSupportActionBar();
        assert titleBar != null;
        titleBar.setTitle("Sign Up");
        titleBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_registration);

        //Initialise Views.
        firstNameEditText = findViewById(R.id.registration_first_name);
        lastNameEditText = findViewById(R.id.registration_last_name);
        emailEditText = findViewById(R.id.registration_email);
        passwordEditText = findViewById(R.id.registration_password);
        confirmPasswordEditText = findViewById(R.id.registration_confirm_password);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstName = firstNameEditText.getText().toString().trim();
                final String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                int value;
                if (firstName.equals("")) {
                    firstNameEditText.setError("Please Enter your Name");
                    firstNameEditText.requestFocus();
                } else if (lastName.equals("")) {
                    lastNameEditText.setError("Please Enter a last Name");
                    lastNameEditText.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid Email Address");
                    emailEditText.requestFocus();
                } else if (emailAlreadyExists(email)) {
                    emailEditText.setError("Email Address already Registered.");
                    emailEditText.requestFocus();
                } else if ((value = isValidPassword(password)) != 0) {
                    switch (value) {
                        case 1:
                            passwordEditText.setError("Enter a password");
                            passwordEditText.requestFocus();
                            break;
                        case 2:
                            passwordEditText.setError("Password must have a minimum length of 8 characters");
                            passwordEditText.requestFocus();
                            break;
                        case 3:
                            passwordEditText.setError("Password must contain at least one UPPERCASE Character");
                            passwordEditText.requestFocus();
                            break;
                        case 4:
                            passwordEditText.setError("Password must contain at least one lowercase Character");
                            passwordEditText.requestFocus();
                            break;
                        case 5:
                            passwordEditText.setError("Password must contain at least one Digit");
                            passwordEditText.requestFocus();
                            break;
                        case 6:
                            passwordEditText.setError("Password must contain at least one Special Character");
                            passwordEditText.requestFocus();
                            break;
                    }
                } else if (!confirmPassword.equals(password)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    confirmPasswordEditText.requestFocus();
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        assert user != null;
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(firstName + " " + lastName).build();
                                        user.updateProfile(profileUpdates);
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            auth.signOut();
                                                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                            intent.putExtra("registration", true);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "An Error Occurred", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private int isValidPassword(String password) {
        if (password.equals(""))
            return 1;
        else if (password.length() < 8)
            return 2;
        else {
            boolean upperCase = false, lowerCase = false, digit = false, special = false;
            for (int i = 0; i < password.length(); i++) {
                if (Character.isUpperCase(password.charAt(i)))
                    upperCase = true;
                else if (Character.isLowerCase(password.charAt(i)))
                    lowerCase = true;
                else if (Character.isDigit(password.charAt(i)))
                    digit = true;
                else if (!Character.isLetterOrDigit(password.charAt(i)))
                    special = true;
            }
            if (!upperCase)
                return 3;
            else if (!lowerCase)
                return 4;
            else if (!digit)
                return 5;
            else if (!special)
                return 6;
        }
        return 0;
    }

    private boolean emailAlreadyExists(String email) {
        final boolean[] exists = new boolean[1];
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        exists[0] = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size() != 0;
                    }
                });
        return exists[0];
    }
}
