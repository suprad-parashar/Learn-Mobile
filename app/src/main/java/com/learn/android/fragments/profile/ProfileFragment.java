package com.learn.android.fragments.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.learn.android.R;
import com.learn.android.activities.AboutActivity;
import com.learn.android.activities.AuthActivity;
import com.learn.android.activities.FAQActivity;
import com.learn.android.activities.HomeActivity;
import com.learn.android.activities.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProfileFragment extends Fragment {

	//Initialise Firebase Variables.
	private FirebaseAuth auth = FirebaseAuth.getInstance();
	private FirebaseUser user = auth.getCurrentUser();

	private ImageView profileImage;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		//Hide Bottom Navigation Bar
		BottomNavigationView bottomBar = requireActivity().findViewById(R.id.nav_view);
		bottomBar.setVisibility(View.GONE);

		//Initialise UI Variables.
		final String[] PROFILE_LINKS_LIST = getResources().getStringArray(R.array.profile_links);
		//Declare UI Variables.
		//	private TextView pointsTextView;
//		ProgressBar wait = view.findViewById(R.id.wait);

		//Declare UI Variables.
		TextView nameTextView = view.findViewById(R.id.profile_user_name);
		TextView emailTextView = view.findViewById(R.id.profile_user_email);
//		pointsTextView = view.findViewById(R.id.profile_user_bp);
		ImageButton settings = view.findViewById(R.id.settings);
		ListView profileLinks = view.findViewById(R.id.profile_links);
		profileImage = view.findViewById(R.id.profile_image);

		//Set Profile Image
		try {
			File file = new File(requireContext().getFilesDir(), "profile.jpg");
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
			profileImage.setImageBitmap(bitmap);
		} catch (FileNotFoundException ignored) {

		} finally {
			final File file = new File(requireContext().getFilesDir(), "profile.jpg");
			StorageReference profilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(user.getUid() + ".jpg");
			profilePictureReference.getFile(file)
					.addOnSuccessListener(taskSnapshot -> {
						try {
							Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
							profileImage.setImageBitmap(bitmap);
						} catch (FileNotFoundException ignored) {

						}
					})
					.addOnFailureListener(e1 -> {
						Toast.makeText(requireContext(), "An error occurred while loading the profile picture", Toast.LENGTH_SHORT).show();
						Log.e("Failure Error", e1.toString());
					});
		}

		//Handle Settings Click.
		settings.setOnClickListener(v -> startActivity(new Intent(requireActivity(), SettingsActivity.class)));

		//Set ProgressBar
//		wait.setVisibility(View.VISIBLE);

		//Set User Data.
		nameTextView.setText(user.getDisplayName());
		emailTextView.setText(user.getEmail());

		//Fetch User Data from Firebase Database.
//		FirebaseDatabase database = FirebaseDatabase.getInstance();
//		DatabaseReference reference = database.getReference().child("users").child(user.getUid()).child("data");
//		reference.addListenerForSingleValueEvent(new ValueEventListener() {
//			@Override
//			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//				long points = (long) dataSnapshot.child("points").getValue();
//				String pointsText = points + " Brains";
//				pointsTextView.setText(pointsText);
//				wait.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onCancelled(@NonNull DatabaseError databaseError) {
//				wait.setVisibility(View.GONE);
//			}
//		});

		//Setup Profile Links.
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.layout_profile_settings, R.id.setting, PROFILE_LINKS_LIST);
		profileLinks.setAdapter(arrayAdapter);
		profileLinks.setOnItemClickListener((parent, view1, position, id) -> {
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
				case "FAQs":
					startActivity(new Intent(getActivity(), FAQActivity.class));
					break;
				case "My Activity":
					HomeActivity.navController.navigate(R.id.navigation_my_activity);
					break;
			}
		});
	}


}