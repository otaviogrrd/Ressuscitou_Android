package br.org.cn.ressuscitou;


import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


public class AdapterIndiceLiturgico extends BaseExpandableListAdapter {
	private Context ctx;
	private LinkedHashMap<String, List<String>> indice_liturgico;
	private List<String> cantos_list;

	public AdapterIndiceLiturgico(Context ctx, LinkedHashMap<String, List<String>> indice_liturgico, List<String> cantos_list){
		this.ctx= ctx;
		this.indice_liturgico=indice_liturgico;
		this.cantos_list=cantos_list;
	}

	@Override
	public int getGroupCount() {
		return indice_liturgico.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return indice_liturgico.get(cantos_list.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return cantos_list.get(groupPosition);
	}

	@Override
	public Object getChild(int parent, int child) {
		return indice_liturgico.get(cantos_list.get(parent)).get(child);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int parent, int child) {
		return child;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int parent, boolean isExpanded, View convertView, ViewGroup parentView) {
		String parent_title = (String) getGroup(parent);
		if (convertView == null){
			LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.parent_indice_liturgico, parentView, false);
		}
		TextView parent_textview  = convertView.findViewById(R.id.parent_txt);
		parent_textview.setTypeface(null, Typeface.BOLD);
		parent_textview.setText(parent_title);
		return convertView;
	}

	@Override
	public View getChildView(int parent, int child, boolean isLastChild, View convertView, ViewGroup parentView) {
		String child_title = (String) getChild(parent,child);
		if (convertView == null){
			LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.child_indice_liturgico, parentView, false);
		}
		TextView child_textview = convertView.findViewById(R.id.child_txt);
		child_textview.setText(child_title);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled(){
		return true;
	}
}
