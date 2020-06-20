package com.learn.android.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.OpenSourceLibrariesAdapter;
import com.learn.android.objects.OpenSourceLibrary;

import java.util.ArrayList;
import java.util.Objects;

public class OpenSourceLibrariesFragment extends Fragment {

	//Declare UI Variables.
	private RecyclerView librariesView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_open_source_libraries, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//Set Toolbar
		Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Open Source Libraries");

		//Setup Recycler View
		librariesView = view.findViewById(R.id.osl_recycler_view);
		LinearLayoutManager manager = new LinearLayoutManager(requireContext());
		manager.setOrientation(RecyclerView.VERTICAL);
		librariesView.setLayoutManager(manager);

		//Get Libraries from Database.
		final ArrayList<OpenSourceLibrary> libraries = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference().child("osl").child("android").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					OpenSourceLibrary library = new OpenSourceLibrary();
					library.setName(String.valueOf(snapshot.child("name").getValue()));
					library.setFrom(String.valueOf(snapshot.child("from").getValue()));
					library.setLink(String.valueOf(snapshot.child("link").getValue()));
					library.setLicence(String.valueOf(snapshot.child("licence").getValue()));
					libraries.add(library);
				}
				librariesView.setAdapter(new OpenSourceLibrariesAdapter(requireContext(), libraries));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}
