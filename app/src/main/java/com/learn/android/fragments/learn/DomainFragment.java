package com.learn.android.fragments.learn;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

public class DomainFragment extends Fragment {

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
		domainView = view.findViewById(R.id.domain_view);
		loading = view.findViewById(R.id.wait);
		loading.setVisibility(View.VISIBLE);

		GridLayoutManager manager = new GridLayoutManager(requireContext(), 2);
		manager.setOrientation(RecyclerView.VERTICAL);
		domainView.setLayoutManager(manager);

		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("domain");
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<String> domains = new ArrayList<>();
				domains.add("My Syllabus");
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					Log.e("DOMAIN", Objects.requireNonNull(snapshot.getKey()));
					domains.add(snapshot.getKey());
				}
				domainView.setAdapter(new DomainAdapter(requireContext(), domains, getParentFragmentManager()));
				loading.setVisibility(View.GONE);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e("ERROR", databaseError.toString());
				loading.setVisibility(View.GONE);
			}
		});
	}
}