package com.learn.android.fragments.learn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.activities.AddResourceActivity;
import com.learn.android.activities.learn.CourseViewActivity;
import com.learn.android.activities.learn.Type;
import com.learn.android.adapters.CourseDetailedElementAdapter;
import com.learn.android.objects.CourseElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class CourseDetailedElementFragment extends Fragment {

	//Declare UI Variables.
	private RecyclerView detailsListView;
	ImageView image;
	TextView titleTextView;
	LinearLayout emptyView;
	Button addResourceButton;

	//Declare Data Variables
	private String title, titleReference, domain, branch;

	public CourseDetailedElementFragment() {
		//Required Default Public Constructor.
	}

	public CourseDetailedElementFragment(String title, String domain, String branch) {
		titleReference = title;
		titleReference = titleReference.replace("#", "Sharp");
		titleReference = titleReference.replace(".", "Dot");
		this.title = title;
		this.domain = domain;
		this.branch = branch;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_course_detailed_element, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		//Initialise UI Variables.
		detailsListView = view.findViewById(R.id.course_detailed_list_view);
		titleTextView = view.findViewById(R.id.title);
		image = view.findViewById(R.id.display_image);
		emptyView = view.findViewById(R.id.empty_view);
		addResourceButton = view.findViewById(R.id.add_resource_button);
		final ArrayList<CourseElement> courseElements = new ArrayList<>();

		//Setup Recycler View.
		LinearLayoutManager manager = new LinearLayoutManager(requireContext());
		manager.setOrientation(RecyclerView.VERTICAL);
		detailsListView.setLayoutManager(manager);

		//Set Title.
		titleTextView.setText(title);

		//Initialise Firebase Variables.
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("links").child(titleReference);
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Glide.with(requireContext())
						.load(String.valueOf(dataSnapshot.child("image").getValue()))
						.centerCrop()
						.placeholder(R.drawable.illustration_1_analyse)
						.into(image);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		//Populate Data.
//		CourseViewActivity.type = Type.VIDEO;
		HashMap<String, Type> map = new HashMap<>();
		map.put("Videos", Type.VIDEO);
		map.put("Documents", Type.DOCUMENT);
		map.put("Courses", Type.COURSE);
		map.put("Projects", Type.PROJECT);

		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				for (DataSnapshot child: snapshot.getChildren()) {
					if (!Objects.equals(child.getKey(), "image")) {
						Log.e("MAIN", Objects.requireNonNull(child.getKey()));
						for (DataSnapshot dataSnapshot : child.getChildren()) {
							Log.e("SUB", Objects.requireNonNull(dataSnapshot.getKey()));
							Log.e("TYPE", child.getKey());
							CourseElement element = getCourseElement(dataSnapshot, map.get(child.getKey()));
							courseElements.add(element);
						}
					}
				}
				if (courseElements.size() == 0) {
					emptyView.setVisibility(View.VISIBLE);
					detailsListView.setVisibility(View.GONE);
				} else {
					detailsListView.setAdapter(new CourseDetailedElementAdapter(requireActivity(), courseElements, domain, branch));
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});

		addResourceButton.setOnClickListener(v -> {
			Intent intent = new Intent(requireContext(), AddResourceActivity.class);
			intent.putExtra("domain", domain);
			intent.putExtra("branch", branch);
			intent.putExtra("course", title);
			startActivity(intent);
		});
	}

	/**
	 * Method to create CourseElement from Firebase Database DataSnapshot.
	 *
	 * @param snapshot The Data Snapshot containing the Data.
	 * @param type     The type of Element.
	 * @return The CourseElement Created.
	 */
	private CourseElement getCourseElement(DataSnapshot snapshot, Type type) {
		CourseElement element = new CourseElement();
		String[] prerequisites = new String[(int) snapshot.child("prerequisites").getChildrenCount()];
		element.setName((String) snapshot.child("name").getValue());
		element.setFrom((String) snapshot.child("from").getValue());
		element.setLink((String) snapshot.child("link").getValue());
		element.setIconUrl((String) snapshot.child("icon").getValue());
		element.setType(type);
		if (type == Type.VIDEO) {
			boolean isPlaylist = Boolean.parseBoolean(String.valueOf(snapshot.child("playlist").getValue()));
			element.setPlaylist(isPlaylist);
			if (isPlaylist) {
				ArrayList<String> videoNames = new ArrayList<>(), videoLinks = new ArrayList<>();
				DataSnapshot playlistSnapShot = snapshot.child("list");
				for (int i = 0; i < playlistSnapShot.getChildrenCount(); i++) {
					String name = String.valueOf(playlistSnapShot.child(String.valueOf(i)).child("name").getValue());
					String link = String.valueOf(playlistSnapShot.child(String.valueOf(i)).child("link").getValue());
					videoLinks.add(link);
					videoNames.add(name);
				}
				element.setVideoNames(videoNames);
				element.setVideoLinks(videoLinks);
			}
		}
		for (int i = 0; i < prerequisites.length; i++)
			prerequisites[i] = snapshot.child("prerequisites").child(String.valueOf(i)).getValue(String.class);
		element.setPrerequisites(prerequisites);
		int[] ratings = new int[(int) snapshot.child("rating").getChildrenCount()];
		int i = 0;
		for (DataSnapshot innerSnapshot : snapshot.child("rating").getChildren()) {
			try {
				ratings[i++] = Integer.parseInt(String.valueOf(innerSnapshot.getValue()));
			} catch (NumberFormatException e) {
				ratings[--i] = 4;
			}
		}
		element.setRatings(ratings);
		element.setReference(snapshot.getRef());
		return element;
	}
}
