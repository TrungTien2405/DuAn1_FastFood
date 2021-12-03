package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.TaiKhoan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DangKyActivity extends AppCompatActivity {

    private EditText edHoTen, edSoDT, edDiaChi, edMaOTP;

    private ImageButton imBtn_ThemHinhDK;
    private ImageView imgTrove, imgHinhDK;

    private Button btnDangKy, btnXacThucOTP;

    // variable for FirebaseAuth class xác thực OTP
    private FirebaseAuth mAuth;
    // string for storing our verification ID OTP
    private String verificationId;

    //CSDL
    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;

    private Dialog dialogOTP, diaLogThongBaoThongTinTK;

    //Load image
    int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName = "";
    private String imageHinhLuu = "";

    private List<String> listSoDTKhachHang;
    private List<TaiKhoan> listTaiKhoan;

    private TaiKhoan taiKhoan;
    private int Quyen = 2;
    private String soDTTT;
    private String diaChi, hoTen, soDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        edHoTen = findViewById(R.id.ed_HoTen);
        edSoDT = findViewById(R.id.ed_SDT);
        edDiaChi = findViewById(R.id.ed_DiaChi);
        imBtn_ThemHinhDK = findViewById(R.id.imBtn_ThemHinhDK);
        imgTrove = findViewById(R.id.imgTrove);
        imgHinhDK = findViewById(R.id.imgHinhDK);
        btnDangKy = findViewById(R.id.btnDangKy);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        getSoDT();
        getAllTaiKhoan(getApplication());

        imgTrove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
               startActivity(intent);
            }
        });

        // Thêm ảnh đại diện
        imBtn_ThemHinhDK.setOnClickListener(new View.OnClickListener() {

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

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                     hoTen = edHoTen.getText().toString();
                     soDT = edSoDT.getText().toString().trim();
                    //trim() xóa kí tự trắng ở đầu và cuối của số điện thoại
                    diaChi = edDiaChi.getText().toString();

                    if(!kiemLoiNhap(hoTen, soDT, diaChi).isEmpty()) {
                        Toast.makeText(getApplicationContext(), kiemLoiNhap(hoTen, soDT, diaChi), Toast.LENGTH_SHORT).show();
                    }else{
                        //Load hình ảnh lên firebase
                        if(imageFileName.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Chưa được thêm hình ảnh", Toast.LENGTH_SHORT).show();
                        }else {
                            //Thêm tài khoản vào database
//                            Random random = new Random();
//                            int x = random.nextInt((1000-1+1)+1);
//                            String maTK = "TK" + x;
                            UUID uuid = UUID.randomUUID();
                            String maTK = String.valueOf(uuid);

                            taiKhoan = new TaiKhoan(maTK, hoTen, soDT, soDT, diaChi, 2, imageFileName,1000000);

                            db = FirebaseFirestore.getInstance();

//                           Kiểm tra mã tài khoản đã tồn tại chưa
                            final CollectionReference reference = db.collection("TAIKHOAN");
                            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int check = 0;
                                    if(task.isSuccessful()){
                                        QuerySnapshot snapshot = task.getResult();
                                        for(QueryDocumentSnapshot doc: snapshot){
                                            //Log.d("=======> ", doc.get("MaTK").toString());
                                            if(String.valueOf(taiKhoan.getMaTK()).equals(doc.get("MaTK").toString()) || taiKhoan.getSDT().equals(doc.get("SDT").toString())){
                                                check = 1;
                                                break;
                                            }
                                        }
                                        if(check == 0){
                                            //Mở dialog xác thực OTP
                                            diaLogOpenOTP();
                                        }else
                                            Toast.makeText(getApplicationContext(), "Mã tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Lỗi " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //
    public void getAllTaiKhoan(Context context){
        listTaiKhoan = new ArrayList<>();

        final CollectionReference reference = db.collection("TAIKHOAN");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String maTK = doc.get("MaTK").toString();
                            String hoTen = doc.get("HoTen").toString();
                            String matKhau = doc.get("MatKhau").toString();
                            String soDT = doc.get("SDT").toString();
                            String diaChi = doc.get("DiaChi").toString();
                            int quyen = Integer.parseInt(doc.get("Quyen").toString());
                            String hinhAnh = doc.get("HinhAnh").toString();
                            int soDu = Integer.parseInt(doc.get("SoDu").toString());

                            taiKhoan = new TaiKhoan(maTK, hoTen, matKhau, soDT, diaChi, quyen, hinhAnh, soDu);
                            listTaiKhoan.add(taiKhoan);
                        }
                    }else{
                        Toast.makeText(DangKyActivity.this, "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d("=====> ", "Lỗi get tài khoản: " + e.getMessage());
                }
            }
        });
    }

    //lấy số điện thoại trong danh sách tài khoản của firestore xuống, lưu vào listSoDTKhachHang
    public void getSoDT(){
        listSoDTKhachHang = new ArrayList<>();

        final CollectionReference reference = db.collection("TAIKHOAN");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            soDTTT = doc.get("SDT").toString();

                            listSoDTKhachHang.add(soDTTT);
                        }
                    }else{
                        Toast.makeText(DangKyActivity.this, "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d("=====> ", "Lỗi: " + e.getMessage());
                }
            }
        });
    }

    //hàm kiểm lỗi nhập
    private String kiemLoiNhap(String _hoTen, String _SDT, String _diaChi) {
        String loi = "";
        //pattern định dạng số điện thoại chỉ được số 03, 08, 09
        String pattern = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
        if (_hoTen.isEmpty())
            loi += "Bạn chưa nhập họ tên";
        else if (!kiemKhoangTrang(_hoTen))
            loi += "Không được nhập khoảng trắng";
        else if (_hoTen.length()<5 || _hoTen.length()>30)
            loi += "Họ tên bao gồm 5 đến 30 kí tự";


        if(_SDT.isEmpty()) {
            loi += "\nBạn chưa nhập số điện thoại";
        }else if(!_SDT.matches(pattern)){
            loi += "\nSố điện thoại không đúng định dạng";
        }else if(kiemTraSoDTTonTai(_SDT)){
            loi += "\nSố điện thoại đã tồn tại";
        }

        if (_diaChi.isEmpty())
            loi += "\nBạn chưa nhập địa chỉ";
        else if (!kiemKhoangTrang(_hoTen))
            loi += "Không được nhập khoảng trắng";
        else if(_diaChi.length()<5 || _diaChi.length()>30)
            loi += "\nĐịa chỉ bao gồm 5 đến 30 kí tự";

        return loi;
    }

    //kiểm tra số điện thoại đã được sử dụng thì không được đăng ký nữa
    private boolean kiemTraSoDTTonTai(String _duLieu){
        try {
            for(String _soDTTT: listSoDTKhachHang){
                if(_soDTTT.equals(_duLieu)){
                    return true;
                }
            }
        }catch (Exception e){
            Log.d("======> ", "Lỗi " + e.getMessage());
            return false;
        }

        return false;
    }

    //hàm kiểm tra lỗi nhập khoảng trắng toàn bộ Edittext
    private Boolean kiemKhoangTrang(String _duLieu){
        for (int i = 0; i < _duLieu.length(); i++) {
            if(!Character.isWhitespace(_duLieu.charAt(i))){
                return true;
            }
        }
        return false;
    }

    //hàm kiểm tra lỗi nhập khoảng cách trắng
    private Boolean kiemKhoangTrangSDT(String _duLieu){
        for (int i = 0; i < _duLieu.length(); i++) {
            if(Character.isWhitespace(_duLieu.charAt(i))){
                return true;
            }
        }
        return false;
    }

    //Thêm tài khoản vào firestore bằng mã tài khoản
    private void themTaiKhoanToFireStore(TaiKhoan taiKhoan){
        final CollectionReference collectionReference = db.collection("TAIKHOAN");

        Map<String, Object> data = new HashMap<>();
        data.put("MaTK", taiKhoan.getMaTK());
        data.put("HoTen", taiKhoan.getHoTen());
        data.put("DiaChi", taiKhoan.getDiaChi());
        data.put("MatKhau", taiKhoan.getMatKhau());
        data.put("SoDu", taiKhoan.getSoDu());
        data.put("SDT", taiKhoan.getSDT());
        data.put("Quyen", Quyen);
        data.put("HinhAnh", taiKhoan.getHinhAnh());

        try {
            collectionReference.document(taiKhoan.getMaTK() + "").set(data);
        }catch (Exception e){
            Log.d("Error_addTKFirebase", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK){
            contenUri = data.getData();
            String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timSamp + "." + getFileExt(contenUri);
            if (data.getExtras() != null){
                Bundle caigio = data.getExtras();
                Bitmap bitmap = (Bitmap) caigio.get("data");
                imgHinhDK.setImageBitmap(bitmap);
            }else{
                imgHinhDK.setImageURI(contenUri);
            }
        }
    }

    private  String getFileExt(Uri uri){
        ContentResolver c = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }

    private void uploadImageToFirebase(String name, Uri contentUri){
        StorageReference image = storageReference.child("IM_TAIKHOAN/"+name);
        try {
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                            Log.d("==> Done", " Load hình ảnh lên Firebase thành công "+ uri.toString());
                            // Thêm tài khoản lên firebase
                            taiKhoan.setHinhAnh(uri.toString());
                            imageHinhLuu = uri.toString();
                            Log.d("=====>", "Lỗi uri: " + uri.toString());
                            themTaiKhoanToFireStore(taiKhoan);

                            //hiển thị dialog thông tin tài khoản
                            diaLogThongBaoThongTinTK();
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
            taiKhoan.setHinhAnh("");
            themTaiKhoanToFireStore(taiKhoan);
        }
    }

    //dialog xác thực mã otp
    private void diaLogOpenOTP(){
        // Gửi mã OTP đến điện thoại
        sendVerificationCode(edSoDT.getText().toString());

        dialogOTP = new Dialog(DangKyActivity.this);
        dialogOTP.setContentView(R.layout.dialog_otp_firebase);

        dialogOTP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.6);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        dialogOTP.getWindow().setLayout(width, height);

        edMaOTP = dialogOTP.findViewById(R.id.edMaOTP);
        btnXacThucOTP = dialogOTP.findViewById(R.id.btnXacThucOTP);

        btnXacThucOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(edMaOTP.getText().toString());
            }
        });

        dialogOTP.show();
    }


    private void diaLogThongBaoThongTinTK(){
        diaLogThongBaoThongTinTK = new Dialog(DangKyActivity.this);
        diaLogThongBaoThongTinTK.setContentView(R.layout.dialog_thongtin_taikhoan);

        diaLogThongBaoThongTinTK.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.8);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.7);
        diaLogThongBaoThongTinTK.getWindow().setLayout(width, height);

        TextView tv_HoTenTTDK = diaLogThongBaoThongTinTK.findViewById(R.id.tv_dialogHoTenTTDK);
        TextView tv_SoDTTTDK = diaLogThongBaoThongTinTK.findViewById(R.id.tv_dialogSoDTTTDK);
        TextView tv_MatKhauTTDK = diaLogThongBaoThongTinTK.findViewById(R.id.tv_dialogMatKhauTTDK);
        TextView tv_DiaChiTTDK = diaLogThongBaoThongTinTK.findViewById(R.id.tv_dialogDiaChiTTDK);
        ImageView imv_HinhTTDK = diaLogThongBaoThongTinTK.findViewById(R.id.imv_dialogHinhTTDK);
        TextView tv_XacNhanTTDK = diaLogThongBaoThongTinTK.findViewById(R.id.tv_dialogXacNhanTTDK);

        tv_HoTenTTDK.setText(hoTen);
        tv_SoDTTTDK.setText(soDT);
        tv_MatKhauTTDK.setText(soDT);
        tv_DiaChiTTDK.setText(diaChi);

        Log.d("======>", "Hinh ảnh"+ imageHinhLuu);
        if(imageHinhLuu.isEmpty()){

            imv_HinhTTDK.setImageResource(R.drawable.im_food);
        }else{
            Picasso.with(getApplication()).load(imageHinhLuu).resize(2048, 1600).centerCrop().onlyScaleDown().into(imv_HinhTTDK);
        }

        tv_XacNhanTTDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               diaLogThongBaoThongTinTK.dismiss();

                Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
                startActivity(intent);
            }
        });

        diaLogThongBaoThongTinTK.show();
    }

    // Gửi mã xác thực OTP
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                edMaOTP.setText(code);
                //verifying the code
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(DangKyActivity.this, "PhoneAuthProvider "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            //ResendToken = forceResendingToken;
        }
    };

    //Gửi mã xác thực
    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+84"+mobile)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(DangKyActivity.this)
                        .setCallbacks(mCallback)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Tiến hành xác thực và đăng nhập vào ứng dụng
    private void verifyCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        //signing the user
        signInWithCredential(credential);
    }

    //
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(DangKyActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DangKyActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();
                            // Load avatar lên firebase
                            uploadImageToFirebase(imageFileName, contenUri);
                            dialogOTP.dismiss();


                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            Toast.makeText(DangKyActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
}