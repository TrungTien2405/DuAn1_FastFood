package com.example.myapplication.Fagment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.MonAnAdapter;
import com.example.myapplication.Model.MonAnNH;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonAnCTFragment extends Fragment {
    private RecyclerView rcv_monAnThem;

    private TextView tv_tenMonCT, tv_giaMonCT, tv_chiTietMACT, tv_PhiVanChuyenMACT, tv_ThoiGianMACT, tv_DanhGiaMACT;
    private ImageView imv_hinhMonAnCT, imv_TroVe, imv_toGioHang;

    private List<MonAnNH> listMonAn;

    private MonAnNH monAnNH;

    //Firestore
    private FirebaseFirestore db;

    public MonAnCTFragment() {
        // Required empty public constructor
    }

    public static MonAnCTFragment newInstance() {
        MonAnCTFragment fragment = new MonAnCTFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mon_an_c_t, container, false);

        //Gọi Firebase xuống
        db = FirebaseFirestore.getInstance();

        anhXa(view);

        getAllMonAnChiTiet(getContext());

        return view;
    }

    private void anhXa(View view) {
        rcv_monAnThem = view.findViewById(R.id.rcv_monAnThemCT);
        tv_PhiVanChuyenMACT = view.findViewById(R.id.tv_PhiVanChuyenMACT);
        tv_ThoiGianMACT = view.findViewById(R.id.tv_ThoiGianMACT);
        tv_DanhGiaMACT = view.findViewById(R.id.tv_DanhGiaMACT);
        imv_hinhMonAnCT = view.findViewById(R.id.imv_hinhMonAnCT);
        imv_TroVe = view.findViewById(R.id.imv_TroVe);
        imv_toGioHang = view.findViewById(R.id.imv_toGioHang);
        tv_tenMonCT = view.findViewById(R.id.tv_tenMonAnCT);
        tv_giaMonCT = view.findViewById(R.id.tv_giaMonAnCT);
        tv_chiTietMACT = view.findViewById(R.id.tv_chiTietMonAnCT);

        //lấy dữ liệu từ fragment món ăn
        Bundle bundle = this.getArguments();
        String maMA = bundle.getString("MaMA");
        String maNH = bundle.getString("MaNH");
        String tenNH = bundle.getString("TenNH");
        String hinhAnhNH = bundle.getString("HinhAnhNH");
        int gia = bundle.getInt("Gia");
        String tenMon = bundle.getString("TenMon");
        String chiTiet = bundle.getString("ChiTiet");
        String hinhAnh = bundle.getString("HinhAnh");
        String thoiGian = bundle.getString("ThoiGian");
        Double danhGia = bundle.getDouble("DanhGia");
        int phiVanChuyen = bundle.getInt("PhiVanChuyen");

        tv_tenMonCT.setText(tenMon);
        tv_giaMonCT.setText(formatNumber(gia) + " VND");
        tv_chiTietMACT.setText(chiTiet);
        tv_PhiVanChuyenMACT.setText(formatNumber(phiVanChuyen) + " VND");
        tv_ThoiGianMACT.setText(thoiGian + " m");
        tv_DanhGiaMACT.setText(danhGia + "");
        if (hinhAnh.isEmpty()) {
            imv_hinhMonAnCT.setImageResource(R.drawable.im_food);
        } else {
            Picasso.with(getContext()).load(hinhAnh).into(imv_hinhMonAnCT);
        }

        imv_TroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("MaMA", maMA);
                bundle.putString("Gia", gia + "");
                bundle.putString("TenMon", tenMon);
                bundle.putString("ChiTiet", chiTiet);
                bundle.putString("HinhAnh", hinhAnhNH);
                bundle.putInt("PhiVanChuyen", phiVanChuyen);
                bundle.putString("ThoiGian", thoiGian);
                bundle.putDouble("DanhGia", danhGia);
                bundle.putString("MaNH", maNH);
                bundle.putString("TenNH", tenNH);
                MonAnFragment monAnFragment = new MonAnFragment();
                monAnFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.nav_FrameFragment, monAnFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        imv_toGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.nav_FrameFragment, new GioHangFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    public void getAllMonAnChiTiet(Context context){
        listMonAn = new ArrayList<>();

        Bundle bundle = this.getArguments();
        String _maMA = bundle.getString("MaMA");

        final CollectionReference reference = db.collection("MONANNH");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String maMA = doc.get("MaMA").toString();
                            String maNH = doc.get("MaNH").toString();
                            String tenMon = doc.get("TenMon").toString();
                            String maMenuNH = doc.get("MaMenuNH").toString();
                            String chiTiet = doc.get("ChiTiet").toString();
                            int gia = Integer.parseInt(doc.get("Gia").toString());
                            String hinhAnh = doc.get("HinhAnh").toString();

                            if (maMA.equals(_maMA)) {
                                monAnNH = new MonAnNH(maMA, maNH, maMenuNH, tenMon, chiTiet, gia, hinhAnh);
                                listMonAn.add(monAnNH);
                            }
                        }
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Định dạng sang số tiền
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }
}