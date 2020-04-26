package com.example.android.learn.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.learn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActualViewProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    TextView userName, collegeName;
    TextView currentlyIn, stream, branch, boardUniversity;
    TextView type;
    TextView boardUniversityLabel;

    RadioGroup userType;
    
    LinearLayout currentlyInLayout, streamLayout, branchLayout, boardUniversityLayout, collegeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_actual_view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userName = view.findViewById(R.id.user_name_view_profile);
        collegeName = view.findViewById(R.id.college_view_profile);
        currentlyIn = view.findViewById(R.id.user_currently_in_view_profile);
        stream = view.findViewById(R.id.stream_view_profile);
        branch = view.findViewById(R.id.branch_view_profile);
        boardUniversity = view.findViewById(R.id.university_board_view_profile);
        type = view.findViewById(R.id.user_type_view_profile);

        userName.setText(user.getDisplayName());

        DatabaseReference reference = database.getReference().child("users").child(user.getUid()).child("data");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile_menu, menu);
    }
}
