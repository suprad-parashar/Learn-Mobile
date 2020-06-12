package com.learn.android.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.activities.AboutActivity;
import com.learn.android.activities.AuthActivity;
import com.learn.android.activities.HomeActivity;
import com.learn.android.activities.SettingsActivity;

public class ProfileFragment extends Fragment {

	//Initialise Firebase Variables.
	private FirebaseAuth auth = FirebaseAuth.getInstance();
	private FirebaseUser user = auth.getCurrentUser();

	//Declare UI Variables.
	private TextView pointsTextView;
	private ProgressBar wait;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		//Initialise UI Variables.
		final String[] PROFILE_LINKS_LIST = getResources().getStringArray(R.array.profile_links);
		wait = view.findViewById(R.id.wait);

		//Declare UI Variables.
		TextView nameTextView = view.findViewById(R.id.profile_user_name);
		TextView emailTextView = view.findViewById(R.id.profile_user_email);
		pointsTextView = view.findViewById(R.id.profile_user_bp);
		ImageButton settings = view.findViewById(R.id.settings);
		ListView profileLinks = view.findViewById(R.id.profile_links);

		//Handle Settings Click.
		settings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(requireActivity(), SettingsActivity.class));
			}
		});

		//Set ProgressBar
		wait.setVisibility(View.VISIBLE);

		//Set User Data.
		nameTextView.setText(user.getDisplayName());
		emailTextView.setText(user.getEmail());

		//Fetch User Data from Firebase Database.
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference reference = database.getReference().child("users").child(user.getUid()).child("data");
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				long points = (long) dataSnapshot.child("points").getValue();
				String pointsText = points + " Brains";
				pointsTextView.setText(pointsText);
				wait.setVisibility(View.GONE);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				wait.setVisibility(View.GONE);
			}
		});

		//Setup Profile Links.
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, PROFILE_LINKS_LIST);
		profileLinks.setAdapter(arrayAdapter);
		profileLinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (PROFILE_LINKS_LIST[position]) {
					case "My Profile":
						HomeActivity.navController.navigate(R.id.navigation_view_profile);
						break;
					case "About":
						startActivity(new Intent(requireActivity(), AboutActivity.class));
						break;
					case "Logout":
						auth.signOut();
						startActivity(new Intent(getActivity(), AuthActivity.class));
						requireActivity().finish();
						break;
					case "My Activity":
						HomeActivity.navController.navigate(R.id.navigation_my_activity);
						break;
				}
			}
		});
	}
}