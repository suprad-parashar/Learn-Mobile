package com.example.android.learn.ui.profile;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.learn.HomeActivity;
import com.example.android.learn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

/**
 * This class is responsible for editing the profile of the User.
 */
public class EditProfileFragment extends Fragment {

    //Initialise Firebase Variables
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference().child("users").child(Objects.requireNonNull(user).getUid()).child("data");

    //Declare UI Variables.
    private EditText userNameEditText, collegeNameEditText;
    private Spinner currentlyIn, stream, branch, boardUniversity, semester;
    private RadioButton studentRadioButton, mentorRadioButton;
    private TextView boardUniversityLabel;
    private RadioGroup userType;
    private LinearLayout currentlyInLayout, streamLayout, branchLayout, boardUniversityLayout, collegeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Initialise UI Variables.
        final ProgressBar wait = view.findViewById(R.id.wait_edit_profile);
        userNameEditText = view.findViewById(R.id.view_profile_name);
        collegeNameEditText = view.findViewById(R.id.view_profile_college);
        currentlyIn = view.findViewById(R.id.view_profile_currently_in);
        stream = view.findViewById(R.id.view_profile_stream);
        branch = view.findViewById(R.id.view_profile_branch);
        boardUniversity = view.findViewById(R.id.view_profile_board_university);
        boardUniversityLabel = view.findViewById(R.id.view_profile_class_board_university_label);
        studentRadioButton = view.findViewById(R.id.student_radio_button);
        mentorRadioButton = view.findViewById(R.id.mentor_radio_button);
        userType = view.findViewById(R.id.user_type_radio_group);
        semester = view.findViewById(R.id.view_profile_semester);

        //Set Progressbar.
        wait.setVisibility(View.VISIBLE);

        //Initialise Maps to get and set Spinners.
        final HashMap<String, Integer> currentlyInList = new HashMap<>();
        HashMap<String, Integer> streamSchoolList = new HashMap<>();
        HashMap<String, Integer> streamUndergraduateList = new HashMap<>();
        HashMap<String, Integer> branchScienceList = new HashMap<>();
        final HashMap<String, Integer> branchEngineeringList = new HashMap<>();
        HashMap<String, Integer> boardsList = new HashMap<>();
        HashMap<String, Integer> universitiesList = new HashMap<>();

        int i = 0;
        for (String s : getResources().getStringArray(R.array.currently_in))
            currentlyInList.put(s, i++);
        i = 0;
        for (String s : getResources().getStringArray(R.array.stream_school))
            streamSchoolList.put(s, i++);
        i = 0;
        for (String s : getResources().getStringArray(R.array.stream_undergraduate))
            streamUndergraduateList.put(s, i++);
        i = 0;
        for (String s : getResources().getStringArray(R.array.branch_science))
            branchScienceList.put(s, i++);
        i = 0;
        for (String s : getResources().getStringArray(R.array.branch_engineering))
            branchEngineeringList.put(s, i++);
        i = 0;
        for (String s : getResources().getStringArray(R.array.boards))
            boardsList.put(s, i++);
        i = 0;
        for (String s : getResources().getStringArray(R.array.universities))
            universitiesList.put(s, i++);

        //Set the user data to the fields.
        userNameEditText.setText(user.getDisplayName());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.child("type").getValue(), "Student")) {
                    if (Objects.equals(dataSnapshot.child("stream").getValue(), "Engineering")) {
                        branch.setSelection(dataSnapshot.child("branch").exists() ? branchEngineeringList.get(dataSnapshot.child("branch").getValue()) : 0);
                        semester.setSelection(dataSnapshot.child("semester").exists() ? Integer.parseInt(((String) dataSnapshot.child("semester").getValue()).split(" ")[1]) - 1 : 0);
                    }
                    collegeNameEditText.setText(dataSnapshot.child("institution").exists() ? (String) dataSnapshot.child("institution").getValue() : "");
                }
                wait.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                wait.setVisibility(View.GONE);
            }
        });

        studentRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentlyInLayout.setVisibility(View.VISIBLE);
                    branchLayout.setVisibility(View.VISIBLE);
                    streamLayout.setVisibility(View.VISIBLE);
                    boardUniversityLayout.setVisibility(View.VISIBLE);
                    collegeLayout.setVisibility(View.VISIBLE);
                } else {
                    currentlyInLayout.setVisibility(View.GONE);
                    branchLayout.setVisibility(View.GONE);
                    streamLayout.setVisibility(View.GONE);
                    boardUniversityLayout.setVisibility(View.GONE);
                    collegeLayout.setVisibility(View.GONE);
                }
            }
        });

        currentlyIn.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.currently_in)));
        currentlyIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
//                    case 0:
//                    case 1:
//                        stream.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.stream_school)));
//                        branch.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.branch_science)));
//                        boardUniversity.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.boards)));
//                        break;
                    case 0:
                        stream.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.stream_undergraduate)));
                        branch.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.branch_engineering)));
                        boardUniversityLabel.setText(R.string.university);
                        boardUniversity.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.universities)));
                        semester.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getSemestersArray(8)));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String[] getSemestersArray(int numberOfSemesters) {
        String[] sems = new String[numberOfSemesters];
        for (int i = 1; i <= numberOfSemesters; i++)
            sems[i - 1] = "Semester " + i;
        return sems;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                HomeActivity.navController.navigate(R.id.navigation_view_profile);
                break;
            case R.id.save_profile_menu:
                String name;
                if (!(name = userNameEditText.getText().toString()).equals(user.getDisplayName()) && !name.equals("")) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    user.updateProfile(profileUpdates);
                }
                reference.child("institution").setValue(collegeNameEditText.getText().toString());
                reference.child("type").setValue(studentRadioButton.isChecked() ? "Student" : "Mentor");
                reference.child("currently_in").setValue(currentlyIn.getSelectedItem().toString());
                reference.child("stream").setValue(stream.getSelectedItem().toString());
                reference.child("branch").setValue(branch.getSelectedItem().toString());
                reference.child("board_university").setValue(boardUniversity.getSelectedItem().toString());
                reference.child("semester").setValue(semester.getSelectedItem().toString());
                HomeActivity.navController.navigate(R.id.navigation_view_profile);
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save_profile_menu, menu);
    }
}
