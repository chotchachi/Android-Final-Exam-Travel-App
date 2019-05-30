package com.example.dulich;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dulich.Adapter.KhachSanAdapter;
import com.example.dulich.Object.KhachSan;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class KhachSanActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView list;
    ArrayList<KhachSan> khachSanArrayList;
    KhachSanAdapter adapter;
    String hotel_anh, hotel_ten, hotel_diachi, hotel_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khach_san);
        AnhXa();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        khachSanArrayList = new ArrayList<>();
        adapter = new KhachSanAdapter(khachSanArrayList, this);
        list.setAdapter(adapter);

        Intent i = getIntent();
        String place_key = i.getStringExtra("place_key");
        String place_name = i.getStringExtra("place_name");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Danh sách khách sạn ở "+place_name);
        ChuyenDoiPlaceKey(place_key);

        //Sự kiện click chọn khách sạn
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = khachSanArrayList.get(position).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }
        });
    }

    private void ChuyenDoiPlaceKey(String place_key) {
        String place_key_doi = null;
        switch (place_key){
            case "da_nang":
                place_key_doi = "da-nang";
                break;
            case "dong_hoi":
                place_key_doi = "quang-binh";
                break;
            case "thua_thien_hue":
                place_key_doi = "hue";
                break;
            case "ha_noi":
                place_key_doi = "ha_noi";
                break;
            case "ho_chi_minh":
                place_key_doi = "tp-ho-chi-minh";
                break;
            case "phu_quoc":
                place_key_doi = "phu-quoc";
                break;
            case "vung_tau":
                place_key_doi = "vung-tau";
                break;
            case "sa_pa":
                place_key_doi = "sapa";
                break;
            case "ha-long":
                place_key_doi = "ha-long";
                break;
            case "hoi-an":
                place_key_doi = "hoi-an";
                break;
            case "phan_thiet":
                place_key_doi = "phan-thiet";
                break;
            case "da-lat":
                place_key_doi = "da-lat";
                break;
        }
        GetDataKhachSan(place_key_doi);
    }

    //Hàm get data bằng Jsoup
    private void GetDataKhachSan(String place_key) {
        //Jsoup get dữ liệu
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.hotel84.com/"+place_key+"/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Elements elements = document.getElementsByClass("row_hotel_list");
                for (Element element : elements){
                    Element elementten = element.getElementsByTag("a").first();
                    hotel_ten = elementten.attr("title");
                    Element elementanh = element.getElementsByTag("img").first();
                    hotel_anh = elementanh.attr("src");
                    Element elementdiaachi = element.getElementsByClass("div_space").get(2);
                    hotel_diachi = elementdiaachi.text();
                    Element elementlink = element.getElementsByTag("a").first();
                    hotel_link = elementlink.attr("href");
                    KhachSan khachSan = new KhachSan("http://www.hotel84.com"+hotel_anh, hotel_ten, hotel_diachi, hotel_link);
                    khachSanArrayList.add(khachSan);
                    adapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(KhachSanActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolbar);
        list = findViewById(R.id.list_hotel);
    }
}
