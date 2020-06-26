package com.learn.android.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.activities.HomeActivity;
import com.learn.android.activities.learn.CourseVideoViewActivity;
import com.learn.android.activities.learn.Type;

public class NotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			//Check if is a reminder Notification.
			boolean isReminderNotification = intent.getBooleanExtra("isReminderNotification", false);

			if (isReminderNotification) {
				Type type = Type.valueOf(intent.getStringExtra("type"));
				Intent resourceIntent;
				String name = intent.getStringExtra("name");
				boolean isStrict = intent.getBooleanExtra("isStrict", false);
				if (type == Type.VIDEO) {
					resourceIntent = new Intent(context, CourseVideoViewActivity.class);
					resourceIntent.putExtra("name", name);
					resourceIntent.putExtra("link", intent.getStringExtra("link"));
					resourceIntent.putExtra("from", intent.getStringExtra("from"));
					resourceIntent.putExtra("isPlaylist", intent.getBooleanExtra("isPlaylist", false));
					resourceIntent.putExtra("time", (float) 0.0);
					resourceIntent.putExtra("index", (long) 0);
					resourceIntent.putExtra("videoNames", intent.getStringArrayListExtra("videoNames"));
					resourceIntent.putExtra("videoLinks", intent.getStringArrayListExtra("videoLinks"));
					resourceIntent.putExtra("reference", intent.getStringExtra("reference"));
				} else {
					resourceIntent = new Intent(Intent.ACTION_VIEW);
					resourceIntent.setData(Uri.parse(intent.getStringExtra("link")));
				}
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 484, resourceIntent, 0);
				NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Learn.SCHEDULED_REMINDER__NOTIFICATION_CHANNEL_ID)
						.setContentIntent(pendingIntent)
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle("Time to Learn")
						.setContentText("It's the time to learn " + name)
						.setPriority(NotificationCompat.PRIORITY_MAX)
						.setOngoing(isStrict);
				NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
				notificationManager.notify(776, builder.build());
			} else {
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
	}
}
