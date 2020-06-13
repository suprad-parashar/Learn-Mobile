package com.learn.android.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.android.R;
import com.learn.android.activities.learn.CourseDocumentViewActivity;
import com.learn.android.activities.learn.CourseVideoViewActivity;
import com.learn.android.activities.learn.Type;
import com.learn.android.objects.CourseElement;

import java.util.ArrayList;

public class CourseDetailedElementAdapter extends RecyclerView.Adapter<CourseDetailedElementAdapter.ElementViewHolder> {

	//Declare UI Variables.
	private ArrayList<CourseElement> courseElements;
	private Activity context;

	public CourseDetailedElementAdapter(Activity context, ArrayList<CourseElement> courseElements) {
		this.context = context;
		this.courseElements = courseElements;
	}

	@NonNull
	@Override
	public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course_element, parent, false);
		return new ElementViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
		//Get Element.
		final CourseElement element = courseElements.get(position);

		//Setup Prerequisites.
		LinearLayoutManager manager = new LinearLayoutManager(context);
		manager.setOrientation(RecyclerView.HORIZONTAL);
		holder.prerequisites.setLayoutManager(manager);
		String[] prerequisites = element.getPrerequisites();
		if (prerequisites == null) {
			holder.prerequisites.setVisibility(View.GONE);
			holder.noPrerequisites.setVisibility(View.VISIBLE);
		} else {
			holder.noPrerequisites.setVisibility(View.GONE);
			holder.prerequisites.setVisibility(View.VISIBLE);
			holder.prerequisitesText.setVisibility(View.VISIBLE);
			holder.prerequisites.setAdapter(new PrerequisitesAdapter(context, prerequisites));
		}

		//Populate Data
		holder.name.setText(element.getName());
		holder.rating.setText(String.valueOf(element.getRating()));
		holder.from.setText(element.getFrom());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (element.getType() == Type.VIDEO) {
					//Video
					Intent intent = new Intent(context, CourseVideoViewActivity.class);
					intent.putExtra("rating", element.getRating());
					intent.putExtra("link", element.getLink());
					intent.putExtra("name", element.getName());
					intent.putExtra("from", element.getFrom());
					intent.putExtra("time", 0.0);
					intent.putExtra("index", 0);
					intent.putExtra("isPlaylist", element.isPlaylist());
					if (element.isPlaylist()) {
						intent.putExtra("videoNames", element.getVideoNames());
						intent.putExtra("videoLinks", element.getVideoLinks());
					}
					intent.putExtra("reference", element.getReference().toString());
					context.startActivity(intent);
					context.finish();
				} else if (element.getType() == Type.DOCUMENT) {
					//Document
					Intent intent = new Intent(context, CourseDocumentViewActivity.class);
					intent.putExtra("link", element.getLink());
					intent.putExtra("name", element.getName());
					intent.putExtra("from", element.getFrom());
					intent.putExtra("reference", element.getReference().toString());
					context.startActivity(intent);
					context.finish();
				} else {
					//Courses and Projects
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(element.getLink()));
					context.startActivity(intent);
				}
			}
		});
//		holder.icon.setImageURI(null);
//		holder.icon.setImageURI(Uri.parse(element.getIconUrl()));
		Glide.with(context)
				.load(element.getIconUrl())
				.placeholder(R.drawable.ic_person)
				.into(holder.icon);
	}

	@Override
	public int getItemCount() {
		return courseElements.size();
	}

	public static class ElementViewHolder extends RecyclerView.ViewHolder {

		TextView name, rating, from, noPrerequisites, prerequisitesText;
		RecyclerView prerequisites;
		ImageView icon;

		public ElementViewHolder(@NonNull View itemView) {
			super(itemView);
			name = itemView.findViewById(R.id.name);
			noPrerequisites = itemView.findViewById(R.id.prerequisites_none);
			prerequisites = itemView.findViewById(R.id.prerequisites);
			rating = itemView.findViewById(R.id.rating);
			prerequisitesText = itemView.findViewById(R.id.prerequisites_text);
			from = itemView.findViewById(R.id.from);
			icon = itemView.findViewById(R.id.image);
		}
	}
}