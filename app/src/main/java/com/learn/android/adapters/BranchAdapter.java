package com.learn.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.learn.android.R;
import com.learn.android.activities.learn.ViewCoursesDataActivity;

import java.util.ArrayList;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchHolder> {

	//Declare Data Variables
	private Context context;
	private ArrayList<Pair<String, String>> branches;
	private DatabaseReference reference;
	String domain;

	public BranchAdapter(Context context, ArrayList<Pair<String, String>> domains, DatabaseReference reference, String domain) {
		this.context = context;
		this.branches = domains;
		this.reference = reference;
		this.domain = domain;
	}

	@NonNull
	@Override
	public BranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_domain, parent, false);
		return new BranchHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull BranchHolder holder, final int position) {
		holder.branch.setText(branches.get(position).first);
		Glide.with(context)
				.load(branches.get(position).second)
				.placeholder(R.drawable.button_filled)
				.fitCenter()
				.into(holder.image);
		holder.itemView.setOnClickListener(v -> {
			Intent intent = new Intent(context, ViewCoursesDataActivity.class);
			intent.putExtra("reference", reference.toString());
			intent.putExtra("name", branches.get(position).first);
			intent.putExtra("domain", domain);
			intent.putExtra("branch", branches.get(position).first);
			context.startActivity(intent);
		});
	}

	@Override
	public int getItemCount() {
		return branches.size();
	}

	static class BranchHolder extends RecyclerView.ViewHolder {
		TextView branch;
		ImageView image;

		BranchHolder(@NonNull View itemView) {
			super(itemView);
			branch = itemView.findViewById(R.id.domain);
			image = itemView.findViewById(R.id.image);
		}
	}
}
