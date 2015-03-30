package com.example.messenger;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<OneComment> {
	
	private TextView countryName;
	private List<OneComment> countries = new ArrayList<OneComment>();
	private RelativeLayout wrapper;

	@Override
	public void add(OneComment object) {
		countries.add(object);
		super.add(object);
	}

	public void removeItem(OneComment item){
		countries.remove(item);
		notifyDataSetChanged();
	}
	
	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.countries.size();
	}

	public OneComment getItem(int index) {
		return this.countries.get(index);
	}
	public List<OneComment> getItemList(){
		return countries;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_chat, parent, false);
		}

		
		wrapper = (RelativeLayout) row.findViewById(R.id.wrapper);
		OneComment coment = getItem(position);
		countryName = (TextView) row.findViewById(R.id.comment);
		countryName.setText(coment.comment);
		
		if(countries.get(position).getComment() == "Priva Message" || countries.get(position).getCountDown() == 99 ){
			countryName.setBackgroundResource(coment.left ? R.drawable.bubble_red : R.drawable.bubble_redr);
		}else{
			countryName.setBackgroundResource(coment.left ? R.drawable.bubble_grey : R.drawable.bubble_purple);
		}
		
		wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);

		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}
	

}
