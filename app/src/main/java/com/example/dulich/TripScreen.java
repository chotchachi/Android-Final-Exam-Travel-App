package com.example.dulich;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dulich.Adapter.Thoitiet6DaysAdapter;
import com.example.dulich.Object.Thoitiet6Days;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TripScreen extends AppCompatActivity {
    ImageView icon_thoitiet;
    TextView tv_city, tv_temp, tv_status;
    String tripid, tripname;
    String place_name, place_key;
    LinearLayout thoitiet_layout;
    GridView gridView;
    ArrayList<Thoitiet6Days> arrayList;
    Thoitiet6DaysAdapter adapter;
    Button btn_hotel, btn_album, btn_phuongtien, btn_food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_screen);
        arrayList = new ArrayList<>();
        adapter = new Thoitiet6DaysAdapter(arrayList, this);

        AnhXa();

        GetTripInfo();

        //Click thời tiết layout
        thoitiet_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetMoreDataThoiTiet();
            }
        });

        //Button hotel
        btn_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripScreen.this, KhachSanActivity.class);
                i.putExtra("place_key", place_key);
                i.putExtra("place_name", place_name);
                startActivity(i);
            }
        });

        //Button album
        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripScreen.this, AlbumTripActivity.class);
                i.putExtra("tripid", tripid);
                startActivity(i);
            }
        });

        //Button food
        btn_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripScreen.this, FoodActivity.class);
                i.putExtra("place_key", place_key);
                startActivity(i);
            }
        });

        //Button phương tiện
        btn_phuongtien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(TripScreen.this);
                dialog.setContentView(R.layout.dialog_chon_phuongtien);
                LinearLayout bus = dialog.findViewById(R.id.btn_2);
                LinearLayout plane = dialog.findViewById(R.id.btn_1);
                LinearLayout train = dialog.findViewById(R.id.btn_3);
                bus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(TripScreen.this, PhuongTienActivity.class);
                        i.putExtra("phuongtien", "bus");
                        startActivity(i);
                    }
                });
                plane.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(TripScreen.this, PhuongTienActivity.class);
                        i.putExtra("phuongtien", "plane");
                        startActivity(i);
                    }
                });
                train.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(TripScreen.this, PhuongTienActivity.class);
                        i.putExtra("phuongtien", "train");
                        startActivity(i);
                    }
                });
                dialog.show();
            }
        });
    }

    //Hàm load data thời tiết 6 ngày sau
    private void GetMoreDataThoiTiet() {
        Dialog dialog = new Dialog(TripScreen.this);
        dialog.setContentView(R.layout.dialog_thoitiet);
        gridView = dialog.findViewById(R.id.grid_6days);
        gridView.setAdapter(adapter);
        dialog.show();
    }

    //Hàm get thông tin Trip bằng Intent
    private void GetTripInfo() {
        Bundle bundle = getIntent().getExtras();
        tripid = bundle.getString("tripid");
        tripname = bundle.getString("tripname");
        place_name = bundle.getString("place_name");
        place_key = bundle.getString("place_key");
        GetDataThoiTiet();
    }

    //Hàm load data thời tiết hôm nay
    private void GetDataThoiTiet() {
        String url = "http://api.worldweatheronline.com/premium/v1/weather.ashx?key=4b38219d1f3d4aaa821102011191105&q="+place_key+"&num_of_days=6&format=json";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectData = jsonObject.getJSONObject( "data" );
                    JSONArray jsonArrayRequest = jsonObjectData.getJSONArray( "request" );
                    JSONObject jsonObjectRequest = jsonArrayRequest.getJSONObject( 0 );
                    String diadiem = jsonObjectRequest.getString("query");
                    tv_city.setText(diadiem);

                    JSONArray jsonArray_current_condition = jsonObjectData.getJSONArray( "current_condition" );
                    JSONObject jsonObject1 = jsonArray_current_condition.getJSONObject(0);
                    int temp = jsonObject1.getInt("temp_C");
                    tv_temp.setText(temp+"°C");
                    ThongBaoThoiTiet(temp);

                    JSONArray jsonArrayStatus = jsonObject1.getJSONArray("weatherDesc");
                    JSONObject jsonObject2 = jsonArrayStatus.getJSONObject(0);
                    String status = jsonObject2.getString("value");
                    tv_status.setText(status);

                    JSONArray jsonArray6Days = jsonObjectData.getJSONArray( "weather" );
                    for (int i=0;i<jsonArray6Days.length();i++){
                        JSONObject jsonObject3 = jsonArray6Days.getJSONObject(i);
                        String day = jsonObject3.getString("date");
                        String temp2 = jsonObject3.getString("maxtempC");
                        Thoitiet6Days thoitiet6Days = new Thoitiet6Days(temp2, day);
                        arrayList.add(thoitiet6Days);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TripScreen.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    //Hàm thông báo dialog cảnh báo thời tiết và set background thời tiết
    private void ThongBaoThoiTiet(int temp) {
        if (temp >= 25){
            Picasso.with(this)
                    .load(R.drawable.sunny)
                    .into(icon_thoitiet);
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_tren30do);
            dialog.show();
        } else if (temp <= 25){
            Picasso.with(this)
                    .load(R.drawable.heavy)
                    .into(icon_thoitiet);
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_duoi20do);
            dialog.show();
        }
    }

    private void AnhXa() {
        icon_thoitiet = findViewById(R.id.icon_thoitiet);
        tv_city = findViewById(R.id.tv_city);
        tv_status = findViewById(R.id.tv_status);
        tv_temp = findViewById(R.id.tv_temp);
        thoitiet_layout = findViewById(R.id.thoitiet_layout);
        btn_hotel = findViewById(R.id.btn_1);
        btn_album = findViewById(R.id.btn_2);
        btn_phuongtien = findViewById(R.id.btn_4);
        btn_food = findViewById(R.id.btn_3);
    }
}
