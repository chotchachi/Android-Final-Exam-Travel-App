package com.example.dulich;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.dulich.Object.User;
import com.example.dulich.Object.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class GPS_Service extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    UserLocation userLocation;
    String username;
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;
    Handler handler;
    Runnable runnable;
    User user;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Khởi tạo
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Hàm xử lý tự động update vị trí
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                //Lấy info user để up lên Firebase
                //Hai phương thức này phải đồng bộ với nhau
                //Nếu không khi đăng xuất thì vị trí vẫn được update
                LayInfoUserDangNhap();
                GetLocation();
                handler.postDelayed(this, 10000);
            }
        }, 3000);
        return START_NOT_STICKY;
    }

    //Hàm lấy info user
    private void LayInfoUserDangNhap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPreferences.getString("usernamedaluu", "");
        databaseReference.child("User").child(username).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Lấy về đối tượng user để gán vào field của UserLocation trong Firebase
                user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Hàm get location, nếu get được thì update lên Firestore
    private void GetLocation() {
        //Cái này kiểm tra permission, tự general, ko quan tâm
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Hàm get chính
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()){
                    Location location = task.getResult();
                    if (location != null) {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        userLocation = new UserLocation();
                        userLocation.setGeoPoint(geoPoint);
                        userLocation.setUser(user);
                        //Sau khi lấy được vị trí thì gửi lên Firebase
                        SaveUserLocation();
                    }
                }
            }
        });
    }

    //Hàm update userlocation trên Firestore
    private void SaveUserLocation() {
        if (userLocation != null){
            if (!username.isEmpty()){
                firestore.collection("UserLocation").document(username).set(userLocation);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Hủy handler
        handler.removeCallbacks(runnable);
    }
}
