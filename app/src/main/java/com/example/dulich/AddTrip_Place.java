package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.dulich.Adapter.PlaceAdapter;
import com.example.dulich.Object.Place;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddTrip_Place extends AppCompatActivity {
    String trip_name;
    DatabaseReference databaseReference;
    ArrayList<Place> placeArrayList;
    PlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip__city);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        placeArrayList = new ArrayList<>();
        adapter = new PlaceAdapter(placeArrayList, this);
        LayInfoPlace();
    }

    //Hàm lấy info các place
    public void LayInfoPlace() {
        databaseReference.child("City").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place place = dataSnapshot.getValue(Place.class);
                placeArrayList.add(place);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Recycler();
    }

    //RecyclerView
    public void Recycler(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);

        //Sự kiện ấn vào Item
        adapter.setOnItemClickListener(new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                //Lấy tripname để gửi đi tiếp
                Intent i = getIntent();
                trip_name = i.getStringExtra("tripname");

                //Đóng gói mới gửi đi
                Intent a = new Intent(AddTrip_Place.this, AddTrip_Info.class);
                a.putExtra("tripname", trip_name);
                a.putExtra("place_name", adapter.getArrayList().get(position).getTen());
                a.putExtra("place_key", adapter.getArrayList().get(position).getKey());
                a.putExtra("place_img", adapter.getArrayList().get(position).getImg());
                startActivity(a);
            }
        });
    }
}
