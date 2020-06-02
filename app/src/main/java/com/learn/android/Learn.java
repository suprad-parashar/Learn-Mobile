package com.learn.android;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Learn extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}
}

