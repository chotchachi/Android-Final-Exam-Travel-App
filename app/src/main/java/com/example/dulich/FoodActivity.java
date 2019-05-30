package com.example.dulich;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.dulich.Adapter.DiaDiemAdapter;
import com.example.dulich.Object.DiaDiem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    String place_key;
    RecyclerView recyclerView;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<DiaDiem> arrayList;
    DiaDiemAdapter adapter;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        AnhXa();
        khoitaoRecylerView();
        //Firebase
        firestore = FirebaseFirestore.getInstance();
        //Map
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapuser);
        mapFragment.getMapAsync(this);
        //Array
        arrayList = new ArrayList<>();
        adapter = new DiaDiemAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        firestore.collection("DiaDiem").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final DiaDiem diaDiem = document.toObject(DiaDiem.class);
                        arrayList.add(diaDiem);
                        adapter.notifyDataSetChanged();
                        final LatLng latLng = new LatLng(diaDiem.getVitri().getLatitude(), diaDiem.getVitri().getLongitude());
                        Picasso.with(FoodActivity.this).load(diaDiem.getImg()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                //Thu nhỏ ảnh bitmap
                                int height = 150;
                                int width = 150;
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
                                Marker marker = map.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(diaDiem.getTen())
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                            }
                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                Toast.makeText(FoodActivity.this, "Lỗi chuyển đổi ảnh sang bitmap", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        });
                    }
                }
            }
        });

        Intent i = getIntent();
        place_key = i.getStringExtra("place_key");
    }

    private void khoitaoRecylerView(){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void AnhXa() {
        recyclerView = findViewById(R.id.recyclerview);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);
    }
}
