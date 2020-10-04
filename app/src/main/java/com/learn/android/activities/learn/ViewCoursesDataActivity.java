package com.learn.android.activities.learn;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.CourseAdapter;
import com.learn.android.adapters.CoursesDataViewPagerAdapter;
import com.learn.android.fragments.learn.DisplayCoursesTemplateFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ViewCoursesDataActivity extends AppCompatActivity {

	//Declare UI Variables.
	ListView listView;
	Toolbar toolbar;

	//Declare Firebase Variables
	DatabaseReference reference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_courses_data);

		//Get Data from Intent.
		String name = getIntent().getStringExtra("name");
		String referencePath = getIntent().getStringExtra("reference");
		final String domain = getIntent().getStringExtra("domain");
		final String branch = getIntent().getStringExtra("branch");

		//Setup Toolbar.
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(name);

		//Setup Recycler View.
		listView = findViewById(R.id.courses_list);

		//Add Data.
		assert referencePath != null;
		assert name != null;
		reference = FirebaseDatabase.getInstance().getReferenceFromUrl(referencePath).child(name);
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<String> courses = new ArrayList<>();
				for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
					for (DataSnapshot course : snapshot.getChildren())
						courses.add(String.valueOf(course.getValue()));
				}
				Collections.sort(courses);
				listView.setAdapter(new CourseAdapter(ViewCoursesDataActivity.this, courses, domain, branch));
				listView.setFastScrollEnabled(true);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}
}
