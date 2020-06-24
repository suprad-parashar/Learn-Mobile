package com.learn.android.activities.learn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.objects.Activity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseVideoViewActivity extends AppCompatActivity {

	//Declare UI Variables
	private RatingBar ratingBar;
	public YouTubePlayer mYouTubePlayer;

	//Declare Data Variables.
	private String name, link, from, reference;
	private boolean isPlaylist;
	private float time, duration;
	private float loadTime;
	public int videoIndex = 0;

	//Initialise Firebase Variables
	private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
	final DatabaseReference activityReference = FirebaseDatabase.getInstance().getReference()
			.child("users")
			.child(Objects.requireNonNull(user).getUid())
			.child("activity");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(Learn.isDark ? R.style.DarkMode : R.style.LightMode);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_video_view);

		//Initialise UI Variables
		YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
		final TextView nameTextView = findViewById(R.id.title);
		TextView fromTextView = findViewById(R.id.from);
		final TextView ratingTextView = findViewById(R.id.rating);
		ratingBar = findViewById(R.id.rating_bar);
		ImageView shareButton = findViewById(R.id.share);
		final ListView videosPlaylist = findViewById(R.id.videos_playlist);

		//Get Data from Intent
		name = getIntent().getStringExtra("name");
		link = getIntent().getStringExtra("link");
		from = getIntent().getStringExtra("from");
		isPlaylist = getIntent().getBooleanExtra("isPlaylist", false);
		loadTime = getIntent().getFloatExtra("time", 0);
		videoIndex = (int) getIntent().getLongExtra("index", 0);
		final ArrayList<String> videoNames = getIntent().getStringArrayListExtra("videoNames");
		final ArrayList<String> videoLinks = getIntent().getStringArrayListExtra("videoLinks");
		reference = getIntent().getStringExtra("reference");

		//Set Reference
		assert reference != null;
		final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(reference).child("rating");

		//Initialise Youtube Player.
		getLifecycle().addObserver(youTubePlayerView);
		youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
			@Override
			public void onReady(@NonNull YouTubePlayer youTubePlayer) {
				mYouTubePlayer = youTubePlayer;
				assert videoLinks != null;
				String id = extractVideoIdFromLink((isPlaylist) ? videoLinks.get(videoIndex) : link);
				mYouTubePlayer.loadVideo(id, loadTime);

				//Setup Playlist
				if (isPlaylist) {
					videosPlaylist.setVisibility(View.VISIBLE);
					assert videoNames != null;
					nameTextView.setText(videoNames.get(videoIndex));
					//Setup Playlist Data.
					videosPlaylist.setAdapter(new ArrayAdapter<>(CourseVideoViewActivity.this, android.R.layout.simple_list_item_activated_1, videoNames));
					videosPlaylist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					videosPlaylist.setItemChecked(videoIndex, true);
					videosPlaylist.setOnItemClickListener((parent, view, position, id1) -> {
						mYouTubePlayer.loadVideo(extractVideoIdFromLink(videoLinks.get(position)), 0);
						videoIndex = position;
					});
				} else {
					nameTextView.setText(name);
					videosPlaylist.setVisibility(View.GONE);
				}
			}

			@Override
			public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState playerState) {
				if (playerState == PlayerConstants.PlayerState.PAUSED) {
					updateActivity();
				}
			}

			@Override
			public void onPlaybackQualityChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackQuality playbackQuality) {

			}

			@Override
			public void onPlaybackRateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackRate playbackRate) {

			}

			@Override
			public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError playerError) {
				Log.e("Player Error", playerError.name());
			}

			@Override
			public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float v) {
				time = v;
			}

			@Override
			public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float v) {
				duration = v;
			}

			@Override
			public void onVideoLoadedFraction(@NonNull YouTubePlayer youTubePlayer, float v) {

			}

			@Override
			public void onVideoId(@NonNull YouTubePlayer youTubePlayer, @NonNull String s) {

			}

			@Override
			public void onApiChange(@NonNull YouTubePlayer youTubePlayer) {

			}
		});

		//Set Rating
		ratingBar.setRating(0);
		databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				double ratingTotal = 0;
				int count = 0;
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					ratingTotal += (long) snapshot.getValue();
					count++;
					if (Objects.equals(snapshot.getKey(), user.getUid()))
						ratingBar.setRating((long) snapshot.getValue());
				}
				ratingTextView.setText(String.valueOf(ratingTotal / count));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				ratingBar.setRating(0);
			}
		});

		//Save Rating
		ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> databaseReference.child(user.getUid()).setValue(rating));

		//Share
		shareButton.setOnClickListener(v -> {
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);

			String shareMessage = "Hey there! I am learning " + name + " from " + from + " on Learn!" +
					"\nURL: " + link +
					"\nWhy don't you join me on Learn!";
			sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
			sendIntent.setType("text/plain");

			Intent shareIntent = Intent.createChooser(sendIntent, null);
			startActivity(shareIntent);
		});

		//Set Data
		fromTextView.setText(from);

		//Add Back Button to Toolbar.
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(name);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Create or Update the User Activity.
	 */
	private void updateActivity() {
		activityReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				boolean found = false;
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					//Update Activity.
					Activity activity = snapshot.getValue(Activity.class);
					assert activity != null;
					if (!activity.getName().equals(name))
						continue;
					found = true;
					activity.setDone(time >= duration - 90);
					activity.setTime(time);
					if (activity.isPlaylist())
						activity.setIndex(videoIndex);
					SimpleDateFormat format = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
					String date = format.format(new Date());
					activity.setDate(date);
					activityReference.child(Objects.requireNonNull(snapshot.getKey())).setValue(activity);
				}
				if (!found) {
					//Create new Activity.
					final Activity activity = new Activity();
					activity.setName(name);
					SimpleDateFormat format = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
					String date = format.format(new Date());
					activity.setDate(date);
					activity.setTime(time);
					activity.setPlaylist(isPlaylist);
					if (isPlaylist)
						activity.setIndex(videoIndex);
					activity.setType(Type.VIDEO);
					activity.setFrom(from);
					activity.setLink(link);
					activity.setDone(time >= duration - 90);
					activity.setReference(reference);
					activityReference.child(String.valueOf(dataSnapshot.getChildrenCount())).setValue(activity);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e("Database Error", databaseError.toString());
			}
		});
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

	@Override
	public void onBackPressed() {
		updateActivity();
		super.onBackPressed();
	}
}
