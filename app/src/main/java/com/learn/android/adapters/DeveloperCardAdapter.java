package com.learn.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.android.R;

public class DeveloperCardAdapter extends RecyclerView.Adapter<DeveloperCardAdapter.DeveloperCardViewHolder> {

	//Declare UI Variables
	private Context context;

	public DeveloperCardAdapter(Context context) {
		this.context = context;
	}

	//Names of Developers
	private String[] names = {
			"Suprad Parashar",
			"Srivalli S B",
			"Sandeep N S"
	};

	//Roles of Developers
	private String[] jobs = {
			"Android Application Developer",
			"Web Developer",
			"Web Developer"
	};

	//GitHub Links of Developers
	private String[] githubs = {
			"https://github.com/suprad-parashar",
			"https://github.com/srivallisb",
			"https://github.com/sandy13521"
	};

	//LinkedIn Profile Links of Developers
	private String[] linkedIns = {
			"https://www.linkedin.com/in/supradparashar/",
			"https://www.linkedin.com/in/srivalli-s-b-a51339188/",
			"https://www.linkedin.com/in/sandeep-n-s/"
	};

	//Instagram Profile Links of Developers
	private String[] instagrams = {
			"https://www.instagram.com/suprad.parashar/",
			"https://www.instagram.com/srivalli_1526/",
			"https://www.instagram.com/sandeep_n_s/"
	};

	@NonNull
	@Override
	public DeveloperCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_developer_card, parent, false);
		return new DeveloperCardViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull DeveloperCardViewHolder holder, final int position) {
		holder.name.setText(names[position]);
		holder.job.setText(jobs[position]);
		holder.github.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(githubs[position]));
				context.startActivity(intent);
			}
		});
		holder.linkedIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(linkedIns[position]));
				context.startActivity(intent);
			}
		});
		holder.instagram.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(instagrams[position]));
				context.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return names.length;
	}

	static class DeveloperCardViewHolder extends RecyclerView.ViewHolder {
		ImageView image, github, linkedIn, instagram;
		TextView name, job;

		DeveloperCardViewHolder(@NonNull View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.developer_image);
			name = itemView.findViewById(R.id.developer_name);
			job = itemView.findViewById(R.id.developer_job);
			github = itemView.findViewById(R.id.github_link_developer);
			linkedIn = itemView.findViewById(R.id.linkedin_link_developer);
			instagram = itemView.findViewById(R.id.instagram_link_developer);
		}
	}
}
