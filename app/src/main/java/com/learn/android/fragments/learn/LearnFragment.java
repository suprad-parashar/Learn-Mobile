package com.learn.android.fragments.learn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.activities.learn.CourseViewActivity;
import com.learn.android.activities.learn.SyllabusViewActivity;
import com.learn.android.objects.TreeViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.Objects;

public class LearnFragment extends Fragment {

	//Declare UI Variables.
	private LinearLayout learnListView;
	private ProgressBar loading;
	private EditText searchBar;
	private ListView searchListView;
	private ImageView backButton;

	//Data Variable
	private final ArrayList<String> searchTopics = new ArrayList<>();

	//Initialise Firebase Variables.
	private FirebaseDatabase database = FirebaseDatabase.getInstance();
	private DatabaseReference reference = database.getReference().child("domain");

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_learn, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Initialise UI Variables.
		learnListView = view.findViewById(R.id.tree_view_layout);
		loading = view.findViewById(R.id.wait);
		searchBar = view.findViewById(R.id.search_bar);
		searchListView = view.findViewById(R.id.search_results_list_view);
		backButton = view.findViewById(R.id.back_button);

		searchListView.setVisibility(View.GONE);
		backButton.setVisibility(View.GONE);
		learnListView.setVisibility(View.VISIBLE);

		//Create Main Root Node.
		final TreeNode root = TreeNode.root();

		searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(requireActivity(), CourseViewActivity.class);
				intent.putExtra("title", searchTopics.get(position));
				startActivity(intent);
			}
		});

		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backButton.setVisibility(View.GONE);
				searchListView.setVisibility(View.GONE);
				learnListView.setVisibility(View.VISIBLE);
			}
		});

		//Populate Courses.
		TreeNode syllabus = new TreeNode("My Syllabus").setViewHolder(new TreeViewHolder(requireContext(), learnListView, 1));
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

		//Add Search Functionality
		searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {

					//Hide Keyboard
					View keyBoardView = requireActivity().getCurrentFocus();
					if (keyBoardView != null) {
						InputMethodManager methodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						assert methodManager != null;
						methodManager.hideSoftInputFromWindow(keyBoardView.getWindowToken(), 0);
					}

					final String searchQuery = searchBar.getText().toString().trim().toLowerCase();
					if (!searchQuery.equals("")) {
						backButton.setVisibility(View.VISIBLE);
						searchListView.setVisibility(View.VISIBLE);
						learnListView.setVisibility(View.GONE);
						loading.setVisibility(View.VISIBLE);
						searchTopics.clear();

						FirebaseDatabase.getInstance().getReference().child("links").addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
								for (DataSnapshot snapshot : dataSnapshot.getChildren())
									if (Objects.requireNonNull(snapshot.getKey()).toLowerCase().contains(searchQuery))
										searchTopics.add(snapshot.getKey());
								searchListView.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, searchTopics));
								loading.setVisibility(View.GONE);
							}

							@Override
							public void onCancelled(@NonNull DatabaseError databaseError) {

							}
						});
					}
				}
				return true;
			}
		});
	}
}