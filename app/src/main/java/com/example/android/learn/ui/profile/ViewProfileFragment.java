package com.example.android.learn.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.android.learn.HomeActivity;
import com.example.android.learn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    TextView userName, collegeName;
    TextView currentlyIn, stream, branch, boardUniversity;
    TextView type, semester;
    TextView boardUniversityLabel;

    RadioGroup userType;

    LinearLayout currentlyInLayout, streamLayout, branchLayout, boardUniversityLayout, collegeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final ProgressBar wait = view.findViewById(R.id.wait_view_profile);
        userName = view.findViewById(R.id.user_name_view_profile);
        collegeName = view.findViewById(R.id.college_view_profile);
        currentlyIn = view.findViewById(R.id.user_currently_in_view_profile);
        stream = view.findViewById(R.id.stream_view_profile);
        branch = view.findViewById(R.id.branch_view_profile);
        boardUniversity = view.findViewById(R.id.university_board_view_profile);
        type = view.findViewById(R.id.user_type_view_profile);
        semester = view.findViewById(R.id.semester_view_profile);

        wait.setVisibility(View.VISIBLE);

        userName.setText(user.getDisplayName());
        final DatabaseReference reference = database.getReference().child("users").child(user.getUid()).child("data");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collegeName.setText(getDataFromFirebase(dataSnapshot, "institution"));
                currentlyIn.setText(getDataFromFirebase(dataSnapshot, "currently_in"));
                stream.setText(getDataFromFirebase(dataSnapshot, "stream"));
                branch.setText(getDataFromFirebase(dataSnapshot, "branch"));
                boardUniversity.setText(getDataFromFirebase(dataSnapshot, "board_university"));
                type.setText(getDataFromFirebase(dataSnapshot, "type"));
                semester.setText(getDataFromFirebase(dataSnapshot, "semester"));
                wait.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "An error occured while fetching Data", Toast.LENGTH_LONG).show();
                wait.setVisibility(View.GONE);
            }
        });
    }

    private String getDataFromFirebase(DataSnapshot dataSnapshot, String key) {
        return dataSnapshot.child(key).exists() ? (String) dataSnapshot.child(key).getValue() : getString(R.string.unknown);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile_menu:
                HomeActivity.navController.navigate(R.id.navigation_edit_profile);
                break;
            case android.R.id.home:
                HomeActivity.navController.navigate(R.id.navigation_profile);
                break;
        }
        return true;
    }
}
