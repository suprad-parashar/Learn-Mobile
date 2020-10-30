package com.learn.android.fragments.learn;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.DomainAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class DomainFragment extends Fragment {

	//Declare UI Variables.
	RecyclerView domainView;
	ProgressBar loading;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_learn_domain, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Initialise UI Variables.
		domainView = view.findViewById(R.id.domain_view);
		loading = view.findViewById(R.id.wait);
		loading.setVisibility(View.VISIBLE);

		//Setup Recycler View.
		GridLayoutManager manager;
		if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			manager = new GridLayoutManager(requireContext(), 2);
		} else {
			manager = new GridLayoutManager(requireContext(), 3);
		}
		manager.setOrientation(RecyclerView.VERTICAL);
		domainView.setLayoutManager(manager);

		//Set Data
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("domain");
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<Pair<String, String>> domains = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					if (!Objects.equals(snapshot.getKey(), "image"))
						domains.add(new Pair<>(snapshot.getKey(), String.valueOf(snapshot.child("image").getValue())));
				}
				Collections.sort(domains, Comparator.comparing(p -> p.first));
				domainView.setAdapter(new DomainAdapter(requireContext(), domains, getParentFragmentManager()));
				loading.setVisibility(View.GONE);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e("Database Error", databaseError.toString());
				loading.setVisibility(View.GONE);
			}
		});
	}
}
