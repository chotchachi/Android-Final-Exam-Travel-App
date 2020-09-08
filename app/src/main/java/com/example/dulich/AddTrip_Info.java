package com.example.dulich;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTrip_Info extends AppCompatActivity {
    EditText edt_date;
    FloatingActionButton btn_next;
    DatabaseReference databaseReference;
    String trip_name,trip_date;
    String place_name, place_img, place_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip__info);
        edt_date = findViewById(R.id.edt_date);
        btn_next = findViewById(R.id.btn_next);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Button chọn ngày
        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChonNgay();
            }
        });

        //Lấy data intent
        Intent i = getIntent();
        trip_name = i.getStringExtra("tripname");
        place_name = i.getStringExtra("place_name");
        place_img = i.getStringExtra("place_img");
        place_key = i.getStringExtra("place_key");

        //Button next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip_date = edt_date.getText().toString();
                if (trip_date.isEmpty()){
                    Toast.makeText(AddTrip_Info.this, "Bạn chưa chọn ngày khởi hành", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(AddTrip_Info.this, AddTrip_Done.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tripname", trip_name);
                    bundle.putString("tripdate", trip_date);
                    bundle.putString("place_name", place_name);
                    bundle.putString("place_img", place_img);
                    bundle.putString("place_key", place_key);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
    }

    //Hàm chọn ngày
    public void ChonNgay() {
        final Calendar calendar = Calendar.getInstance();
        int nam = calendar.get(Calendar.YEAR);
        int thang = calendar.get(Calendar.MONTH);
        int ngay = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edt_date.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam, thang, ngay);
        datePickerDialog.show();
    }
}
