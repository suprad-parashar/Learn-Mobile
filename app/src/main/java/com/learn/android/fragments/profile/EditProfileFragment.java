package com.learn.android.fragments.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.learn.android.R;
import com.learn.android.activities.HomeActivity;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * This class is responsible for editing the profile of the User.
 */
public class EditProfileFragment extends Fragment {

	//Define Constants
	private static final int RC_TAKE_PHOTO = 787;
	private static final int RC_CHOOSE_IMAGE = 724;

	//Initialise Firebase Variables
	private FirebaseAuth auth = FirebaseAuth.getInstance();
	private FirebaseUser user = auth.getCurrentUser();
	private FirebaseDatabase database = FirebaseDatabase.getInstance();
	private DatabaseReference reference = database.getReference().child("users").child(Objects.requireNonNull(user).getUid()).child("data");

	//Declare UI Variables.
	private EditText userNameEditText, collegeNameEditText;
	//	private Spinner currentlyIn, stream, branch, boardUniversity, semester;
//	private TextView boardUniversityLabel;
	CircleImageView profileImage;

	//Initialise Data Variables.
	Bitmap bitmap = null;
	private Uri imageUri;
	boolean isPhotoChanged = false;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_edit_profile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		//Initialise UI Variables.
		final ProgressBar wait = view.findViewById(R.id.wait);
		userNameEditText = view.findViewById(R.id.name);
		collegeNameEditText = view.findViewById(R.id.institution);
//		currentlyIn = view.findViewById(R.id.currently_in);
//		stream = view.findViewById(R.id.view_profile_stream);
//		branch = view.findViewById(R.id.branch);
//		boardUniversity = view.findViewById(R.id.board_university);
//		boardUniversityLabel = view.findViewById(R.id.board_university_label);
//		semester = view.findViewById(R.id.view_profile_semester);
		profileImage = view.findViewById(R.id.profile_image);

		//Set Progressbar.
		wait.setVisibility(View.VISIBLE);

//		//Initialise Maps to get and set Spinners.
//		final HashMap<String, Integer> currentlyInList = new HashMap<>();
//		HashMap<String, Integer> streamSchoolList = new HashMap<>();
//		HashMap<String, Integer> streamUndergraduateList = new HashMap<>();
//		HashMap<String, Integer> branchScienceList = new HashMap<>();
//		final HashMap<String, Integer> branchEngineeringList = new HashMap<>();
//		HashMap<String, Integer> boardsList = new HashMap<>();
//		HashMap<String, Integer> universitiesList = new HashMap<>();
//
//		int i = 0;
//		for (String s : getResources().getStringArray(R.array.currently_in))
//			currentlyInList.put(s, i++);
//		i = 0;
//		for (String s : getResources().getStringArray(R.array.stream_school))
//			streamSchoolList.put(s, i++);
//		i = 0;
//		for (String s : getResources().getStringArray(R.array.stream_undergraduate))
//			streamUndergraduateList.put(s, i++);
//		i = 0;
//		for (String s : getResources().getStringArray(R.array.branch_science))
//			branchScienceList.put(s, i++);
//		i = 0;
//		for (String s : getResources().getStringArray(R.array.branch_engineering))
//			branchEngineeringList.put(s, i++);
//		i = 0;
//		for (String s : getResources().getStringArray(R.array.boards))
//			boardsList.put(s, i++);
//		i = 0;
//		for (String s : getResources().getStringArray(R.array.universities))
//			universitiesList.put(s, i++);

		//Set the user data to the fields.
		userNameEditText.setText(user.getDisplayName());
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//				if (Objects.equals(dataSnapshot.child("type").getValue(), "Student")) {
//					if (Objects.equals(dataSnapshot.child("stream").getValue(), "Engineering")) {
//						branch.setSelection(dataSnapshot.child("branch").exists() ? branchEngineeringList.get(dataSnapshot.child("branch").getValue()) : 0);
//						semester.setSelection(dataSnapshot.child("semester").exists() ? Integer.parseInt(((String) Objects.requireNonNull(dataSnapshot.child("semester").getValue())).split(" ")[1]) - 1 : 0);
//					}
//				}
				collegeNameEditText.setText(dataSnapshot.child("institution").exists() ? (String) dataSnapshot.child("institution").getValue() : "");
				wait.setVisibility(View.GONE);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				wait.setVisibility(View.GONE);
			}
		});

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

		//Handle Profile Image Change
		profileImage.setOnClickListener(v -> Dexter.withContext(requireContext())
				.withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				.withListener(new MultiplePermissionsListener() {
					@Override
					public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
						if (multiplePermissionsReport.areAllPermissionsGranted()) {
							AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
							builder.setTitle("Select Mode")
									.setItems(new String[]{
											"Take Photo",
											"Choose From Gallery"
									}, (dialog, which) -> {
										if (which == 0) {
											Intent intent = new Intent(ACTION_IMAGE_CAPTURE);
											startActivityForResult(intent, RC_TAKE_PHOTO);
										} else {
											Intent intent = new Intent();
											intent.setType("image/*");
											intent.setAction(Intent.ACTION_GET_CONTENT);
											startActivityForResult(intent, RC_CHOOSE_IMAGE);
										}
									})
									.setCancelable(true);
							builder.create().show();
						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
							builder.setTitle("Permissions Required")
									.setMessage("Storage and Camera Permissions are required for obtaining Image.")
									.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
									.setCancelable(false);
							builder.create().show();
						}
					}

					@Override
					public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
						permissionToken.continuePermissionRequest();
					}
				}).check());

//		//Set Adapters
//		currentlyIn.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.currently_in)));
//		currentlyIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				switch (position) {
//					case 0:
//						stream.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.stream_undergraduate)));
//						branch.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.branch_engineering)));
//						boardUniversityLabel.setText(R.string.university);
//						boardUniversity.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.universities)));
//						semester.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getSemestersArray(8)));
//						break;
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//
//			}
//		});
	}

	/**
	 * Returns a List of Semesters.
	 *
	 * @param numberOfSemesters The number of Semesters to generate.
	 * @return An Array of String containing Text Form of Semesters.
	 */
	private String[] getSemestersArray(int numberOfSemesters) {
		String[] sems = new String[numberOfSemesters];
		for (int i = 1; i <= numberOfSemesters; i++)
			sems[i - 1] = "Semester " + i;
		return sems;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				HomeActivity.navController.navigate(R.id.navigation_view_profile);
				break;
			case R.id.save_profile_menu:
				String name;
				if (!(name = userNameEditText.getText().toString()).equals(user.getDisplayName()) && !name.equals("")) {
					UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
							.setDisplayName(name).build();
					user.updateProfile(profileUpdates);
				}
				reference.child("institution").setValue(collegeNameEditText.getText().toString());
//				reference.child("currently_in").setValue(currentlyIn.getSelectedItem().toString());
//				reference.child("stream").setValue(stream.getSelectedItem().toString());
//				reference.child("branch").setValue(branch.getSelectedItem().toString());
//				reference.child("board_university").setValue(boardUniversity.getSelectedItem().toString());
//				reference.child("semester").setValue(semester.getSelectedItem().toString());

				if (isPhotoChanged) {

					if (bitmap == null) {
						try {
							bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
						} catch (IOException e) {
							Log.e("IO Error", e.toString());
							Toast.makeText(requireContext(), "An error occurred while saving profile picture", Toast.LENGTH_SHORT).show();
						}
					}
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
					byte[] byteArray = stream.toByteArray();
					try {
						File file = new File(requireContext().getFilesDir(), "profile.jpg");
						FileOutputStream outputStream = new FileOutputStream(file);
						outputStream.write(byteArray);
						outputStream.close();
					} catch (IOException e) {
						Log.e("IO Error", e.toString());
						Toast.makeText(requireContext(), "An error occurred while saving profile picture", Toast.LENGTH_SHORT).show();
					}
					FirebaseStorage storage = FirebaseStorage.getInstance();
					StorageReference reference = storage.getReference().child("Profile Pictures").child(user.getUid() + ".jpg");
					UploadTask task = reference.putBytes(byteArray);
					task.addOnSuccessListener(taskSnapshot ->
							Toast.makeText(requireContext(), "Profile Picture Uploaded", Toast.LENGTH_SHORT).show()).addOnFailureListener(e ->
							Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show());
				}
				HomeActivity.navController.navigate(R.id.navigation_view_profile);
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == RC_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {
			isPhotoChanged = true;
			bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
			profileImage.setImageBitmap(bitmap);
		} else if (requestCode == RC_CHOOSE_IMAGE && resultCode == RESULT_OK && data != null) {
			Uri uri = data.getData();
			assert uri != null;
			isPhotoChanged = true;
			imageUri = Uri.fromFile(new File(requireContext().getCacheDir(), "IMG_" + System.currentTimeMillis()));
			UCrop.of(uri, imageUri)
					.withAspectRatio(1, 1)
					.withMaxResultSize(512, 512) // any resolution you want
					.start(requireActivity());
			Glide.with(requireContext())
					.load(imageUri)
					.into(profileImage);
		}
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.save_profile_menu, menu);
	}
}
