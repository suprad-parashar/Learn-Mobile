package com.learn.android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.fragments.auth.LoginFragment;

public class AuthActivity extends AppCompatActivity {

	//Declare UI Variables
	public static boolean isOnLoginPage = true;

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
//		setTheme(Learn.isDark ? R.style.DarkMode : R.style.LightMode);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		//Change to Login Fragment
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
				.replace(R.id.auth_fragment, new LoginFragment())
				.commit();
	}

	@Override
	public void onBackPressed() {
		if (isOnLoginPage) {
			super.onBackPressed();
		} else {
			//Change to Login Fragment
			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
					.replace(R.id.auth_fragment, new LoginFragment())
					.commit();
		}
	}
}
