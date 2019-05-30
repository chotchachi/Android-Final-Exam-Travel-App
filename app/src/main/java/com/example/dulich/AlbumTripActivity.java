package com.example.dulich;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulich.Adapter.AlbumImageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AlbumTripActivity extends AppCompatActivity {
    String tripid;
    Toolbar toolbar;
    TextView btn_themanh, no_image;
    GridView gridView;
    ArrayList<String> arrayListAnh;
    AlbumImageAdapter adapter;
    Dialog dialog;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference imageRef;
    StorageTask storageTask;
    BoNhoSharedPreferences sharedPreferences;
    String user_login;
    Set<String> imageIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_trip);
        AnhXa();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://dulich-ae09a.appspot.com/");
        arrayListAnh = new ArrayList<>();
        adapter = new AlbumImageAdapter(arrayListAnh, this);
        gridView.setAdapter(adapter);

        sharedPreferences = new BoNhoSharedPreferences();
        user_login = sharedPreferences.TraSP("usernamedaluu", this);

        Intent i = getIntent();
        tripid = i.getStringExtra("tripid");

        ThemAnh();

        LayAnh();
    }

    //Hàm lấy ảnh hiển thị trên Gridview
    private void LayAnh() {
        databaseReference.child("User").child(user_login).child("Trip").child(tripid).child("album").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    //Dùng set<> để kiểm tra image đã lấy về chưa, nếu chưa thì gán vào set<>. Nếu không image sẽ bị lặp đi lặp lại
                    if (!imageIds.contains(postSnapshot.getValue().toString())){
                        imageIds.add(postSnapshot.getValue().toString());
                        arrayListAnh.add(postSnapshot.getValue().toString());
                        adapter.notifyDataSetChanged();                    }
                }
                if (!arrayListAnh.isEmpty()){
                    no_image.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Hàm thêm ảnh
    private void ThemAnh() {
        btn_themanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Chọn ảnh"), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if(data.getData()!=null){
                Uri image_uri = data.getData();
                dialog = new Dialog(AlbumTripActivity.this);
                dialog.setContentView(R.layout.dialog_loading);
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                UpLoad(image_uri);
            } else {
                if (data.getClipData() != null) {
                    ClipData clip_data = data.getClipData();
                    dialog = new Dialog(AlbumTripActivity.this);
                    dialog.setContentView(R.layout.dialog_loading);
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                    for (int i = 0; i < clip_data.getItemCount(); i++) {
                        ClipData.Item item = clip_data.getItemAt(i);
                        Uri image_uri = item.getUri();
                        UpLoad(image_uri);
                    }
                }
            }
        } else {
            Toast.makeText(this, "Bạn chưa chọn ảnh nào", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //Hàm upload
    private void UpLoad(Uri image_uri) {
        //Hàm up ảnh lên Firestorage
        final long ten = System.currentTimeMillis();
        //Hàm tạo tên file trên Storage và upload lên Storage
        imageRef = storageRef.child("albumtrip/"+user_login+"/"+tripid+"/"+ten+".jpg");
        //Lấy url ảnh để up lên Firebase Database
        storageTask = imageRef.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Nếu lấy về thành công thì up lên
                storageRef.child("albumtrip/"+user_login+"/"+tripid+"/"+ten+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String anh_id = databaseReference.push().getKey();
                        databaseReference.child("User").child(user_login).child("Trip").child(tripid).child("album").child(anh_id).setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AlbumTripActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AlbumTripActivity.this, "Lỗi lấy Url trên FireStorage", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolbar);
        no_image = findViewById(R.id.no_image);
        btn_themanh = toolbar.findViewById(R.id.btn_themanh);
        gridView = findViewById(R.id.gridview_album);
    }
}
