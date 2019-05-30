package com.example.dulich.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulich.BoNhoSharedPreferences;
import com.example.dulich.Object.BinhLuan;
import com.example.dulich.Object.HoatDong;
import com.example.dulich.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HoatDongAdapter extends RecyclerView.Adapter<HoatDongAdapter.ViewHolder> {
    private static final int RESIZE_RONG = 1000;
    private static final int RESIZE_CAO = 1000;
    String user_login;
    ArrayList<HoatDong> arrayListHoatDong;
    Activity activity;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String avatar_url;
    BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();

    public HoatDongAdapter(ArrayList<HoatDong> arrayListHoatDong, Activity activity) {
        this.arrayListHoatDong = arrayListHoatDong;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_hoatdong, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        /********************************************** HIỂN THỊ DATA CHO CÁC VIEW TRONG ITEM HOẠT ĐỘNG ******************************************/
        //Lấy user đang login
        user_login = sharedPreferences.TraSP("usernamedaluu", activity);

        //Set data cho các view trong hoạt động
        viewHolder.username.setText(arrayListHoatDong.get(i).getUsername());
        viewHolder.thoigian.setText(arrayListHoatDong.get(i).getTimestamp().toString());
        viewHolder.status.setText(arrayListHoatDong.get(i).getStatus());

        //Set vị trí
        if (arrayListHoatDong.get(i).getVitri() != null){
            Geocoder gcd = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(arrayListHoatDong.get(i).getVitri().getLatitude(), arrayListHoatDong.get(i).getVitri().getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                String locality = addresses.get(0).getLocality();
                viewHolder.vitri.setText(locality);
            }
        } else {
            viewHolder.vitri.setText("Không có vị trí");
        }

        //Set data cho imageview, nếu ko có ảnh thì ẩn imageview đi
        if (arrayListHoatDong.get(i).getImages().isEmpty()){
            viewHolder.image.setVisibility(View.GONE);
        } else {
            Picasso.with(activity).load(arrayListHoatDong.get(i).getImages().get(0)).resize(RESIZE_RONG, RESIZE_CAO).centerCrop().into(viewHolder.image);
        }

        //Gán avatar user cho item hoạt động
        databaseReference.child("User").child(arrayListHoatDong.get(i).getUsername()).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                avatar_url = dataSnapshot.child("avatar_url").getValue().toString();
                Picasso.with(activity).load(avatar_url).into(viewHolder.avatar);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, "Lỗi khi lấy avatar user", Toast.LENGTH_SHORT).show();
            }
        });

        /************************************************ XỬ LÝ HOẠT ĐỘNG CỦA CÁC BUTTON *********************************************************/
        //Button like
        //Kiểm tra để set icon cho button like
        //Nếu likes của item đó có chứa user login thì set background cho nó
        if (arrayListHoatDong.get(i).getLikes() != null){
            if (arrayListHoatDong.get(i).getLikes().contains(user_login)){
                viewHolder.btn_like.setBackgroundResource(R.drawable.like);
            } else {
                viewHolder.btn_like.setBackgroundResource(R.drawable.unlike);
            }
        }
        viewHolder.btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Đầu tiên kiểm tra xem likes của item có rỗng hay ko
                //Nếu rỗng thì gắn like cho user login
                //Nếu ko rỗng thì tiếp tục kiểm tra likes đó có chứa user_login hay ko
                //Nếu có thì xóa đi
                //Nếu ko thì thêm vào
                if (arrayListHoatDong.get(i).getLikes() != null){
                    if (arrayListHoatDong.get(i).getLikes().contains(user_login)){
                        firestore.collection("HoatDong")
                                .document(arrayListHoatDong.get(i).getId())
                                .update("likes", FieldValue.arrayRemove(user_login))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                viewHolder.btn_like.setBackgroundResource(R.drawable.unlike);
                            }
                        });
                    } else {
                        firestore.collection("HoatDong")
                                .document(arrayListHoatDong.get(i).getId())
                                .update("likes", FieldValue.arrayUnion(user_login))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        viewHolder.btn_like.setBackgroundResource(R.drawable.like);
                                    }
                                });
                    }
                } else {
                    firestore.collection("HoatDong")
                            .document(arrayListHoatDong.get(i).getId())
                            .update("likes", FieldValue.arrayUnion(user_login))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            viewHolder.btn_like.setBackgroundResource(R.drawable.like);
                        }
                    });
                }
            }
        });

        //Button comment
        viewHolder.btn_cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.dialog_binhluan);

                //Xử lí phần hiển thị bình luận
                ListView listView = dialog.findViewById(R.id.list_cm);
                final ArrayList<BinhLuan> arrayListBinhLuan = new ArrayList<>();
                final BinhLuanAdapter adapter = new BinhLuanAdapter(arrayListBinhLuan, activity);
                listView.setAdapter(adapter);
                firestore.collection("HoatDong").document(arrayListHoatDong.get(i).getId()).collection("BinhLuan").orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BinhLuan binhLuan = document.toObject(BinhLuan.class);
                                arrayListBinhLuan.add(binhLuan);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(activity, "Lỗi khi lấy bình luận", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Xử lý phần đăng bình luận
                final EditText edt_cm = dialog.findViewById(R.id.edt_cm);
                Button btn_cm = dialog.findViewById(R.id.btn_dang_cm);
                btn_cm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = edt_cm.getText().toString();
                        BinhLuan binhLuan = new BinhLuan();
                        binhLuan.setBinhluan(comment);
                        binhLuan.setUsername(user_login);
                        firestore.collection("HoatDong").document(arrayListHoatDong.get(i).getId()).collection("BinhLuan").document().set(binhLuan).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListHoatDong.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView avatar;
        TextView username, status, vitri, thoigian;
        ImageView image;
        Button btn_like, btn_cm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_user);
            username = itemView.findViewById(R.id.ten_user);
            vitri = itemView.findViewById(R.id.location);
            thoigian = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image_hoatdong);
            btn_like = itemView.findViewById(R.id.btn_like);
            btn_cm = itemView.findViewById(R.id.btn_cm);
        }
    }
}
