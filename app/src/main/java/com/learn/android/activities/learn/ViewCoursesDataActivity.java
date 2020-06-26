package com.learn.android.activities.learn;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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
import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.adapters.CoursesDataViewPagerAdapter;
import com.learn.android.fragments.learn.DisplayCoursesTemplateFragment;

import java.util.Objects;

public class ViewCoursesDataActivity extends AppCompatActivity {

	//Declare UI Variables.
	ViewPager pager;
	TabLayout tabLayout;
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(Learn.isDark ? R.style.DarkMode : R.style.LightMode);
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

		//Initialise UI Variables.
		pager = findViewById(R.id.pager);
		tabLayout = findViewById(R.id.tab_layout);

		//Setup Tabs and Courses.
		final CoursesDataViewPagerAdapter adapter = new CoursesDataViewPagerAdapter(getSupportFragmentManager(), 100);
		assert referencePath != null;
		assert name != null;
		final DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(referencePath).child(name);
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					String heading = snapshot.getKey();
					assert heading != null;
					if (!heading.equals("image"))
						adapter.addFragment(new DisplayCoursesTemplateFragment(reference.child(heading), domain, branch), heading);
				}
				pager.setAdapter(adapter);
				tabLayout.setupWithViewPager(pager);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e("Database Error", databaseError.toString());
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
