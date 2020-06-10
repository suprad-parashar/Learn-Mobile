package com.learn.android.activities.learn;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.learn.android.R;
import com.learn.android.fragments.learn.CourseOverViewFragment;

import java.util.Objects;

public class CourseViewActivity extends AppCompatActivity {

	//Initialise UI Variables
	public String title;
	public static Type type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_view);

//		final SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
//		int isDark = settings.getInt("darkMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//		AppCompatDelegate.setDefaultNightMode(isDark);

		//Get Title
		title = getIntent().getStringExtra("title");

		//Show Course Overview.
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.course_view_fragment, new CourseOverViewFragment(title))
				.commit();

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}
}
