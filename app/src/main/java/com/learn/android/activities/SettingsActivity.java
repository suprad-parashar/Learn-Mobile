package com.learn.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.learn.android.R;

public class SettingsActivity extends AppCompatActivity {

	Switch darkMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		darkMode = findViewById(R.id.dark_mode);

		final SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		boolean isDark = settings.getBoolean("darkMode", AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
		darkMode.setChecked(isDark);

		darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("darkMode", true);
					editor.apply();
				} else {
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("darkMode", false);
					editor.apply();
				}
			}
		});
	}
}