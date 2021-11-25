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
import android.widget.TextView;

import com.example.myapplication.DangNhapActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;


public class CaiDatFragment extends Fragment {


    Button btnDangXuat;
    private FirebaseAuth auth;

    TextView tvHoTen,tvSDT,tvDiaChi,tvSoDu,tvThongTinUD,tvHoTro, tvLSMua, tvDoanhThu, tvDoiMatKhau;

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
        tvDoanhThu = v.findViewById(R.id.tv_DoanhThuCD);
        tvDoiMatKhau = v.findViewById(R.id.tv_DoiMKCD);

        btnDangXuat=v.findViewById(R.id.btn_DangXuatCD);

        tvLSMua=v.findViewById(R.id.tv_lsMuaHangCD);

        getDataIntent();


        tvThongTinUD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.nav_FrameFragment, new ThongTinUDFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });


        tvHoTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new ThanhToanFragment()).commit();
            }
        });

        btnDangXuat.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               signOut();
                                           }
                                       });

        tvLSMua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.nav_FrameFragment, new LichSuMuaFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        
        //Chuyển qua fragment doanh thu
        tvDoanhThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.nav_FrameFragment, new DoanhThuNHFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        //Chuyển qua fragment đổi mật khẩu
        tvDoiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.nav_FrameFragment, new DoiMatKhauFragment())
                        .addToBackStack(null)
                        .commit();
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
        Intent intent = new Intent(getContext(), DangNhapActivity.class);
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