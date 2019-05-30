package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    final static String url = "http://192.168.2.28/androidapp/login.php";
    LoginButton loginButton;
    Button btn_dangnhap, btn_dangky;
    EditText edt_user, edt_pass;
    String usernamedanhap, passworddanhap;
    CallbackManager callbackManager;
    BoNhoSharedPreferences sharedPreferences = new BoNhoSharedPreferences();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        AnhXa();

        LoginFB();

        //Button đăng ký
        btn_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignIn.class);
                startActivity(i);
            }
        });

        //Button đăng nhập
        btn_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    //Đăng nhập cách thường - dùng thư viện Volley
    private void Login() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (edt_user.getText().toString().isEmpty() || edt_pass.getText().toString().isEmpty()) {
                    Toast.makeText(Login.this, "Bạn chưa điền tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        String hovaten = jsonObject.getString("hovaten");
                        String username = jsonObject.getString("username");
                        if (success.equals("1")) {
                            Toast.makeText(Login.this, "Chàc mừng "+hovaten, Toast.LENGTH_LONG).show();
                            LayInfoUserFirebase(username);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Login.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                usernamedanhap = edt_user.getText().toString().trim();
                passworddanhap = edt_pass.getText().toString().trim();
                params.put("username", usernamedanhap);
                params.put("password", passworddanhap);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    //Đăng nhập bằng Facebook, lấy về access token
    private void LoginFB() {
        loginButton.setReadPermissions("email",  "public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                layDataFB(accessToken);
            }
            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    //Get Data Facebook nhờ access token
    private void layDataFB(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    String email = object.getString("email");
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    //Lưu lại giữ trạng thái login
                    sharedPreferences.GanSP("usernamedaluu", name, Login.this);
                    sharedPreferences.GanSP("avatardaluu", image, Login.this);
                    sharedPreferences.GanSP("hovatendaluu", email, Login.this);
                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    //Hàm lấy info user trên firebase và chuyển qua MainActivity
    private void LayInfoUserFirebase(String username) {
        databaseReference.child("User").child(username).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Có thể lấy theo cách này
                //avatar_url = String.valueOf(dataSnapshot.child("avatar_url").getValue());
                User user = dataSnapshot.getValue(User.class);
                String username = user.getUsername();
                String user_id = user.getUser_id();
                String hovaten = user.getHovaten();
                String birth = user.getSinh_nhat();
                String avatar_url = user.getAvatar_url();

                //Lưu vào bộ nhớ SharedPreferences
                sharedPreferences.GanSP("usernamedaluu", username, Login.this);
                sharedPreferences.GanSP("useriddaluu", user_id, Login.this);
                sharedPreferences.GanSP("hovatendaluu", hovaten, Login.this);
                sharedPreferences.GanSP("birthdaluu", birth, Login.this);
                sharedPreferences.GanSP("avatardaluu", avatar_url, Login.this);

                //Chuyển Activity
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    //Pt trả về
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void AnhXa() {
        btn_dangky = findViewById(R.id.btn_dangky);
        btn_dangnhap = findViewById(R.id.btn_dangnhap);
        loginButton = findViewById(R.id.login_button);
        edt_user = findViewById(R.id.username);
        edt_pass = findViewById(R.id.password);
    }
}
