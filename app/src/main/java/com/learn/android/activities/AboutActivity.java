package com.learn.android.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.adapters.DeveloperCardAdapter;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		//Set Toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
			Log.e("PackageManager Error", e.toString());
			Toast.makeText(AboutActivity.this, "Error occurred while fetching the version", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.change_log_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		} else if (item.getItemId() == R.id.change_log_menu) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("What's New")
					.setMessage(Learn.CHANGE_MESSAGE)
					.setCancelable(true)
					.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
			builder.create().show();
		} else if (item.getItemId() == R.id.terms_and_conditions_menu) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/learn-634be.appspot.com/o/Privacy%20Policies%20and%20T%26Cs%2FTC.html?alt=media&token=0ca0b178-1dca-4c2d-9440-07c5da1b96ae"));
			startActivity(intent);
		} else if (item.getItemId() == R.id.privacy_policy_menu) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/learn-634be.appspot.com/o/Privacy%20Policies%20and%20T%26Cs%2FPP.html?alt=media&token=a9841668-2719-4cd6-a740-be0e6755b0a9"));
			startActivity(intent);
		}
		return true;
	}
}
