package com.learn.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.learn.android.R;
import com.learn.android.activities.learn.ViewCoursesDataActivity;

import java.util.ArrayList;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchHolder> {

	//Declare UI Variables
	private Context context;
	private ArrayList<String> branches;
	private DatabaseReference reference;
	String domain;

	public BranchAdapter(Context context, ArrayList<String> domains, DatabaseReference reference, String domain) {
		this.context = context;
		this.branches = domains;
		this.reference = reference;
		this.domain = domain;
	}

	@NonNull
	@Override
	public BranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_branch, parent, false);
		return new BranchHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull BranchHolder holder, final int position) {
		holder.branch.setText(branches.get(position));
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ViewCoursesDataActivity.class);
				intent.putExtra("reference", reference.toString());
				intent.putExtra("name", branches.get(position));
				intent.putExtra("domain", domain);
				intent.putExtra("branch", branches.get(position));
				context.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return branches.size();
	}

	static class BranchHolder extends RecyclerView.ViewHolder {
		TextView branch;

		BranchHolder(@NonNull View itemView) {
			super(itemView);
			branch = itemView.findViewById(R.id.branch);
		}
	}
}
