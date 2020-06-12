package com.learn.android.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class CoursesDataViewPagerAdapter extends FragmentPagerAdapter {

	//Declare Adapter Data.
	private final ArrayList<Fragment> fragments = new ArrayList<>();
	private final ArrayList<String> titles = new ArrayList<>();

	public CoursesDataViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
		super(fm, behavior);
	}

	public void addFragment(Fragment fragment, String name) {
		fragments.add(fragment);
		titles.add(name);
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return titles.get(position);
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
}
