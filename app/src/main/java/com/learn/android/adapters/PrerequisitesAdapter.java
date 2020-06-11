package com.learn.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.android.R;

public class PrerequisitesAdapter extends RecyclerView.Adapter<PrerequisitesAdapter.PrerequisitesHolder> {

	//Declare UI Variables
	private Context context;
	private String[] prerequisites;

	public PrerequisitesAdapter(Context context, String[] prerequisites) {
		this.context = context;
		this.prerequisites = prerequisites;
	}

	@NonNull
	@Override
	public PrerequisitesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prerequisites_layout, parent, false);
		return new PrerequisitesHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull PrerequisitesHolder holder, final int position) {
		holder.prerequisite.setText(prerequisites[position]);
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
