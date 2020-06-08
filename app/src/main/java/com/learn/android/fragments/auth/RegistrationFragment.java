package com.learn.android.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.learn.android.R;

public class RegistrationFragment extends Fragment {

	//Declare Firebase Variables
	FirebaseAuth auth = FirebaseAuth.getInstance();

	//Declare UI Variables
	EditText firstNameEditText;
	EditText lastNameEditText;
	EditText emailEditText;
	EditText passwordEditText;
	Button registerButton;
	TextView signInButton;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_registration, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Initialise Views.
		firstNameEditText = view.findViewById(R.id.first_name);
		lastNameEditText = view.findViewById(R.id.last_name);
		emailEditText = view.findViewById(R.id.email);
		passwordEditText = view.findViewById(R.id.password);
		registerButton = view.findViewById(R.id.sign_up_button);
		signInButton = view.findViewById(R.id.sign_in_button);

		//Handle Register Button Clicks
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String firstName = firstNameEditText.getText().toString().trim();
				final String lastName = lastNameEditText.getText().toString().trim();
				final String email = emailEditText.getText().toString().trim();
				final String password = passwordEditText.getText().toString();
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
							passwordEditText.setError("Password must contain at least one UPPERCASE, lowercase, Digit and a special Character");
							passwordEditText.requestFocus();
							break;
					}
				} else {
					//Create new user using Email and Password.
					auth.createUserWithEmailAndPassword(email, password)
							.addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
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

										Toast.makeText(requireContext(), "You are registered! Verification email sent. Check your Inbox", Toast.LENGTH_LONG).show();

										getParentFragmentManager()
												.beginTransaction()
												.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
												.replace(R.id.auth_fragment, new LoginFragment())
												.commit();
									} else {
										Toast.makeText(requireContext(), "Email Already Registered", Toast.LENGTH_LONG).show();
									}
								}
							});
				}
			}
		});

		//Go back to Sign In Page
		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
						.replace(R.id.auth_fragment, new LoginFragment())
						.commit();
			}
		});
	}

	/**
	 * Checks if Password meets conditions.
	 *
	 * @param password The password to be checked.
	 * @return True if password satisfies all conditions else False.
	 */
	public static int isValidPassword(String password) {
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
			if (upperCase && lowerCase && digit && special)
				return 0;
			else
				return 3;
		}
	}
}
