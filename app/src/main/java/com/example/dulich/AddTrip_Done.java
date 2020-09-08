package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.dulich.Object.Chat;
import com.example.dulich.Object.Place;
import com.example.dulich.Object.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddTrip_Done extends AppCompatActivity {
    Button btn_ok;
    Switch aSwitch;
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;
    String username, tripname, tripdate;
    String place_name, place_key, place_img;
    ListView list_banbe_check;
    ArrayList<String> banBeArrayList;
    ArrayAdapter<String> adapter;
    List<String> banbedachon = new ArrayList<>();
    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip__done);
        btn_ok = findViewById(R.id.btn_ok);
        aSwitch = findViewById(R.id.btn_switch);
        list_banbe_check = findViewById(R.id.list_banbe_check);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        banBeArrayList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, banBeArrayList);
        list_banbe_check.setAdapter(adapter);

        //Lấy username
        BoNhoSharedPreferences boNhoSharedPreferences = new BoNhoSharedPreferences();
        username = boNhoSharedPreferences.TraSP("usernamedaluu", this);

        LayDanhSachBanBeFirebase();

        list_banbe_check.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_banbe_check.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                boolean check = checkedTextView.isChecked();
                String ten_ban_be_chon = banBeArrayList.get(position);
                if (check==true){
                    banbedachon.add(ten_ban_be_chon);
                } else {
                    banbedachon.remove(ten_ban_be_chon);
                }
            }
        });
        banbedachon.add(username);

        //Get bundle
        Bundle bundle = getIntent().getExtras();
        tripname = bundle.getString("tripname");
        tripdate = bundle.getString("tripdate");
        place_name = bundle.getString("place_name");
        place_key = bundle.getString("place_key");
        place_img = bundle.getString("place_img");

        //Lấy thông tin place dựa vào place_key;
        LayThongTinPlace();

        //Sự kiện button done
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemTripFirebase();
            }
        });
    }

    private void LayThongTinPlace() {
        databaseReference.child("City").child(place_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    place = dataSnapshot.getValue(Place.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Hàm lấy danh sách bạn bè Firebase
    private void LayDanhSachBanBeFirebase() {
        databaseReference.child("User").child(username).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String user_banbe = String.valueOf(postSnapshot.getValue());
                    banBeArrayList.add(user_banbe);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Hàm thêm trip vào Firebase
    private void ThemTripFirebase() {
        String tripid = databaseReference.push().getKey(); //Tạo Random ID để sau nay lấy dữ liệu trong ID đó
        Trip newtrip = new Trip(tripid, username, tripname, place, tripdate, place_img, banbedachon);

        //Firebase gửi dữ liệu
        databaseReference.child("User").child(username).child("Trip").child(tripid).setValue(newtrip, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null){
                    Toast.makeText(AddTrip_Done.this, "Thêm thành công", Toast.LENGTH_SHORT).show();

                    //Nếu chekc switch thì tạo nhóm chat
                    if (aSwitch.isChecked()){
                        TaoNhomChat();
                    }

                    Intent i = new Intent(AddTrip_Done.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(AddTrip_Done.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Hàm tạo nhóm chat
    private void TaoNhomChat() {
        String chat_id = firestore.collection("Chat").document().getId();
        Chat newChat = new Chat(tripname, chat_id, banbedachon);
        firestore.collection("Chat").document(chat_id).set(newChat);
    }
}
