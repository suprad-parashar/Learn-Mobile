package com.learn.android.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.adapters.FAQAdapter;
import com.learn.android.objects.FAQ;

import java.util.ArrayList;
import java.util.Objects;

public class FAQActivity extends AppCompatActivity {

	ExpandableListView faqListView;
	private int lastPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq);

		//Setup Toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		//Setup ListView
		faqListView = findViewById(R.id.faq_list);
		faqListView.setOnGroupExpandListener(groupPosition -> {
			if (lastPosition != -1 && groupPosition != lastPosition) {
				faqListView.collapseGroup(lastPosition);
			}
			lastPosition = groupPosition;
		});
		ArrayList<FAQ> faqs = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference().child("faq").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					FAQ faq = dataSnapshot.getValue(FAQ.class);
					faqs.add(faq);
				}
				faqListView.setAdapter(new FAQAdapter(FAQActivity.this, faqs));
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}
}