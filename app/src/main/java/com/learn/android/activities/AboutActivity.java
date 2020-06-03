package com.learn.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.learn.android.R;
import com.learn.android.adapters.DeveloperCardAdapter;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

	private RecyclerView developersView;
	private TextView version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		//Set Toolbar
		ActionBar toolbar = getSupportActionBar();
		assert toolbar != null;
		toolbar.setDisplayHomeAsUpEnabled(true);
		toolbar.setTitle("About");

		developersView = findViewById(R.id.developers_recycler_view);
		LinearLayoutManager manager = new LinearLayoutManager(this);
		manager.setOrientation(RecyclerView.HORIZONTAL);
		developersView.setLayoutManager(manager);
		developersView.setAdapter(new DeveloperCardAdapter(this));

		version = findViewById(R.id.version);
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