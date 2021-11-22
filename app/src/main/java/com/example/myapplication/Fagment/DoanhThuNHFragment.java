package com.example.myapplication.Fagment;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.DoanhThuNHAdapter;
import com.example.myapplication.Adapter.GioHangAdapter;
import com.example.myapplication.Model.DanhGiaNH;
import com.example.myapplication.Model.DoanhThuNH;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.Model.MonAnNH;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DoanhThuNHFragment extends Fragment {
    private List<MonAnNH> listMonAn;
    private List<GioHangCT> listGioHangCT;
    private List<NhaHang> listNhaHang;
    private List<DoanhThuNH> listDoanhThu;
    private List<DanhGiaNH> listDanhGia;

    //Model
    private MonAnNH monAnNH;
    private GioHangCT gioHangCT;
    private NhaHang nhaHang;

    private RecyclerView rcv_doanhThuNH; // hiện thôn tin các món ăn trong giỏ hàng
    private TextView tvChonNgay1, tvChonNgay2;
    private ImageView imvTroVe;

    private int lastSelectedYear; // Lưu năm để hiện lên ngày chọn
    private int lastSelectedMonth; // Lưu tháng để hiện lên ngày chọn
    private int lastSelectedDayOfMonth; // Lưu ngày để hiện lên ngày chọn

    private String ngayDau, ngayCuoi; // Biến toàn cục lưu biến ngày đầu tiên và ngày cuối


    //Firestore
    private FirebaseFirestore db;

    public DoanhThuNHFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doanh_thu_n_h, container, false);

        anhxa(v);

        db = FirebaseFirestore.getInstance();

        getAllDanhGia(getContext()); // Lấy danh sách đánh giá từ nhà hàng xuống
        getAllMonAn(getContext()); // Lấy danh sách móm ăn từ Firestore xuống
        getAllNhaHang(getContext()); // Lấy danh sách nhà hàng từ Firestore xuống

        //Chọn ngày đầu để hiển thị doanh thu
        tvChonNgay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonNgayDatePicker(0);
            }
        });

        //Chọn ngày cuối để thể hiện doanh thu
        tvChonNgay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonNgayDatePicker(1);
            }
        });

        //Trở về màn hình cài đặt
        imvTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new CaiDatFragment()).commit();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    //Ánh xạ component
    private void anhxa(View v){
        rcv_doanhThuNH = v.findViewById(R.id.rcv_doanhThuNH);
        tvChonNgay1 = v.findViewById(R.id.tv_chonNgay1DTNH);
        tvChonNgay2 = v.findViewById(R.id.tv_chonNgay2DTNH);
        imvTroVe  = v.findViewById(R.id.imv_TroveTrongDTNH);
    }

    //Hiện lên DatePickerDialog để chọn ngày
    private void chonNgayDatePicker(int vitri){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        this.lastSelectedYear = c.get(Calendar.YEAR);
        this.lastSelectedMonth = c.get(Calendar.MONTH);
        this.lastSelectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        // Date Select Listener.
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String day = String.valueOf(dayOfMonth);
                String mon = String.valueOf(month+1);

                if(dayOfMonth<10) day = "0"+day;
                if(month<9) mon = "0"+mon;

                String kq = day + "-" + mon + "-" + year;

                if(vitri == 0){
                    tvChonNgay1.setText(kq);
                    ngayDau = kq;
                }else {
                    tvChonNgay2.setText(kq);
                    ngayCuoi = kq;

                    getAllGioHangCT(getContext()); // Lấy danh sách giỏ hàng từ Firestore xuống
                };
            }
        };

        DatePickerDialog datePickerDialog = null;

        // Create DatePickerDialog:
        datePickerDialog = new DatePickerDialog(getContext(),
                dateSetListener, lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth);


        // Show
        datePickerDialog.show();
    }


    //Tính tổng doanh thu từng nhà hàng
    private void tinhDoanhThuNH(){
        listDoanhThu = new ArrayList<>();
        for(NhaHang nh: listNhaHang){
            int tongDT = 0;
            int demDH = 0;

            //Tính doanh thu nhà hàng theo mã nhà hàng
            for(GioHangCT gh: listGioHangCT){
                if(gh.getMaTK().equals(nh.getMaNH())){ // Mã tài khoản ở đây, tôi đang để nhờ, thực ra đó là mã nhà hàng.
                    tongDT += (gh.getGiaMA() * gh.getSoLuong());
                    demDH += 1; //Đếm tổng đơn hàng
                }
            }
            DoanhThuNH dt = new DoanhThuNH(nh.getMaNH(), demDH, nh.getTenNH(), tongDT, nh.getHinhAnh(), nh.getDanhGia());
            listDoanhThu.add(dt);
        }

        //Sắp xếp danh sách doanh thu giảm dần
        Collections.sort(listDoanhThu, new Comparator<DoanhThuNH>() {
            @Override
            public int compare(DoanhThuNH o1, DoanhThuNH o2) {
                return String.valueOf(o2.getTongDT()).compareTo(String.valueOf(o1.getTongDT()));
            }
        });



        //Đẩy list lên adapter giỏ hàng
        adapter_gioHang();
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
    public void getAllGioHangCT(Context context){
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
                            Timestamp thoiGian = (Timestamp) doc.get("ThoiGian");
                            int trangThai = Integer.parseInt(doc.get("TrangThai").toString());

                            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            String ngayMua = format.format(thoiGian.toDate());

                            if(trangThai==1 && ngayMua.compareTo(ngayCuoi) < 0 && ngayMua.compareTo(ngayDau) > 0) {
                                gioHangCT = new GioHangCT(maGH, maGHCT, maMA, "", soLuong, 0, "", tenMonThem, ngayMua, trangThai, "", false);
                                listGioHangCT.add(gioHangCT);
                            }
                        }

                        // Thêm đầy đủ thông tin vào giỏ hàng chi tiét
                        getAllDetail_gioHang();

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Error getAllGioHangCT"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("=====>", e.getMessage());
                }
            }
        });
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
                            String MaDG = doc.get("MaDG").toString();

                            Double danhGia = tinhDanhGiaTB(MaNH);
                            nhaHang = new NhaHang(MaNH, MaLoaiNH, MaTK, TenNH, ThoiGian, PhiVanChuyen, HinhAnh, danhGia, MaDG, "");
                            listNhaHang.add(nhaHang);

                        }
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
//                    Toast.makeText(getContext(), "Lỗi get nhà hàng: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("=====> ", "Lỗi get nhà hàng: " + e.getMessage());
                }
            }
        });
    }



    //Xuất tất cả đánh giá
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

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(),"Error getAllDanhGia: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Tính lượt sao của nhà hàng
    private Double tinhDanhGiaTB(String maNH){
        for (DanhGiaNH dg: listDanhGia){
            if(maNH.equals(dg.getMaNH())) {
                Double kq = (Double.valueOf(dg.getTongDG()) / Double.valueOf(dg.getLuotDG()));


                return Math.round(kq*100)/100.00;
            }
        }
        return 0.0;
    }

    //Set Adapter giỏ hàng
    private void adapter_gioHang(){
        DoanhThuNHAdapter adapter  = new DoanhThuNHAdapter(listDoanhThu, getContext());
        rcv_doanhThuNH.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_doanhThuNH.setAdapter(adapter);
    }

    //Cập nhật đầy đủ thông tin giỏ hàng lên listGioHangCT;
    private void getAllDetail_gioHang(){
        for(int i=0; i<listGioHangCT.size(); i++){
            addDetail_gioHang(listGioHangCT.get(i).getMaMA(), i);
        }


        //Tính doanh thu của từng nhà hàng
        tinhDoanhThuNH();

    }

    //Tìm kiếm món ăn bằng mã món ăn, nếu có thêm món ăn vào list giỏ hàng
    private void addDetail_gioHang(String maMA, int positon){
        for(MonAnNH ma: listMonAn){
            if(maMA.equals(ma.getMaMA())){
                listGioHangCT.get(positon).setTenMA(ma.getTenMon());
                listGioHangCT.get(positon).setGiaMA(ma.getGia());
                listGioHangCT.get(positon).setHinhAnh(ma.getHinhAnh());
                listGioHangCT.get(positon).setMaTK(ma.getMaNH()); // ==> Coi chừng, đag để tạm mã nhà hàng vô trường mà tài khoản

            }
        }
    }
}