package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulich.Adapter.TinNhanAdapter;
import com.example.dulich.Object.TinNhan;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class MessageScreen extends AppCompatActivity {
    Toolbar toolbar;
    TextView tv_chatname;
    EditText edt_tinnhan;
    Button btn_send, btn_map;
    String chatid, chatname;
    FirebaseFirestore firestore;
    DatabaseReference databaseReference;
    String my_username, my_userid, my_hovaten, my_avatarurl, my_sinhnhat;
    ArrayList<TinNhan> arrayList;
    TinNhanAdapter adapter;
    ListView listView;
    Set<String> messageIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_screen);
        AnhXa();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayChatInfo();
        AnBanPhimAo();

        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        arrayList = new ArrayList<>();
        adapter = new TinNhanAdapter(this, arrayList);
        listView.setAdapter(adapter);

        //Lấy info user đang login
        BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
        my_username = sharedPreferences.TraSP("usernamedaluu", this);
        my_userid = sharedPreferences.TraSP("useriddaluu", this);
        my_hovaten = sharedPreferences.TraSP("hovatendaluu", this);
        my_avatarurl = sharedPreferences.TraSP("avatardaluu", this);
        my_sinhnhat = sharedPreferences.TraSP("birthdaluu", this);

        //Button send
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuiTinNhan();
            }
        });

        //Button map
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageScreen.this, MapUserChatActivity.class);
                i.putExtra("chatid", chatid);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LayDuLieuTinNhan();
    }

    //Hàm lấy chatid và chatname
    private void LayChatInfo() {
        Intent i = getIntent();
        chatid = i.getStringExtra("chatid");
        chatname = i.getStringExtra("chatname");
        tv_chatname.setText(chatname);
    }

    //Hàm lấy tin nhắn
    private void LayDuLieuTinNhan() {
        firestore.collection("Chat").document(chatid).collection("Mess").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    TinNhan tinNhan = doc.toObject(TinNhan.class);
                    //Dùng set<> để kiểm tra messsage_id đã lấy về chưa, nếu chưa thì gán vào set<>. Nếu không tin nhắn sẽ bị lặp đi lặp lại
                    if (!messageIds.contains(tinNhan.getMessage_id())){
                        messageIds.add(tinNhan.getMessage_id());
                        arrayList.add(tinNhan);
                    }
                }
                cuonListViewxuongduoi();
                adapter.notifyDataSetChanged();
            }
        });
    }

    //Hàm gửi tin nhắn
    private void GuiTinNhan() {
        if (edt_tinnhan.getText().toString().equals("")){
            Toast.makeText(MessageScreen.this, "Bạn chưa viết tin nhắn", Toast.LENGTH_SHORT).show();
        } else {
            String message = edt_tinnhan.getText().toString();
            String message_id = firestore.collection("Chat").document(chatid).collection("Mess").document().getId();

            final TinNhan tinNhan = new TinNhan();
            tinNhan.setUser(my_username);
            tinNhan.setMessage(message);
            tinNhan.setMessage_id(message_id);

            firestore.collection("Chat").document(chatid).collection("Mess").document(message_id).set(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Xóa hết edittext sau khi gửi xong
                    edt_tinnhan.getText().clear();
                }
            });
        }
    }

    //Cuộn listview xuống dưới cùng
    private void cuonListViewxuongduoi() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                //Hàm cuộn xuống
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    //Ẩn bàn phím ảo
    private void AnBanPhimAo(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolbar);
        tv_chatname = toolbar.findViewById(R.id.tv_chatname);
        btn_map = toolbar.findViewById(R.id.btn_map);
        edt_tinnhan = findViewById(R.id.edt_tinnhan);
        btn_send = findViewById(R.id.btn_send);
        listView = findViewById(R.id.list_tinnhan);
    }
}
