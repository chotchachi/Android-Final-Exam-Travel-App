package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dulich.Object.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    final static String url = "http://192.168.2.28/androidapp/signin.php";
    final static String AVATAR_URL_MACDINH = "http://s3.amazonaws.com/37assets/svn/765-default-avatar.png";
    EditText edt_hovaten, edt_sinhnhat, edt_username, edt_password;
    Button btn_dangky, btn_dangnhap;
    DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        AnhXa();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        //Button đăng ký
        btn_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DangKy();
            }
        });

        //Button đăng nhập sau khi đăng ký thành công
        btn_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Hàm xử lý đăng ký
    public void DangKy() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (edt_hovaten.getText().toString().isEmpty() || edt_username.getText().toString().isEmpty() ||
                        edt_password.getText().toString().isEmpty() || edt_sinhnhat.getText().toString().isEmpty()) {
                    Toast.makeText(SignIn.this, "Bạn chưa điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    if (response.trim().equals("thanh cong")) {
                        Toast.makeText(SignIn.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        UpdateUserTrenFireBase();
                        btn_dangnhap.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(SignIn.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignIn.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hovaten", edt_hovaten.getText().toString().trim());
                params.put("username", edt_username.getText().toString().trim());
                params.put("password", edt_password.getText().toString().trim());
                params.put("birth", edt_sinhnhat.getText().toString().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    //Hàm update thông tin User lên Firebase
    private void UpdateUserTrenFireBase() {
        String username = edt_username.getText().toString();
        String user_id = firebaseDatabase.push().getKey();
        String hovaten = edt_hovaten.getText().toString();
        String sinh_nhat = edt_sinhnhat.getText().toString();
        User newuser = new User(username, user_id, hovaten, AVATAR_URL_MACDINH, sinh_nhat);
        firebaseDatabase.child("User").child(username).child("Info").setValue(newuser);
    }

    public void AnhXa() {
        edt_hovaten = findViewById(R.id.hovaten);
        edt_sinhnhat = findViewById(R.id.birth);
        edt_username = findViewById(R.id.username);
        edt_password = findViewById(R.id.password);
        btn_dangky = findViewById(R.id.btn_dangky);
        btn_dangnhap = findViewById(R.id.btn_dangnhap);
        btn_dangnhap.setVisibility(View.GONE); //Ẩn button đăng nhập
    }
}
