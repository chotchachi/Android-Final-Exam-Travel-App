package com.example.dulich;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dulich.Object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignIn extends AppCompatActivity {
    final static String DEFAULT_AVATAR = "http://s3.amazonaws.com/37assets/svn/765-default-avatar.png";
    EditText edt_hovaten, edt_sinhnhat, edt_username, edt_password;
    Button btn_dangky, btn_dangnhap;
    DatabaseReference firebaseDatabase;
    private FirebaseAuth mAuth;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        init();

        btn_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        btn_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void signIn() {
        if (edt_hovaten.getText().toString().isEmpty() || edt_username.getText().toString().isEmpty() ||
                edt_password.getText().toString().isEmpty() || edt_sinhnhat.getText().toString().isEmpty()) {
            Toast.makeText(SignIn.this, "Bạn chưa điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
//            mAuth.createUserWithEmailAndPassword(edt_username.getText().toString(), edt_password.getText().toString())
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                createUserFirebase();
//                            } else {
//                                //Log.d("xxx", task.getException().getMessage());
//                            }
//                        }
//                    });
            createUserFirebase();
        }
    }

    //Hàm update thông tin User lên Firebase
    private void createUserFirebase() {
        String username = edt_username.getText().toString();
        String user_id = firebaseDatabase.push().getKey();
        String hovaten = edt_hovaten.getText().toString();
        String sinh_nhat = edt_sinhnhat.getText().toString();
        User newuser = new User(username, user_id, hovaten, DEFAULT_AVATAR, sinh_nhat, edt_password.getText().toString());
        firebaseDatabase.child("User").child(username).child("Info").setValue(newuser);
        finish();
    }

    public void init() {
        edt_hovaten = findViewById(R.id.hovaten);
        edt_sinhnhat = findViewById(R.id.birth);
        edt_username = findViewById(R.id.username);
        edt_password = findViewById(R.id.password);
        btn_dangky = findViewById(R.id.btn_dangky);
        btn_dangnhap = findViewById(R.id.btn_dangnhap);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edt_sinhnhat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignIn.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edt_sinhnhat.setText(sdf.format(myCalendar.getTime()));
    }
}
