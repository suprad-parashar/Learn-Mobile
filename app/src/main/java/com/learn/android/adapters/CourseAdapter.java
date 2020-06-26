package com.learn.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learn.android.R;
import com.learn.android.activities.learn.CourseViewActivity;

import java.util.ArrayList;

public class CourseAdapter extends BaseAdapter {

	//Declare Data Variables
	private Context context;
	private ArrayList<String> courses;
	String domain, branch;

	public CourseAdapter(Context context, ArrayList<String> courses, String domain, String branch) {
		this.context = context;
		this.courses = courses;
		this.domain = domain;
		this.branch = branch;
	}

	@Override
	public int getCount() {
		return courses.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_course, parent, false);
		TextView course = convertView.findViewById(R.id.course);
		course.setText(courses.get(position));
		convertView.setOnClickListener(v -> {
			Intent intent = new Intent(context, CourseViewActivity.class);
			intent.putExtra("title", courses.get(position));
			intent.putExtra("domain", domain);
			intent.putExtra("branch", branch);
			context.startActivity(intent);
		});
		return convertView;
	}
}
