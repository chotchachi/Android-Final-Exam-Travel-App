package com.example.dulich;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.dulich.Object.UserLocation;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class TimQuanhDayActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;
    ArrayList<String> arrayListUser;
    UserLocation userLocation;
    String user_login;
    BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_quanh_day);
        //Map
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapquanhday);
        mapFragment.getMapAsync(this);
        //Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        //Array
        arrayListUser = new ArrayList<>();

        user_login = sharedPreferences.TraSP("usernamedaluu", this);

        LayDanhSachUser();
    }

    private void LayDanhSachUser() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    arrayListUser.add(postSnapshot.getKey());
                }
                LayViTriUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LayViTriUser() {
        for (int i = 0; i<arrayListUser.size();i++){
            firestore.collection("UserLocation").document(arrayListUser.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userLocation = document.toObject(UserLocation.class);
                            if (userLocation != null) {
                                if (userLocation.getUser() != null){
                                    final LatLng latLng = new LatLng(userLocation.getGeoPoint().getLatitude(), userLocation.getGeoPoint().getLongitude());
                                    final String hovaten = userLocation.getUser().getHovaten();
                                    final String user = userLocation.getUser().getUsername();

                                    //Hàm convert avatar url sang bitmap để hiển thị được trong marker
                                    Picasso.with(TimQuanhDayActivity.this).load(userLocation.getUser().getAvatar_url()).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            //Thu nhỏ ảnh bitmap
                                            int height = 150;
                                            int width = 150;
                                            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
                                            final Marker userMarker = map.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(hovaten)
                                                    .snippet(user)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                            userMarker.setTag(0);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                            Toast.makeText(TimQuanhDayActivity.this, "Lỗi chuyển đổi ảnh sang bitmap", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Kết bạn với "+marker.getSnippet());
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = databaseReference.push().getKey();
                databaseReference.child("User").child(user_login).child("Friends").child(id).setValue(marker.getSnippet()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TimQuanhDayActivity.this, "Kết bạn thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
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
