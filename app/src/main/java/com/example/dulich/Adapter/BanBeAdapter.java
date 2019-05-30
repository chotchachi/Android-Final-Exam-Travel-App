package com.example.dulich.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dulich.Object.User;
import com.example.dulich.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BanBeAdapter extends BaseAdapter {
    ArrayList<User> banBeArrayList;
    Activity activity;

    public BanBeAdapter(ArrayList<User> banBeArrayList, Activity activity) {
        this.banBeArrayList = banBeArrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return banBeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return banBeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        TextView tv_tenbanbe, tv_username, tv_sinhnhat;
        CircleImageView avatar_banbe;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.banbe_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_tenbanbe = convertView.findViewById(R.id.tv_tenbanbe);
            viewHolder.tv_username = convertView.findViewById(R.id.tv_username);
            viewHolder.tv_sinhnhat = convertView.findViewById(R.id.tv_sinhnhat);
            viewHolder.avatar_banbe = convertView.findViewById(R.id.avatar_banbe);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User banBe = banBeArrayList.get(position);
        viewHolder.tv_tenbanbe.setText(banBe.getHovaten());
        viewHolder.tv_username.setText(banBe.getUsername());
        viewHolder.tv_sinhnhat.setText(banBe.getSinh_nhat());
        Picasso.with(activity).load(banBe.getAvatar_url()).into(viewHolder.avatar_banbe);
        return convertView;
    }
}
