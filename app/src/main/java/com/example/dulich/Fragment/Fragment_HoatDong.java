package com.example.dulich.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dulich.Adapter.HoatDongAdapter;
import com.example.dulich.BoNhoSharedPreferences;
import com.example.dulich.NewHoatDongActivity;
import com.example.dulich.Object.HoatDong;
import com.example.dulich.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_HoatDong extends Fragment {
    CircleImageView avatar;
    RelativeLayout up_status;
    BoNhoSharedPreferences sharedPreferences;
    String user_login, my_avatar;
    DatabaseReference databaseReference;
    RecyclerView view_hoat_dong;
    ArrayList<HoatDong> arrayList;
    HoatDongAdapter adapter;
    FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment__hoat_dong, container, false);
        AnhXa(view);
        khoitaoRecylerView();

        //Khởi tạo
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        adapter = new HoatDongAdapter(arrayList, getActivity());
        view_hoat_dong.setAdapter(adapter);

        LayInfoUserLogin();
        LayDataHoatDong();

        //Chuyển activity new hoạt động
        up_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NewHoatDongActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    //Hàm lấy data các hoạt động trong Firestore
    private void LayDataHoatDong() {
        firestore.collection("HoatDong").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        HoatDong hoatDong = document.toObject(HoatDong.class);
                        arrayList.add(hoatDong);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Hàm lấy info user login
    private void LayInfoUserLogin() {
        sharedPreferences = new BoNhoSharedPreferences();
        user_login = sharedPreferences.TraSP("usernamedaluu", getActivity());
        my_avatar = sharedPreferences.TraSP("avatardaluu", getActivity());
        Picasso.with(getActivity()).load(my_avatar).into(avatar);

        databaseReference.child("User").child(user_login).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void khoitaoRecylerView(){
        view_hoat_dong.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        view_hoat_dong.setLayoutManager(layoutManager);
    }

    private void AnhXa(View view) {
        avatar = view.findViewById(R.id.avatar);
        up_status = view.findViewById(R.id.up_status);
        view_hoat_dong = view.findViewById(R.id.recyclerview);
    }
}
