package com.learn.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.fragments.auth.LoginFragment;

public class AuthActivity extends AppCompatActivity {

	@Override
	protected void onStart() {
		super.onStart();
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			startActivity(new Intent(AuthActivity.this, HomeActivity.class));
			finish();
		} else {
			AppCompatDelegate.setDefaultNightMode(Learn.isDark);
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		//Change to Login Fragment
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
				.replace(R.id.auth_fragment, new LoginFragment())
				.commit();
	}
}
