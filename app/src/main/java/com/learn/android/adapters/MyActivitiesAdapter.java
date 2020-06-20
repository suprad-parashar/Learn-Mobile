package com.learn.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.activities.learn.CourseVideoViewActivity;
import com.learn.android.objects.Activity;

import java.util.ArrayList;

public class MyActivitiesAdapter extends RecyclerView.Adapter<MyActivitiesAdapter.MyActivitiesHolder> {

	//Declare Data Variables
	private Context context;
	private ArrayList<Activity> activities;
	private boolean isList;

	public MyActivitiesAdapter(Context context, ArrayList<Activity> activities, boolean isList) {
		this.context = context;
		this.activities = activities;
		this.isList = isList;
	}

	@NonNull
	@Override
	public MyActivitiesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_activity, parent, false);
		return new MyActivitiesHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MyActivitiesHolder holder, final int position) {
		final Activity activity = activities.get(position);

		//Check if Data presentation is a list or card.
		if (!isList)
			holder.mainLayout.setMaxWidth(750);

		holder.title.setText(getTitle(activity));
		holder.date.setText(activity.getDate());
		String buttonText;
		switch (activity.getType()) {
			case VIDEO:
				buttonText = (activity.isDone()) ? "Watch Again" : "Resume";
				holder.button.setOnClickListener(v -> {
					final Intent intent = new Intent(context, CourseVideoViewActivity.class);
					intent.putExtra("name", activity.getName());
					intent.putExtra("link", activity.getLink());
					intent.putExtra("from", activity.getFrom());
					intent.putExtra("isPlaylist", activity.isPlaylist());
					intent.putExtra("time", activity.getTime());
					intent.putExtra("index", activity.getIndex());
					intent.putExtra("reference", activity.getReference());
					final ArrayList<String> videoNames = new ArrayList<>(), videoLinks = new ArrayList<>();
					if (activity.isPlaylist()) {
						FirebaseDatabase.getInstance()
								.getReferenceFromUrl(activity.getReference())
								.child("list")
								.addListenerForSingleValueEvent(new ValueEventListener() {
									@Override
									public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
										for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
											videoNames.add(String.valueOf(snapshot.child("name").getValue()));
											videoLinks.add(String.valueOf(snapshot.child("link").getValue()));
										}
										intent.putExtra("videoNames", videoNames);
										intent.putExtra("videoLinks", videoLinks);
										context.startActivity(intent);
									}

									@Override
									public void onCancelled(@NonNull DatabaseError databaseError) {

									}
								});
					} else {
						intent.putExtra("videoNames", videoNames);
						intent.putExtra("videoLinks", videoLinks);
						context.startActivity(intent);
					}
				});
				break;
			case DOCUMENT:
				buttonText = "Read";
				holder.button.setOnClickListener(v -> {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(activity.getLink()));
					context.startActivity(intent);
				});
				break;
			case COURSE:
				buttonText = "Go to Course";
				holder.button.setOnClickListener(v -> {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(activity.getLink()));
					context.startActivity(intent);
				});
				break;
			case PROJECT:
				buttonText = "View Project";
				holder.button.setOnClickListener(v -> {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(activity.getLink()));
					context.startActivity(intent);
				});
				break;
			default:
				buttonText = "Go to Link";
				holder.button.setOnClickListener(v -> {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(activity.getLink()));
					context.startActivity(intent);
				});
				break;
		}
		holder.button.setText(buttonText);
	}

	@Override
	public int getItemCount() {
		return activities.size();
	}

	/**
	 * Obtain the title of the Activity.
	 *
	 * @param activity The Activity for which the title is to be generated.
	 * @return Title of the activity.
	 */
	private String getTitle(Activity activity) {
		String title = "";
		switch (activity.getType()) {
			case VIDEO:
				title += (activity.isDone()) ? "Completed " : "Started ";
				title += "watching ";
				title += activity.getName() + " ";
				title += "by ";
				title += activity.getFrom();
				break;
			case DOCUMENT:
				title += "Read " + activity.getName() + " by " + activity.getFrom();
				break;
			case COURSE:
				title += "Started " + activity.getName() + " by " + activity.getFrom();
				break;
			case PROJECT:
				title += "Created " + activity.getName() + " by " + activity.getFrom();
				break;
		}
		return title;
	}

	static class MyActivitiesHolder extends RecyclerView.ViewHolder {
		TextView title, date;
		Button button;
		ConstraintLayout mainLayout;

		MyActivitiesHolder(@NonNull View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.title);
			date = itemView.findViewById(R.id.from);
			button = itemView.findViewById(R.id.button);
			mainLayout = itemView.findViewById(R.id.main_layout);
		}
	}
}
