package com.learn.android;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Learn extends Application {
	public static final String CHANGE_MESSAGE = "1. Redesigned Profile Fragment" +
			"\n2. Redesigned Edit Profile Fragment" +
			"\n3. Redesigned Settings Activity" +
			"\n4. Added ChangeLog Feature in About Section";

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}
}