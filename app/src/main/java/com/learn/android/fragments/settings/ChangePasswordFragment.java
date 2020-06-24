package com.learn.android.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.learn.android.Learn;
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
		final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Learn.isDark ? R.style.DarkMode : R.style.LightMode);
		LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
		return localInflater.inflate(R.layout.fragment_change_password, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Set Title.
		Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Change Password");

		//Initialise UI Variables.
		currentPasswordEditText = view.findViewById(R.id.change_password_current_password);
		newPasswordEditText = view.findViewById(R.id.change_password_new_password);
		confirmPasswordEditText = view.findViewById(R.id.change_password_confirm_password);
		Button saveButton = view.findViewById(R.id.save_password_button);

		//Handle Password Change.
		saveButton.setOnClickListener(v -> {
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
						.addOnCompleteListener(task -> {
							if (task.isSuccessful()) {
								user.updatePassword(newPassword)
										.addOnCompleteListener(task1 -> {
											Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_LONG).show();
											HomeActivity.navController.navigate(R.id.navigation_profile);
										});
							} else {
								currentPasswordEditText.setError("Incorrect Password");
								currentPasswordEditText.requestFocus();
							}
						});
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getParentFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
					.replace(R.id.settings_fragment_view, new SettingsOverviewFragment())
					.commit();
		}
		return true;
	}
}
