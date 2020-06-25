package com.learn.android;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;
import com.learn.android.utils.NotificationReceiver;

public class Learn extends Application {
	public static final String CHANGE_MESSAGE = "1. Added Points";

	public static final String DAILY_REMINDER__NOTIFICATION_CHANNEL_ID = "LearnDailyReminder";
	public static final String SCHEDULED_REMINDER__NOTIFICATION_CHANNEL_ID = "LearnScheduledReminder";

	public static int randomVideoNumber;
	public static boolean isDark;

	public static SharedPreferences settings;

	@Override
	public void onCreate() {
		super.onCreate();

		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

		//Set Caching of Firebase Database Data.
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);

		//Setup Settings SharedPreferences.
		settings = getSharedPreferences("settings", MODE_PRIVATE);

		//RandomVideoNumber Data.
		if (settings.contains("randomVideoNumber"))
			randomVideoNumber = settings.getInt("randomVideoNumber", 0);
		else {
			settings.edit().putInt("randomVideoNumber", 0).apply();
			randomVideoNumber = 0;
		}

		//Create Notification Channels.
		createNotificationChannel("Daily Reminder", "Daily Reminders to help you learn something new everyday", DAILY_REMINDER__NOTIFICATION_CHANNEL_ID);
		createNotificationChannel("Reminders", "Scheduled Reminders set by you for learning something new", SCHEDULED_REMINDER__NOTIFICATION_CHANNEL_ID);
		sendDailyNotifications();

		//Dark Mode
		if (!settings.contains("darkMode"))
			settings.edit().putBoolean("darkMode", false).apply();
		isDark = settings.getBoolean("darkMode", false);
	}

	/**
	 * Sends a daily notification.
	 */
	private void sendDailyNotifications() {
		Intent intent = new Intent(this, NotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 736, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 12);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		assert manager != null;
		manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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

	/**
	 * Increments the Random Video Number of the user.
	 * This helps in avoiding viewing of the repeated videos as well as help watch newly added videos.
	 */
	public static void incrementRandomVideoNumber() {
		settings.edit().putInt("randomVideoNumber", ++randomVideoNumber).apply();
	}

	public static void setApplicationTheme(boolean isDark) {
		settings.edit().putBoolean("darkMode", isDark).apply();
		Learn.isDark = isDark;
	}
}