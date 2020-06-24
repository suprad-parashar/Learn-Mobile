package com.learn.android.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.fragments.settings.SettingsOverviewFragment;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(Learn.isDark ? R.style.DarkMode : R.style.LightMode);
		setContentView(R.layout.activity_settings);

		//Set Toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		//Change to Overview.
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
				.replace(R.id.settings_fragment_view, new SettingsOverviewFragment())
				.commit();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}
}
