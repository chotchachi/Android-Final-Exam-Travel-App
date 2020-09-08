package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulich.Object.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    TextView tvAlert;
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

        init();

        btn_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignIn.class);
                startActivity(i);
            }
        });

        btn_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }


    public static boolean getSpecialCharacterCount(String s) {
        if (s == null || s.trim().isEmpty()) {
            System.out.println("Incorrect format of string");
            return false;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(s);
        // boolean b = m.matches();
        boolean b = m.find();
        return b;
    }


    private void Login() {
         if (getSpecialCharacterCount(edt_user.getText().toString())) {
            tvAlert.setVisibility(View.VISIBLE);
            tvAlert.setText("Username has constain Special characters");
        }
        else if (edt_user.getText().toString().isEmpty()) {
            tvAlert.setVisibility(View.VISIBLE);
            tvAlert.setText("You must not empty Username");
        } else if (edt_pass.getText().toString().isEmpty()){
            tvAlert.setVisibility(View.VISIBLE);
            tvAlert.setText("You must not empty Password");
        } else if (edt_pass.getText().toString().isEmpty() && edt_user.getText().toString().isEmpty()) {
            tvAlert.setVisibility(View.VISIBLE);
            tvAlert.setText("You must not empty Username and Password");
        } else if (edt_user.getText().toString().length() < 6) {
            tvAlert.setVisibility(View.VISIBLE);
            tvAlert.setText("Username must not be less than 6 characters");
        } else if (edt_pass.getText().toString().length() < 6) {
            tvAlert.setVisibility(View.VISIBLE);
            tvAlert.setText("Password must not be less than 6 characters");
        } else {
            getFirebaseUserInfo(edt_user.getText().toString(), edt_pass.getText().toString(), new OnLoginListener() {
                @Override
                public void onLoginCompleted(boolean success) {
                    if (!success) {
                        Toast.makeText(Login.this, "Incorrect information", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (edt_user.getText().toString().isEmpty() || edt_pass.getText().toString().isEmpty()) {
//                    Toast.makeText(Login.this, "Bạn chưa điền tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        String success = jsonObject.getString("success");
//                        String hovaten = jsonObject.getString("hovaten");
//                        String username = jsonObject.getString("username");
//                        if (success.equals("1")) {
//                            Toast.makeText(Login.this, "Chàc mừng "+hovaten, Toast.LENGTH_LONG).show();
//                            getFirebaseUserInfo(username);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast.makeText(Login.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                usernamedanhap = edt_user.getText().toString().trim();
//                passworddanhap = edt_pass.getText().toString().trim();
//                params.put("username", usernamedanhap);
//                params.put("password", passworddanhap);
//                return params;
//            }
//        };
//        requestQueue.add(stringRequest);
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
    private void getFirebaseUserInfo(String username, final String pass, final OnLoginListener listener) {
        databaseReference.child("User").child(username).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Có thể lấy theo cách này
                //avatar_url = String.valueOf(dataSnapshot.child("avatar_url").getValue());
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String username = user.getUsername();
                    String user_id = user.getUser_id();
                    String hovaten = user.getHovaten();
                    String birth = user.getSinh_nhat();
                    String avatar_url = user.getAvatar_url();

                    if (pass.equals(user.getPassword())) {
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
                    } else {
                        listener.onLoginCompleted(false);
                    }
                } else {
                    listener.onLoginCompleted(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onLoginCompleted(false);
            }
        });
    }


    @Override
    //Pt trả về
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void init() {
        btn_dangky = findViewById(R.id.btn_dangky);
        btn_dangnhap = findViewById(R.id.btn_dangnhap);
        loginButton = findViewById(R.id.login_button);
        edt_user = findViewById(R.id.username);
        edt_pass = findViewById(R.id.password);
        tvAlert = findViewById(R.id.tv_alert);
    }

    private interface OnLoginListener {
        void onLoginCompleted(boolean success);
    }
}
