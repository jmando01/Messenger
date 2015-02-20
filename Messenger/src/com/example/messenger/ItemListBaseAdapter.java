package com.example.messenger;

import java.util.ArrayList;






import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListBaseAdapter extends BaseAdapter {
	private static ArrayList<ItemDetails> itemDetailsrrayList;
	
	private Integer[] imgid = {
			R.drawable.ic_perfil,
			R.drawable.ic_priva,
			//R.drawable.p2,
			//R.drawable.bb5,
			//R.drawable.bb6,
			//R.drawable.d1
			};
	
	private LayoutInflater l_Inflater;

	public ItemListBaseAdapter(Context context, ArrayList<ItemDetails> results) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
	}
	
	public void removeItem(ItemDetails item){
		itemDetailsrrayList.remove(item);
		notifyDataSetChanged();
	}

	public void addItem(ItemDetails item) {
		itemDetailsrrayList.add(item);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return itemDetailsrrayList.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.item_details_view, null);
			holder = new ViewHolder();
			holder.txt_itemName = (TextView) convertView.findViewById(R.id.name);
			holder.txt_itemMessage = (TextView) convertView.findViewById(R.id.message);
			holder.txt_itemDate = (TextView) convertView.findViewById(R.id.date);
			holder.txt_itemCounter = (TextView) convertView.findViewById(R.id.counter);
			holder.itemImage = (ImageView) convertView.findViewById(R.id.photo);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txt_itemName.setText((itemDetailsrrayList.get(position).getName()).substring(0, itemDetailsrrayList.get(position).getName().indexOf('@')));
		holder.txt_itemMessage.setText(itemDetailsrrayList.get(position).isPrivate() ? "Priva Conversation": itemDetailsrrayList.get(position).getMessage());
		holder.txt_itemDate.setText(itemDetailsrrayList.get(position).getDate());
		
		try {
			if(itemDetailsrrayList.get(position).getCounter().equals("0")){
				holder.txt_itemCounter.setText(" ");
			}else{
				holder.txt_itemCounter.setText(itemDetailsrrayList.get(position).getCounter());
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			holder.txt_itemCounter.setText(itemDetailsrrayList.get(position).getCounter());
		}
		
		//holder.itemImage.setImageBitmap(itemDetailsrrayList.get(position).getImage());
		
		try {
			holder.itemImage.setImageBitmap(
			Bitmap.createScaledBitmap(
			Connect.db.getPhotoDBByUserName(itemDetailsrrayList.get(position).getName()).getPhoto(), 125, 125, false));
			Connect.db.close();
		} catch (Exception e) {
			Log.d("ItemListAdapter","No se encontro una imagen para el usuario: "+ itemDetailsrrayList.get(position).getName());
			holder.itemImage.setImageResource(imgid[itemDetailsrrayList.get(position).getImage() - 1]);
			e.getStackTrace();
		}
		//imageLoader.DisplayImage("http://192.168.1.28:8082/ANDROID/images/BEVE.jpeg", holder.itemImage);

		return convertView;
	}

	static class ViewHolder {
		TextView txt_itemCounter;
		TextView txt_itemName;
		TextView txt_itemMessage;
		TextView txt_itemDate;
		ImageView itemImage;
	}
}
