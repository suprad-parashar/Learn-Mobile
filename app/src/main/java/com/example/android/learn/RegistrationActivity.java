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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * Activity to Register new Users.
 */
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

        //Handle Register Button Clicks
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstName = firstNameEditText.getText().toString().trim();
                final String lastName = lastNameEditText.getText().toString().trim();
                final String email = emailEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString();
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
                    //Check if email is already registered.
                    auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size() != 0) {
                                        emailEditText.setError("Email Address already Registered.");
                                        emailEditText.requestFocus();
                                    } else {
                                        //Create new user using Email and Password.
                                        auth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = auth.getCurrentUser();
                                                            assert user != null;

                                                            //Update Name
                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(firstName + " " + lastName).build();
                                                            user.updateProfile(profileUpdates);

                                                            user.sendEmailVerification();
                                                            auth.signOut();

                                                            //Go Back to Login Page.
                                                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                            intent.putExtra("registration", true);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegistrationActivity.this, "An Error Occurred", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * Checks if Password meets conditions.
     *
     * @param password The password to be checked.
     * @return True if password satisfies all conditions else False.
     */
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
}
