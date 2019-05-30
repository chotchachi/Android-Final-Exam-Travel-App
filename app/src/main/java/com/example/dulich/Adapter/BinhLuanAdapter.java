package com.example.dulich.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dulich.BoNhoSharedPreferences;
import com.example.dulich.Object.BinhLuan;
import com.example.dulich.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BinhLuanAdapter extends BaseAdapter {
    ArrayList<BinhLuan> arrayList;
    Activity activity;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
    String user_login, avatar_login;

    public BinhLuanAdapter(ArrayList<BinhLuan> arrayList, Activity activity) {
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

    public class ViewHolder{
        CircleImageView avatar_user;
        TextView username, comment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView==null){
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_list_binhluan, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar_user = convertView.findViewById(R.id.avatar_user_cm);
            viewHolder.username = convertView.findViewById(R.id.user_cm);
            viewHolder.comment = convertView.findViewById(R.id.comment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final BinhLuan binhLuan = arrayList.get(position);
        viewHolder.username.setText(binhLuan.getUsername());
        viewHolder.comment.setText(binhLuan.getBinhluan());
        databaseReference.child("User").child(binhLuan.getUsername()).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String other_avatarurl = String.valueOf(dataSnapshot.child("avatar_url").getValue());
                user_login = sharedPreferences.TraSP("usernamedaluu", activity);
                avatar_login = sharedPreferences.TraSP("avatardaluu", activity);
                if (binhLuan.getUsername().equals(user_login)){
                    Picasso.with(activity).load(avatar_login).into(viewHolder.avatar_user);
                } else {
                    Picasso.with(activity).load(other_avatarurl).into(viewHolder.avatar_user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Picasso.with(activity).load(binhLuan.getUsername()).into(viewHolder.avatar_user);
        return convertView;
    }
}
