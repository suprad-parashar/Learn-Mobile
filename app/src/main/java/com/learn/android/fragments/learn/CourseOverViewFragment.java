package com.learn.android.fragments.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.learn.android.R;
import com.learn.android.activities.learn.CourseViewActivity;

public class CourseOverViewFragment extends Fragment {

	private CardView coursesCard, videosCard, documentsCard, projectsCard;
	private String title;

	public CourseOverViewFragment() {
		//Required Public Default Constructor.
	}

	public CourseOverViewFragment(String title) {
		this.title = title;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_course_overview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		//Initialise UI Variables.
		coursesCard = view.findViewById(R.id.courses);
		videosCard = view.findViewById(R.id.videos);
		documentsCard = view.findViewById(R.id.documents);
		projectsCard = view.findViewById(R.id.projects);

		coursesCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CourseViewActivity.type = 2;
				requireActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title))
						.commit();
			}
		});
		videosCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CourseViewActivity.type = 0;
				requireActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title))
						.commit();
			}
		});
		documentsCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CourseViewActivity.type = 1;
				requireActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title))
						.commit();
			}
		});
		projectsCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CourseViewActivity.type = 3;
				requireActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title))
						.commit();
			}
		});

	}
}
