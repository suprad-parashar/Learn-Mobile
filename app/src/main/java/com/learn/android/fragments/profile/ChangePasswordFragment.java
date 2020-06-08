package com.learn.android.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.learn.android.R;
import com.learn.android.activities.HomeActivity;
import com.learn.android.fragments.auth.RegistrationFragment;

import java.util.Objects;

public class ChangePasswordFragment extends Fragment {

	//Declare UI Variables.
	private TextInputEditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_change_password, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Initialise UI Variables.
		currentPasswordEditText = view.findViewById(R.id.change_password_current_password);
		newPasswordEditText = view.findViewById(R.id.change_password_new_password);
		confirmPasswordEditText = view.findViewById(R.id.change_password_confirm_password);
		Button saveButton = view.findViewById(R.id.save_password_button);

		//Handle Password Change.
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String currentPassword = Objects.requireNonNull(currentPasswordEditText.getText()).toString();
				final String newPassword = Objects.requireNonNull(newPasswordEditText.getText()).toString();
				String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString();
				int value;
				if (currentPassword.equals("")) {
					currentPasswordEditText.setError("Enter your current Password");
					currentPasswordEditText.requestFocus();
				} else if ((value = RegistrationFragment.isValidPassword(newPassword)) != 0) {
					switch (value) {
						case 1:
							newPasswordEditText.setError("Enter a password");
							newPasswordEditText.requestFocus();
							break;
						case 2:
							newPasswordEditText.setError("Password must have a minimum length of 8 characters");
							newPasswordEditText.requestFocus();
							break;
						case 3:
							newPasswordEditText.setError("Password must contain at least one UPPERCASE, lowercase, digit and special Character");
							newPasswordEditText.requestFocus();
							break;
					}
				} else if (confirmPassword.equals("")) {
					confirmPasswordEditText.setError("Enter password again!");
					confirmPasswordEditText.requestFocus();
				} else if (!confirmPassword.equals(newPassword)) {
					confirmPasswordEditText.setError("Passwords do not match");
					confirmPasswordEditText.requestFocus();
				} else {
					final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
					assert user != null;
					AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPassword);
					user.reauthenticate(credential)
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										user.updatePassword(newPassword)
												.addOnCompleteListener(new OnCompleteListener<Void>() {
													@Override
													public void onComplete(@NonNull Task<Void> task) {
														Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_LONG).show();
														HomeActivity.navController.navigate(R.id.navigation_profile);
													}
												});
									} else {
										currentPasswordEditText.setError("Incorrect Password");
										currentPasswordEditText.requestFocus();
									}
								}
							});
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			HomeActivity.navController.navigate(R.id.navigation_profile);
		return true;
	}
}
