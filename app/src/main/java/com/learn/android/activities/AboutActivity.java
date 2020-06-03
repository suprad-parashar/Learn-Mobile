package com.learn.android.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.android.R;
import com.learn.android.adapters.DeveloperCardAdapter;

public class AboutActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		//Set Toolbar
		ActionBar toolbar = getSupportActionBar();
		assert toolbar != null;
		toolbar.setDisplayHomeAsUpEnabled(true);
		toolbar.setTitle("About");

		//Setup Recycler View for Developer's Card.
		RecyclerView developersView = findViewById(R.id.developers_recycler_view);
		LinearLayoutManager manager = new LinearLayoutManager(this);
		manager.setOrientation(RecyclerView.HORIZONTAL);
		developersView.setLayoutManager(manager);
		developersView.setAdapter(new DeveloperCardAdapter(this));

		//Display Version of Learn.
		TextView version = findViewById(R.id.version);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version.setText(pInfo.versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}
}