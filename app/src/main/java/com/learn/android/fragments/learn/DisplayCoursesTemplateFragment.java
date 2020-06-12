package com.learn.android.fragments.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.BranchAdapter;
import com.learn.android.adapters.CourseAdapter;

import java.util.ArrayList;

public class DisplayCoursesTemplateFragment extends Fragment {

	DatabaseReference reference;
	RecyclerView recyclerView;

	public DisplayCoursesTemplateFragment(DatabaseReference reference) {
		this.reference = reference;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_display_courses, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView = view.findViewById(R.id.courses_list);
		LinearLayoutManager manager = new LinearLayoutManager(requireContext());
		manager.setOrientation(RecyclerView.VERTICAL);
		recyclerView.setLayoutManager(manager);

		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<String> courses = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren())
					courses.add(String.valueOf(snapshot.getValue()));
				recyclerView.setAdapter(new CourseAdapter(requireContext(), courses));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}