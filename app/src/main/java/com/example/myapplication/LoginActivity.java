package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tip_SDT, tip_MatKhau;
    Button btnDangNhap;
    CheckBox chkLuuTK;
    TextView tv_QuenMatKhau, tv_DangKy;

    public FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tip_SDT = findViewById(R.id.tip_SDT);
        tip_MatKhau = findViewById(R.id.tip_MatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        chkLuuTK = findViewById(R.id.chkLuuMK);
        tv_QuenMatKhau = findViewById(R.id.tv_QuenMatKhau);
        tv_DangKy = findViewById(R.id.tv_DangKy);

        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        tip_SDT.getEditText().setText(pref.getString("USERNAME",""));
        tip_MatKhau.getEditText().setText(pref.getString("PASSWORD",""));
        chkLuuTK.setChecked(pref.getBoolean("STATUS", false));


        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = tip_SDT.getEditText().getText().toString();
                String password = tip_MatKhau.getEditText().getText().toString();

                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
                }else {
                    CheckAccount(username, password);
                }
            }
        });
    }

    public void CheckAccount(String username, String password){

        firestore =  FirebaseFirestore.getInstance();
        final CollectionReference reference = firestore.collection("TAIKHOAN");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int check = 0;
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    for(QueryDocumentSnapshot doc: snapshot){
                        if(username.equals(doc.get("SDT")) && password.equals(doc.get("MatKhau"))){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("SDT", username);
                            intent.putExtra("HoTen", doc.get("HoTen").toString());
                            intent.putExtra("HinhAnh", doc.get("HinhAnh").toString());
                            intent.putExtra("MaTK", doc.get("MaTK").toString());
                            intent.putExtra("SoDu", doc.get("SoDu").toString());
                            intent.putExtra("DiaChi", doc.get("DiaChi").toString());
                            intent.putExtra("Quyen", Integer.parseInt(doc.get("Quyen").toString()));

                            startActivity(intent);
                            remeberUser(username, password, chkLuuTK.isChecked());
                            check = 1;
                            break;
                        }
                    }
                    if(check == 0){
                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void remeberUser(String u, String p, boolean status){
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        if(!status){
            edit.clear();
        }else{
            //Luu du lieu
            edit.putString("USERNAME", u);
            edit.putString("PASSWORD", p);
            edit.putBoolean("STATUS", status);
        }
        //luu lai toan bo
        edit.commit();
    }
}