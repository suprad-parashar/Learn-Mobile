package com.learn.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.activities.learn.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AddResourceActivity extends AppCompatActivity {

	//Define Constants
	private static final int MAIL_INTENT_RC = 647;

	//Declare UI Variables
	EditText titleEditText, linkEditText, prerequisitesEditText;
	Spinner domainSpinner, branchSpinner, courseSpinner, typeSpinner;
	ProgressBar loading;
	Button addResourceButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(Learn.isDark ? R.style.DarkMode : R.style.LightMode);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_resource);

		//Setup Toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		//Initialise UI Variables
		titleEditText = findViewById(R.id.resource_title);
		linkEditText = findViewById(R.id.resource_link);
		prerequisitesEditText = findViewById(R.id.resource_prerequisites);
		domainSpinner = findViewById(R.id.resource_domain);
		branchSpinner = findViewById(R.id.resource_branch);
		courseSpinner = findViewById(R.id.resource_course);
		typeSpinner = findViewById(R.id.resource_type);
		addResourceButton = findViewById(R.id.save_resource);
		loading = findViewById(R.id.wait);

		//Get Data from Intent
		final String domain = getIntent().getStringExtra("domain");
		final String branch = getIntent().getStringExtra("branch");
		final String course = getIntent().getStringExtra("course");
		String type = getIntent().getStringExtra("type");

		//Setup Type Spinner
		typeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Type.values()));
		typeSpinner.setSelection(Type.valueOf(type).ordinal());

		//Populate Domains
		final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("domain");
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<String> domains = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren())
					domains.add(snapshot.getKey());
				Collections.sort(domains);
				domainSpinner.setAdapter(new ArrayAdapter<>(AddResourceActivity.this, R.layout.support_simple_spinner_dropdown_item, domains));
				domainSpinner.setSelection(domains.indexOf(domain));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		//Populate Branches
		domainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				final String domainSelected = String.valueOf(domainSpinner.getSelectedItem());
				reference.child(domainSelected).addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						ArrayList<String> branches = new ArrayList<>();
						for (DataSnapshot snapshot : dataSnapshot.getChildren())
							branches.add(snapshot.getKey());
						branches.remove("image");
						Collections.sort(branches);
						if (domainSelected.equals(domain))
							branchSpinner.setAdapter(new ArrayAdapter<>(AddResourceActivity.this, R.layout.support_simple_spinner_dropdown_item, branches));
						branchSpinner.setSelection(branches.indexOf(branch));
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		//Populate Courses
		branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				final String domainSelected = String.valueOf(domainSpinner.getSelectedItem());
				final String branchSelected = String.valueOf(branchSpinner.getSelectedItem());
				reference.child(domainSelected).child(branchSelected).addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						ArrayList<String> courses = new ArrayList<>();
						for (DataSnapshot typeSnapshot : dataSnapshot.getChildren())
							for (DataSnapshot snapshot : typeSnapshot.getChildren())
								courses.add(String.valueOf(snapshot.getValue()));
						Collections.sort(courses);
						courseSpinner.setAdapter(new ArrayAdapter<>(AddResourceActivity.this, R.layout.support_simple_spinner_dropdown_item, courses));
						if (branchSelected.equals(branch))
							courseSpinner.setSelection(courses.indexOf(course));
						loading.setVisibility(View.GONE);
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						Toast.makeText(AddResourceActivity.this, "Database Error", Toast.LENGTH_LONG).show();
					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		//Add Resource
		addResourceButton.setOnClickListener(v -> {
			String title = titleEditText.getText().toString().trim();
			String link = linkEditText.getText().toString().trim();
			String prerequisites = prerequisitesEditText.getText().toString().trim();
			prerequisites = prerequisites.equals("") ? "None" : prerequisites;
			if (title.equals("")) {
				titleEditText.setError("Please add a Title to the Resource");
				titleEditText.requestFocus();
			} else if (!URLUtil.isValidUrl(link)) {
				linkEditText.setError("Invalid URL");
				linkEditText.requestFocus();
			} else {
				Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
				mailIntent.setData(Uri.parse("mailto:"));
				String type1 = typeSpinner.getSelectedItem().toString();
				mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"learnhelp@googlegroups.com"});
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Add " + type1 + " Resource");
				String body = "Hi there," +
						"\nI would like to contribute to this Project and help others by suggesting the following " + type1 + "." +
						"\n\nInformation about the " + type1 + ":" +
						"\n\tName: " + title +
						"\n\tLink: " + link +
						"\n\tDomain: " + domainSpinner.getSelectedItem().toString() +
						"\n\tBranch: " + branchSpinner.getSelectedItem().toString() +
						"\n\tCourse: " + courseSpinner.getSelectedItem().toString() +
						"\n\tPrerequisites: " + prerequisites;
				mailIntent.putExtra(Intent.EXTRA_TEXT, body);
				startActivityForResult(mailIntent, MAIL_INTENT_RC);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MAIL_INTENT_RC) {
			Toast.makeText(this, "Suggestion Under Review", Toast.LENGTH_LONG).show();
			onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			onBackPressed();
		return true;
	}
}
