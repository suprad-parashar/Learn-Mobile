package com.learn.android.activities.learn;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.learn.android.R;
import com.learn.android.fragments.learn.CourseOverViewFragment;

import java.util.Objects;

public class CourseViewActivity extends AppCompatActivity {

	//Initialise UI Variables
	public String title, domain, branch;
	public static Type type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_view);

		//Get Title
		title = getIntent().getStringExtra("title");
		domain = getIntent().getStringExtra("domain");
		branch = getIntent().getStringExtra("branch");

		//Show Course Overview.
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.course_view_fragment, new CourseOverViewFragment(title, domain, branch))
				.commit();

		//Setup Toolbar.
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
