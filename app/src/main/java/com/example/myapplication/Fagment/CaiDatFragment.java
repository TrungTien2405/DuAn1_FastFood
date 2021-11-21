package com.example.myapplication.Fagment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.logging.LogManager;


public class CaiDatFragment extends Fragment {
    TextView tvHoTen,tvSDT,tvDiaChi,tvSoDu,tvThongTinUD,tvHoTro;
    Button btnDangXuat;
    private FirebaseAuth auth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cai_dat, container, false);
        tvHoTen=v.findViewById(R.id.tv_TenKHCD);
        tvSDT=v.findViewById(R.id.tv_SDTKHCD);
        tvDiaChi=v.findViewById(R.id.tv_DiaChiKHCD);
        tvSoDu=v.findViewById(R.id.tv_SoDuCD);
        tvThongTinUD=v.findViewById(R.id.tv_ThongTinUngDungCD);
        tvHoTro=v.findViewById(R.id.tv_HoTroCD);
        btnDangXuat=v.findViewById(R.id.btn_DangXuatCD);
        getDataIntent();


        tvThongTinUD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new ThongTinUDFragment()).commit();
            }
        });

        tvHoTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new HoTroFragment()).commit();
            }
        });

        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return v;

    }
    public void signOut() {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();

        auth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);

    }

    private void getDataIntent(){
        Intent intent = getActivity().getIntent();
        String hoTen= intent.getStringExtra("HoTen");
        tvHoTen.setText(hoTen);
        String SDT=intent.getStringExtra("SDT");
        tvSDT.setText(SDT);
        String diaChi=intent.getStringExtra("DiaChi");
        tvDiaChi.setText(diaChi);
        String soDu=intent.getStringExtra("SoDu");
        tvSoDu.setText(soDu);
    }

}