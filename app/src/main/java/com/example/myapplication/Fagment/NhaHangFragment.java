package com.example.myapplication.Fagment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.LoaiNhaHangAdapter;
import com.example.myapplication.Adapter.NhaHangAdapter;
import com.example.myapplication.Model.DanhGiaNH;
import com.example.myapplication.Model.LoaiNhaHang;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NhaHangFragment extends Fragment {
    private List<NhaHang> listNhaHang;
    private List<NhaHang> listNhaHangTheoLoai;
    private List<DanhGiaNH> listDanhGia;
    private List<LoaiNhaHang> listLoaiNhaHang;
    private NhaHang nhaHang;

    private RecyclerView rcv_nhahang;
    private RecyclerView rcv_loainhahang;

    private TextView tvTenTK;
    private TextInputLayout tipSoDuTK;
    private ImageView imvThemLoaiNH;

    private Dialog dialogThemLoaiNH;

    public int viTriLoaiNH = 0 ;

    //Firestore
    FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        //Lấy danh sách đánh giá xuống
        getAllDanhGia(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nha_hang, container, false);
        //Thêm thông tin phần tài khoản màn hình chính
        anhxa(v);

        rcv_nhahang =v.findViewById(R.id.rcv_restaurant);
        rcv_loainhahang =v.findViewById(R.id.rcv_categoryRes);

        rcv_loainhahang.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcv_loainhahang, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                getAllNhaHangTheoLoai(listLoaiNhaHang.get(position).getTenLoaiNH());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //Nhấn nút thêm loại nhà hàng
        imvThemLoaiNH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_themLoaiNH(0);
            }
        });

        return v;
    }

    private void anhxa(View v){
        tvTenTK = v.findViewById(R.id.tv_tenTaiKhoan_NhaHang);
        tipSoDuTK = v.findViewById(R.id.tip_soDuTK);
        imvThemLoaiNH = v.findViewById(R.id.imv_addLoaiNhFragNH);

        Intent intent = getActivity().getIntent();
        String tentk = intent.getStringExtra("HoTen");
        int soDu = Integer.parseInt(intent.getStringExtra("SoDu"));

        tvTenTK.setText(tentk);
        tipSoDuTK.getEditText().setText("Số dư    "+formatNumber(soDu)+" VND");
    }

    // Định dạng sang số tiền
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }

    //Xuất tất cả nhà nhà hàng lên list
    public void getAllNhaHang(Context context){
        listNhaHang = new ArrayList<>();

        final CollectionReference reference = db.collection("NHAHANG");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaNH = doc.get("MaNH").toString();
                            String MaLoaiNH = doc.get("MaLoaiNH").toString();
                            String MaTK = doc.get("MaTK").toString();
                            String TenNH = doc.get("TenNH").toString();
                            String ThoiGian = doc.get("ThoiGian").toString();
                            int PhiVanChuyen = Integer.parseInt(doc.get("PhiVanChuyen").toString());
                            String HinhAnh = doc.get("HinhAnh").toString();

                            nhaHang = new NhaHang(MaNH, getTenLoaiNhaHang(MaLoaiNH), MaTK, TenNH, ThoiGian, PhiVanChuyen, HinhAnh, tinhDanhGiaTB(MaNH));
                            listNhaHang.add(nhaHang);

                        }
//                        NhaHangAdapter adapter  = new NhaHangAdapter(listNhaHang, getContext());
//                        rcv_nhahang.setLayoutManager(new LinearLayoutManager(getContext()));
//                        rcv_nhahang.setAdapter(adapter);

                          //Xuất danh sách nhà hàng được chọn theo loại

                          getAllNhaHangTheoLoai(listLoaiNhaHang.get(0).getTenLoaiNH());
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Tính lượt sao của nhà hàng
    private Double tinhDanhGiaTB(String maNH){
        for (DanhGiaNH dg: listDanhGia){
            if(maNH.equals(dg.getMaNH()))
                return (Double.valueOf(dg.getTongDG() / dg.getLuotDG()));
        }
        return 0.0;
    }

    //Lấy tên loại của nhà hàng
    private String getTenLoaiNhaHang(String maLoaiNH){
        for (LoaiNhaHang loai: listLoaiNhaHang){
            if(maLoaiNH.equals(loai.getMaLoaiNH()))
                return loai.getTenLoaiNH();
        }
        return "";
    }



    //Xuất tất cả đánh giá lên list
    public void getAllDanhGia(Context context){
        listDanhGia = new ArrayList<>();

        final CollectionReference reference = db.collection("DANHGIANH");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaDanhGia = doc.get("MaDanhGia").toString();
                            String MaNH = doc.get("MaNH").toString();
                            int LuotDG = Integer.parseInt(doc.get("LuotDG").toString());
                            int TongDG = Integer.parseInt(doc.get("TongDG").toString());

                            DanhGiaNH danhGiaNH = new DanhGiaNH(MaDanhGia, MaNH, LuotDG, TongDG);
                            listDanhGia.add(danhGiaNH);
                        }

                        //Lấy danh sách loại nhà hàng xuống
                        getAllLoaiNhaHang(getContext());

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Lấy danh sách nhà hàng
    public void getAllLoaiNhaHang(Context context){
        listLoaiNhaHang = new ArrayList<>();

        final CollectionReference reference = db.collection("LOAINHAHANG");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaLoaiNH = doc.get("MaLoaiNH").toString();
                            String TenLoaiNH = doc.get("TenLoaiNH").toString();
                            String HinhAnh = doc.get("HinhAnh").toString();

                            LoaiNhaHang loaiNhaHang =  new LoaiNhaHang(MaLoaiNH, TenLoaiNH, HinhAnh);
                            listLoaiNhaHang.add(loaiNhaHang);
                        }

                        LinearLayoutManager layoutManager
                                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

                        LoaiNhaHangAdapter adapter  = new LoaiNhaHangAdapter(listLoaiNhaHang, getContext());
                        rcv_loainhahang.setLayoutManager(layoutManager);
                        rcv_loainhahang.setAdapter(adapter);

                        //Xuat danh sach nha hang len recycleview
                        getAllNhaHang(getContext());

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Xuất List nhà hàng theo loại nhà hàng chọn
    private void getAllNhaHangTheoLoai(String maLoai){

        listNhaHangTheoLoai = new ArrayList<>();
        for(NhaHang nh: listNhaHang){
            if(nh.getMaLoaiNH().equals(maLoai)){
                listNhaHangTheoLoai.add(nh);
            }
        }
        NhaHangAdapter adapter  = new NhaHangAdapter(listNhaHangTheoLoai, getContext());
        rcv_nhahang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_nhahang.setAdapter(adapter);
    }

    //Dialog thêm loại nhà hàng
    private void dialog_themLoaiNH(int positon){
        dialogThemLoaiNH =  new Dialog(getContext());
        dialogThemLoaiNH.setContentView(R.layout.dialog_themloainh);

        dialogThemLoaiNH.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.9);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.6);
        dialogThemLoaiNH.getWindow().setLayout(width,height);

//
//        edt_dialogTenSach = dialog_suaSach.findViewById(R.id.edt_dialogTenSuaSach);
//        edt_dialogGiaThueSach= dialog_suaSach.findViewById(R.id.edt_dialogGiaThueSuaSach);
//        edt_dialogSLSach= dialog_suaSach.findViewById(R.id.edt_dialogSoLuongSuaSach);
//        btn_dialogGuiSuaSach = dialog_suaSach.findViewById(R.id.btn_dialogGuiSuaSach);
//        imgBtn_dialogAvatarSuaSach = dialog_suaSach.findViewById(R.id.imgBtn_dialogAvatarSuaSach);
//
//        // Thêm dữ liệu vào dialog sửa
//        edt_dialogTenSach.setText(list.get(positon).getTenSach());
//        edt_dialogSLSach.setText(list.get(positon).getSoLuong()+"");
//        edt_dialogGiaThueSach.setText(list.get(positon).getGiaThue()+"");
//
//        sach = list.get(positon);
//
//        btn_dialogGuiSuaSach.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String tenSach = edt_dialogTenSach.getText().toString();
//                String soluongSach = edt_dialogSLSach.getText().toString();
//                String giaSach = edt_dialogGiaThueSach.getText().toString();
//
//                if(tenSach.isEmpty() || soluongSach.isEmpty() ||  giaSach.isEmpty()){
//                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
//                }else{
//                    list.get(positon).setTenSach(tenSach);
//                    list.get(positon).setSoLuong(Integer.parseInt(soluongSach));
//                    list.get(positon).setGiaThue(Integer.parseInt(giaSach));
//
//                    if(imageFileName.isEmpty()){
//                        updateFirebase(list.get(positon));
//                    }else uploadImageToFirebase(imageFileName, contenUri);
//                }
//
//
//            }
//        });
//
//        // Chọn hình ảnh
//        imgBtn_dialogAvatarSuaSach.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
//            }
//        });

        dialogThemLoaiNH.show();
    }


}