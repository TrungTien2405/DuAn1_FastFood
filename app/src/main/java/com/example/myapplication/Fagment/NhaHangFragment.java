package com.example.myapplication.Fagment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.textfield.TextInputLayout;
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

import static android.app.Activity.RESULT_OK;


public class NhaHangFragment extends Fragment {
    private List<NhaHang> listNhaHang;
    private List<NhaHang> listNhaHangTheoLoai;
    private List<DanhGiaNH> listDanhGia;
    private List<LoaiNhaHang> listLoaiNhaHang;

    private NhaHang nhaHang;
    private LoaiNhaHang loaiNhaHang;

    private RecyclerView rcv_nhahang;
    private RecyclerView rcv_loainhahang;

    private TextView tvTenTK;
    private TextInputLayout tipSoDuTK;
    private ImageView imvThemLoaiNH;

    private Dialog dialogThemLoaiNH;

    public int viTriLoaiNH = 0 ;

    //Dialog thêm loại nhà hàng
    private ImageView imvHinhLoai;

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

        //Lấy danh sách đánh giá xuống
        getAllDanhGia(getContext());


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

        // Chọn hình ảnh
        imvHinhLoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        dialogThemLoaiNH.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALEERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                contenUri = data.getData();
                String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timSamp + "." + getFileExt(contenUri);
                imvHinhLoai.setImageURI(contenUri);
            }
        }

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
    }

    private  String getFileExt(Uri uri){
        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }

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


    private void themLoaiNHToFireStore(LoaiNhaHang loaiNhaHang){
        final CollectionReference collectionReference = db.collection("LOAINHAHANG");

        Map<String, Object> data = new HashMap<>();
        data.put("MaLoaiNH", loaiNhaHang.getMaLoaiNH());
        data.put("TenLoaiNH", loaiNhaHang.getTenLoaiNH());
        data.put("HinhAnh", loaiNhaHang.getHinhAnh());

        try {
            collectionReference.document(loaiNhaHang.getMaLoaiNH() + "").set(data);
            dialogThemLoaiNH.dismiss();
            Toast.makeText(getContext(), "Thêm mã loại nhà hàng thành công", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }
    }



}