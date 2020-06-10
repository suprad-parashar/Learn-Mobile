package com.learn.android.fragments.settings;

import android.content.Context;
import android.content.Intent;
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

import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.activities.SettingsActivity;

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

		darkMode.setChecked(Learn.isDark != AppCompatDelegate.MODE_NIGHT_NO);

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
