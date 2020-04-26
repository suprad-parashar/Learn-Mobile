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

import java.util.Objects;

public class ViewProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    EditText userNameEditText, collegeNameEditText;
    Spinner currentlyIn, stream, branch, boardUniversity;
    RadioButton studentRadioButton;
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
        userNameEditText = view.findViewById(R.id.view_profile_name);
        collegeNameEditText = view.findViewById(R.id.view_profile_college);
        currentlyIn = view.findViewById(R.id.view_profile_currently_in);
        stream = view.findViewById(R.id.view_profile_stream);
        branch = view.findViewById(R.id.view_profile_branch);
        boardUniversity = view.findViewById(R.id.view_profile_board_university);
        boardUniversityLabel = view.findViewById(R.id.view_profile_class_board_university_label);
        studentRadioButton = view.findViewById(R.id.student_radio_button);
        userType = view.findViewById(R.id.user_type_radio_group);

        userNameEditText.setFocusable(false);
        userNameEditText.setClickable(false);
        userNameEditText.setFocusableInTouchMode(false);

        currentlyInLayout = view.findViewById(R.id.view_profile_currently_in_layout);
        branchLayout = view.findViewById(R.id.view_profile_branch_layout);
        streamLayout = view.findViewById(R.id.view_profile_stream_layout);
        boardUniversityLayout = view.findViewById(R.id.view_profile_board_university_layout);
        collegeLayout = view.findViewById(R.id.view_profile_college_layout);

        if (userNameEditText == null)
            Log.e("FAIL", "FUCK YOU");

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

        userNameEditText.setText(user.getDisplayName());
        currentlyIn.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.currently_in)));


        currentlyIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                    case 1:
                        stream.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.stream_school)));
                        branch.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.branch_science)));
                        boardUniversity.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.boards)));
                        break;
                    case 2:
                        stream.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.stream_undergraduate)));
                        branch.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.branch_engineering)));
                        boardUniversity.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.universities)));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile_menu, menu);
    }
}
