package com.learn.android.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.learn.android.activities.HomeActivity;

public class ViewProfileFragment extends Fragment {

	//Initialise Firebase Variables.
	private FirebaseAuth auth = FirebaseAuth.getInstance();
	private FirebaseUser user = auth.getCurrentUser();
	private FirebaseDatabase database = FirebaseDatabase.getInstance();

	//Initialise UI Variables.
	private TextView collegeName;
	private TextView currentlyIn, stream, branch, boardUniversity;
	private TextView type, semester;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_view_profile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		//Initialise UI Variables.
		final ProgressBar wait = view.findViewById(R.id.wait_view_profile);
		TextView userName = view.findViewById(R.id.user_name_view_profile);
		collegeName = view.findViewById(R.id.college_view_profile);
		currentlyIn = view.findViewById(R.id.user_currently_in_view_profile);
		stream = view.findViewById(R.id.stream_view_profile);
		branch = view.findViewById(R.id.branch_view_profile);
		boardUniversity = view.findViewById(R.id.university_board_view_profile);
		type = view.findViewById(R.id.user_type_view_profile);
		semester = view.findViewById(R.id.semester_view_profile);

		//Set ProgressBar.
		wait.setVisibility(View.VISIBLE);

		//Set User Data.
		userName.setText(user.getDisplayName());
		final DatabaseReference reference = database.getReference().child("users").child(user.getUid()).child("data");
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				collegeName.setText(getDataFromFirebase(dataSnapshot, "institution"));
				currentlyIn.setText(getDataFromFirebase(dataSnapshot, "currently_in"));
				stream.setText(getDataFromFirebase(dataSnapshot, "stream"));
				branch.setText(getDataFromFirebase(dataSnapshot, "branch"));
				boardUniversity.setText(getDataFromFirebase(dataSnapshot, "board_university"));
				type.setText(getDataFromFirebase(dataSnapshot, "type"));
				semester.setText(getDataFromFirebase(dataSnapshot, "semester"));
				wait.setVisibility(View.GONE);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Toast.makeText(getActivity(), "An error occurred while fetching Data", Toast.LENGTH_LONG).show();
				wait.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.edit_profile_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit_profile_menu:
				HomeActivity.navController.navigate(R.id.navigation_edit_profile);
				break;
			case android.R.id.home:
				HomeActivity.navController.navigate(R.id.navigation_profile);
				break;
		}
		return true;
	}

	/**
	 * Simplified Method to fetch Data from Firebase.
	 *
	 * @param dataSnapshot The Data Snapshot from Firebase Database.
	 * @param key          The key of the data to be fetched.
	 * @return The value of the key if exists else Unknown.
	 */
	private String getDataFromFirebase(DataSnapshot dataSnapshot, String key) {
		return dataSnapshot.child(key).exists() ? (String) dataSnapshot.child(key).getValue() : getString(R.string.unknown);
	}
}
