package com.example.myapplication.Fagment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Adapter.ChuNhaHangAdapter;
import com.example.myapplication.Model.TaiKhoan;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChuNhaHangFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChuNhaHangFragment extends Fragment {
    private RecyclerView rcv_ChuNhaHang;

    private TaiKhoan taiKhoan;
    private List<TaiKhoan> listTKChuNhaHang;

    private FirebaseFirestore db;

    public ChuNhaHangFragment() {
        // Required empty public constructor
    }

    public static ChuNhaHangFragment newInstance() {
        ChuNhaHangFragment fragment = new ChuNhaHangFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chu_nha_hang, container, false);
        rcv_ChuNhaHang = view.findViewById(R.id.rcv_ChuNhaHang);

        //gọi firebase
        db = FirebaseFirestore.getInstance();

        getAllChuNhaHang(getContext());

        return view;
    }

    //lấy dữ liệu của bảng TAIKHOAN từ firestore về, lưu vào listTKChuNhaHang
    public void getAllChuNhaHang(Context context) {
        listTKChuNhaHang = new ArrayList<>();

        final CollectionReference reference = db.collection("TAIKHOAN");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        for (QueryDocumentSnapshot doc : snapshot) {
                            String maTK = doc.get("MaTK").toString();
                            String hoTen = doc.get("HoTen").toString();
                            String diaChi = doc.get("DiaChi").toString();
                            String hinhAnh = doc.get("HinhAnh").toString();
                            String soDT = doc.get("SDT").toString();
                            int soDu = Integer.parseInt(doc.get("SoDu").toString());
                            String matKhau = doc.get("MatKhau").toString();
                            int quyen = Integer.parseInt(doc.get("Quyen").toString());

                            if (quyen == 1) {
                                taiKhoan = new TaiKhoan(maTK, hoTen, matKhau, soDT, diaChi, quyen, hinhAnh, soDu);
                                listTKChuNhaHang.add(taiKhoan);
                            }
                        }
                        goiAdapter();
                    } else {
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //gọi adapter, xuất danh sách listTKChuNhaHang lên rcv
    private void goiAdapter() {
        ChuNhaHangAdapter adapter = new ChuNhaHangAdapter(listTKChuNhaHang, getContext(), this);
        rcv_ChuNhaHang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_ChuNhaHang.setAdapter(adapter);
    }

    //dialog thông báo xóa tài khoản chủ nhà hàng
    public void dialogXoaChuNhaHang(String _maTK){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn xóa tài khoản không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            xoaChuNhaHangFirestore(_maTK);
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

    //câu lệnh xóa tài khoản theo mã tài khoản của Firebase
    public void xoaChuNhaHangFirestore(String _maTK){
        db.collection("TAIKHOAN").document(_maTK)
                .delete();

        getAllChuNhaHang(getContext()); //Lấy danh tất cả danh sách tài khoản từ Firebase xuống, sau đó đưa danh sách tài khoản chủ nhà hàng lên rcv
    }
}