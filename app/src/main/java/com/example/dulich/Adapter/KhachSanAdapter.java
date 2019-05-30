package com.example.dulich.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dulich.Object.KhachSan;
import com.example.dulich.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class KhachSanAdapter extends BaseAdapter {
    ArrayList<KhachSan> khachSanArrayList;
    Activity activity;

    public KhachSanAdapter(ArrayList<KhachSan> khachSanArrayList, Activity activity) {
        this.khachSanArrayList = khachSanArrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return khachSanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return khachSanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder{
        TextView ten, diachi;
        ImageView anh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_list_khachsan, null);

            viewHolder = new ViewHolder();
            viewHolder.ten = convertView.findViewById(R.id.ten);
            viewHolder.diachi = convertView.findViewById(R.id.diachi);
            viewHolder.anh = convertView.findViewById(R.id.anh);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        KhachSan khachSan = khachSanArrayList.get(position);
        viewHolder.ten.setText(khachSan.getTen());
        viewHolder.diachi.setText(khachSan.getDiaChi());
        Picasso.with(activity).load(khachSan.getAnh()).into(viewHolder.anh);
        return convertView;
    }
}
