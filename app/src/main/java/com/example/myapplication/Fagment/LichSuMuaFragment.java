package com.example.myapplication.Fagment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.GioHangAdapter;
import com.example.myapplication.Adapter.LichSuMHAdapter;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.Model.MonAnNH;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LichSuMuaFragment extends Fragment {

    private RecyclerView rcv_lsMua;
    private TextView tvXoa;
    private ImageView imvTroVe;

    private List<MonAnNH> listMonAn;
    private List<GioHang> listGioHang;
    private List<GioHangCT> listGioHangCT;
    private List<String> listTenNH;

    private GioHang gioHang;
    private MonAnNH monAnNH;
    private GioHangCT gioHangCT;

    //Firestore
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lich_su_mua, container, false);

        anhxa(v);

        //Gọi Firebase xuống
        db = FirebaseFirestore.getInstance();

        listTenNH = new ArrayList<>();

        getAllMonAn(getContext()); // Lấy tất cả món ăn từ Firebase xuống

        tvXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickXoa();
            }
        });

        imvTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_out, R.anim.fade_out, R.anim.fade_in, R.anim.slide_in)
                        .replace(R.id.nav_FrameFragment, new CaiDatFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    private void anhxa(View v){
        rcv_lsMua = v.findViewById(R.id.rcv_lichSuMH);
        tvXoa = v.findViewById(R.id.tv_xoaLichSu);
        tvXoa.setVisibility(View.INVISIBLE);

        imvTroVe = v.findViewById(R.id.imv_TroveTrongLSMH);
    }


    // Định dạng sang số tiền
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }

    //Lưu lại các checkbox đã chọn
    public void checkedGioHang(int positon){
        listGioHangCT.get(positon).setTrangThaiCheckbox(true); // lưu lại các món ăn đã nhấn chọn
    }

    //Bỏ lưu các checkbox
    public void uncheckedGioHang(int positon){
        listGioHangCT.get(positon).setTrangThaiCheckbox(false); // lưu lại các món ăn đã nhấn chọn
    }


    //Lấy danh sách giỏ hàng từ Firebase xuống
    public void getAllGioHang(Context context){
        listGioHang = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        String _maTK = intent.getStringExtra("MaTK");

        final CollectionReference reference = db.collection("GIOHANG");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String maGH = doc.get("MaGH").toString();
                            String maTK = doc.get("MaTK").toString();

                            if(maTK.equals(_maTK)) {
                                gioHang = new GioHang(maGH, maTK);
                                listGioHang.add(gioHang);

                                // Lấy list giỏ hàng chi tiết
                                getAllGioHangCT(getContext(), maGH);
                                break;
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

    // Lấy danh sách món ăn từ Firebase xuống
    public void getAllMonAn(Context context){
        listMonAn = new ArrayList<>();

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

                            monAnNH = new MonAnNH(maMA, maNH, maMenuNH, tenMon, chiTiet, gia, hinhAnh);
                            listMonAn.add(monAnNH);
                        }

                        getAllGioHang(getContext()); //Lấy danh tất cả danh sách giỏ hàng từ Firebase xuống

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Lấy danh sách giỏ hàng chi tiết từ Firebase xuống
    public void getAllGioHangCT(Context context, String _maGH){
        listGioHangCT = new ArrayList<>();

        final CollectionReference reference = db.collection("GIOHANGCT");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String maMA = doc.get("MaMA").toString();
                            String maGHCT = doc.get("MaGHCT").toString();
                            String maGH = doc.get("MaGH").toString();
                            int soLuong = Integer.parseInt(doc.get("SoLuong").toString());
                            String tenMonThem = doc.get("TenMonThem").toString();
                            String thoiGian = doc.get("ThoiGian").toString();
                            int trangThai = Integer.parseInt(doc.get("TrangThai").toString());

                            if(_maGH.equals(maGH) && trangThai==1) {
                                gioHangCT = new GioHangCT(maGH, maGHCT, maMA, "", soLuong, 0, "", tenMonThem, thoiGian, trangThai, "", false);
                                listGioHangCT.add(gioHangCT);

                            }
                        }
                        // Thêm đầy đủ thông tin vào giỏ hàng chi tiét
                        getAllDetail_gioHang();

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("=====>", e.getMessage());
                }
            }
        });
    }


    //Set Adapter giỏ hàng
    private void adapter_gioHang(){
        LichSuMHAdapter adapter  = new LichSuMHAdapter(listGioHangCT, getContext(), this);
        rcv_lsMua.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_lsMua.setAdapter(adapter);
    }

    //Cập nhật đầy đủ thông tin giỏ hàng lên listGioHangCT;
    private void getAllDetail_gioHang(){
        for(int i=0; i<listGioHangCT.size(); i++){
            addDetail_gioHang(listGioHangCT.get(i).getMaMA(), i);
        }

        //Đẩy list lên adapter giỏ hàng
        adapter_gioHang();
    }

    //Tìm kiếm món ăn bằng mã món ăn, nếu có thêm món ăn vào list giỏ hàng
    private void addDetail_gioHang(String maMA, int positon){
        for(MonAnNH ma: listMonAn){
            if(maMA.equals(ma.getMaMA())){
                listGioHangCT.get(positon).setTenMA(ma.getTenMon());
                listGioHangCT.get(positon).setGiaMA(ma.getGia());
                listGioHangCT.get(positon).setHinhAnh(ma.getHinhAnh());

                //Cộng dồn vào tổng tiền giỏ hàng

                Intent intent = getActivity().getIntent();
                //Thêm mã tài khoản vào list giỏ hàng
                listGioHangCT.get(positon).setMaTK(intent.getStringExtra("MaTK"));
            }
        }
    }

    // Duyệt danh sách kiểm tra xem item nào có chọn Checkbox thì xóa nó
    public void clickXoa(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn xóa nhà hàng không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            int duyet = 0;
                            for(GioHangCT gh: listGioHangCT){
                                if(gh.getTrangThaiCheckbox()) {
                                    deleteGioHangCTFirestore(gh.getMaGHCT());
                                    duyet = 1;
                                }
                            }

                            if(duyet == 0) Toast.makeText(getContext(), "Bạn chưa chọn checkbox nào!!", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    // Xóa món ăn trong giỏ hàng
    private void deleteGioHangCTFirestore(String _maGHCT){

        db.collection("GIOHANGCT").document(_maGHCT)
                .delete();

        getAllGioHang(getContext()); //Lấy danh tất cả danh sách giỏ hàng từ Firebase xuống, sau đó đưa danh sách giỏ hàng lên rcv
    }




}