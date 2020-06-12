package com.learn.android.activities.learn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.objects.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CourseDocumentViewActivity extends AppCompatActivity {

	//Declare UI Variables.
	WebView webView;

	//Data
	String link, name, from;

	//Initialise Firebase Variables.
	DatabaseReference databaseReference;
	FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
	final DatabaseReference activityReference = FirebaseDatabase.getInstance().getReference()
			.child("users")
			.child(Objects.requireNonNull(user).getUid())
			.child("activity");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_document_view);

		//Get Data from Intent
		link = getIntent().getStringExtra("link");
		from = getIntent().getStringExtra("from");
		name = getIntent().getStringExtra("name");
		final String reference = getIntent().getStringExtra("reference");

		//Set Reference
		assert reference != null;
		databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(reference).child("rating").child(user.getUid());

		//Add Back Button to Toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(name);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		//Load WebPage.
		webView = findViewById(R.id.web_view);
		webView.loadUrl(link);

		//Add User Activity.
		activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				boolean found = false;
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					//Modify Existing Activity.
					Activity activity = snapshot.getValue(Activity.class);
					assert activity != null;
					if (!activity.getName().equals(name))
						continue;
					found = true;
					SimpleDateFormat format = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
					String date = format.format(new Date());
					activity.setDate(date);
					activityReference.child(Objects.requireNonNull(snapshot.getKey())).setValue(activity);
				}
				if (!found) {
					//Create a new Activity.
					final Activity activity = new Activity();
					activity.setName(name);
					SimpleDateFormat format = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
					String date = format.format(new Date());
					activity.setDate(date);
					activity.setType(Type.DOCUMENT);
					activity.setFrom(from);
					activity.setLink(link);
					activity.setReference(reference);
					activityReference.child(String.valueOf(dataSnapshot.getChildrenCount())).setValue(activity);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e("ERROR", databaseError.toString());
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			onBackPressed();
		else if (item.getItemId() == R.id.rate_menu_item) {
			//Create a Rating Bar.
			final LinearLayout linearLayout = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			linearLayout.setLayoutParams(params);
			linearLayout.setGravity(Gravity.CENTER);
			final RatingBar ratingBar = new RatingBar(this);
			ratingBar.setNumStars(5);
			ratingBar.setStepSize(1);
			ratingBar.setForegroundGravity(Gravity.CENTER);
			linearLayout.addView(ratingBar);

			//Set Rating (If Any)
			databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					try {
						long ratingValue = (long) dataSnapshot.getValue();
						ratingBar.setRating(ratingValue);
					} catch (NullPointerException e) {
						ratingBar.setRating(0);
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					ratingBar.setRating(0);
				}
			});

			//Show Dialog to set rating.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true)
					.setTitle("Rating")
					.setView(linearLayout)
					.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							databaseReference.setValue(ratingBar.getRating());
						}
					});
			builder.create().show();
		} else if (item.getItemId() == R.id.share_menu_item) {
			//Share Link
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			String shareMessage = "Hey there! I am learning " + name + " on Learn!" +
					"\nURL: " + link +
					"\nWhy don't you join me on Learn!";
			sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
			sendIntent.setType("text/plain");
			Intent shareIntent = Intent.createChooser(sendIntent, null);
			startActivity(shareIntent);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rate_document_view, menu);
		return true;
	}
}
