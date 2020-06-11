package com.learn.android.objects;

import com.google.firebase.database.DatabaseReference;
import com.learn.android.activities.learn.Type;

import java.util.ArrayList;

/**
 * Object to hold Course Details.
 */
public class CourseElement {
	private String name;
	private String link;
	private String from;
	private Type type;
	private int[] ratings;
	private String[] prerequisites;
	private DatabaseReference reference;
	private boolean isPlaylist;
	private ArrayList<String> videoNames, videoLinks;

	public boolean isPlaylist() {
		return isPlaylist;
	}

	public void setPlaylist(boolean playlist) {
		isPlaylist = playlist;
	}

	public ArrayList<String> getVideoNames() {
		return videoNames;
	}

	public void setVideoNames(ArrayList<String> videoNames) {
		this.videoNames = videoNames;
	}

	public ArrayList<String> getVideoLinks() {
		return videoLinks;
	}

	public void setVideoLinks(ArrayList<String> videoLinks) {
		this.videoLinks = videoLinks;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
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

	public String[] getPrerequisites() {
		if (prerequisites.length == 1 && prerequisites[0].equals("None"))
			return null;
		return prerequisites;
	}

	public void setPrerequisites(String[] prerequisites) {
		this.prerequisites = prerequisites;
	}
}
