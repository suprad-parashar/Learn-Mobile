package com.learn.android.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.OpenSourceLibrariesAdapter;
import com.learn.android.objects.OpenSourceLibrary;

import java.util.ArrayList;

public class OpenSourceLibrariesActivity extends AppCompatActivity {

	private RecyclerView librariesView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_source_libraries);

		//Set Toolbar
		ActionBar toolbar = getSupportActionBar();
		assert toolbar != null;
		toolbar.setDisplayHomeAsUpEnabled(true);
		toolbar.setTitle("Open Source Libraries");

		librariesView = findViewById(R.id.osl_recycler_view);
		LinearLayoutManager manager = new LinearLayoutManager(this);
		manager.setOrientation(RecyclerView.VERTICAL);
		librariesView.setLayoutManager(manager);

		final ArrayList<OpenSourceLibrary> libraries = new ArrayList<>();

		FirebaseDatabase.getInstance().getReference().child("osl").child("android").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					OpenSourceLibrary library = new OpenSourceLibrary();
					library.setName(String.valueOf(snapshot.child("name").getValue()));
					library.setFrom(String.valueOf(snapshot.child("from").getValue()));
					library.setLink(String.valueOf(snapshot.child("link").getValue()));
					library.setLicence(String.valueOf(snapshot.child("licence").getValue()));
					libraries.add(library);
					Log.e("LIB", library.toString());
				}
				librariesView.setAdapter(new OpenSourceLibrariesAdapter(OpenSourceLibrariesActivity.this, libraries));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}
