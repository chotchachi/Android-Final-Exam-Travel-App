package com.example.dulich.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dulich.BoNhoSharedPreferences;
import com.example.dulich.Object.TinNhan;
import com.example.dulich.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TinNhanAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<TinNhan> arrayList;
    BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
    String username, avatar;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public TinNhanAdapter(Activity activity, ArrayList<TinNhan> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
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
        TextView tv_message, tv_username;
        CircleImageView avatar;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        LayoutInflater inflater;
        if (convertView==null){
            viewHolder = new ViewHolder();
            username = sharedPreferences.TraSP("usernamedaluu", activity);
            avatar = sharedPreferences.TraSP("avatardaluu", activity);
            if (arrayList.get(position).getUser().equals(username)){
                inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.tinnhan_right_item, null);
            } else {
                inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.tinnhan_left_item, null);
            }
            viewHolder.tv_message = convertView.findViewById(R.id.tinnhan);
            viewHolder.tv_username = convertView.findViewById(R.id.username);
            viewHolder.avatar = convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TinNhan tinNhan = arrayList.get(position);
        viewHolder.tv_username.setText(tinNhan.getUser());
        databaseReference.child("User").child(arrayList.get(position).getUser()).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String other_avatarurl = String.valueOf(dataSnapshot.child("avatar_url").getValue());
                if (arrayList.get(position).getUser().equals(username)){
                    Picasso.with(activity).load(avatar).into(viewHolder.avatar);
                } else {
                    Picasso.with(activity).load(other_avatarurl).into(viewHolder.avatar);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewHolder.tv_message.setText(tinNhan.getMessage());
        return convertView;
    }
}