package com.example.dulich.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.dulich.Adapter.BanBeAdapter;
import com.example.dulich.BoNhoSharedPreferences;
import com.example.dulich.Object.User;
import com.example.dulich.R;
import com.example.dulich.ThemBanBeActivity;
import com.example.dulich.TimQuanhDayActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_MyFriends extends Fragment {
    LinearLayout btn_thembanbe, btn_danhsachbanbe, btn_timquanhday;
    ListView list_banbe;
    ArrayList<User> arrayList;
    BanBeAdapter adapter;
    DatabaseReference databaseReference;
    String username;
    List<String> list_username_banbe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment__my_friends, container, false);
        AnhXa(view);

        BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
        username = sharedPreferences.TraSP("usernamedaluu", getActivity());

        list_username_banbe = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        LayDanhSachBanBe();

        //Button thêm bạn bè
        btn_thembanbe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ThemBanBeActivity.class);
                startActivity(i);
            }
        });

        //Button danh sách bạn bè
        btn_danhsachbanbe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogDanhSachBanBe();
            }
        });

        //Button tìm quanh đây
        btn_timquanhday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TimQuanhDayActivity.class);
                startActivity(i);
            }
        });
        return view;
    }

    //Hảm lấy username trong danh sách bạn bè
    private void LayDanhSachBanBe() {
        databaseReference.child("User").child(username).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String user_banbe = String.valueOf(postSnapshot.getValue());
                    list_username_banbe.add(user_banbe);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Hàm show dialog
    private void DialogDanhSachBanBe() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_danhsachbanbe);
        list_banbe = dialog.findViewById(R.id.list_banbe);
        arrayList = new ArrayList<>();
        adapter = new BanBeAdapter(arrayList, getActivity());
        list_banbe.setAdapter(adapter);
        LayInfoBanBe();
        dialog.show();
    }

    //Hàm lấy info bạn bè
    private void LayInfoBanBe() {
        if (!list_username_banbe.isEmpty()){
            for (int i = 0; i<list_username_banbe.size(); i++){
                databaseReference.child("User").child(list_username_banbe.get(i)).child("Info").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User banbe = dataSnapshot.getValue(User.class);
                        arrayList.add(banbe);
                        list_banbe.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void AnhXa(View view) {
        btn_danhsachbanbe = view.findViewById(R.id.danhsach_banbe);
        btn_thembanbe = view.findViewById(R.id.them_banbe);
        btn_timquanhday = view.findViewById(R.id.timquanhday);
    }
}
