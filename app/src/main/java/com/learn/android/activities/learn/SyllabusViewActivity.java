package com.learn.android.activities.learn;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.learn.android.Learn;
import com.learn.android.R;

import java.util.ArrayList;
import java.util.Objects;

public class SyllabusViewActivity extends AppCompatActivity {

	//Declare UI Variables.
	ListView syllabusList;

	//Declare Data Variables
	ArrayList<String> syllabus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(Learn.isDark ? R.style.DarkMode : R.style.LightMode);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_syllabus_view);

		//Set Back button to Toolbar.
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		//Initialise UI Variables.
		syllabusList = findViewById(R.id.syllabus_list);
		syllabus = new ArrayList<>();

		//Initialise Firebase Variables.
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		assert user != null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			onBackPressed();
		return super.onOptionsItemSelected(item);
	}
}
