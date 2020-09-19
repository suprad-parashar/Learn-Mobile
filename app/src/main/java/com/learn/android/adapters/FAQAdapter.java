package com.learn.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.learn.android.R;
import com.learn.android.objects.FAQ;

import java.util.ArrayList;

public class FAQAdapter extends BaseExpandableListAdapter {

	//Declare Data Variables.
	ArrayList<FAQ> faqs;
	Context context;

	public FAQAdapter(Context context, ArrayList<FAQ> faqs) {
		this.faqs = faqs;
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		return faqs.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.layout_question, parent, false);
		TextView question = convertView.findViewById(R.id.question);
		ImageView dropDown = convertView.findViewById(R.id.drop_down);
		if (isExpanded)
			dropDown.setImageDrawable(context.getDrawable(R.drawable.ic_drop_up));
		else
			dropDown.setImageDrawable(context.getDrawable(R.drawable.ic_drop_down));
		question.setText(faqs.get(groupPosition).getQuestion());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.layout_answer, parent, false);
		TextView question = convertView.findViewById(R.id.answer);
		question.setText(faqs.get(groupPosition).getAnswer());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
