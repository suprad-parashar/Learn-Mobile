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

public class PrerequisitesAdapter extends RecyclerView.Adapter<PrerequisitesAdapter.PrerequisitesHolder> {

	//Declare Data Variables
	private Context context;
	private String[] prerequisites;
	private String domain, branch;

	public PrerequisitesAdapter(Context context, String[] prerequisites, String domain, String branch) {
		this.context = context;
		this.prerequisites = prerequisites;
		this.domain = domain;
		this.branch = branch;
	}

	@NonNull
	@Override
	public PrerequisitesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_prerequisites, parent, false);
		return new PrerequisitesHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull PrerequisitesHolder holder, final int position) {
		holder.prerequisite.setText(prerequisites[position]);
		holder.itemView.setOnClickListener(v -> {
			Intent intent = new Intent(context, CourseViewActivity.class);
			intent.putExtra("title", prerequisites[position]);
			intent.putExtra("domain", domain);
			intent.putExtra("branch", branch);
			context.startActivity(intent);
		});
	}

	@Override
	public int getItemCount() {
		return prerequisites.length;
	}

	static class PrerequisitesHolder extends RecyclerView.ViewHolder {
		TextView prerequisite;

		PrerequisitesHolder(@NonNull View itemView) {
			super(itemView);
			prerequisite = itemView.findViewById(R.id.prerequisite);
		}
	}
}
