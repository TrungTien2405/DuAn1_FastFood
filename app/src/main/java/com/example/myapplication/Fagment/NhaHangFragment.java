package com.example.myapplication.Fagment;

import android.app.Activity;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.LoaiNhaHangAdapter;
import com.example.myapplication.Adapter.NhaHangAdapter;
import com.example.myapplication.Model.DanhGiaNH;
import com.example.myapplication.Model.LoaiNhaHang;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.Model.TaiKhoan;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;


public class NhaHangFragment extends Fragment {
    private List<NhaHang> listNhaHang;
    private List<NhaHang> listNhaHangTheoLoai;
    private List<DanhGiaNH> listDanhGia;
    private List<LoaiNhaHang> listLoaiNhaHang;

    //List cho spinner
    private List<String> listMaLoaiNH;
    private List<String> listMaTK;


    private NhaHang nhaHang;
    private LoaiNhaHang loaiNhaHang;

    private RecyclerView rcv_nhahang;
    private RecyclerView rcv_loainhahang;

    private TextView tvTenTK;
    private TextInputLayout tipSoDuTK;
    private ImageView imvThemLoaiNH;
    private FloatingActionButton flBtnThemNH;

    //Dialog
    private Spinner spMaTK;
    private Spinner spMaLoaiNH;

    private Dialog dialogThemLoaiNH;
    private Dialog dialogThemNH;
    private Dialog dialogSuaNH;

    public int viTriLoaiNH = 0 ;

    //Dialog hình thêm loại nhà hàng
    private ImageView imvHinhLoai;

    //Dialog Hình thêm nhà hàng
    private ImageView imvHinh;


    // variable for FirebaseAuth class xác thực OTP
    private FirebaseAuth mAuth;
    //Firestore
    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;


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

        rcv_nhahang =v.findViewById(R.id.rcv_restaurant);
        rcv_loainhahang =v.findViewById(R.id.rcv_categoryRes);

        //Gọi Firebase xuống
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        //Lấy danh sách đánh giá xuống
        getAllDanhGia(getContext());


        //Loại nhà hàng
        rcv_loainhahang.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcv_loainhahang, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                getAllNhaHangTheoLoai(listLoaiNhaHang.get(position).getTenLoaiNH());
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
                deleteNhaHangFireBase(position);
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

        return v;
    }

    private void anhxa(View v){
        tvTenTK = v.findViewById(R.id.tv_tenTaiKhoan_NhaHang);
        tipSoDuTK = v.findViewById(R.id.tip_soDuTK);
        imvThemLoaiNH = v.findViewById(R.id.imv_addLoaiNhFragNH);
        flBtnThemNH = v.findViewById(R.id.fbtn_themNhaHang);


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
                                    getAllNhaHangTheoLoai("NH01");
                                    Toast.makeText(getContext(), "Xóa bảng thành công", Toast.LENGTH_SHORT).show();
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
                            String MaYT = doc.get("MaYT").toString();

                            nhaHang = new NhaHang(MaNH, getTenLoaiNhaHang(MaLoaiNH), MaTK, TenNH, ThoiGian, PhiVanChuyen, HinhAnh, tinhDanhGiaTB(MaNH), MaDG, MaYT);
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

        // Lấy danh sách mã loại nhà hàng để đẩy lên spinner
        listMaLoaiNH = new ArrayList<>();
        for(LoaiNhaHang lnh: listLoaiNhaHang){
            listMaLoaiNH.add(lnh.getMaLoaiNH());
        }

        NhaHangAdapter adapter  = new NhaHangAdapter(listNhaHangTheoLoai, getContext(), this);
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

        EditText edtTenLoai = dialogThemLoaiNH.findViewById(R.id.edt_dialogThemTenLoaiNH);
        EditText edtMaLoai = dialogThemLoaiNH.findViewById(R.id.edt_dialogThemMaLoaiNH);
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
                String maLoai = edtMaLoai.getText().toString();
                String tenLoai = edtTenLoai.getText().toString();

                if(maLoai.isEmpty() || tenLoai.isEmpty()){
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }else{
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

        EditText edtMaNH = dialogThemNH.findViewById(R.id.ed_dialogMaNHThemNH);
        EditText edtPhiChuyenNh = dialogThemNH.findViewById(R.id.ed_dialogPhiVCThemNH);
        EditText edtTenNH = dialogThemNH.findViewById(R.id.ed_dialogTenNHThemNH);
        EditText edtThoiGian = dialogThemNH.findViewById(R.id.ed_dialogThoiGianGiaoThemNH);
        spMaLoaiNH = dialogThemNH.findViewById(R.id.sp_dialogMaLoaiNHThemNH);
        spMaTK = dialogThemNH.findViewById(R.id.sp_dialogMaTKThemNH);
        imvHinh = dialogThemNH.findViewById(R.id.imv_dialogHinhThemNH);
        TextView tvHuyThem = dialogThemNH.findViewById(R.id.tv_dialogHuyThemNH);
        TextView tvXacNhan = dialogThemNH.findViewById(R.id.tv_dialogXacNhanThemNH);

        //Lấy danh sách mã loại nhà hàng lên spinner
        getMaLoaiLoaiNHToSpiner();
        //Lấy danh sách mã tài khoản lê spinner
        getMaLoaiTKToSpiner();

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
                String maNH = edtMaNH.getText().toString();
                String tenNH = edtTenNH.getText().toString();
                String thoiGian = edtThoiGian.getText().toString();
                int phiVanChuyen = Integer.parseInt(edtPhiChuyenNh.getText().toString());

                if(maNH.isEmpty() || tenNH.isEmpty() || thoiGian.isEmpty() || phiVanChuyen == 0){
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }else{
                    //Thêm đánh giá và thêm nhà hàng lên Firebase
                    themDanhGiaNHToFireStore(maNH, tenNH, thoiGian, phiVanChuyen);

                }
            }
        });

        dialogThemNH.show();
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

        //Xử lí thêm ảnh lên imageview ảnh  nhà hàng
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


    }

    private  String getFileExt(Uri uri){
        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }


    //Load hình lên folder hình ảnh của  nhà hàng
    private void uploadImageNHToFirebase(String name, Uri contentUri){
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
                            themNHToFireStore(loaiNhaHang);
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
            nhaHang.setHinhAnh("");
            themNHToFireStore(loaiNhaHang);
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

        Random random =  new Random();
        int x = random.nextInt((10000-1+1)+1);
        String MaDG = "DG" + x;

        Map<String, Object> data = new HashMap<>();
        data.put("MaDanhGia", MaDG);
        data.put("MaNH", maNH);
        data.put("TongDG", 0);
        data.put("LuotDG", 0);


        try {
            collectionReference.document(MaDG).set(data);

            nhaHang = new NhaHang(maNH, spMaLoaiNH.getSelectedItem().toString(), spMaTK.getSelectedItem().toString(), tenNH, thoiGian, phiVanChuyen, imageFileName, 0.0, MaDG, "");
            uploadImageNHToFirebase(imageFileName, contenUri);
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }

    }

    // Thêm object nhà hàng lên Firebase
    private void themNHToFireStore(LoaiNhaHang loaiNhaHang){
        final CollectionReference collectionReference = db.collection("NHAHANG");

        Map<String, Object> data = new HashMap<>();
        data.put("MaLoaiNH", nhaHang.getMaLoaiNH());
        data.put("MaTK", nhaHang.getMaTK());
        data.put("HinhAnh", nhaHang.getHinhAnh());
        data.put("MaNH", nhaHang.getMaNH());
        data.put("PhiVanChuyen", nhaHang.getPhiVanChuyen());
        data.put("ThoiGian", nhaHang.getThoiGian());
        data.put("TenNH", nhaHang.getTenNH());


        try {
            collectionReference.document(nhaHang.getMaNH() + "").set(data);
            dialogThemNH.dismiss();
            Toast.makeText(getContext(), "Thêm mã nhà hàng thành công", Toast.LENGTH_SHORT).show();
            getAllNhaHang(getContext());
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }
    }

    //////////////// Spinner mã loại nhà hàng

    private void getMaLoaiLoaiNHToSpiner(){

//        listMaLoaiNH = new ArrayList<>();
//
//        for(LoaiNhaHang lnh: listLoaiNhaHang){
//            listMaLoaiNH.add(lnh.getMaLoaiNH());
//        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, listMaLoaiNH);

        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spMaLoaiNH.setAdapter(adapter);
    }



    //////////////// Spinner mã tài khoản

    private void getMaLoaiTKToSpiner(){

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

                            listMaTK.add(MaTK);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1, listMaTK);

                        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spMaTK.setAdapter(adapter);
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //Dialog sửa nhà hàng

    //Dialog sưa nhà hàng
    public void dialog_suaNH(int positon){
        dialogSuaNH =  new Dialog(getContext());
        dialogSuaNH.setContentView(R.layout.dialog_suanhahang);

        dialogSuaNH.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels*0.9);
        int height = (int)(getContext().getResources().getDisplayMetrics().heightPixels*0.8);
        dialogSuaNH.getWindow().setLayout(width,height);

        EditText edtMaNH = dialogSuaNH.findViewById(R.id.ed_dialogMaNHThemNH);
        EditText edtPhiChuyenNh = dialogSuaNH.findViewById(R.id.ed_dialogPhiVCThemNH);
        EditText edtTenNH = dialogSuaNH.findViewById(R.id.ed_dialogTenNHThemNH);
        EditText edtThoiGian = dialogSuaNH.findViewById(R.id.ed_dialogThoiGianGiaoThemNH);
        spMaLoaiNH = dialogSuaNH.findViewById(R.id.sp_dialogMaLoaiNHThemNH);
        spMaTK = dialogSuaNH.findViewById(R.id.sp_dialogMaTKThemNH);
        imvHinh = dialogSuaNH.findViewById(R.id.imv_dialogHinhThemNH);
        TextView tvHuyThem = dialogSuaNH.findViewById(R.id.tv_dialogHuyThemNH);
        TextView tvXacNhan = dialogSuaNH.findViewById(R.id.tv_dialogXacNhanThemNH);

//        //Lấy danh sách mã loại nhà hàng lên spinner
//        getMaLoaiLoaiNHToSpiner();
//        //Lấy danh sách mã tài khoản lê spinner
//        getMaLoaiTKToSpiner();
//
//        imvHinh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                Intent lib = new Intent(Intent.ACTION_GET_CONTENT);
//                lib.setType("image/*");
//
//                Intent chua = Intent.createChooser(cam, "Chọn");
//                chua.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{lib});
//
//                startActivityForResult(chua, 888);
//                //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                //startActivityForResult(gallery, GALEERY_REQUEST_CODE);
//            }
//        });
//
//        tvXacNhan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String maNH = edtMaNH.getText().toString();
//                String tenNH = edtTenNH.getText().toString();
//                String thoiGian = edtThoiGian.getText().toString();
//                int phiVanChuyen = Integer.parseInt(edtPhiChuyenNh.getText().toString());
//
//                if(maNH.isEmpty() || tenNH.isEmpty() || thoiGian.isEmpty() || phiVanChuyen == 0){
//                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
//                }else{
//                    //Thêm đánh giá và thêm nhà hàng lên Firebase
//                    themDanhGiaNHToFireStore(maNH, tenNH, thoiGian, phiVanChuyen);
//
//                }
//            }
//        });

        dialogSuaNH.show();
    }

}