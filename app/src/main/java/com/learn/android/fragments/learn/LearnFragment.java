package com.learn.android.fragments.learn;

import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.activities.learn.CourseViewActivity;
import com.learn.android.activities.learn.SyllabusViewActivity;
import com.learn.android.objects.TreeViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

public class LearnFragment extends Fragment {

	//Declare UI Variables.
	private LinearLayout learnListView;
	private ProgressBar loading;

	//Initialise Firebase Variables.
	private FirebaseDatabase database = FirebaseDatabase.getInstance();
	private DatabaseReference reference = database.getReference().child("domain");

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_learn, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Initialise UI Variables.
		learnListView = view.findViewById(R.id.list_learn);
		loading = view.findViewById(R.id.wait);

		//Create Main Root Node.
		final TreeNode root = TreeNode.root();

		//Populate Courses.
		TreeNode syllabus = new TreeNode("My Syllabus");
		syllabus.setClickListener(new TreeNode.TreeNodeClickListener() {
			@Override
			public void onClick(TreeNode node, Object value) {
				startActivity(new Intent(requireActivity(), SyllabusViewActivity.class));
			}
		});
		root.addChild(syllabus);
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					TreeNode domain = new TreeNode(snapshot.getKey()).setViewHolder(new TreeViewHolder(requireContext(), learnListView, 1));
					for (DataSnapshot data : snapshot.getChildren()) {
						TreeNode branch = new TreeNode(data.getKey()).setViewHolder(new TreeViewHolder(requireContext(), learnListView, 2));
						for (DataSnapshot child : data.getChildren()) {
							TreeNode type = new TreeNode(child.getKey()).setViewHolder(new TreeViewHolder(requireContext(), learnListView, 3));
							for (int i = 0; i < child.getChildrenCount(); i++) {
								TreeNode value = new TreeNode(child.child(String.valueOf(i)).getValue(String.class)).setViewHolder(new TreeViewHolder(requireContext(), learnListView, 4));
								value.setClickListener(new TreeNode.TreeNodeClickListener() {
									@Override
									public void onClick(TreeNode node, Object value) {
										Intent intent = new Intent(requireActivity(), CourseViewActivity.class);
										intent.putExtra("title", String.valueOf(value));
										startActivity(intent);
									}
								});
								type.addChild(value);
							}
							branch.addChild(type);
						}
						domain.addChild(branch);
					}
					root.addChild(domain);
				}
				AndroidTreeView treeView = new AndroidTreeView(requireContext(), root);
				learnListView.addView(treeView.getView());
				loading.setVisibility(View.GONE);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e("ERROR", databaseError.toString());
			}
		});
	}
}