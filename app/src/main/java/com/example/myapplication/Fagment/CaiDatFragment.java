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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.DangNhapActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;


public class CaiDatFragment extends Fragment {


    Button btnDangXuat;
    private FirebaseAuth auth;

    private ImageButton imgBtn;

    private ImageView imvAvatar;

    TextView tvHoTen,tvSDT,tvDiaChi,tvSoDu,tvThongTinUD,tvHoTro, tvLSMua, tvDoanhThu, tvDoiMatKhau;

    public int QuyenDN = 2;


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
        imgBtn = v.findViewById(R.id.imgbtn_DoanhThuCD);
        tvDoiMatKhau = v.findViewById(R.id.tv_DoiMKCD);
        imvAvatar = v.findViewById(R.id.imv_avatarTaiKhoan_CaiDat);

        btnDangXuat=v.findViewById(R.id.btn_DangXuatCD);

        tvLSMua=v.findViewById(R.id.tv_lsMuaHangCD);

        getDataIntent();

        kiemTraQuyenDangNhap();


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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new HoTroFragment()).commit();
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
        QuyenDN = intent.getIntExtra("Quyen", 2);
        String hinhAnh = intent.getStringExtra("HinhAnh");

        tvHoTen.setText(hoTen);
        String SDT=intent.getStringExtra("SDT");
        tvSDT.setText(SDT);
        String diaChi=intent.getStringExtra("DiaChi");
        tvDiaChi.setText(diaChi);
        int soDu=Integer.parseInt(intent.getStringExtra("SoDu"));
        tvSoDu.setText("Số dư  "+formatNumber(soDu) + " VND");

        if(hinhAnh.isEmpty()){
            imvAvatar.setImageResource(R.drawable.avatar);
        }else Picasso.with(getContext()).load(hinhAnh).into(imvAvatar);
    }

    //Kiểm tra quyền đăng nhập phù hợp với người dùng
    public void kiemTraQuyenDangNhap(){
        if(QuyenDN == 2){
            tvDoanhThu.setVisibility(View.INVISIBLE);
            imgBtn.setVisibility(View.INVISIBLE);
        }
    }

    // Định dạng sang số tiền
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }

}