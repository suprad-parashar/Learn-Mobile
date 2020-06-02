package com.learn.android.fragments.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import com.learn.android.adapters.CourseDetailedElementListViewAdapter;
import com.learn.android.objects.CourseElement;

import java.util.ArrayList;

public class CourseDetailedElementFragment extends Fragment {

	//Declare UI Variables.
	private ListView detailsListView;
	private String title;

	public CourseDetailedElementFragment() {
		//Required Default Public Constructor.
	}

	CourseDetailedElementFragment(String title) {
		this.title = title;
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
		final ArrayList<CourseElement> courseElements = new ArrayList<>();

		//Initialise Firebase Variables.
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("links").child(title);

		switch (CourseViewActivity.type) {
			case 0:
				//Videos
				reference.child("Videos").addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
							CourseElement element = getCourseElement(snapshot, 0);
							courseElements.add(element);
						}
						detailsListView.setAdapter(new CourseDetailedElementListViewAdapter(requireActivity(), courseElements));
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
				break;
			case 1:
				//Documents
				reference.child("Documents").addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
							CourseElement element = getCourseElement(snapshot, 1);
							courseElements.add(element);
						}
						detailsListView.setAdapter(new CourseDetailedElementListViewAdapter(requireActivity(), courseElements));
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
				break;
			case 2:
				//Courses
				reference.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
							CourseElement element = getCourseElement(snapshot, 2);
							courseElements.add(element);
						}
						detailsListView.setAdapter(new CourseDetailedElementListViewAdapter(requireActivity(), courseElements));
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
				break;
			case 3:
				//Projects
				reference.child("Projects").addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
							CourseElement element = getCourseElement(snapshot, 3);
							courseElements.add(element);
						}
						detailsListView.setAdapter(new CourseDetailedElementListViewAdapter(requireActivity(), courseElements));
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
				break;
		}
	}

	/**
	 * Method to create CourseElement from Firebase Database DataSnapshot.
	 *
	 * @param snapshot The Data Snapshot containing the Data.
	 * @param type     The type of Element.
	 * @return The CourseElement Created.
	 */
	private CourseElement getCourseElement(DataSnapshot snapshot, int type) {
		CourseElement element = new CourseElement();
		String[] prerequisites = new String[(int) snapshot.child("prerequisites").getChildrenCount()];
		element.setName((String) snapshot.child("name").getValue());
		element.setFrom((String) snapshot.child("from").getValue());
		element.setLink((String) snapshot.child("link").getValue());
		element.setType(type);
		for (int i = 0; i < prerequisites.length; i++)
			prerequisites[i] = snapshot.child("prerequisites").child(String.valueOf(i)).getValue(String.class);
		element.setPrerequisites(prerequisites);
		int[] ratings = new int[(int) snapshot.child("rating").getChildrenCount()];
		int i = 0;
		for (DataSnapshot innerSnapshot : snapshot.child("rating").getChildren()) {
			ratings[i++] = Integer.parseInt(String.valueOf(innerSnapshot.getValue()));
		}
		element.setRatings(ratings);
		element.setReference(snapshot.getRef());
		return element;
	}
}
