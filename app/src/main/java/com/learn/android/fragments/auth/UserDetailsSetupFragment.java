package com.learn.android.fragments.auth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class UserDetailsSetupFragment extends Fragment {

	//Initialise Constants
	private static final int PICK_IMAGE = 274;
	private static final int TAKE_IMAGE = 284;

	//Initialise UI Variables
	private ImageView profileImage;
	private EditText usernameEditText;
	private TextView notAvailableTextView;
	private Button nextButton;

	//Initialise Firebase Variables
	private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
	private FirebaseStorage storage = FirebaseStorage.getInstance();
	private StorageReference storageReference = storage.getReference().child(Objects.requireNonNull(user).getUid());
	private FirebaseDatabase database = FirebaseDatabase.getInstance();
	private DatabaseReference databaseReference = database.getReference().child("users");

	//Declare Data Variables
	private byte[] imageData = null;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user_details_setup, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Initialise UI Variables
		profileImage = view.findViewById(R.id.user_profile_image);
		usernameEditText = view.findViewById(R.id.username_edit_text);
		notAvailableTextView = view.findViewById(R.id.availability_text_view);
		nextButton = view.findViewById(R.id.next_button);

		//Hide Availability TextView
		notAvailableTextView.setVisibility(View.GONE);

		//Pick Image.
		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Dexter.withContext(requireContext())
						.withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
						.withListener(new MultiplePermissionsListener() {
							@Override
							public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
								if (multiplePermissionsReport.areAllPermissionsGranted()) {
//									CropImage.activity().start(requireContext(), UserDetailsSetupFragment.this);
									AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
									builder.setTitle("Choose Mode")
											.setItems(new String[]{
													"Take Photo",
													"Choose from Gallery"
											}, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													if (which == 0) {
														Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
														startActivityForResult(captureIntent, TAKE_IMAGE);
													} else {
//														Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//														startActivityForResult(gallery, PICK_IMAGE);

														Intent intent = new Intent();
														intent.setType("image/*");
														intent.setAction(Intent.ACTION_GET_CONTENT);
														startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
													}
												}
											})
											.setCancelable(true);
									builder.create().show();
								} else {
									AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
											.setTitle("Permissions Required")
											.setMessage("Camera and Storage Permissions are needed to upload your profile picture")
											.setPositiveButton("OK", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											});
									builder.create().show();
								}
							}

							@Override
							public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
								permissionToken.continuePermissionRequest();
							}
						}).check();
			}
		});

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String username = usernameEditText.getText().toString().trim().toLowerCase();
				if (username.equals("")) {
					usernameEditText.setError("Username Cannot be Empty");
					usernameEditText.requestFocus();
				} else {
					Query query = databaseReference.child("users")
							.orderByChild("username")
							.equalTo(username);
					query.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							if (dataSnapshot.getChildrenCount() == 0) {
								databaseReference.child(user.getUid()).child("data").child("username").setValue(username);
								if (imageData != null) {
									UploadTask uploadTask = storageReference.putBytes(imageData);
									uploadTask.addOnFailureListener(new OnFailureListener() {
										@Override
										public void onFailure(@NonNull Exception exception) {
											Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_LONG).show();
											Log.e("Firebase Storage Failure", exception.toString());
										}
									}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
										@Override
										public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
											Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_LONG).show();
										}
									});
								}
								databaseReference.child(user.getUid()).child("data").child("setup").setValue(true);
							} else {
								notAvailableTextView.setVisibility(View.VISIBLE);
							}
						}

						@Override
						public void onCancelled(@NonNull DatabaseError databaseError) {

						}
					});
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
			Uri imageUri = data.getData();
			profileImage.setImageURI(imageUri);
			InputStream imageStream;
			try {
				imageStream = requireContext().getContentResolver().openInputStream(imageUri);
				Bitmap bmp = BitmapFactory.decodeStream(imageStream);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.WEBP, 60, stream);
				imageData = stream.toByteArray();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (requestCode == TAKE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
			Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
			profileImage.setImageBitmap(bitmap);
//			InputStream imageStream;
			try {
//				imageStream = requireContext().getContentResolver().openInputStream(imageUri);
//				Bitmap bmp = BitmapFactory.decodeStream(imageStream);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				assert bitmap != null;
				bitmap.compress(Bitmap.CompressFormat.WEBP, 60, stream);
				imageData = stream.toByteArray();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (requestCode == UCrop.REQUEST_CROP) {
//			assert data != null;
//			final Uri imageUri = UCrop.getOutput(data);
//			profileImage.setImageURI(imageUri);
//			InputStream imageStream;
//			try {
//				assert imageUri != null;
//				imageStream = requireContext().getContentResolver().openInputStream(imageUri);
//				Bitmap bmp = BitmapFactory.decodeStream(imageStream);
//				ByteArrayOutputStream stream = new ByteArrayOutputStream();
//				bmp.compress(Bitmap.CompressFormat.WEBP, 60, stream);
//				imageData = stream.toByteArray();
//				stream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		} else if (requestCode == UCrop.RESULT_ERROR) {
			Toast.makeText(requireContext(), "FUCK YOU!!!!!!!!!!!!!!!", Toast.LENGTH_LONG).show();
			assert data != null;
			Log.e("FUCK YOU", UCrop.getError(data).toString());
		}
//		} else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//			CropImage.ActivityResult result = CropImage.getActivityResult(data);
//			if (resultCode == RESULT_OK) {
//				assert result != null;
//				Uri imageUri = result.getUri();
//				profileImage.setImageURI(imageUri);
//				InputStream imageStream;
//				try {
//					imageStream = requireContext().getContentResolver().openInputStream(imageUri);
//					Bitmap bmp = BitmapFactory.decodeStream(imageStream);
//					ByteArrayOutputStream stream = new ByteArrayOutputStream();
//					bmp.compress(Bitmap.CompressFormat.WEBP, 60, stream);
//					imageData = stream.toByteArray();
//					stream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//				Toast.makeText(requireContext(), "FUCK YOU", Toast.LENGTH_LONG).show();
//			}
//		}
	}
}
