package com.example.dulich.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulich.Adapter.ChatAdapter;
import com.example.dulich.BoNhoSharedPreferences;
import com.example.dulich.MessageScreen;
import com.example.dulich.Object.Chat;
import com.example.dulich.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Fragment_Chat extends Fragment {
    ListView list_chat;
    FloatingActionButton add_chat;
    ArrayList<Chat> arrayList;
    ChatAdapter adapter;
    FirebaseFirestore firestore;
    EditText edt_chatname;
    TextView btn_ok, btn_cancel;
    String chatname, chatid, chatid_trave, username;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment__chat, container, false);
        list_chat = view.findViewById(R.id.list_chat);
        //add_chat = view.findViewById(R.id.add_chat);
        firestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        adapter = new ChatAdapter(arrayList, getActivity());

        //Lấy username
        BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
        username = sharedPreferences.TraSP("usernamedaluu", getActivity());

        //Tham chiếu đến Chat Collection
        //Tìm xem những Document Chat nào có field User = username đang login => Lấy hết những Document đó
        //Lấy ChatID ở trong những document đó lưu vào biến chatid_trave
        //Dùng biến chatid_trave đưa vào phương thức GetChatList để lấy về danh sách những Chat có User ở trong đó
        firestore.collection("Chat").whereArrayContains("user", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        chatid_trave = document.getString("chatID");
                        GetChatList(chatid_trave);
                    }
                } else {
                    Toast.makeText(getActivity(), "Loi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button new chat
        /*add_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaoChat();
            }
        });*/

        //Click item chat
        list_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chatid = arrayList.get(position).getChatID();
                String chatname = arrayList.get(position).getChatName();
                MoCuaSoChat(chatid, chatname);
            }
        });
        return view;
    }

    //Xử lí tạo chat tại Client
    /*private void TaoChat() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_new_chat);
        edt_chatname = dialog.findViewById(R.id.edt_chatname);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_ok = dialog.findViewById(R.id.btn_ok);
        dialog.show();
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_chatname.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Bạn cần đặt tên cho cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                } else {
                    chatname = edt_chatname.getText().toString();
                    chatid = firestore.collection("Chat").document().getId();
                    ThemUserVaoGroupChat(chatname, chatid);
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }*/

    //Hàm thêm user vào group chat
    /*private void ThemUserVaoGroupChat(final String chatname, final String chatid) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_them_user_chatroom);
        final EditText edt_add = dialog.findViewById(R.id.edt_add);
        Button btn_add = dialog.findViewById(R.id.btn_add);
        Button btn_done = dialog.findViewById(R.id.btn_done);
        final ArrayList<String> user_list = new ArrayList<>();
        user_list.add(username);
        dialog.show();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_add = edt_add.getText().toString();
                user_list.add(user_add);
                edt_add.getText().clear();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat newchat = new Chat(chatname, chatid, user_list);
                TaoChatFirebase(newchat, chatid);
                dialog.dismiss();
                MoCuaSoChat(chatid, chatname);
            }
        });
    }*/

    //Xử lí tạo chat trên Firebase
    /*private void TaoChatFirebase(Object newchat, String chatid) {
        firestore.collection("Chat").document(chatid).set(newchat);
    }*/

    //Mở MessageScreen
    private void MoCuaSoChat(String chatid, String chatname) {
        Intent i = new Intent(getActivity(), MessageScreen.class);
        i.putExtra("chatid", chatid);
        i.putExtra("chatname", chatname);
        startActivity(i);
    }

    //Lấy chat list từ firebase
    private void GetChatList(final String chatid_trave) {
        firestore.collection("Chat").document(chatid_trave).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String chatname = document.getString("chatName");
                        List<String> users = (List<String>) document.get("user");
                        Chat chat = new Chat(chatname, chatid_trave, users);
                        arrayList.add(chat);
                        list_chat.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

}
