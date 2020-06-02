package com.learn.android.activities.learn;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;

public class CourseDocumentViewActivity extends AppCompatActivity {

	//Declare UI Variables.
	WebView webView;

	//Initialise Firebase Variables.
	DatabaseReference databaseReference;
	FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_document_view);

		//Get Data from Intent
		String link = getIntent().getStringExtra("link");
		String name = getIntent().getStringExtra("name");
		String reference = getIntent().getStringExtra("reference");

		//Set Reference
		assert reference != null;
		databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(reference).child("rating").child(user.getUid());

		//Add Back Button to Toolbar
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setTitle(name);
		actionBar.setDisplayHomeAsUpEnabled(true);

		//Load WebPage.
		webView = findViewById(R.id.web_view);
		webView.loadUrl(link);
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
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rate_document_view, menu);
		return true;
	}
}
