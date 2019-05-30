package com.example.dulich.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulich.Adapter.TripAdapter;
import com.example.dulich.AddTripActivity;
import com.example.dulich.BoNhoSharedPreferences;
import com.example.dulich.ImagePickerActivity;
import com.example.dulich.Login;
import com.example.dulich.Object.Trip;
import com.example.dulich.R;
import com.example.dulich.TripScreen;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Fragment_MyTrip extends Fragment {
    CircleImageView avatar;
    TextView tv_hovaten, tv_username, tv_sinhnhat;
    String usernamelogin, hovatenlogin, sinhnhatdaluu;
    FloatingActionButton add_trip;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    ListView trip_list;
    ArrayList<Trip> arrayListTrip;
    TripAdapter arrayAdapter;
    RelativeLayout relativeLayout;
    LinearLayout user_panel;
    String avatar_url;
    View view;
    private static final int REQUEST_IMAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment__my_trip, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://dulich-ae09a.appspot.com/");

        AnhXa();
        CheckPermission();
        GanInfoUser();

        arrayListTrip = new ArrayList<>();
        arrayAdapter = new TripAdapter(arrayListTrip, getActivity());
        trip_list.setAdapter(arrayAdapter);

        NhanDuLieuTripFirebase();

        //Button Tạo chuyến đi
        add_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddTripActivity.class);
                startActivity(i);
            }
        });

        //Sự kiện click User panel
        user_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickUserPanel();
            }
        });

        //Sự kiện click item trip
        trip_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClickTripItem(position);
            }
        });

        //Sự kiện long click item trip
        trip_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               LongClickTripItem(position);
               return false;
            }
        });

        return view;
    }

    //Hàm gán info user
    private void GanInfoUser(){
        BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
        usernamelogin = sharedPreferences.TraSP("usernamedaluu", getActivity());
        hovatenlogin = sharedPreferences.TraSP("hovatendaluu", getActivity());
        sinhnhatdaluu = sharedPreferences.TraSP("birthdaluu", getActivity());
        avatar_url = sharedPreferences.TraSP("avatardaluu", getActivity());
        tv_username.setText(usernamelogin);
        tv_sinhnhat.setText(sinhnhatdaluu);
        tv_hovaten.setText(hovatenlogin);
        if (avatar_url.isEmpty()){
            Picasso.with(getActivity()).load("http://s3.amazonaws.com/37assets/svn/765-default-avatar.png").into(avatar);
        } else {
            Picasso.with(getActivity()).load(avatar_url).into(avatar);
        }
    }

    //Hàm nhận list trip từ Firebase
    private void NhanDuLieuTripFirebase() {
        databaseReference.child("User").child(usernamelogin).child("Trip").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                arrayListTrip.add(trip);
                arrayAdapter.notifyDataSetChanged();
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
    }

    //Hàm click Trip Item
    private void ClickTripItem(int position) {
        String tripid = arrayListTrip.get(position).getTripID();
        String tripname = arrayListTrip.get(position).getTenTrip();
        String place_name = arrayListTrip.get(position).getDiaDiem().getTen();
        String place_key = arrayListTrip.get(position).getDiaDiem().getKey();
        Intent i = new Intent(getActivity(), TripScreen.class);
        Bundle bundle = new Bundle();
        bundle.putString("tripid", tripid);
        bundle.putString("tripname", tripname);
        bundle.putString("place_name", place_name);
        bundle.putString("place_key", place_key);
        i.putExtras(bundle);
        startActivity(i);
    }

    //Hàm long click Trip Item
    private void LongClickTripItem(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xóa chuyến đi của bạn");
        builder.setMessage("Bạn có chắc chắn muốn xóa "+arrayListTrip.get(position).getTenTrip()+" không ?");
        builder.setCancelable(true);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("User").child(usernamelogin).child("Trip").child(arrayListTrip.get(position).getTripID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Đã xóa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Hàm click UserPanel
    private void ClickUserPanel() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_user_panel);
        CircleImageView avatar = dialog.findViewById(R.id.avatar);
        if (avatar_url.isEmpty()){
            Picasso.with(getActivity()).load("http://s3.amazonaws.com/37assets/svn/765-default-avatar.png").into(avatar);
        } else {
            Picasso.with(getActivity()).load(avatar_url).into(avatar);
        }
        Button dangxuat = dialog.findViewById(R.id.dangxuat);

        //Hàm click button đăng xuất
        dangxuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
                sharedPreferences.XoaSP(getActivity());   //Logout thường
                LoginManager.getInstance().logOut(); //Logout Facebook
                Intent i = new Intent(getActivity(), Login.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        //Hàm click vào avatar
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogChonHanhDong();
            }
        });

        dialog.show();
    }

    //Dialog chọn hành dộng
    private void ShowDialogChonHanhDong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] hanhdong = {"Chụp ảnh","Chọn ảnh"};
        builder.setItems(hanhdong, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ChupAnh();
                        break;
                    case 1:
                        ChonAnh();
                        break;
                }
            }
        });
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
    private void ChupAnh() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.REQUEST_LUA_CHON, ImagePickerActivity.REQUEST_CHUP_ANH);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    private void ChonAnh() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.REQUEST_LUA_CHON, ImagePickerActivity.REQUEST_CHON_ANH);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    //Nhận đường dẫn image và Upload lên Firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                final Uri uri = data.getParcelableExtra("path");
                //Hàm tải ảnh lên FireStorage
                StorageReference avatarRef = storageRef.child("avatar/"+usernamelogin+".jpg");
                StorageTask uploadTask = avatarRef.putFile(uri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Lỗi Upload avatar lên FireStorage", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Gắn avatar mới sau khi chọn
                        UpdateAvatar(uri.toString());
                        //Cập nhật avatar url trên firebase
                        UpdateAvatarURLFireBase();
                    }
                });
            }
        }
    }

    //Gán avatar sau khi chọn
    private void UpdateAvatar(String url) {
        Picasso.with(getActivity()).load(url).into(avatar);
    }

    //Lấy avatar url trên firestorage và update trường avatar trong firestore
    private void UpdateAvatarURLFireBase() {
        storageRef.child("avatar/"+usernamelogin+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                databaseReference.child("User").child(usernamelogin).child("Info").child("avatar_url").setValue(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Lỗi lấy Url avatar trên FireStorage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Hàm check Permission
    private void CheckPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                }
                if (report.isAnyPermissionPermanentlyDenied()) {
                }
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
        .check();
    }

    private void AnhXa(){
        user_panel = view.findViewById(R.id.user_panel);
        avatar = view.findViewById(R.id.avatar);
        tv_hovaten = view.findViewById(R.id.hovaten);
        tv_username = view.findViewById(R.id.username);
        tv_sinhnhat = view.findViewById(R.id.sinhnhat);
        add_trip = view.findViewById(R.id.add_trip);
        trip_list = view.findViewById(R.id.list_trip);
        relativeLayout = view.findViewById(R.id.rela_layout);
    }
}
