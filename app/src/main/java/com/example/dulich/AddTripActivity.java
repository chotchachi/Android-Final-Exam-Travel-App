package com.example.dulich;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddTripActivity extends AppCompatActivity {
    EditText tripten;
    FloatingActionButton btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        tripten = findViewById(R.id.tripten);
        btn_ok = findViewById(R.id.btn_next);

        //Button ok
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ten = tripten.getText().toString();
                if (ten.isEmpty()){
                    Toast.makeText(AddTripActivity.this, "Bạn chưa điền tên cho chuyến đi", Toast.LENGTH_SHORT).show();
                } else{
                    Intent i = new Intent(AddTripActivity.this, AddTrip_Place.class);
                    i.putExtra("tripname", ten);
                    startActivity(i);
                }
            }
        });

    }
}
