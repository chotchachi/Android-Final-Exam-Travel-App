package com.example.dulich.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dulich.Object.Chat;
import com.example.dulich.R;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    ArrayList<Chat> arrayList;
    Activity activity;

    public ChatAdapter(ArrayList<Chat> arrayList, Activity activity) {
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
        TextView chatname;
        TextView users;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.chat_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.chatname = convertView.findViewById(R.id.tv_chatname);
            viewHolder.users = convertView.findViewById(R.id.tv_users);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Chat chat = arrayList.get(position);
        viewHolder.chatname.setText(chat.getChatName());
        viewHolder.users.setText(chat.getUser().toString());
        return convertView;
    }
}
