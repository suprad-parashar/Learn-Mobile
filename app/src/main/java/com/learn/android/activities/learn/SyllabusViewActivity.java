package com.learn.android.activities.learn;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.learn.android.R;

import java.util.ArrayList;
import java.util.Objects;

public class SyllabusViewActivity extends AppCompatActivity {

	//Declare UI Variables.
	ListView syllabusList;
	ArrayList<String> syllabus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_syllabus_view);

		//Set Back button to Toolbar.
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		//Initialise UI Variables.
		syllabusList = findViewById(R.id.syllabus_list);
		syllabus = new ArrayList<>();

		//Initialise Firebase Variables.
//		FirebaseDatabase database = FirebaseDatabase.getInstance();
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		assert user != null;
//		final DatabaseReference reference = database.getReference().child("users").child(user.getUid()).child("data");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			onBackPressed();
		return super.onOptionsItemSelected(item);
	}
}
