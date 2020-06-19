package com.learn.android.fragments.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.activities.SettingsActivity;

public class SettingsOverviewFragment extends Fragment {

	//Declare UI Variables.
	Switch darkMode, reminders;
	TextView changePassword, openSourceLibraries, reminderTime;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_settings_overview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Initialise UI Variables.
		darkMode = view.findViewById(R.id.dark_mode);
		changePassword = view.findViewById(R.id.change_password);
		openSourceLibraries = view.findViewById(R.id.osl);
		reminders = view.findViewById(R.id.reminders_switch);
		reminderTime = view.findViewById(R.id.reminders_time);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
			assert manager != null;
			NotificationChannel channel = manager.getNotificationChannel(Learn.DAILY_REMINDER__NOTIFICATION_CHANNEL_ID);
			reminders.setChecked(channel.getImportance() != NotificationManager.IMPORTANCE_NONE);
		} else {
			reminders.setChecked(NotificationManagerCompat.from(requireContext()).areNotificationsEnabled());
		}

		reminders.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
					intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
					intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
					intent.putExtra(Settings.EXTRA_CHANNEL_ID, Learn.DAILY_REMINDER__NOTIFICATION_CHANNEL_ID);
					startActivity(intent);
				} else {
					intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
					intent.putExtra("app_package", requireContext().getPackageName());
					intent.putExtra("app_uid", requireContext().getApplicationInfo().uid);
				}
			}
		});

		//Handle Change Password.
		changePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
						.replace(R.id.settings_fragment_view, new ChangePasswordFragment())
						.commit();
			}
		});

		//Set Dark Mode Switch
		darkMode.setChecked(Learn.isDark != AppCompatDelegate.MODE_NIGHT_NO);

		//Handle Dark Mode Selection.
		darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				final SharedPreferences settings = requireActivity().getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				if (isChecked) {
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
					Learn.isDark = AppCompatDelegate.MODE_NIGHT_YES;
					editor.putInt("darkMode", AppCompatDelegate.MODE_NIGHT_YES);
				} else {
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
					Learn.isDark = AppCompatDelegate.MODE_NIGHT_NO;
					editor.putInt("darkMode", AppCompatDelegate.MODE_NIGHT_NO);
				}
				editor.apply();
				editor.commit();
				startActivity(new Intent(requireActivity(), SettingsActivity.class));
				requireActivity().finish();
			}
		});

		//Handle OSL Click.
		openSourceLibraries.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
						.replace(R.id.settings_fragment_view, new OpenSourceLibrariesFragment())
						.commit();
			}
		});
	}
}
