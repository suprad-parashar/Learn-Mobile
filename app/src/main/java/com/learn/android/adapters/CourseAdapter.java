package com.learn.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.android.R;
import com.learn.android.activities.learn.CourseViewActivity;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseHolder> {

	//Declare UI Variables
	private Context context;
	private ArrayList<String> courses;

	public CourseAdapter(Context context, ArrayList<String> courses) {
		this.context = context;
		this.courses = courses;
	}

	@NonNull
	@Override
	public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_branch, parent, false);
		return new CourseHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CourseHolder holder, final int position) {
		holder.branch.setText(courses.get(position));
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CourseViewActivity.class);
				intent.putExtra("title", courses.get(position));
				context.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return courses.size();
	}

	static class CourseHolder extends RecyclerView.ViewHolder {
		TextView branch;

		CourseHolder(@NonNull View itemView) {
			super(itemView);
			branch = itemView.findViewById(R.id.branch);
		}
	}
}
