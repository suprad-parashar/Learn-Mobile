package com.learn.android.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.learn.android.R;
import com.learn.android.activities.learn.CourseVideoViewActivity;
import com.learn.android.activities.learn.Type;
import com.learn.android.objects.CourseElement;
import com.learn.android.utils.NotificationReceiver;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CourseDetailedElementAdapter extends RecyclerView.Adapter<CourseDetailedElementAdapter.ElementViewHolder> {

	//Declare UI Variables.
	private ArrayList<CourseElement> courseElements;
	private Activity context;
	private String domain, branch;

	public CourseDetailedElementAdapter(Activity context, ArrayList<CourseElement> courseElements, String domain, String branch) {
		this.context = context;
		this.courseElements = courseElements;
		this.domain = domain;
		this.branch = branch;
	}

	@NonNull
	@Override
	public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course_element, parent, false);
		return new ElementViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
		//Get Element.
		final CourseElement element = courseElements.get(position);

		//Setup Prerequisites.
		LinearLayoutManager manager = new LinearLayoutManager(context);
		manager.setOrientation(RecyclerView.HORIZONTAL);
		holder.prerequisites.setLayoutManager(manager);
		String[] prerequisites = element.getPrerequisites();
		if (prerequisites == null) {
			holder.prerequisites.setVisibility(View.GONE);
			holder.noPrerequisites.setVisibility(View.VISIBLE);
		} else {
			holder.noPrerequisites.setVisibility(View.GONE);
			holder.prerequisites.setVisibility(View.VISIBLE);
			holder.prerequisitesText.setVisibility(View.VISIBLE);
			holder.prerequisites.setAdapter(new PrerequisitesAdapter(context, prerequisites, domain, branch));
		}

		//Setup Options
		holder.options.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(context, holder.options);
				context.getMenuInflater().inflate(R.menu.resource_options, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId() == R.id.resource_share_item) {
							Intent sendIntent = new Intent();
							sendIntent.setAction(Intent.ACTION_SEND);

							String shareMessage = "Hey there! I am learning " + element.getName() + " from " + element.getFrom() + " on Learn!" +
									"\nURL: " + element.getLink() +
									"\nWhy don't you join me on Learn!";
							sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
							sendIntent.setType("text/plain");

							Intent shareIntent = Intent.createChooser(sendIntent, null);
							context.startActivity(shareIntent);
						} else if (item.getItemId() == R.id.resource_rate_item) {
							//Create a Rating Bar.
							final LinearLayout linearLayout = new LinearLayout(context);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							linearLayout.setLayoutParams(params);
							linearLayout.setGravity(Gravity.CENTER);
							final RatingBar ratingBar = new RatingBar(context);
							ratingBar.setNumStars(5);
							ratingBar.setStepSize(1);
							ratingBar.setForegroundGravity(Gravity.CENTER);
							linearLayout.addView(ratingBar);

							//Set Rating (If Any)
							DatabaseReference reference = element.getReference().child("rating").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
							reference.addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
									try {
										long ratingValue = (long) dataSnapshot.getValue();
										ratingBar.setRating(ratingValue);
									} catch (NullPointerException e) {
										ratingBar.setRating(0);
									}
								}

								@Override
								public void onCancelled(@NonNull DatabaseError databaseError) {
									ratingBar.setRating(0);
								}
							});

							//Show Dialog to set rating.
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setCancelable(true)
									.setTitle("Rating")
									.setView(linearLayout)
									.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											reference.setValue(ratingBar.getRating());
										}
									});
							builder.create().show();
						} else if (item.getItemId() == R.id.resource_remind_item) {
							RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_remind, null, false);
							Calendar calendar = Calendar.getInstance();

							//Date Picker
							EditText date = layout.findViewById(R.id.date_pick);
							date.setInputType(InputType.TYPE_NULL);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
								date.setFocusable(View.NOT_FOCUSABLE);
							}
							String dateString = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(calendar.getTime());
							date.setText(dateString);
							date.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
										@Override
										public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
											calendar.set(Calendar.YEAR, year);
											calendar.set(Calendar.MONTH, month);
											calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
											String dateString = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(calendar.getTime());
											date.setText(dateString);
										}
									}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
									datePickerDialog.show();
								}
							});

							//Time Picker
							EditText time = layout.findViewById(R.id.time_pick);
							time.setInputType(InputType.TYPE_NULL);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
								time.setFocusable(View.NOT_FOCUSABLE);
							}
							String timeString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());
							time.setText(timeString);
							time.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
										@Override
										public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
											calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
											calendar.set(Calendar.MINUTE, minute);
											String timeString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());
											time.setText(timeString);
										}
									}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
									timePickerDialog.show();
								}
							});

							//Strictness
							Switch strict = layout.findViewById(R.id.strict);

							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setCancelable(true)
									.setTitle("Remind me on")
									.setView(layout)
									.setPositiveButton("Set Reminder", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											Log.e("ALPHA", "Stage 1");
											Intent intent = new Intent(context, NotificationReceiver.class);
											intent.putExtra("name", element.getName());
											intent.putExtra("link", element.getLink());
											intent.putExtra("from", element.getFrom());
											intent.putExtra("isPlaylist", element.isPlaylist());
											intent.putExtra("videoNames", element.getVideoNames());
											intent.putExtra("videoLinks", element.getVideoLinks());
											intent.putExtra("reference", element.getReference().toString());
											intent.putExtra("type", element.getType().toString());
											intent.putExtra("isReminderNotification", true);
											intent.putExtra("isStrict", strict.isChecked());

											PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 736, intent, PendingIntent.FLAG_UPDATE_CURRENT);
											AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
											assert manager != null;
											manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
											Log.e("ALPHA", "Stage 2");
										}
									})
									.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
							builder.create().show();
						}
						return true;
					}
				});
				popup.show();
			}
		});

		//Populate Data
		holder.name.setText(element.getName());
		holder.rating.setText(String.valueOf(element.getRating()));
		holder.from.setText(element.getFrom());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (element.getType() == Type.VIDEO) {
					//Video
					Intent intent = new Intent(context, CourseVideoViewActivity.class);
					intent.putExtra("rating", element.getRating());
					intent.putExtra("link", element.getLink());
					intent.putExtra("name", element.getName());
					intent.putExtra("from", element.getFrom());
					intent.putExtra("time", 0.0);
					intent.putExtra("index", 0);
					intent.putExtra("isPlaylist", element.isPlaylist());
					if (element.isPlaylist()) {
						intent.putExtra("videoNames", element.getVideoNames());
						intent.putExtra("videoLinks", element.getVideoLinks());
					}
					intent.putExtra("reference", element.getReference().toString());
					context.startActivity(intent);
					context.finish();
				} else {
					//Documents, Courses and Projects
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(element.getLink()));
					context.startActivity(intent);
				}
			}
		});
		Glide.with(context)
				.load(element.getIconUrl())
				.placeholder(R.mipmap.ic_launcher)
				.into(holder.icon);
	}

	@Override
	public int getItemCount() {
		return courseElements.size();
	}

	public static class ElementViewHolder extends RecyclerView.ViewHolder {

		TextView name, rating, from, noPrerequisites, prerequisitesText, options;
		RecyclerView prerequisites;
		ImageView icon;

		public ElementViewHolder(@NonNull View itemView) {
			super(itemView);
			name = itemView.findViewById(R.id.name);
			noPrerequisites = itemView.findViewById(R.id.prerequisites_none);
			prerequisites = itemView.findViewById(R.id.prerequisites);
			rating = itemView.findViewById(R.id.rating);
			prerequisitesText = itemView.findViewById(R.id.prerequisites_text);
			from = itemView.findViewById(R.id.from);
			icon = itemView.findViewById(R.id.image);
			options = itemView.findViewById(R.id.options);
		}
	}
}