package com.learn.android.fragments.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.learn.android.R;

public class ToolsFragment extends Fragment {
	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tools, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Hide Bottom Navigation Bar
		BottomNavigationView bottomBar = requireActivity().findViewById(R.id.nav_view);
		bottomBar.setVisibility(View.GONE);
	}
}