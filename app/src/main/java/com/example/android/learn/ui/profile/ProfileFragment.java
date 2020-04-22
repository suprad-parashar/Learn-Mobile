package com.example.android.learn.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.android.learn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    private TextView nameTextView, emailTextView, typeTextView, pointsTextView, statusTextView;
    private ImageView dpImageView;

    ProgressBar wait;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        wait = view.findViewById(R.id.wait_profile);
        nameTextView = view.findViewById(R.id.profile_user_name);
        emailTextView = view.findViewById(R.id.profile_user_email);
        typeTextView = view.findViewById(R.id.profile_user_type);
        pointsTextView = view.findViewById(R.id.profile_user_bp);
        statusTextView = view.findViewById(R.id.profile_user_status);

        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());

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
                Toast.makeText(getContext(), "Firebase Profile Sucks", Toast.LENGTH_LONG).show();
                wait.setVisibility(View.GONE);
            }
        });
    }
}