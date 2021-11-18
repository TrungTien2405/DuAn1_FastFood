package com.example.myapplication.Fagment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;


public class CaiDatFragment extends Fragment {
    TextView tvHoTen,tvSDT,tvDiaChi,tvSoDu,tvThongTinUD,tvHoTro;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
//    getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new ThongTinUDFragment).commit();
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
        getDataIntent();

        tvThongTinUD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new ThongTinUDFragment()).commit();
            }
        });

        return v;

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