package com.learn.android.activities.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.learn.android.R;
import com.learn.android.activities.HomeActivity;
import com.learn.android.fragments.auth.LoginFragment;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

	@Override
	protected void onStart() {
		super.onStart();
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			startActivity(new Intent(AuthActivity.this, HomeActivity.class));
			finish();
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		Objects.requireNonNull(getSupportActionBar()).hide();
		setContentView(R.layout.activity_auth);

		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
				.replace(R.id.auth_fragment, new LoginFragment())
				.commit();
	}
}
