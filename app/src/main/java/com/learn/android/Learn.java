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
	public static final String CHANGE_MESSAGE = "1. Added Daily Notifications (Works only if app is in foreground)" +
			"\n2. Added Scheduled Reminder Notifications (Works only if app is in foreground)" +
			"\n3. Changed Primary Color";

	public static final String DAILY_REMINDER__NOTIFICATION_CHANNEL_ID = "LearnDailyReminder";
	public static final String SCHEDULED_REMINDER__NOTIFICATION_CHANNEL_ID = "LearnScheduledReminder";

	public static int isDark;

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);

		//Create Notification Channels.
		createNotificationChannel("Daily Reminder", "Daily Reminders to help you learn something new everyday", DAILY_REMINDER__NOTIFICATION_CHANNEL_ID);
		createNotificationChannel("Reminders", "Scheduled Reminders set by you for learning something new", SCHEDULED_REMINDER__NOTIFICATION_CHANNEL_ID);
		SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
		if (!settings.contains("darkMode"))
			settings.edit().putInt("darkMode", AppCompatDelegate.MODE_NIGHT_NO).apply();
		isDark = settings.getInt("darkMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		sendDailyNotifications();
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
}