package com.example.dulich.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dulich.Object.Thoitiet6Days;
import com.example.dulich.R;

import java.util.ArrayList;

public class Thoitiet6DaysAdapter extends BaseAdapter {
    ArrayList<Thoitiet6Days> arrayList;
    Activity activity;

    public Thoitiet6DaysAdapter(ArrayList<Thoitiet6Days> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        TextView tv_temp, tv_day;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.thoitiet_6days_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_temp = convertView.findViewById(R.id.tv_temp);
            viewHolder.tv_day = convertView.findViewById(R.id.tv_day);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Thoitiet6Days thoitiet6Days = arrayList.get(position);
        viewHolder.tv_temp.setText(thoitiet6Days.getTemp()+"°C");
        viewHolder.tv_day.setText(thoitiet6Days.getDay());
        return convertView;
    }
}
