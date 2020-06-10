package com.learn.android.activities.learn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

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

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseVideoViewActivity extends AppCompatActivity {

	//Declare UI Variables
	private RatingBar ratingBar;
	private TextView nameTextView;
//	private Button next, previous;
//	private Spinner videoIndexSpinner;
	private YouTubePlayer mYouTubePlayer;
	private ListView videosPlaylist;
	private int count;

	//Video Index
	private int videoIndex = 0;

	//Initialise Firebase Variables
	private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_video_view);

//		final SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
//		int isDark = settings.getInt("darkMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//		AppCompatDelegate.setDefaultNightMode(isDark);

		//Initialise UI Variables
		YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
		nameTextView = findViewById(R.id.title);
		TextView fromTextView = findViewById(R.id.from);
		TextView ratingTextView = findViewById(R.id.rating);
		ratingBar = findViewById(R.id.rating_bar);
		ImageView shareButton = findViewById(R.id.share);
//		previous = findViewById(R.id.previous);
//		next = findViewById(R.id.next);
//		videoIndexSpinner = findViewById(R.id.video_index);
		videosPlaylist = findViewById(R.id.videos_playlist);

		//Get Data from Intent
		final String name = getIntent().getStringExtra("name");
		double rating = getIntent().getDoubleExtra("rating", 0);
		final String link = getIntent().getStringExtra("link");
		final String from = getIntent().getStringExtra("from");
		final boolean isPlaylist = getIntent().getBooleanExtra("isPlaylist", false);
		final ArrayList<String> videoNames = getIntent().getStringArrayListExtra("videoNames");
		final ArrayList<String> videoLinks = getIntent().getStringArrayListExtra("videoLinks");
		final String reference = getIntent().getStringExtra("reference");

		//Set Reference
		assert reference != null;
		final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(reference).child("rating").child(user.getUid());

		//Initialise Youtube Player.
		getLifecycle().addObserver(youTubePlayerView);
		youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
			@Override
			public void onReady(@NonNull YouTubePlayer youTubePlayer) {
				mYouTubePlayer = youTubePlayer;
				assert videoLinks != null;
				String id = extractVideoIdFromLink((isPlaylist) ? videoLinks.get(0) : link);
				mYouTubePlayer.loadVideo(id, 0);

//				videoIndexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//					@Override
//					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//						videoIndex = position;
//						if (position == count - 1) {
//							next.setEnabled(false);
//							previous.setEnabled(true);
//						} else if (position == 0) {
//							previous.setEnabled(false);
//							next.setEnabled(true);
//						} else {
//							previous.setEnabled(true);
//							next.setEnabled(true);
//						}
//
//						assert videoNames != null;
//						mYouTubePlayer.loadVideo(extractVideoIdFromLink(videoLinks.get(videoIndex)), 0);
//						nameTextView.setText(videoNames.get(videoIndex));
//						videoIndexSpinner.setSelection(videoIndex);
//					}
//
//					@Override
//					public void onNothingSelected(AdapterView<?> parent) {
//
//					}
//				});
			}
		});

		if (isPlaylist) {
//			next.setVisibility(View.VISIBLE);
//			previous.setVisibility(View.VISIBLE);
//			videoIndexSpinner.setVisibility(View.VISIBLE);
			assert videoLinks != null;
			count = videoLinks.size();
//			previous.setEnabled(false);
//			videoIndexSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getVideoNumbersStringList(count)));
//			videoIndexSpinner.setSelection(0);
			assert videoNames != null;
			videosPlaylist.setVisibility(View.VISIBLE);
			videosPlaylist.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, videoNames));
			videosPlaylist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			videosPlaylist.setItemChecked(0, true);
			videosPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					mYouTubePlayer.loadVideo(extractVideoIdFromLink(videoLinks.get(position)), 0);
				}
			});
			nameTextView.setText(videoNames.get(0));
		} else {
//			next.setVisibility(View.GONE);
//			previous.setVisibility(View.GONE);
//			videoIndexSpinner.setVisibility(View.GONE);
			nameTextView.setText(name);
		}



//		next.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				videoIndex++;
//				if (videoIndex == count - 1)
//					next.setEnabled(false);
//				previous.setEnabled(true);
//				assert videoNames != null;
//				assert videoLinks != null;
//				mYouTubePlayer.loadVideo(extractVideoIdFromLink(videoLinks.get(videoIndex)), 0);
//				nameTextView.setText(videoNames.get(videoIndex));
//				videoIndexSpinner.setSelection(videoIndex);
//			}
//		});
//
//		previous.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				videoIndex--;
//				if (videoIndex == 0)
//					previous.setEnabled(false);
//				next.setEnabled(true);
//				assert videoNames != null;
//				assert videoLinks != null;
//				mYouTubePlayer.loadVideo(extractVideoIdFromLink(videoLinks.get(videoIndex)), 0);
//				nameTextView.setText(videoNames.get(videoIndex));
//				videoIndexSpinner.setSelection(videoIndex);
//			}
//		});

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

		//Set Data
		fromTextView.setText(from);
		ratingTextView.setText(String.valueOf(rating));

		//Add Back Button to Toolbar.
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(name);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

	/**
	 * Obtain Strings containing the the Video Numbers in text.
	 *
	 * @param count The total count of videos in playlist
	 * @return String Array
	 */
	private String[] getVideoNumbersStringList(int count) {
		String[] data = new String[count];
		for (int i = 1; i <= count; i++)
			data[i - 1] = "Video " + i;
		return data;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			onBackPressed();
		return true;
	}
}
