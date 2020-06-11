package com.learn.android;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;

public class Learn extends Application {
	public static final String CHANGE_MESSAGE = "Version 1.4.3 Alpha - " +
			"\n1. Removed Dark Mode" +
			"\n2. Redesigned Playlist View in Course Videos" +
			"\n\nVersion 1.4.2 Alpha - " +
			"\n1. Redesigned Profile Fragment" +
			"\n2. Redesigned Edit Profile Fragment" +
			"\n3. Redesigned Settings Activity" +
			"\n4. Added ChangeLog Feature in About Section";

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