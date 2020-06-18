package com.learn.android;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.database.FirebaseDatabase;

public class Learn extends Application {
	public static final String CHANGE_MESSAGE = "1. Redesigned Home Page";

	public static int isDark;

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		createNotificationChannel("Upload", "Notifications on Upload Progress and Status", "LearnUpload");
		SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
		if (!settings.contains("darkMode"))
			settings.edit().putInt("darkMode", AppCompatDelegate.MODE_NIGHT_NO).apply();
		isDark = settings.getInt("darkMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
	}

	/**
	 * Create a Notification Channel
	 */
	private void createNotificationChannel(String name, String description, String channelId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(channelId, name, importance);
			channel.setDescription(description);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			assert notificationManager != null;
			notificationManager.createNotificationChannel(channel);
		}
	}
}