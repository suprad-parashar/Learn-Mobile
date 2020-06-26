package com.learn.android.fragments.learn;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.learn.android.Learn;
import com.learn.android.R;
import com.learn.android.activities.learn.CourseViewActivity;
import com.learn.android.activities.learn.Type;

public class CourseOverViewFragment extends Fragment {

	//Declare Data Variables.
	private String title, domain, branch;

	public CourseOverViewFragment() {
		//Required Public Default Constructor.
	}

	public CourseOverViewFragment(String title, String domain, String branch) {
		this.title = title;
		this.domain = domain;
		this.branch = branch;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_course_overview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		//Initialise UI Variables.
		CardView coursesCard = view.findViewById(R.id.courses);
		CardView videosCard = view.findViewById(R.id.videos);
		CardView documentsCard = view.findViewById(R.id.documents);
		CardView projectsCard = view.findViewById(R.id.projects);

		//Courses Card
		coursesCard.setOnClickListener(v -> {
			CourseViewActivity.type = Type.COURSE;
			requireActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack(CourseOverViewFragment.class.getName())
					.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title, domain, branch))
					.commit();
		});

		//Videos Card
		videosCard.setOnClickListener(v -> {
			CourseViewActivity.type = Type.VIDEO;
			requireActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack(CourseOverViewFragment.class.getName())
					.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title, domain, branch))
					.commit();
		});

		//Documents Card
		documentsCard.setOnClickListener(v -> {
			CourseViewActivity.type = Type.DOCUMENT;
			requireActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack(CourseOverViewFragment.class.getName())
					.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title, domain, branch))
					.commit();
		});

		//Projects Card
		projectsCard.setOnClickListener(v -> {
			CourseViewActivity.type = Type.PROJECT;
			requireActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack(CourseOverViewFragment.class.getName())
					.replace(R.id.course_view_fragment, new CourseDetailedElementFragment(title, domain, branch))
					.commit();
		});
	}
}
