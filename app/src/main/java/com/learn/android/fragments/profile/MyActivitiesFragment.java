package com.learn.android.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.MyActivitiesAdapter;
import com.learn.android.objects.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MyActivitiesFragment extends Fragment {

	//Declare UI Variables.
	RecyclerView recyclerView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_activites, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Setup Recycler View.
		recyclerView = view.findViewById(R.id.activities_list);
		LinearLayoutManager manager = new LinearLayoutManager(requireContext());
		manager.setOrientation(RecyclerView.VERTICAL);
		recyclerView.setLayoutManager(manager);

		final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
				.child("users")
				.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
				.child("activity");
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<Activity> activities = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					Activity activity = snapshot.getValue(Activity.class);
					activities.add(0, activity);
				}
				recyclerView.setAdapter(new MyActivitiesAdapter(requireContext(), activities));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}
