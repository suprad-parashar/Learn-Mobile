package com.learn.android.objects;

import android.os.Build;

import com.google.firebase.database.DatabaseReference;

public class CourseElement {
	private String name;
	private String link;
	private String from;
	private int type;
	private int[] ratings;
	private String[] prerequisites;
	private DatabaseReference reference;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public double getRating() {
		double rating = 0;
		for (int r : ratings)
			rating += r;
		return rating / ratings.length;
	}

	public void setRatings(int[] ratings) {
		this.ratings = ratings;
	}

	public DatabaseReference getReference() {
		return reference;
	}

	public void setReference(DatabaseReference reference) {
		this.reference = reference;
	}

	public String getPrerequisites() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			return String.join(", ", prerequisites);
		else
			return prerequisites[0];
	}

	public void setPrerequisites(String[] prerequisites) {
		this.prerequisites = prerequisites;
	}
}
