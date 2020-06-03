package com.learn.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.android.R;
import com.learn.android.objects.OpenSourceLibrary;

import java.util.ArrayList;

public class OpenSourceLibrariesAdapter extends RecyclerView.Adapter<OpenSourceLibrariesAdapter.OpenSourceLibrariesViewHolder> {

	private Context context;
	private ArrayList<OpenSourceLibrary> libraries;

	public OpenSourceLibrariesAdapter(Context context, ArrayList<OpenSourceLibrary> libraries) {
		this.context = context;
		this.libraries = libraries;
	}

	@NonNull
	@Override
	public OpenSourceLibrariesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.osl_card, parent, false);
		return new OpenSourceLibrariesViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull OpenSourceLibrariesViewHolder holder, final int position) {
		holder.name.setText(libraries.get(position).getName());
		holder.from.setText(libraries.get(position).getFrom());

		SpannableString content = new SpannableString(libraries.get(position).getLink());
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		holder.link.setText(content);

		holder.link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(libraries.get(position).getLink()));
				context.startActivity(intent);
			}
		});

		holder.licence.setText(libraries.get(position).getLicence());
	}

	@Override
	public int getItemCount() {
		return libraries.size();
	}

	static class OpenSourceLibrariesViewHolder extends RecyclerView.ViewHolder {
		TextView name, from, link, licence;

		OpenSourceLibrariesViewHolder(@NonNull View itemView) {
			super(itemView);
			name = itemView.findViewById(R.id.name);
			from = itemView.findViewById(R.id.from);
			licence = itemView.findViewById(R.id.licence);
			link = itemView.findViewById(R.id.link);
		}
	}
}
