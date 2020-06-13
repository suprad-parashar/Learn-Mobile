package com.learn.android;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;

public class Learn extends Application {
	public static final String CHANGE_MESSAGE = "1. Added Navigation Drawer Support" +
			"\n2. Added Prerequisites Intent" +
			"\n3. Video Playback Bug Fixes";

	public static int isDark;

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
		if (!settings.contains("darkMode"))
			settings.edit().putInt("darkMode", AppCompatDelegate.MODE_NIGHT_NO).apply();
		isDark = settings.getInt("darkMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
	}
}