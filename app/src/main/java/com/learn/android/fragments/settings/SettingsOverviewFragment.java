package com.learn.android.fragments.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.learn.android.R;

public class SettingsOverviewFragment extends Fragment {

	Switch darkMode;
	TextView changePassword, openSourceLibraries;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_settings_overview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		darkMode = view.findViewById(R.id.dark_mode);
		changePassword = view.findViewById(R.id.change_password);
		openSourceLibraries = view.findViewById(R.id.osl);

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

		final SharedPreferences settings = requireActivity().getPreferences(Context.MODE_PRIVATE);
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
