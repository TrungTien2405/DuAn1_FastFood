package com.example.myapplication.Fagment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.GioHangAdapter;
import com.example.myapplication.Adapter.LoaiNhaHangAdapter;
import com.example.myapplication.Adapter.NhaHangAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.DanhGiaNH;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.Model.MonAnNH;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GioHangFragment extends Fragment {
    private RecyclerView rcv_GioHang; // hiện thôn tin các món ăn trong giỏ hàng
    public TextView tvTongTienGH; // Hiện thông tin tổng tiền từ các món khách hàng đã chọn
    private TextView tvXoaGH; // Hiện thông tin tổng tiền từ các món khách hàng đã chọn
    private Button btnThanhToanGH; // Nhấn thanh toán giỏ hàng

    //List chứa danh sách
    private List<MonAnNH> listMonAn;
    private List<GioHang> listGioHang;
    private List<GioHangCT> listGioHangCT;

    //Model
    private GioHang gioHang;
    private MonAnNH monAnNH;
    private GioHangCT gioHangCT;

    private int soDuTK; //Số tiền của người đăng nhập

    //Số tiền được tính tổng từ các món ăn đã chọn checkbox trong giỏ hàng
    public int TongTienGH = 0;

    //Firestore
    private FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gio_hang, container, false);
        anhxa(view);


        //Gọi Firebase xuống
        db = FirebaseFirestore.getInstance();

        getAllGioHang(getContext()); //Lấy danh tất cả danh sách giỏ hàng từ Firebase xuống
        getAllMonAn(getContext()); // Lấy tất cả món ăn từ Firebase xuống
        getSoDuND(getContext()); // Lấy số tiền của người dùng

        //Nhấn xóa
        tvXoaGH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickXoa();
            }
        });

        //Nhấn nút thanh toán
        btnThanhToanGH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickThanhToan();
            }
        });

        return view;
    }

    private void anhxa(View v){
        tvTongTienGH = v.findViewById(R.id.tv_tongTienGH);
        tvXoaGH = v.findViewById(R.id.tv_xoaGioHang);
        btnThanhToanGH = v.findViewById(R.id.btn_thanhToanGH);
        rcv_GioHang = v.findViewById(R.id.rcv_GioHang);
    }

    //Tính tổng tiền trong giỏ hàng
    public void tinhTongTien(){
        try {
            TongTienGH = 0;
            for (GioHangCT gh : listGioHangCT) {
                if (gh.getTrangThaiCheckbox()) TongTienGH += gh.getSoLuong() * gh.getGiaMA();
            }
            if(TongTienGH == 0){
                tvTongTienGH.setText("00.00");
            }else tvTongTienGH.setText(formatNumber(TongTienGH));
        }catch (Exception e){


        }
    }

    //Tính tổng giá các món ăn trong giỏ hàng, khi nhấn chọn
    public void checkedGioHang(int positon, int giaMon){
        TongTienGH += giaMon;
        tvTongTienGH.setText(formatNumber(TongTienGH)); // Hiển thị giá tiền trên TextView

        listGioHangCT.get(positon).setTrangThaiCheckbox(true); // lưu lại các món ăn đã nhấn chọn
    }

    //Tính hiệu giá các món ăn trong giỏ hàng, khi nhấn chọn
    public void uncheckedGioHang(int positon, int giaMon){
        TongTienGH -= giaMon;
        tvTongTienGH.setText(formatNumber(TongTienGH)); // Hiển thị giá tiền trên TextView

        listGioHangCT.get(positon).setTrangThaiCheckbox(false); // lưu lại các món ăn đã nhấn chọn
    }


    // Định dạng sang số tiền
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }


    // Duyệt danh sách kiểm tra xem item nào có chọn Checkbox thì chọn thanh toán
    public void clickThanhToan(){
        int duyet = 0;
        Intent intent = getActivity().getIntent();
        String maTK = intent.getStringExtra("MaTK");

        List<GioHangCT> listThanhToan = new ArrayList<>();

        for(int i=0; i<listGioHangCT.size(); i++){
            if(listGioHangCT.get(i).getTrangThaiCheckbox()) {
                listThanhToan.add(listGioHangCT.get(i));

                duyet = 1; //xác nhận đã có checkbox chọn

            }

        }


        if(duyet == 0){
            Toast.makeText(getContext(), "Bạn chưa chọn món ăn nào!!", Toast.LENGTH_SHORT).show();
        }else {

            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("listThanhToan", (ArrayList<? extends Parcelable>) listThanhToan);
            bundle.putSerializable("listThanhToan", (Serializable) listThanhToan);
            ThanhToanFragment thanhToanFragment = new ThanhToanFragment();
            thanhToanFragment.setArguments(bundle);

            //getFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, monAnFragment).commit();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .replace(R.id.nav_FrameFragment,  thanhToanFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    //Lấy danh sách người dùng từ Firebase xuống, để kiểm tra số dư tài khoản
    public void getSoDuND(Context context){
        Intent intent = getActivity().getIntent();
        String _maTK = intent.getStringExtra("MaTK");

        final CollectionReference reference = db.collection("TAIKHOAN");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String soDu = doc.get("SoDu").toString();
                            String maTK = doc.get("MaTK").toString();

                            if(maTK.equals(_maTK)) {
                                soDuTK  = Integer.parseInt(soDu);
                                return;
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
                    Toast.makeText(getContext(), "Error getAllGioHang: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

                            if(_maGH.equals(maGH) && trangThai==0) {
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
                    Toast.makeText(getContext(), "Error getAllGioHangCT"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("=====>", e.getMessage());
                }
            }
        });
    }

    //Set Adapter giỏ hàng
    private void adapter_gioHang(){
        GioHangAdapter adapter  = new GioHangAdapter(listGioHangCT, getContext(), this);
        rcv_GioHang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_GioHang.setAdapter(adapter);
        tinhTongTien();
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

    public void clickXoa(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn xóa nhà hàng không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            int ktCheck = 0;
                            for(GioHangCT gh: listGioHangCT){
                                if(gh.getTrangThaiCheckbox()){
                                    ktCheck = 1;
                                    deleteGioHangGioHangCTFirestore(gh.getMaGH(), gh.getMaGHCT());
                                }
                            }

                            if(ktCheck == 0) Toast.makeText(getContext(), "Bạn chưa chọn món ăn nào!!", Toast.LENGTH_SHORT).show();
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
    public void deleteGioHangGioHangCTFirestore(String _maGH, String _maGHCT){
//        db.collection("GIOHANG").document(_maGH)
//                .delete();

        try {

            db.collection("GIOHANGCT").document(_maGHCT)
                    .delete();

            Toast.makeText(getContext(), "Bạn đã xóa thành công", Toast.LENGTH_SHORT).show();

            getAllGioHang(getContext()); //Lấy danh tất cả danh sách giỏ hàng từ Firebase xuống, sau đó đưa danh sách giỏ hàng lên rcv
            getAllMonAn(getContext()); // Lấy tất cả món ăn từ Firebase xuống
        }catch (Exception e){
            Log.d("===> ", e.getMessage());
        }
    }


    public void updateSoLuongGH(String _maGHCT, int _soLuong, int position){
        db.collection("GIOHANGCT").document(_maGHCT)
                .update(
                        "SoLuong" , _soLuong
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listGioHangCT.get(position).setSoLuong(_soLuong); //update số lượng trong list giỏ hàng chi tiết
//                listGioHangCT.get(position).setGiaMA(_soLuong * listGioHangCT.get(position).getGiaMA());

            }
        });

    }
}










