package com.learn.android.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learn.android.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Object to Display TreeView Data.
 */
public class TreeViewHolder extends TreeNode.BaseNodeViewHolder<String> {

	//Declare UI Variables
	private ViewGroup parent;
	private Context context;
	private int level;

	public TreeViewHolder(Context context, ViewGroup parent, int level) {
		super(context);
		this.context = context;
		this.parent = parent;
		this.level = level;
	}

	@Override
	public View createNodeView(TreeNode node, String value) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(R.layout.layout_treeview_element, parent, false);
		TextView valueTextView = view.findViewById(R.id.value);
		valueTextView.setText(value);
		valueTextView.setPadding(8 * level, 8, 8, 8);
		return view;
	}
}
