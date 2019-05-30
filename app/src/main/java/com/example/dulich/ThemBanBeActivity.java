package com.example.dulich;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dulich.Object.BanBe;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ThemBanBeActivity extends AppCompatActivity {
    EditText edt_them;
    Button btn_them;
    String username_banbe, id_banbe, username;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thembanbe);

        edt_them = findViewById(R.id.edt_them);
        btn_them = findViewById(R.id.btn_them);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //
        BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
        username = sharedPreferences.TraSP("usernamedaluu", ThemBanBeActivity.this);

        btn_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_banbe = edt_them.getText().toString();
                id_banbe = databaseReference.push().getKey();
                BanBe banBe = new BanBe(username_banbe, id_banbe);
                databaseReference.child("User").child(username).child("Friends").child(id_banbe).setValue(banBe, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(ThemBanBeActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
