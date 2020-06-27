package com.learn.android.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.android.R;
import com.learn.android.fragments.learn.BranchFragment;
import com.learn.android.fragments.learn.DomainFragment;

import java.util.ArrayList;

public class DomainAdapter extends RecyclerView.Adapter<DomainAdapter.DomainHolder> {

	//Declare Data Variables
	private ArrayList<Pair<String, String>> domains;
	private Context context;
	private FragmentManager fragmentManager;

	public DomainAdapter(Context context, ArrayList<Pair<String, String>> domains, FragmentManager fragmentManager) {
		this.domains = domains;
		this.fragmentManager = fragmentManager;
		this.context = context;
	}

	@NonNull
	@Override
	public DomainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_domain, parent, false);
		return new DomainHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull DomainHolder holder, final int position) {
		holder.domain.setText(domains.get(position).first);
		Glide.with(context)
				.load(domains.get(position).second)
				.placeholder(R.drawable.logo_square)
				.fitCenter()
				.into(holder.image);
		holder.itemView.setOnClickListener(v -> fragmentManager
				.beginTransaction()
				.addToBackStack(DomainFragment.class.getName())
				.replace(R.id.learn_fragment_view, new BranchFragment(domains.get(position).first))
				.commit());
	}

	@Override
	public int getItemCount() {
		return domains.size();
	}

	static class DomainHolder extends RecyclerView.ViewHolder {
		TextView domain;
		ImageView image;

		DomainHolder(@NonNull View itemView) {
			super(itemView);
			domain = itemView.findViewById(R.id.domain);
			image = itemView.findViewById(R.id.image);
		}
	}
}
