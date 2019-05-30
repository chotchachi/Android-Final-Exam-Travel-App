package com.example.dulich;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulich.Object.HoatDong;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.List;

public class NewHoatDongActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView done;
    RelativeLayout up_image, tag_friend;
    String user_login;
    EditText edt_status;
    Dialog dialog;
    //Firebase
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference imageRef;
    StorageTask storageTask;
    //Tag bạn bè
    ListView list_banbe_tag;
    ArrayList<String> banBeArrayList;
    ArrayAdapter<String> adapter;
    List<String> banbedachon = new ArrayList<>();
    //Uri
    ArrayList<Uri> arrayUri = new ArrayList<>();
    List<String> list_path_uri = new ArrayList<>();
    List<String> list_url_anh_firestorage = new ArrayList<>();
    //
    String imageEncoded;
    List<String> imagesEncodedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_hoat_dong);
        AnhXa();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://dulich-ae09a.appspot.com/");

        LayInfoUserLogin();

        //Button đăng
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpLoad();
            }
        });

        //Button chọn ảnh
        up_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Chọn ảnh"), 1);
            }
        });

        //Button tag bạn bè
        tag_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagFriend();
            }
        });
    }

    private void LayInfoUserLogin() {
        BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
        user_login = sharedPreferences.TraSP("usernamedaluu", this);
    }

    //Hàm chọn bạn bè
    private void TagFriend() {
        banBeArrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, banBeArrayList);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tag_banbe);
        list_banbe_tag = dialog.findViewById(R.id.list_tag_banbe);
        list_banbe_tag.setAdapter(adapter);
        LayDanhSachBanBe();
        list_banbe_tag.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_banbe_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                boolean check = checkedTextView.isChecked();
                String ten_ban_be_chon = banBeArrayList.get(position);
                if (check==true){
                    if (!banbedachon.contains(ten_ban_be_chon)){
                        banbedachon.add(ten_ban_be_chon);
                    }
                } else {
                    banbedachon.remove(ten_ban_be_chon);
                }
            }
        });
        dialog.show();
    }

    //Hàm lấy danh sách bạn bè của user
    private void LayDanhSachBanBe() {
        databaseReference.child("User").child(user_login).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String user_banbe = String.valueOf(postSnapshot.getValue());
                    banBeArrayList.add(user_banbe);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Hàm upload
    private void UpLoad() {
        dialog = new Dialog(NewHoatDongActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        final String id = firestore.collection("HoatDong").document().getId();
        String status = edt_status.getText().toString();
        final HoatDong hoatDong = new HoatDong();
        hoatDong.setId(id);
        hoatDong.setUsername(user_login);
        hoatDong.setStatus(status);
        hoatDong.setTag_friends(banbedachon);

        final List<String> ten_files = new ArrayList<>();

        //Dùng vòng lặp lấy các uri trong list rồi upload lên FireStorage
        for (int i = 0; i<arrayUri.size(); i++){
            //Hàm up ảnh lên Firestorage
            long ten = System.currentTimeMillis();
            ten_files.add(ten+".jpg");
            //Hàm tạo tên file trên Storage và upload lên Storage
            imageRef = storageRef.child("hoatdong/"+user_login+"/"+ten+".jpg");
            storageTask = imageRef.putFile(arrayUri.get(i));
        }

        //Hàm upload hoạt động lên Firestore
        //Nếu list ten_files trống thì chỉ up những thứ khác vì list_url_anh_firestorage để trống sẽ bị crash
        //Nếu list ten_files có thì up đầy đủ
        if (ten_files.isEmpty()){
            firestore.collection("HoatDong").document(id).set(hoatDong).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(NewHoatDongActivity.this, "Đăng thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            //Hàm download ảnh từ FireStorage về bằng các tên file trong mảng ten_files để lấy url ảnh lưu trên FireStotage
            //rồi dùng url đó lưu vào list_url_anh_firestorage
            storageTask.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    for (int i = 0; i<ten_files.size(); i++){
                        storageRef.child("hoatdong/"+user_login+"/"+ten_files.get(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                list_url_anh_firestorage.add(uri.toString());
                                //Cho nó vào trong vòng lặp luôn
                                //Ko nghĩ ra được cách khác
                                //Vì nó gán bằng id nên ko sợ spam
                                hoatDong.setImages(list_url_anh_firestorage);
                                firestore.collection("HoatDong").document(id).set(hoatDong).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(NewHoatDongActivity.this, "Đăng thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(NewHoatDongActivity.this, "Lỗi lấy Url trên FireStorage", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolbar);
        done = toolbar.findViewById(R.id.done);
        up_image = findViewById(R.id.up_image);
        tag_friend = findViewById(R.id.tag_friend);
        edt_status = findViewById(R.id.edt_stt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<>();
                if(data.getData()!=null){
                    Uri image_uri = data.getData();
                    arrayUri.add(image_uri);

                    /*Cursor cursor = getContentResolver().query(image_uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    Log.d("LOG_TAG", imageEncoded+"");
                    cursor.close();*/
                } else {
                    if (data.getClipData() != null) {
                        ClipData clip_data = data.getClipData();
                        for (int i = 0; i < clip_data.getItemCount(); i++) {
                            ClipData.Item item = clip_data.getItemAt(i);
                            Uri image_uri = item.getUri();
                            arrayUri.add(image_uri);

                            /*Cursor cursor = getContentResolver().query(image_uri, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();*/

                        }
                    }
                }
            } else {
                Toast.makeText(this, "Bạn chưa chọn ảnh nào", Toast.LENGTH_LONG).show();
            }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
