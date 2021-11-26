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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.ThanhToanAdapter;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.Model.TaiKhoan;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ThanhToanFragment extends Fragment {
    private RecyclerView rcv_thanhToan;

    private TaiKhoan taiKhoan;
    private List<GioHangCT> list;

    private TextView tvDiaChi, tvHoTenSDT, tvTongTienHang, tvTongPhiVC, tvTongThanhToan1, tvTongThanhToan2;
    private Button btnDatHang;

    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        adapterThanhToan();

        return view;
    }

    private void adapterThanhToan(){
        ThanhToanAdapter adapter = new ThanhToanAdapter(list, getContext());
        rcv_thanhToan.setFocusable(false);
        rcv_thanhToan.setNestedScrollingEnabled(false);
        rcv_thanhToan.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_thanhToan.setAdapter(adapter);
    }

    // Lấy thông tin cá nhân từ intent xuống
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
        tvTongThanhToan1 = view.findViewById(R.id.tv_tongThanhToanTT2);
        btnDatHang = view.findViewById(R.id.btn_datHangTT);
    }
}