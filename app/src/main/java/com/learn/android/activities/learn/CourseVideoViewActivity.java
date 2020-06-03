package com.learn.android.activities.learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseVideoViewActivity extends AppCompatActivity {

	//Declare UI Variables
	private RatingBar ratingBar;
	private ImageView shareButton;
	private TextView nameTextView, fromTextView, ratingTextView;
	private YouTubePlayerView youTubePlayerView;

	//Initialise Firebase Variables
	private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_video_view);

		//Initialise UI Variables
		youTubePlayerView = findViewById(R.id.youtube_player);
		nameTextView = findViewById(R.id.title);
		fromTextView = findViewById(R.id.from);
		ratingTextView = findViewById(R.id.rating);
		ratingBar = findViewById(R.id.rating_bar);
		shareButton = findViewById(R.id.share);

		//Get Data from Intent
		final String name = getIntent().getStringExtra("name");
		double rating = getIntent().getDoubleExtra("rating", 0);
		final String link = getIntent().getStringExtra("link");
		final String from = getIntent().getStringExtra("from");
		final String reference = getIntent().getStringExtra("reference");

		//Set Reference
		assert reference != null;
		final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(reference).child("rating").child(user.getUid());

		//Set Rating
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

		//Save Rating
		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				databaseReference.setValue(rating);
			}
		});

		//Share
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);

				String shareMessage = "Hey there! I am learning " + name + " from " + from + " on Learn!" +
						"\nURL: " + link +
						"\nWhy don't you join me on Learn!";
				sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
				sendIntent.setType("text/plain");

				Intent shareIntent = Intent.createChooser(sendIntent, null);
				startActivity(shareIntent);
			}
		});

		//Initialise Youtube Player.
		getLifecycle().addObserver(youTubePlayerView);
		youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
			@Override
			public void onReady(@NonNull YouTubePlayer youTubePlayer) {
				youTubePlayer.loadVideo(extractVideoIdFromLink(link), 0);
			}
		});

		//Set Data
		nameTextView.setText(name);
		fromTextView.setText(from);
		ratingTextView.setText(String.valueOf(rating));

		//Add Back Button to Toolbar.
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setTitle(name);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Extracts the Youtube Video ID from URL.
	 *
	 * @param youTubeUrl The URL of the Video
	 * @return The Video ID of the Video
	 */
	public static String extractVideoIdFromLink(String youTubeUrl) {
		String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed/)[^#&?]*";
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(youTubeUrl);
		if (matcher.find()) {
			return matcher.group();
		} else {
			return "error";
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			onBackPressed();
		return true;
	}
}
