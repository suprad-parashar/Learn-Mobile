package com.learn.android.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learn.android.R;
import com.learn.android.activities.learn.CourseDocumentViewActivity;
import com.learn.android.activities.learn.CourseVideoViewActivity;
import com.learn.android.objects.CourseElement;

import java.util.ArrayList;

public class CourseDetailedElementListViewAdapter extends BaseAdapter {

	//Declare UI Variables.
	private ArrayList<CourseElement> courseElements;
	private Activity context;

	public CourseDetailedElementListViewAdapter(Activity context, ArrayList<CourseElement> courseElements) {
		this.context = context;
		this.courseElements = courseElements;
	}

	@Override
	public int getCount() {
		return courseElements.size();
	}

	@Override
	public Object getItem(int position) {
		return courseElements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(context).inflate(R.layout.course_element_layout, parent, false);
		final CourseElement element = courseElements.get(position);

		//Initialise UI Variables
		TextView nameTextView = convertView.findViewById(R.id.name);
		TextView prerequisitesTextView = convertView.findViewById(R.id.prerequisites);
		TextView ratingTextView = convertView.findViewById(R.id.rating);
		TextView fromTextView = convertView.findViewById(R.id.from);

		//Populate Data
		nameTextView.setText(element.getName());
		prerequisitesTextView.setText(element.getPrerequisites());
		ratingTextView.setText(String.valueOf(element.getRating()));
		fromTextView.setText(element.getFrom());
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (element.getType() == 0) {
					//Video
					Intent intent = new Intent(context, CourseVideoViewActivity.class);
					intent.putExtra("rating", element.getRating());
					intent.putExtra("link", element.getLink());
					intent.putExtra("name", element.getName());
					intent.putExtra("from", element.getFrom());
					intent.putExtra("reference", element.getReference().toString());
					context.startActivity(intent);
					context.finish();
				} else if (element.getType() == 1) {
					//Document
					Intent intent = new Intent(context, CourseDocumentViewActivity.class);
					intent.putExtra("link", element.getLink());
					intent.putExtra("name", element.getName());
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
		return convertView;
	}
}