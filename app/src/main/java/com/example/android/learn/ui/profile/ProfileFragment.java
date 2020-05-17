package com.example.android.learn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.learn.HomeActivity;
import com.example.android.learn.LoginActivity;
import com.example.android.learn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    //Initialise Firebase Variables.
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //Declare UI Variables.
    private TextView nameTextView, emailTextView, typeTextView, pointsTextView, statusTextView;
    private ImageButton settings;
    private ListView profileLinks;
    private ProgressBar wait;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Initialise UI Variables.
        String[] PROFILE_LINKS_LIST = getResources().getStringArray(R.array.profile_links);
        wait = view.findViewById(R.id.wait_profile);
        nameTextView = view.findViewById(R.id.profile_user_name);
        emailTextView = view.findViewById(R.id.profile_user_email);
        typeTextView = view.findViewById(R.id.profile_user_type);
        pointsTextView = view.findViewById(R.id.profile_user_bp);
        statusTextView = view.findViewById(R.id.profile_user_status);
        settings = view.findViewById(R.id.settings);
        profileLinks = view.findViewById(R.id.profile_links);

        //Set ProgressBar
        wait.setVisibility(View.VISIBLE);

        //Set User Data.
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());

        //Fetch User Data from Firebase Database.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users").child(user.getUid()).child("data");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type = (String) dataSnapshot.child("type").getValue();
                long points = (long) dataSnapshot.child("points").getValue();
                String status = (String) dataSnapshot.child("status").getValue();
                typeTextView.setText(type);
                pointsTextView.setText(String.valueOf(points));
                statusTextView.setText(status);
                wait.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                wait.setVisibility(View.GONE);
            }
        });

        //Setup Profile Links.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, PROFILE_LINKS_LIST);
        profileLinks.setAdapter(arrayAdapter);
        profileLinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        HomeActivity.navController.navigate(R.id.navigation_view_profile);
                        break;
                    case 4:
                        HomeActivity.navController.navigate(R.id.navigation_change_password);
                        break;
                    case 5:
                        auth.signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        requireActivity().finish();
                        break;
                }
            }
        });
    }
}