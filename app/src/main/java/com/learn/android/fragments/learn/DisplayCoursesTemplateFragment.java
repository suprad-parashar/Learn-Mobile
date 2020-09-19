package com.learn.android.fragments.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.CourseAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class DisplayCoursesTemplateFragment extends Fragment {

	//Declare UI Variables
	ListView listView;

	//Declare Firebase Variables.
	DatabaseReference reference;

	//Declare Data Variables
	String domain, branch;

	public DisplayCoursesTemplateFragment() {
		//Required Empty Constructor.
	}

	public DisplayCoursesTemplateFragment(@NonNull DatabaseReference reference, String domain, String branch) {
		this.reference = reference;
		this.domain = domain;
		this.branch = branch;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_display_courses, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Setup Recycler View.
		listView = view.findViewById(R.id.courses_list);

		//Add Data.
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<String> courses = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren())
					courses.add(String.valueOf(snapshot.getValue()));
				Collections.sort(courses);
				listView.setAdapter(new CourseAdapter(requireContext(), courses, domain, branch));
				listView.setFastScrollEnabled(true);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}
