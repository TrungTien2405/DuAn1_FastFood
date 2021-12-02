package com.example.myapplication.Fagment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.DangNhapActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoiMatKhauFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoiMatKhauFragment extends Fragment {
    Dialog dialogOTP;
    EditText ed_MaOTP;
    Button btnDongY, btnHuy, btnXacThucOTP;
    ImageView imv_TroVe;

    TextInputLayout tip_matKhauCu, tip_matKhauMoi, tip_xacNhanMatKhau;

    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;

    String mkMoi;

    // variable for FirebaseAuth class xác thực OTP
    private FirebaseAuth mAuth;
    // string for storing our verification ID OTP
    private String verificationId;

    public DoiMatKhauFragment() {
        // Required empty public constructor
    }

    public static DoiMatKhauFragment newInstance(String param1, String param2) {
        DoiMatKhauFragment fragment = new DoiMatKhauFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doi_mat_khau, container, false);
        imv_TroVe = view.findViewById(R.id.imv_TroVe);

        imv_TroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.nav_FrameFragment, new CaiDatFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tip_matKhauCu = view.findViewById(R.id.tip_matKhauCu);
        tip_matKhauMoi = view.findViewById(R.id.tip_matKhauMoi);
        tip_xacNhanMatKhau = view.findViewById(R.id.tip_xacNhanMatKhau);
        btnDongY = view.findViewById(R.id.btnDongYDMK);
        btnHuy = view.findViewById(R.id.btnHuyDMK);

        //lấy dữ liệu
        Intent intent = getActivity().getIntent();
        String matKhau = intent.getStringExtra("MatKhau");
        String sdt = intent.getStringExtra("SDT");

        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matKhauCu = tip_matKhauCu.getEditText().getText().toString().trim();
                String matKhauMoi = tip_matKhauMoi.getEditText().getText().toString().trim();
                String xnMatKhauMoi = tip_xacNhanMatKhau.getEditText().getText().toString().trim();

                if (matKhauCu.isEmpty()) {
                    Toast.makeText(getContext(), "Không được để trống thông tin", Toast.LENGTH_SHORT).show();
                    }else if(!matKhauCu.equals(matKhau)) {
                        Toast.makeText(getContext(), "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                    } else if (!kiemLoi(matKhauMoi).isEmpty()) {
                        Toast.makeText(getContext(), kiemLoi(matKhauMoi), Toast.LENGTH_SHORT).show();
                    } else if (!kiemLoi(xnMatKhauMoi).isEmpty()) {
                        Toast.makeText(getContext(), "Xác nhận mật khẩu mới không chính xác", Toast.LENGTH_SHORT).show();
                    } else if (matKhauMoi.equals(xnMatKhauMoi)) {
                        mkMoi = matKhauMoi;

                        dialog_OpenOTP(sdt);
                    } else {
                        Toast.makeText(getContext(), "Mật khẩu không giống nhau, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                    }
                }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tip_matKhauCu.getEditText().setText("");
                tip_matKhauMoi.getEditText().setText("");
                tip_xacNhanMatKhau.getEditText().setText("");
            }
        });
    }

    //dialog cho phép nhập mã otp khi được gửi tới qua sdt
    private void dialog_OpenOTP(String sdt){
        // Gửi mã OTP đến điện thoại
        sendVerificationCode(sdt);

        dialogOTP = new Dialog(getContext());
        dialogOTP.setContentView(R.layout.dialog_otp_firebase);

        dialogOTP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.6);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        dialogOTP.getWindow().setLayout(width, height);

        ed_MaOTP = dialogOTP.findViewById(R.id.edMaOTP);
        btnXacThucOTP = dialogOTP.findViewById(R.id.btnXacThucOTP);

        btnXacThucOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(ed_MaOTP.getText().toString());
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
                ed_MaOTP.setText(code);
                //verifying the code
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), "PhoneAuthProvider "+e.getMessage(), Toast.LENGTH_LONG).show();
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
                        .setActivity(getActivity())
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
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Xác thực thành công", Toast.LENGTH_SHORT).show();

                            //Lấy sdt, mat khau, tk
                            Intent intent = getActivity().getIntent();
                            String maTV = intent.getStringExtra("MaTK").toString();
                            // Cập nhật mật khẩu firestore
                            updatePass(mkMoi, maTV);

                            //Chuyển sang fragment Trang chính
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, new CaiDatFragment()).commit();

                            dialogOTP.dismiss();
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
                    }
                });
    }

    //Thay đổi password
    private void updatePass(String mk, String MaTK){
        try {
            db.collection("TAIKHOAN").document(MaTK).update("MatKhau", mk);
        }catch (Exception e){
            Toast.makeText(getContext(), "Không đổi được mật khẩu, lỗi "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Kiểm lỗi mật khẩu
    private String kiemLoi(String mk){
        String loi = "";
        Boolean hoa = true, thuong = true, so = true;
        if(mk.length()<8) loi = "Mật khẩu quá ngắn";

        for(int i = 0; i<mk.length(); i++){
            int ascii = (int) mk.charAt(i);
            if(ascii > 64 && ascii < 91) hoa = false;
            if(ascii > 96 && ascii < 123) thuong = false;
            if(ascii > 47 && ascii < 58) so = false;
        }

        if(hoa) loi += " phải có chữ hoa";
        if(thuong) loi += " thiếu kí tự thường";
        if(so) loi += " phải có số";

        return loi;
    }
}