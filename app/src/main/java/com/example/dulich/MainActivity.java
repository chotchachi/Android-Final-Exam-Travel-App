package com.example.dulich;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dulich.Fragment.Fragment_Chat;
import com.example.dulich.Fragment.Fragment_MyFriends;
import com.example.dulich.Fragment.Fragment_MyTrip;
import com.example.dulich.Fragment.Fragment_HoatDong;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    private static final int TRA_VE_QUYEN_TRUY_CAP_VI_TRI = 1;
    boolean quyentruycapvitri;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragment = null;
            switch (item.getItemId()) {
                case R.id.homescreen:
                    fragment = new Fragment_MyTrip();
                    break;
                case R.id.chatscreen:
                    fragment = new Fragment_Chat();
                    break;
                case R.id.nhatkyscreen:
                    fragment = new Fragment_HoatDong();
                    break;
                case R.id.friendscreen:
                    fragment = new Fragment_MyFriends();
                    break;
            }
            fragmentTransaction.replace(R.id.frame_home, fragment);
            fragmentTransaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getLocationPermission();
        //checkMapServices();

        CheckQuyenViTri();

        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Fragment_MyTrip();
        fragmentTransaction.add(R.id.frame_home, fragment);
        fragmentTransaction.commit();
    }

    //Hàm check quyền vị trí bằng Dexter
    private void CheckQuyenViTri() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()){
                    ShowSettingDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    //Hàm hiện thông báo yêu cầu truy cập vị trí
    private void ShowSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn cần cho phép ứng dụng truy cập vị trí");
        builder.setNegativeButton("Mở cài đặt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                i.setData(uri);
                startActivityForResult(i, 1);*/
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
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //YÊU CẦU QUYỀN TRUY CẬP VỊ TRÍ ĐẦU TIÊN

    //Kiểm tra ĐÃ CÓ QUYỀN hay chưa
    private void getLocationPermission() {
        //CHƯA CÓ(!=)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Trả về nếu ứng dụng CHƯA CÓ QUYỀN vị trí, trước đó đã bị TỪ CHỐI (trả về mãi nếu chưa có quyền)
                Toast.makeText(this, "Bạn chưa cấp quyền vị trí đó nha", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        TRA_VE_QUYEN_TRUY_CAP_VI_TRI);
                //Trả về khi hiển thị hộp thoại cấp quyền
                /*Toast.makeText(this, "Hãy cấp quyền vị trí cho ứng dụng nhé", Toast.LENGTH_SHORT).show();*/
            }
        }
        //ĐÃ CÓ
        else {
            /*Toast.makeText(this, "Bạn đã cấp quyền vị trí rồi đó nha", Toast.LENGTH_SHORT).show();*/
        }
    }

    //Xử lý kết quả trả về khi YÊU CẦU CẤP QUYỀN (hộp thoại)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case TRA_VE_QUYEN_TRUY_CAP_VI_TRI: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Trả về nếu ẤN ĐỒNG Ý khi YÊU CẦU CẤP QUYỀN (khi dùng hộp thoại)
                    Toast.makeText(this, "Bạn ĐỒNG Ý", Toast.LENGTH_SHORT).show();
                    quyentruycapvitri = true;
                }
                else {
                    //Trả về nêu ẤN TỪ CHỐI khi YÊU CẦU CẤP QUYỀN (khi dùng hộp thoại)
                    Toast.makeText(this, "Bạn TỪ CHỐI", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //KIỂM TRA VỊ TRÍ ĐÃ BẬT HAY CHƯA

    //Kiểm tra Map Services
    private boolean checkMapServices(){
        if(checkServices()){
            if(checkBatGPS()){
                return true;
            }
        }
        return false;
    }

    //Kiểm tra GPS ĐÃ BẬT HAY CHƯA
    public boolean checkBatGPS(){
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ) {
            return false;
        }
        return true;
    }

    //Kiểm tra Google Services
    public boolean checkServices(){
        /*int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available == ConnectionResult.SUCCESS){
            //THÀNH CÔNG
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //LỖI CÓ THỂ SỬA TỪ NGƯỜI DÙNG (chả hiểu gì hết)
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            //LỖI
            Toast.makeText(this, "Lỗi Services", Toast.LENGTH_SHORT).show();
        }
        return false;*/
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }
}
