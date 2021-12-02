package com.example.myapplication.Fagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.LoaiNhaHangAdapter;
import com.example.myapplication.Adapter.NhaHangAdapter;
import com.example.myapplication.Model.DanhGiaNH;
import com.example.myapplication.Model.LoaiNhaHang;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.Model.YeuThich;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class    NhaHangFragment extends Fragment {
    private List<NhaHang> listNhaHang;
    private List<NhaHang> listNhaHangTheoLoai;
    private List<DanhGiaNH> listDanhGia;
    private List<LoaiNhaHang> listLoaiNhaHang;
    private List<NhaHang> listNHSearch;
    private List<YeuThich> listYeuThich;

    //List cho spinner
    private List<String> listMaLoaiNH;
    private List<String> listMaTK;


    private NhaHang nhaHang;
    private LoaiNhaHang loaiNhaHang;

    private RecyclerView rcv_nhahang;
    private RecyclerView rcv_loainhahang;

    private TextView tvTenTK;
    private TextInputLayout tipSoDuTK;
    private ImageView imvThemLoaiNH, imvAvatar;
    private FloatingActionButton flBtnThemNH;
    private SearchView svNhaHang;

    //Dialog spinner thêm nhà hàng
    private Spinner spMaTK;
    private Spinner spMaLoaiNH;

    //Dialog spinner sửa nhà hàng
    private Spinner spMaTKSuaNH;
    private Spinner spMaLoaiNHSuaNH;
    private ImageView imvHinhSuaNH;

    private Dialog dialogThemLoaiNH;
    private Dialog dialogThemNH;
    private Dialog dialogSuaNH;

    //Vị trí hiện tại đang chọn loại nhà hàng
    public int viTriLoaiNH = 0 ;

    public int QuyenDN = 2;

    //Dialog hình thêm loại nhà hàng
    private ImageView imvHinhLoai;

    //Dialog Hình thêm nhà hàng
    private ImageView imvHinh;

    public String _maTK;


    // variable for FirebaseAuth class xác thực OTP
    private FirebaseAuth mAuth;
    //Firestore
    private FirebaseFirestore db;
    //Image firebase
    private StorageReference storageReference;


    //Load image
    int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName ="";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nha_hang, container, false);
        //Thêm thông tin phần tài khoản màn hình chính
        anhxa(v);

        kiemTraQuyenDangNhap();

        rcv_nhahang =v.findViewById(R.id.rcv_restaurant);
        rcv_loainhahang =v.findViewById(R.id.rcv_categoryRes);

        //Gọi Firebase xuống
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Lấy danh yêu thích
        getAllYeuThich(getContext());

        //Lấy danh sách đánh giá xuống
        getAllDanhGia(getContext());

        //Loại nhà hàng
        rcv_loainhahang.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcv_loainhahang, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Gán biến cho giá trị vị trí loại nhà hàng
                viTriLoaiNH = position;
                getAllNhaHangTheoLoai(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        // RecycleView nhà hàng
        rcv_nhahang.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcv_nhahang, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
//                deleteNhaHangFireBase(position);
            }
        }));

        //Nhấn nút thêm loại nhà hàng
        imvThemLoaiNH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_themLoaiNH(0);
            }
        });

        //Nhấn nút để thêm nhà hàng
        flBtnThemNH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_themNH(0);
            }
        });

        //Nhấn tìm kiếm nút search
        search();

        return v;
    }

    public String getMaTK(){
        return _maTK;
    }

    public void chuyenDenFragmentMonAN(String _maNH, String _tenNH, String _hinhNH, int _phiVanChuyen, String _thoiGian, Double _danhGia, String _maDG){
        Bundle bundle = new Bundle();
        bundle.putString("MaNH", _maNH);
        bundle.putString("TenNH", _tenNH);
        bundle.putString("HinhAnh", _hinhNH);
        bundle.putInt("PhiVanChuyen", _phiVanChuyen);
        bundle.putString("ThoiGian", _thoiGian);
        bundle.putDouble("DanhGia", _danhGia);
        bundle.putString("MaDanhGia", _maDG);
        MonAnFragment monAnFragment = new MonAnFragment();
        monAnFragment.setArguments(bundle);

        //getFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, monAnFragment).commit();

        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.nav_FrameFragment, monAnFragment)
                .addToBackStack(null)
                .commit();
    }


    //Kiểm tra những nhà hàng mà tài khoản yêu thích
    public Boolean check_favorite(String _maNH, int position){
        try {
            for(YeuThich yt: listYeuThich){
                if(yt.getMaNH().equals(_maNH)){
                    listNhaHang.get(position).setMaYT(yt.getMaYT());
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }

        return false;
    }


    private void anhxa(View v){
        tvTenTK = v.findViewById(R.id.tv_tenTaiKhoan_NhaHang);
        tipSoDuTK = v.findViewById(R.id.tip_soDuTK);
        imvThemLoaiNH = v.findViewById(R.id.imv_addLoaiNhFragNH);
        flBtnThemNH = v.findViewById(R.id.fbtn_themNhaHang);
        svNhaHang  = v.findViewById(R.id.sv_nhaHang);
        imvAvatar = v.findViewById(R.id.imv_avatarTaiKhoan_NhaHang);

        Intent intent = getActivity().getIntent();
        String tentk = intent.getStringExtra("HoTen");
        _maTK = intent.getStringExtra("MaTK");
        QuyenDN = intent.getIntExtra("Quyen", 2);
        String hinhAnh = intent.getStringExtra("HinhAnh");
        int soDu = Integer.parseInt(intent.getStringExtra("SoDu"));

        tvTenTK.setText(tentk);
        tipSoDuTK.getEditText().setText("Số dư    "+formatNumber(soDu)+" VND");

        if(hinhAnh.isEmpty()){
            imvAvatar.setImageResource(R.drawable.avatar);
        }else Picasso.with(getContext()).load(hinhAnh).into(imvAvatar);
    }

    //Kiểm tra quyền đăng nhập phù hợp với người dùng
    public void kiemTraQuyenDangNhap(){
        if(QuyenDN >= 1){
            flBtnThemNH.setVisibility(View.INVISIBLE);
            imvThemLoaiNH.setVisibility(View.INVISIBLE);
        }
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
                            String MaNH = doc.get("MaNH").toString().trim();
                            String MaLoaiNH = doc.get("MaLoaiNH").toString().trim();
                            String MaTK = doc.get("MaTK").toString().trim();
                            String TenNH = doc.get("TenNH").toString().trim();
                            String ThoiGian = doc.get("ThoiGian").toString().trim();
                            int PhiVanChuyen = Integer.parseInt(doc.get("PhiVanChuyen").toString());
                            String HinhAnh = doc.get("HinhAnh").toString();
                            String MaDG = doc.get("MaDG").toString().trim();

                            Double danhGia = tinhDanhGiaTB(MaNH);
                            nhaHang = new NhaHang(MaNH, MaLoaiNH, MaTK, TenNH, ThoiGian, PhiVanChuyen, HinhAnh, danhGia, MaDG, "");
                            listNhaHang.add(nhaHang);

                        }
//                        NhaHangAdapter adapter  = new NhaHangAdapter(listNhaHang, getContext());
//                        rcv_nhahang.setLayoutManager(new LinearLayoutManager(getContext()));
//                        rcv_nhahang.setAdapter(adapter);

                          //Xuất danh sách nhà hàng được chọn theo loại

                          getAllNhaHangTheoLoai(0);
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

    //Lấy tên loại của nhà hàng
    public String getTenLoaiNhaHang(String maLoaiNH){
        for (LoaiNhaHang loai: listLoaiNhaHang){
            if(maLoaiNH.equals(loai.getMaLoaiNH()))
                return loai.getTenLoaiNH();
        }
        return "";
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

                        //Lấy danh sách loại nhà hàng xuống
                        getAllLoaiNhaHang(getContext());

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(),"Error getAllDanhGia: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Error getLoaiNhaHang "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Lấy danh sách yeu thích
    public void getAllYeuThich(Context context){
        listYeuThich = new ArrayList<>();

        final CollectionReference reference = db.collection("YEUTHICH");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String maNH = doc.get("MaNH").toString();
                            String maTK = doc.get("MaTK").toString();
                            String maYT = doc.get("MaYT").toString();

                            if (maTK.equals(_maTK)) {
                                YeuThich yt = new YeuThich(maNH, maTK, maYT);
                                listYeuThich.add(yt);
                            }
                        }

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Error getAllYeuThich"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Xuất List nhà hàng theo loại nhà hàng chọn
    private void getAllNhaHangTheoLoai(int position){
        try {
            //Xuất list lên recycleView nhà hàng
            listNhaHangTheoLoai = new ArrayList<>();

            String maLoai = listLoaiNhaHang.get(position).getMaLoaiNH();
            for (int i=0; i<listNhaHang.size(); i++) {
                if (position == 0 && check_favorite(listNhaHang.get(i).getMaNH(), i)) {
                    //Lấy yêu thích của tài khoản
                    listNhaHangTheoLoai.add(listNhaHang.get(i));
                } else if (listNhaHang.get(i).getMaLoaiNH().equals(maLoai) && position != 0) {
                    listNhaHangTheoLoai.add(listNhaHang.get(i));
                }
            }

            // Lấy danh sách mã loại nhà hàng để đẩy lên spinner
            listMaLoaiNH = new ArrayList<>();
            for (LoaiNhaHang lnh : listLoaiNhaHang) {
                if(!lnh.getMaLoaiNH().equals("LNH01")) listMaLoaiNH.add(lnh.getMaLoaiNH());
            }

            NhaHangAdapter adapter = new NhaHangAdapter(listNhaHangTheoLoai, getContext(), this);
            rcv_nhahang.setLayoutManager(new LinearLayoutManager(getContext()));
            rcv_nhahang.setAdapter(adapter);
        }catch (Exception e){
            //Toast.makeText(getContext(), "Error adapter nha hang: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("===> ", "Error adapter nha hang: " + e.getMessage());
        }

    }

    //Dialog thêm loại nhà hàng
    private void dialog_themLoaiNH(int positon){
        dialogThemLoaiNH =  new Dialog(getContext());
        dialogThemLoaiNH.setContentView(R.layout.dialog_themloainh);

        dialogThemLoaiNH.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.9);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.6);
        dialogThemLoaiNH.getWindow().setLayout(width,height);

        EditText edtTenLoai = dialogThemLoaiNH.findViewById(R.id.edt_dialogThemTenLoaiNH);
        imvHinhLoai = dialogThemLoaiNH.findViewById(R.id.imv_dialogThemHinhLoaiNH);
        TextView tvHuyThem = dialogThemLoaiNH.findViewById(R.id.tv_dialogHuyThemLoaiNH);
        TextView tvXacNhan = dialogThemLoaiNH.findViewById(R.id.tv_dialogXacNhanThemLoaiNH);

        imvHinhLoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent lib = new Intent(Intent.ACTION_GET_CONTENT);
                lib.setType("image/*");

                Intent chua = Intent.createChooser(cam, "Chọn");
                chua.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{lib});

                startActivityForResult(chua, 999);
//                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });


        tvXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenLoai = edtTenLoai.getText().toString().trim();

                if(tenLoai.isEmpty()){
                    Toast.makeText(getContext(), "Vui lòng nhập tên loại nhà hàng", Toast.LENGTH_SHORT).show();
                }else if(!kiemKhoangTrang(tenLoai)){
                    Toast.makeText(getContext(), "Không được nhập khoảng trắng", Toast.LENGTH_SHORT).show();
                }else{

                    UUID uuid = UUID.randomUUID();
                    String maLoai = String.valueOf(uuid);

                    loaiNhaHang = new LoaiNhaHang(maLoai, tenLoai, "");

                    uploadImageToFirebase(imageFileName, contenUri);
                }


            }
        });

        tvHuyThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogThemLoaiNH.dismiss();
            }
        });

        dialogThemLoaiNH.show();
    }


    //Dialog thêm nhà hàng
    private void dialog_themNH(int positon){
        dialogThemNH =  new Dialog(getContext());
        dialogThemNH.setContentView(R.layout.dialog_themnhahang);

        dialogThemNH.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.9);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.8);
        dialogThemNH.getWindow().setLayout(width,height);

        EditText edtPhiChuyenNh = dialogThemNH.findViewById(R.id.ed_dialogPhiVCThemNH);
        EditText edtTenNH = dialogThemNH.findViewById(R.id.ed_dialogTenNHThemNH);
        EditText edtThoiGian = dialogThemNH.findViewById(R.id.ed_dialogThoiGianGiaoThemNH);
        spMaLoaiNH = dialogThemNH.findViewById(R.id.sp_dialogMaLoaiNHThemNH);
        spMaTK = dialogThemNH.findViewById(R.id.sp_dialogMaTKThemNH);
        imvHinh = dialogThemNH.findViewById(R.id.imv_dialogHinhThemNH);
        TextView tvHuyThem = dialogThemNH.findViewById(R.id.tv_dialogHuyThemNH);
        TextView tvXacNhan = dialogThemNH.findViewById(R.id.tv_dialogXacNhanThemNH);

        //Lấy danh sách mã loại nhà hàng lên spinner
        getMaLoaiLoaiNHToSpiner(0);
        //Lấy danh sách mã tài khoản lê spinner
        getMaLoaiTKToSpiner(0);

        imvHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent lib = new Intent(Intent.ACTION_GET_CONTENT);
                lib.setType("image/*");

                Intent chua = Intent.createChooser(cam, "Chọn");
                chua.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{lib});

                startActivityForResult(chua, 888);
                //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        tvXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenNH = edtTenNH.getText().toString().trim();
                String thoiGian = edtThoiGian.getText().toString().trim();
                String phiVanChuyen = edtPhiChuyenNh.getText().toString().trim();

                if(!kiemLoiONhap(tenNH, thoiGian, phiVanChuyen).isEmpty()){
                    Toast.makeText(getContext(), kiemLoiONhap(tenNH, thoiGian, phiVanChuyen), Toast.LENGTH_SHORT).show();
                }else{
                    UUID uuid = UUID.randomUUID();
                    String maNH = String.valueOf(uuid);

                    int ship = Integer.parseInt(phiVanChuyen);
                    //Thêm đánh giá và thêm nhà hàng lên Firebase
                    themDanhGiaNHToFireStore(maNH, tenNH, thoiGian, ship);

                }
            }
        });

        tvHuyThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogThemNH.dismiss();
            }
        });

        dialogThemNH.show();
    }

    private String kiemLoiONhap(String tenNh, String thoiGian, String phiVC){
        String loi = "";
        try {
            if (tenNh.isEmpty()) loi += "\nBạn chưa nhập tên nhà hàng";
            else if (!kiemKhoangTrang(tenNh))
                loi += "\nTên nhà hàng không được nhập khoảng trằng";

            if (thoiGian.isEmpty()) loi += "\nBạn chưa nhập thời gian giao hàng";
            else if (!kiemKhoangTrang(thoiGian))
                loi += "\nThời gian không được nhập khoảng trắng";

            int _thoiGian = Integer.parseInt(thoiGian);
            if (_thoiGian <= 1) loi += "\nThời gian giao hàng phải lớn hơn một";


            if (phiVC.isEmpty()) loi += "\nBạn chưa nhập phí vận chuyển";
        }catch (Exception e){
            loi += "\n" + e.getMessage();
        }
        return loi;

    }

    private Boolean kiemKhoangTrang(String _duLieu){
        for (int i = 0; i < _duLieu.length(); i++) {
            if(!Character.isWhitespace(_duLieu.charAt(i))){
                return true;
            }
        }
        return false;
    }

    //////////////// Spinner mã loại nhà hàng

    private void getMaLoaiLoaiNHToSpiner(int chucNang){

//        listMaLoaiNH = new ArrayList<>();
//
//        for(LoaiNhaHang lnh: listLoaiNhaHang){
//            listMaLoaiNH.add(lnh.getMaLoaiNH());
//        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, listMaLoaiNH);

        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(chucNang == 0){
            spMaLoaiNH.setAdapter(adapter);
        }else{
            spMaLoaiNHSuaNH.setAdapter(adapter);
        }

    }



    //////////////// Spinner mã tài khoản

    private void getMaLoaiTKToSpiner(int chucNang){

        listMaTK = new ArrayList<>();

        final CollectionReference reference = db.collection("TAIKHOAN");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaTK = doc.get("MaTK").toString();
                            String _quyen = doc.get("Quyen").toString();

                            //Không cho admin làm chủ hàng hàng
                            if(!_quyen.equals("0")) listMaTK.add(MaTK);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1, listMaTK);

                        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        if(chucNang == 0) {
                            spMaTK.setAdapter(adapter);
                        }else{
                            spMaTKSuaNH.setAdapter(adapter);
                        }
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Error getMaLoaiToSpinner"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //Dialog sửa nhà hàng
    public void dialog_suaNH(int positon){
        dialogSuaNH =  new Dialog(getContext());
        dialogSuaNH.setContentView(R.layout.dialog_suanhahang);

        dialogSuaNH.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels*0.9);
        int height = (int)(getContext().getResources().getDisplayMetrics().heightPixels*0.8);
        dialogSuaNH.getWindow().setLayout(width,height);

        EditText edtPhiChuyenNh = dialogSuaNH.findViewById(R.id.ed_dialogPhiVCSuaNH);
        EditText edtTenNH = dialogSuaNH.findViewById(R.id.ed_dialogTenNHSuaNH);
        EditText edtThoiGian = dialogSuaNH.findViewById(R.id.ed_dialogThoiGianGiaoSuaNH);
        spMaLoaiNHSuaNH = dialogSuaNH.findViewById(R.id.sp_dialogMaLoaiNHSuaNH);
        spMaTKSuaNH = dialogSuaNH.findViewById(R.id.sp_dialogMaTKSuaNH);
        imvHinhSuaNH = dialogSuaNH.findViewById(R.id.imv_dialogHinhSuaNH);
        TextView tvHuyThem = dialogSuaNH.findViewById(R.id.tv_dialogHuySuaNH);
        TextView tvXacNhan = dialogSuaNH.findViewById(R.id.tv_dialogXacNhanSuaNH);
        imvHinhSuaNH = dialogSuaNH.findViewById(R.id.imv_dialogHinhSuaNH);

        //Lấy danh sách mã loại nhà hàng lên spinner
        getMaLoaiLoaiNHToSpiner(1);
        //Lấy danh sách mã tài khoản lê spinner
        getMaLoaiTKToSpiner(1);

        // Đẩy thông tin lên dialog sửa nhà hàng
        edtPhiChuyenNh.setText(listNhaHangTheoLoai.get(positon).getPhiVanChuyen()+"");
        edtTenNH.setText(listNhaHangTheoLoai.get(positon).getTenNH());
        edtThoiGian.setText(listNhaHangTheoLoai.get(positon).getThoiGian());
        if(listNhaHangTheoLoai.get(positon).getHinhAnh().isEmpty()){
            imvHinhSuaNH.setImageResource(R.drawable.im_food);
        }else{
            Picasso.with(getContext()).load(listNhaHangTheoLoai.get(positon).getHinhAnh()).resize(2048, 1600).centerCrop().onlyScaleDown().into(imvHinhSuaNH);
        }
        //Lưu đường dẫn hình ảnh
        imageFileName = listNhaHangTheoLoai.get(positon).getHinhAnh();

        tvHuyThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSuaNH.dismiss();
            }
        });

        imvHinhSuaNH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent lib = new Intent(Intent.ACTION_GET_CONTENT);
                lib.setType("image/*");

                Intent chua = Intent.createChooser(cam, "Chọn");
                chua.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{lib});

                startActivityForResult(chua, 777);
                //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        tvXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenNH = edtTenNH.getText().toString().trim();
                String thoiGian = edtThoiGian.getText().toString().trim();
                String phiVanChuyen = edtPhiChuyenNh.getText().toString().trim();

                if(!kiemLoiONhap(tenNH, thoiGian, phiVanChuyen).isEmpty()){
                    Toast.makeText(getContext(), kiemLoiONhap(tenNH, thoiGian, phiVanChuyen), Toast.LENGTH_SHORT).show();
                }else{
                    //Thêm đánh giá và thêm nhà hàng lên Firebase
                    String maNH = listNhaHangTheoLoai.get(positon).getMaNH();
                    String maDG = listNhaHangTheoLoai.get(positon).getMaDG();
                    String maYT = listNhaHangTheoLoai.get(positon).getMaYT();
                    Double danhGia = listNhaHangTheoLoai.get(positon).getDanhGia();

                    nhaHang  = new NhaHang(maNH, spMaLoaiNHSuaNH.getSelectedItem().toString(), spMaTKSuaNH.getSelectedItem().toString(), tenNH, thoiGian, Integer.parseInt(phiVanChuyen), "", danhGia, maDG, maYT);
                    //Đẩy hình ảnh lên firebase sau đó cập nhật tất cả dũ liệu lên Firebase
                    uploadImageNHToFirebase(imageFileName, contenUri, 1); // Số 0 là thêm nhà hàng, số 1 là sửa nhà hàng
                    dialogSuaNH.dismiss();
                }
            }
        });

        dialogSuaNH.show();
    }

    // Nhấn yêu thích nhà hàng
    public void press_favorite(int position){
        Random random =  new Random();
        int x = random.nextInt((10000-1+1)+1);
        String maYT = "YT" + x;

        Intent intent = getActivity().getIntent();
        String maTK = intent.getStringExtra("MaTK");

        String maNH = listNhaHangTheoLoai.get(position).getMaNH();

        themYeuThichNHToFireStore(new YeuThich(maNH, maTK, maYT));
    }

    // bỏ chọn yêu thích nhà hàng
    public void unpress_favorite(int position){
        db.collection("YEUTHICH").document(listNhaHangTheoLoai.get(position).getMaYT() + "")
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // Lấy danh yêu thích
                getAllYeuThich(getContext());

                //Lấy danh sách đánh giá xuống
                getAllDanhGia(getContext());
            }
        });
    }

    //Tìm kiếm nhà hàng

    private void search(){
        try {
            svNhaHang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    try {
                        listNHSearch = new ArrayList<>();

                        for (NhaHang nh : listNhaHangTheoLoai) {
                            String tenNh = nh.getTenNH();

                            if (tenNh.contains(query)) {
                                listNHSearch.add(nh);
                            }
                        }

                        getNhaHangSearch(listNHSearch);
                    }catch (Exception e){ Toast.makeText(getContext(), "Lỗi: chưa có dữ liệu", Toast.LENGTH_SHORT).show();}
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    try {
                        listNHSearch = new ArrayList<>();

                        for (NhaHang nh : listNhaHangTheoLoai) {
                            String tenNh = nh.getTenNH();

                            if (tenNh.contains(newText)) {
                                listNHSearch.add(nh);
                            }
                        }

                        getNhaHangSearch(listNHSearch);
                    }catch (Exception e){ Toast.makeText(getContext(), "Lỗi: chưa có dữ liệu", Toast.LENGTH_SHORT).show();}
                    return false;
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getNhaHangSearch(List<NhaHang> list){
        NhaHangAdapter adapter  = new NhaHangAdapter(list, getContext(), this);
        rcv_nhahang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_nhahang.setAdapter(adapter);
    }



    // Xử lí sự kiện load hình lên ImaveView
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == GALEERY_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                contenUri = data.getData();
//                String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                imageFileName = "JPEG_" + timSamp + "." + getFileExt(contenUri);
//                imvHinhLoai.setImageURI(contenUri);
//            }
//        }


        //Xử lí thêm ảnh lên imageview ảnh loại nhà hàng
        if (requestCode == 999 && resultCode == RESULT_OK){
            contenUri = data.getData();
            String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timSamp + "." + getFileExt(contenUri);
            if (data.getExtras() != null){
                Bundle caigio = data.getExtras();
                Bitmap bitmap = (Bitmap) caigio.get("data");
                imvHinhLoai.setImageBitmap(bitmap);
            }else{
                imvHinhLoai.setImageURI(contenUri);
            }
        }

        //Xử lí thêm ảnh lên imageview ảnh dialog thêm  nhà hàng
        if (requestCode == 888 && resultCode == RESULT_OK){
            contenUri = data.getData();
            String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timSamp + "." + getFileExt(contenUri);
            if (data.getExtras() != null){
                Bundle caigio = data.getExtras();
                Bitmap bitmap = (Bitmap) caigio.get("data");
                imvHinh.setImageBitmap(bitmap);
            }else{
                imvHinh.setImageURI(contenUri);
            }
        }

        //Xử lí thêm ảnh lên imageview ảnh  dialog sửa thông tin nhà hàng
        if (requestCode == 777 && resultCode == RESULT_OK){
            contenUri = data.getData();
            String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timSamp + "." + getFileExt(contenUri);
            if (data.getExtras() != null){
                Bundle caigio = data.getExtras();
                Bitmap bitmap = (Bitmap) caigio.get("data");
                imvHinhSuaNH.setImageBitmap(bitmap);
            }else{
                imvHinhSuaNH.setImageURI(contenUri);
            }
        }


    }

    private  String getFileExt(Uri uri){
        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }


    //Load hình lên folder hình ảnh của  nhà hàng
    private void uploadImageNHToFirebase(String name, Uri contentUri, int congViec){
        StorageReference image = storageReference.child("IM_NHAHANG/"+name);
        try {
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Log.d("==> Done", " Load hình ảnh lên Firebase thành công "+ uri.toString());
                            // Thêm nhà hàng lên firebase
                            nhaHang.setHinhAnh(uri.toString());
                            if(congViec == 0) {
                                themNHToFireStore();
                            }else{
                                updateFirebase(nhaHang);
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("==> Exception", e.getMessage());
                }
            });
        }catch (Exception e){
            if(congViec == 0) {
                themNHToFireStore();
            }else{
                nhaHang.setHinhAnh(imageFileName);
                updateFirebase(nhaHang);
            }
        }
    }

    //Load hình lên folder hình ảnh của loại nhà hàng
    private void uploadImageToFirebase(String name, Uri contentUri){
        StorageReference image = storageReference.child("IM_LOAINHAHANG/"+name);
        try {
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Log.d("==> Done", " Load hình ảnh lên Firebase thành công "+ uri.toString());
                            // Thêm nhà hàng lên firebase
                            loaiNhaHang.setHinhAnh(uri.toString());
                            themLoaiNHToFireStore(loaiNhaHang);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("==> Exception", e.getMessage());
                }
            });
        }catch (Exception e){
            loaiNhaHang.setHinhAnh("");
            themLoaiNHToFireStore(loaiNhaHang);
        }
    }


    // Cập nhập thông tin bảng nhà hàng lên Firebase
    private void updateFirebase(NhaHang nhaHang){
        final CollectionReference reference = db.collection("NHAHANG");
        try {
            Map map = new HashMap<String, Object>();
            map.put("MaNH", nhaHang.getMaNH());
            map.put("MaLoaiNH", nhaHang.getMaLoaiNH());
            map.put("MaTK", nhaHang.getMaTK());
            map.put("TenNH", nhaHang.getTenNH());
            map.put("ThoiGian", nhaHang.getThoiGian());
            map.put("PhiVanChuyen", nhaHang.getPhiVanChuyen());
            map.put("HinhAnh", nhaHang.getHinhAnh());
            map.put("MaDG", nhaHang.getMaDG());
            map.put("MaYT", nhaHang.getMaYT());
            reference.document(nhaHang.getMaNH() + "").set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Cập nhật lại listView
                    getAllNhaHang(getContext());
//                    getAllDanhGia(getContext());
                }
            });

        }catch (Exception e){
            Toast.makeText(getContext(), "Error update Firebase: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //Đẩy nhà hàng lên Firestore
    private void themLoaiNHToFireStore(LoaiNhaHang loaiNhaHang){
        final CollectionReference collectionReference = db.collection("LOAINHAHANG");

        Map<String, Object> data = new HashMap<>();
        data.put("MaLoaiNH", loaiNhaHang.getMaLoaiNH());
        data.put("TenLoaiNH", loaiNhaHang.getTenLoaiNH());
        data.put("HinhAnh", loaiNhaHang.getHinhAnh());

        try {
            collectionReference.document(loaiNhaHang.getMaLoaiNH() + "").set(data);
            dialogThemLoaiNH.dismiss();
            Toast.makeText(getContext(), "Thêm loại nhà hàng thành công", Toast.LENGTH_SHORT).show();
            getAllLoaiNhaHang(getContext());
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }
    }

    // Thêm bảng đánh giá khi thêm nhà hàng mới
    private void themDanhGiaNHToFireStore(String maNH, String tenNH, String thoiGian, int phiVanChuyen){
        final CollectionReference collectionReference = db.collection("DANHGIANH");

        UUID uuid = UUID.randomUUID();
        String MaDG = String.valueOf(uuid);

        Map<String, Object> data = new HashMap<>();
        data.put("MaDanhGia", MaDG);
        data.put("MaNH", maNH);
        data.put("TongDG", 0);
        data.put("LuotDG", 0);

        try {
            collectionReference.document(MaDG).set(data);

            nhaHang = new NhaHang(maNH, spMaLoaiNH.getSelectedItem().toString(), spMaTK.getSelectedItem().toString(), tenNH, thoiGian, phiVanChuyen, imageFileName, 0.0, MaDG, "");
            uploadImageNHToFirebase(imageFileName, contenUri, 0); // Số 0 là thêm nhà hàng, số 1 là sửa nhà hàng
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }

    }

    // Thêm object nhà hàng lên Firebase
    private void themNHToFireStore(){

        final CollectionReference collectionReference = db.collection("NHAHANG");

        Map<String, Object> data = new HashMap<>();
        data.put("MaLoaiNH", nhaHang.getMaLoaiNH());
        data.put("MaTK", nhaHang.getMaTK());
        data.put("HinhAnh", nhaHang.getHinhAnh());
        data.put("MaNH", nhaHang.getMaNH());
        data.put("PhiVanChuyen", nhaHang.getPhiVanChuyen());
        data.put("ThoiGian", nhaHang.getThoiGian());
        data.put("TenNH", nhaHang.getTenNH());
        data.put("MaDG", nhaHang.getMaDG());


        try {
            //Cho tài khoản thành quyền chủ nhà hàng
            db.collection("TAIKHOAN").document(nhaHang.getMaTK())
                    .update(
                            "Quyen", 1
                    );

            collectionReference.document(nhaHang.getMaNH()).set(data);
            dialogThemNH.dismiss();
            Toast.makeText(getContext(), "Thêm mã nhà hàng thành công", Toast.LENGTH_SHORT).show();
            getAllNhaHang(getContext());
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }
    }


    // Thêm object nhà hàng lên Firebase
    private void themYeuThichNHToFireStore(YeuThich yt){
        final CollectionReference collectionReference = db.collection("YEUTHICH");

        Map<String, Object> data = new HashMap<>();
        data.put("MaNH", yt.getMaNH());
        data.put("MaTK", yt.getMaTK());
        data.put("MaYT", yt.getMaYT());

        try {
            collectionReference.document(yt.getMaYT() + "").set(data);

            // Lấy danh yêu thích
            getAllYeuThich(getContext());

            //Lấy danh sách đánh giá xuống
            getAllDanhGia(getContext());
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }
    }


    // Delete nhà hàng
    private void deleteNhaHangFireBase(int positon){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn xóa nhà hàng không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            // Delete bảng nhà hàng
                            db.collection("NHAHANG").document(listNhaHangTheoLoai.get(positon).getMaNH() + "")
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    getAllNhaHangTheoLoai(viTriLoaiNH);
                                    Toast.makeText(getContext(), "Xóa nhà hàng thành công", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //Delete bảng dánh giá nhà hàng có mã nhà hàng vừa xóa
                            db.collection("DANHGIANH").document(listNhaHangTheoLoai.get(positon).getMaDG() + "")
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });

                            //Delete bảng YEUTHICH có cùng mã nhà hàng vừa xóa
                            db.collection("YEUTHICH").document(listNhaHangTheoLoai.get(positon).getMaYT() + "")
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });

                            //Xóa món ăn của nhà hàng
                            deleteMonAnNHFireBase(listNhaHangTheoLoai.get(positon).getMaNH());
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


    // Delete giỏ hàng từ mã món ăn
    private void deleteGioHangFireBase(String _maMA){

        try {
            final CollectionReference reference = db.collection("GIOHANGCT");
            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    try {
                        if(task.isSuccessful()){
                            QuerySnapshot snapshot = task.getResult();
                            for(QueryDocumentSnapshot doc: snapshot){
                                String maMA = doc.get("MaMA").toString();
                                String maGHCT = doc.get("MaGHCT").toString();
                                int trangThai = Integer.parseInt(doc.get("TrangThai").toString());

                                if (maMA.equals(_maMA) && trangThai == 0) {
                                    // Delete bảng nhà hàng
                                    db.collection("GIOHANGCT").document(maGHCT)
                                            .delete();
                                }
                            }
                        }else{
                            Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Error getAllYeuThich"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    // Delete món ăn từ mã nhà hàng
    private void deleteMonAnNHFireBase(String _maNH){

        try {

            final CollectionReference reference = db.collection("MONANNH");
            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    try {
                        if(task.isSuccessful()){
                            QuerySnapshot snapshot = task.getResult();
                            for(QueryDocumentSnapshot doc: snapshot){
                                String maMA = doc.get("MAMA").toString();
                                String maNH = doc.get("MaNH").toString();

                                if (maNH.equals(_maNH)) {
                                    // Delete bảng nhà hàng
                                    db.collection("MONANNH").document(maMA)
                                            .delete();


                                    deleteGioHangFireBase(maMA);
                                }
                            }

                        }else{
                            Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Error getAllYeuThich"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}