package com.learn.android.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;

import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.activities.HomeActivity;
import com.learn.android.activities.SettingsActivity;

public class SettingsOverviewFragment extends Fragment {

	//Declare UI Variables.
	Switch darkMode;
	TextView changePassword, openSourceLibraries, reminders;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Learn.isDark ? R.style.DarkMode : R.style.LightMode);
		LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
		return localInflater.inflate(R.layout.fragment_settings_overview, container, false);
//		return inflater.inflate(R.layout.fragment_settings_overview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//Initialise UI Variables.
		darkMode = view.findViewById(R.id.dark_mode);
		changePassword = view.findViewById(R.id.change_password);
		openSourceLibraries = view.findViewById(R.id.osl);
		reminders = view.findViewById(R.id.toggle_reminders);

		//Handle Reminder Clicks
		reminders.setOnClickListener(v -> {
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
		});

		//Handle Change Password.
		changePassword.setOnClickListener(v -> getParentFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
				.replace(R.id.settings_fragment_view, new ChangePasswordFragment())
				.commit());

		//Set Dark Mode Switch
		darkMode.setChecked(Learn.isDark);

		//Handle Dark Mode Selection.
		darkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
			Learn.setApplicationTheme(isChecked);
			requireActivity().setTheme(isChecked ? R.style.DarkMode : R.style.LightMode);
			TaskStackBuilder.create(requireActivity())
					.addNextIntent(new Intent(getActivity(), HomeActivity.class))
					.addNextIntent(requireActivity().getIntent())
					.startActivities();
//			startActivity(new Intent(requireActivity(), SettingsActivity.class));
//			requireActivity().finish();
		});

		//Handle OSL Click.
		openSourceLibraries.setOnClickListener(v -> getParentFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
				.replace(R.id.settings_fragment_view, new OpenSourceLibrariesFragment())
				.commit());
	}
}
