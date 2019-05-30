package com.example.dulich.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dulich.Object.Trip;
import com.example.dulich.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TripAdapter extends BaseAdapter {
    ArrayList<Trip> arrayTrip;
    Activity activity;

    public TripAdapter(ArrayList<Trip> arrayTrip, Activity activity) {
        this.arrayTrip = arrayTrip;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return arrayTrip.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayTrip.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        TextView tenTrip, diadiemTrip, thoigianTrip;
        ImageView anhTrip;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_list_trip, null);
            viewHolder = new ViewHolder();
            viewHolder.tenTrip = convertView.findViewById(R.id.ten_trip);
            viewHolder.diadiemTrip = convertView.findViewById(R.id.diadiem_trip);
            viewHolder.thoigianTrip = convertView.findViewById(R.id.thoigian_trip);
            viewHolder.anhTrip = convertView.findViewById(R.id.anh_trip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Trip trip = arrayTrip.get(position);
        viewHolder.tenTrip.setText(trip.getTenTrip());
        viewHolder.diadiemTrip.setText(trip.getDiaDiem().getTen());
        viewHolder.thoigianTrip.setText(trip.getThoiGian());
        Picasso.with(activity).load(trip.getAnh()).into(viewHolder.anhTrip);
        return convertView;
    }
}
