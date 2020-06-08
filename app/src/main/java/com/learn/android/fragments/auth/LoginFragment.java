package com.learn.android.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.activities.HomeActivity;

public class LoginFragment extends Fragment {

	//Constants
	private static final int GOOGLE_SIGN_IN_RC = 474;

	//Declare Firebase Variables
	FirebaseAuth auth = FirebaseAuth.getInstance();

	// Declare UI Variables
	EditText emailEditText, passwordEditText;
	Button loginButton, signUpButton;
	ImageView googleSignInButton;
	GoogleSignInClient client;
	TextView forgotPassword;
	ProgressBar load;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//Initialise Views.
		emailEditText = view.findViewById(R.id.email);
		passwordEditText = view.findViewById(R.id.password);
		loginButton = view.findViewById(R.id.login_button);
		signUpButton = view.findViewById(R.id.sign_up_button);
		googleSignInButton = view.findViewById(R.id.google_sign_in_button);
		forgotPassword = view.findViewById(R.id.forgot_password);
		load = view.findViewById(R.id.wait);

		//Disable Loading Icon.
		load.setVisibility(View.GONE);

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
				} else if (password.equals("")) {
					passwordEditText.setError("Enter a password");
					passwordEditText.requestFocus();
				} else {
					//Sign In Using Email and Password.
					auth.signInWithEmailAndPassword(email, password)
							.addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
								@Override
								public void onComplete(@NonNull Task<AuthResult> task) {
									if (task.isSuccessful()) {
										FirebaseUser user = auth.getCurrentUser();
										assert user != null;
										if (user.isEmailVerified()) {
											insertDefaultUserProfileDataInFirebaseDatabase();
											load.setVisibility(View.GONE);
											Intent intent = new Intent(requireContext(), HomeActivity.class);
											intent.putExtra("signIn", true);
											startActivity(intent);
											requireActivity().finish();
										} else {
											load.setVisibility(View.GONE);
											Toast.makeText(requireContext(), "Verify your email before logging in", Toast.LENGTH_LONG).show();
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
				getParentFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
						.replace(R.id.auth_fragment, new RegistrationFragment())
						.commit();
			}
		});

		//Google Sign In Options for Google Sign In Authentication.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		client = GoogleSignIn.getClient(requireActivity(), gso);

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
					auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful())
								Toast.makeText(requireContext(), "Reset Link sent to mail.", Toast.LENGTH_LONG).show();
							else {
								emailEditText.setError("Email Address not Registered! Register now.");
								emailEditText.requestFocus();
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
				.addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							FirebaseUser user = auth.getCurrentUser();
							assert user != null;
							insertDefaultUserProfileDataInFirebaseDatabase();
							load.setVisibility(View.GONE);
							Intent intent = new Intent(requireActivity(), HomeActivity.class);
							intent.putExtra("signIn", true);
							startActivity(intent);
							requireActivity().finish();
						} else {
							Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_LONG).show();
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
				Toast.makeText(requireActivity(), "Authentication Failed.", Toast.LENGTH_LONG).show();
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
				checkAndAddData(reference, dataSnapshot, "points", 0);
				checkAndAddData(reference, dataSnapshot, "status", "Newbie");
				checkAndAddData(reference, dataSnapshot, "setup", false);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e("Data Insert Error", databaseError.toString());
				Toast.makeText(requireContext(), "Firebase Database Error Occurred.", Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * Checks if key and value exists in the Database. If not, adds the data into the Firebase Database.
	 *
	 * @param reference    The Path in the Database.
	 * @param dataSnapshot The Data Snapshot of the page in the Database.
	 * @param key          The key to be added.
	 * @param value        The value of the key.
	 * @param <T>          Generic Class Parameter to accommodate All types of Objects.
	 */
	private <T> void checkAndAddData(DatabaseReference reference, DataSnapshot dataSnapshot, String key, T value) {
		if (!dataSnapshot.child(key).exists()) {
			reference.child(key).setValue(value);
		}
	}
}
