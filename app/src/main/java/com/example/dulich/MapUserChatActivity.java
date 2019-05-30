package com.example.dulich;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dulich.Adapter.BanBeAdapter;
import com.example.dulich.Object.User;
import com.example.dulich.Object.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.List;

public class MapUserChatActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap map;
    ListView list_user;
    ArrayList<User> array_user;
    BanBeAdapter adapter;
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;
    String chatid;
    FusedLocationProviderClient fusedLocationProviderClient;
    User user;
    List<String> all_username;
    UserLocation userLocation;
    ArrayList<UserLocation> arrayListUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_user_chat);
        //Ánh xạ
        list_user = findViewById(R.id.list_user_chat);
        //Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        //Array
        array_user = new ArrayList<>();
        arrayListUserLocation = new ArrayList<>();
        adapter = new BanBeAdapter(array_user, MapUserChatActivity.this);
        list_user.setAdapter(adapter);
        //Map
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapuser);
        mapFragment.getMapAsync(this);

        //Lấy chatid để get list user
        Intent i = getIntent();
        chatid = i.getStringExtra("chatid");

        //Dùng biến chatid để lấy danh sách User trong nhóm chat
        LayDanhSachUserTrongNhomChat();

        //Click item listview
        list_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    ClickUserItem(position);
                } catch (Exception e){
                }
            }
        });
    }

    //Lấy user trong nhóm chat
    private void LayDanhSachUserTrongNhomChat() {
        firestore.collection("Chat").document(chatid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Vì list user trong nhóm chat chỉ lưu username, nên phải lấy về hết user rồi lưu trong List<String>
                        //sau đó dùng vòng lặp lấy những username đó truyền vào LayInfoUserTrenRealtimeDatabase() để lấy Info của user
                        all_username = (List<String>) document.get("user");
                        for (int i = 0; i < all_username.size(); i++){
                            String username = all_username.get(i);
                            LayInfoUser(username);
                        }
                        //Phải bỏ sau phương thức này vì để List<String> có đủ username thì mới get vị trí được, nếu không List sẽ null gây crash
                        LayLocationAllUser();
                    }
                }
            }
        });
    }

    //Dùng user vừa lấy ở trên để lấy thông tin user để hiển thị trên listview
    private void LayInfoUser(String username) {
        databaseReference.child("User").child(username).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                array_user.add(user);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapUserChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Hàm lấy user location
    private void LayLocationAllUser() {
        //Dùng List<String> để lấy các username truyền vào để lấy thông tin vị trí
        for (int i = 0; i < all_username.size(); i++){
            final String username  = all_username.get(i);
            //Dùng handler để cập nhật lại vị trí mới của user
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    firestore.collection("UserLocation").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    userLocation = document.toObject(UserLocation.class);
                                    arrayListUserLocation.add(userLocation);
                                    if (userLocation != null){
                                        if (userLocation.getUser() != null) {
                                            final LatLng latLng = new LatLng(userLocation.getGeoPoint().getLatitude(), userLocation.getGeoPoint().getLongitude());
                                            final String hovaten = userLocation.getUser().getHovaten();

                                            //Hàm convert avatar url sang bitmap để hiển thị được trong marker
                                            Picasso.with(MapUserChatActivity.this).load(userLocation.getUser().getAvatar_url()).into(new Target() {
                                                @Override
                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                    //Thu nhỏ ảnh bitmap
                                                    int height = 150;
                                                    int width = 150;
                                                    Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
                                                    final Marker userMarker = map.addMarker(new MarkerOptions()
                                                            .position(latLng)
                                                            .title(hovaten)
                                                            .snippet(username)
                                                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                                    //userMarker.setTag(0);
                                                }
                                                @Override
                                                public void onBitmapFailed(Drawable errorDrawable) {
                                                    Toast.makeText(MapUserChatActivity.this, "Lỗi chuyển đổi ảnh sang bitmap", Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        Toast.makeText(MapUserChatActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MapUserChatActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MapUserChatActivity.this, "Lỗi khi lấy về dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    handler.postDelayed(this, 9800);
                    //Xóa marker cũ
                    map.clear();
                }
            });
        }
    }

    //Hàm click vào item user trên listview
    private void ClickUserItem(int position) {
            double duoi = arrayListUserLocation.get(position).getGeoPoint().getLatitude() - .1;
            double trai = arrayListUserLocation.get(position).getGeoPoint().getLongitude() - .1;
            double tren = arrayListUserLocation.get(position).getGeoPoint().getLatitude() + .1;
            double phai = arrayListUserLocation.get(position).getGeoPoint().getLongitude() + .1;
            LatLngBounds latLngBounds = new LatLngBounds(
                    new LatLng(duoi, trai),
                    new LatLng(tren, phai)
            );
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
