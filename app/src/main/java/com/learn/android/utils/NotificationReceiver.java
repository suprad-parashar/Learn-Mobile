package com.learn.android.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.activities.HomeActivity;

public class NotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent homeIntent = new Intent(context, HomeActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 484, homeIntent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Learn.DAILY_REMINDER__NOTIFICATION_CHANNEL_ID)
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("Let's Learn")
				.setContentText("What shall we learn today?")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify(536, builder.build());
	}
}
