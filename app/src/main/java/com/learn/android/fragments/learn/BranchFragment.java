package com.learn.android.fragments.learn;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.BranchAdapter;
import com.learn.android.adapters.DomainAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class BranchFragment extends Fragment {

	RecyclerView branchView;
	ProgressBar loading;
	String domain;

	public BranchFragment(String domain) {
		this.domain = domain;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_learn_branch, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		branchView = view.findViewById(R.id.branch_view);
		loading = view.findViewById(R.id.wait);
		loading.setVisibility(View.VISIBLE);

		LinearLayoutManager manager = new LinearLayoutManager(requireContext());
		manager.setOrientation(RecyclerView.VERTICAL);
		branchView.setLayoutManager(manager);

		final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("domain").child(domain);
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ArrayList<String> branches = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					branches.add(snapshot.getKey());
				}
				branchView.setAdapter(new BranchAdapter(requireContext(), branches, reference));
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
