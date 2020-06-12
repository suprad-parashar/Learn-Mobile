package com.learn.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.android.R;
import com.learn.android.fragments.learn.BranchFragment;

import java.util.ArrayList;

public class DomainAdapter extends RecyclerView.Adapter<DomainAdapter.DomainHolder> {

	//Declare UI Variables
	private Context context;
	private ArrayList<String> domains;
	private FragmentManager fragmentManager;

	public DomainAdapter(Context context, ArrayList<String> domains, FragmentManager fragmentManager) {
		this.context = context;
		this.domains = domains;
		this.fragmentManager = fragmentManager;
	}

	@NonNull
	@Override
	public DomainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_domain, parent, false);
		return new DomainHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull DomainHolder holder, final int position) {
		holder.domain.setText(domains.get(position));
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager
						.beginTransaction()
						.replace(R.id.learn_fragment_view, new BranchFragment(domains.get(position)))
						.commit();
			}
		});
	}

	@Override
	public int getItemCount() {
		return domains.size();
	}

	static class DomainHolder extends RecyclerView.ViewHolder {
		TextView domain;

		DomainHolder(@NonNull View itemView) {
			super(itemView);
			domain = itemView.findViewById(R.id.domain);
		}
	}
}
