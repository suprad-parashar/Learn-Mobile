package com.learn.android.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.Objects;

public class HomeFragment extends Fragment {

	//Declare UI Variables.
	RecyclerView homeActivitiesRecyclerView;
	TextView nameView;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Display Name
		nameView = view.findViewById(R.id.person_name);
		nameView.setText(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName()).split(" ")[0]);

		//Show Bottom Navigation Bar
		BottomNavigationView bottomBar = requireActivity().findViewById(R.id.nav_view);
		bottomBar.setVisibility(View.VISIBLE);

		//Setup Recycler View.
		homeActivitiesRecyclerView = view.findViewById(R.id.home_cards_list);
		LinearLayoutManager manager = new LinearLayoutManager(requireContext());
		manager.setOrientation(RecyclerView.HORIZONTAL);
		homeActivitiesRecyclerView.setLayoutManager(manager);

		DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
				.child("users")
				.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
				.child("activity");
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<Activity> activities = new ArrayList<>();
				long index = dataSnapshot.getChildrenCount() - 1;
				int count = 3;
				while (index >= 0 && count > 0) {
					Activity activity = dataSnapshot.child(String.valueOf(index--)).getValue(Activity.class);
					count--;
					activities.add(activity);
				}
				homeActivitiesRecyclerView.setAdapter(new MyActivitiesAdapter(requireContext(), activities, false));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}