package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    EditText edHoTen, edSoDT, edDiaChi, edMaOTP;

    ImageButton imBtn_ThemHinhDK;
    ImageView imgTrove, imgHinhDK;

    Button btnDangKy, btnXacThucOTP;

    // variable for FirebaseAuth class xác thực OTP
    private FirebaseAuth mAuth;
    // string for storing our verification ID OTP
    private String verificationId;

    //CSDL
    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;

    Dialog dialogOTP;

    //Load image
    int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName ="";

    TaiKhoan taiKhoan;
    int Quyen = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        imgTrove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
//                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String hoTen = edHoTen.getText().toString();
                    String soDT = edSoDT.getText().toString();
                    String diaChi = edDiaChi.getText().toString();

                    if(hoTen.isEmpty() || soDT.isEmpty() || diaChi.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Không được để trống thông tin", Toast.LENGTH_SHORT).show();
                    }else{
                        //Load hình ảnh lên firebase
                        if(imageFileName.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Chưa được thêm hình ảnh", Toast.LENGTH_SHORT).show();
                        }else {
                            //Thêm tài khoản vào database
                            Random random =  new Random();
                            int x = random.nextInt((1000-1+1)+1);
                            String maTK = "TK" + x;
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
//        if (requestCode == GALEERY_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                contenUri = data.getData();
//                String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                imageFileName = "JPEG_" + timSamp + "." + getFileExt(contenUri);
//                imgThemHinhAnh.setImageURI(contenUri);
//            }
//        }

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
                            //Log.d("==> Done", " Load hình ảnh lên Firebase thành công "+ uri.toString());
                            // Thêm tài khoản lên firebase
                            taiKhoan.setHinhAnh(uri.toString());
                            themTaiKhoanToFireStore(taiKhoan);
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

    private void diaLogOpenOTP(){
        // Gửi mã OTP đến điện thoại
        sendVerificationCode(edSoDT.getText().toString());

        dialogOTP = new Dialog(RegisterActivity.this);
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
            Toast.makeText(RegisterActivity.this, "PhoneAuthProvider "+e.getMessage(), Toast.LENGTH_LONG).show();
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
                        .setActivity(RegisterActivity.this)
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
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();
                            // Load avatar lên firebase
                            uploadImageToFirebase(imageFileName, contenUri);

                            //Chuyển về màn hình đăng nhập
//                            RegisterActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment
//                                    , new FragmentThanhVien()).commit();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);

                            dialogOTP.dismiss();
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
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