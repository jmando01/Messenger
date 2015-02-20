package com.example.messenger;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MySimpleSearchAdapter extends BaseAdapter {

	private ArrayList<String> mData = new ArrayList<String>();
	private LayoutInflater mInflater;

	public MySimpleSearchAdapter(Activity activity) {
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(String item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public int getCount() {
		return mData.size();
	}

	public String getItem(int position) {
		return mData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_one, null);
			holder.textView = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String str = mData.get(position);
		holder.textView.setText(str);
		return convertView;
	}

	public class ViewHolder {
		public TextView textView;
	}

}
