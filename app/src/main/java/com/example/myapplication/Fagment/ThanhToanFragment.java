package com.example.myapplication.Fagment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.ThanhToanAdapter;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.Model.TaiKhoan;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ThanhToanFragment extends Fragment {
    private RecyclerView rcv_thanhToan;

    private TaiKhoan taiKhoan;
    private List<GioHangCT> list;

    private TextView tvDiaChi, tvHoTenSDT, tvTongTienHang, tvTongPhiVC, tvTongThanhToan1, tvTongThanhToan2;
    private Button btnDatHang;
    private ImageView btnTroVe;

    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();   
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thanh_toan, container, false);
        anhxa(view);

        getIntentThongTinCaNhan();

        Bundle listbundle = this.getArguments();
        list = (List<GioHangCT>) listbundle.getSerializable("listThanhToan");

        tinhTong();
        adapterThanhToan();

        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDatHang();
            }
        });

        btnTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_out, R.anim.fade_out, R.anim.fade_in, R.anim.slide_in)
                        .replace(R.id.nav_FrameFragment, new GioHangFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }



    // C???p nh???t th??ng tin thanh to??n m??n ??n c???a kh??ch h??ng
    public void clickDatHang(){
        Intent intent = getActivity().getIntent();
        String maTK = intent.getStringExtra("MaTK");
        int soDuTK = Integer.parseInt(intent.getStringExtra("SoDu"));

        int ktSoDu = 0;
        for(GioHangCT gh: list){
            ktSoDu += (gh.getGiaMA() * gh.getSoLuong()) + 20000;
        }

        ktSoDu = soDuTK - ktSoDu;
        if(ktSoDu>=0) {
            for (GioHangCT gh : list) {
                int _soDuTK = (soDuTK - (gh.getGiaMA() * gh.getSoLuong()) - 20000);
                    //C???p nh???t th??ng tin
                    db.collection("GIOHANGCT").document(gh.getMaGHCT())
                            .update(
                                    "TrangThai", 1,
                                    "ThoiGian", FieldValue.serverTimestamp(),
                                    "TongTien", (gh.getGiaMA() * gh.getSoLuong()) + 20000
                            );

                    //C???p nh???t th??ng tin
                    db.collection("TAIKHOAN").document(maTK)
                            .update(
                                    "SoDu", _soDuTK
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //C???p nh???t s??? d?? trong intent
                            intent.putExtra("SoDu", _soDuTK + "");
                            startActivity(intent);
                        }
                    });


                    soDuTK = _soDuTK;

            }

            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .replace(R.id.nav_FrameFragment, new NhaHangFragment())
                    .addToBackStack(null)
                    .commit();

            Toast.makeText(getContext(), "B???n ???? ?????t h??ng th??nh c??ng", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "S??? d?? t??i kho???n c???a b???n kh??ng ?????", Toast.LENGTH_SHORT).show();
        }

    }


    private void adapterThanhToan(){
        ThanhToanAdapter adapter = new ThanhToanAdapter(list, getContext());
        rcv_thanhToan.setFocusable(false);
        rcv_thanhToan.setNestedScrollingEnabled(false);
        rcv_thanhToan.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_thanhToan.setAdapter(adapter);
    }

    // L???y th??ng tin c?? nh??n t??? intent xu???ng
    private void getIntentThongTinCaNhan(){
        Intent intent = getActivity().getIntent();
        String hoTen = intent.getStringExtra("HoTen");
        String diaChi = intent.getStringExtra("DiaChi");
        String sdt = intent.getStringExtra("SDT");

        tvHoTenSDT.setText(hoTen +" | "+sdt);
        tvDiaChi.setText(diaChi);
    }

    private void anhxa(View view){
        rcv_thanhToan = view.findViewById(R.id.rcv_thanhToan);
        tvDiaChi = view.findViewById(R.id.tv_diaChiNHThanhToanFrag);
        tvHoTenSDT = view.findViewById(R.id.tv_hoTenSDTThanhToanFrag);
        tvTongPhiVC = view.findViewById(R.id.tv_tongPhiVCThanhToan);
        tvTongTienHang = view.findViewById(R.id.tv_tongTienHangThanhToan);
        tvTongThanhToan1 = view.findViewById(R.id.tv_tongThanhToanTT1);
        tvTongThanhToan2 = view.findViewById(R.id.tv_tongThanhToanTT2);
        btnDatHang = view.findViewById(R.id.btn_datHangTT);
        btnTroVe = view.findViewById(R.id.imv_TroveTrongDTNH);
    }

    private void tinhTong(){
        int tongGiaoHang = 0;
        int tongTienHang = 0;
        int tongThanhToan = 0;
        for(GioHangCT gh: list){
            tongGiaoHang += 20000;
            tongTienHang += gh.getSoLuong() * gh.getGiaMA();
        }

        tongThanhToan = tongTienHang + tongGiaoHang;

        tvTongTienHang.setText(formatNumber(tongTienHang));
        tvTongPhiVC.setText(formatNumber(tongGiaoHang));
        tvTongThanhToan1.setText(formatNumber(tongThanhToan));
        tvTongThanhToan2.setText(formatNumber(tongThanhToan));
    }


    //?????nh d???ng sang s??? ti???n
    private String formatNumber(int number){
        // t???o 1 NumberFormat ????? ?????nh d???ng s??? theo ti??u chu???n c???a n?????c Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }


}