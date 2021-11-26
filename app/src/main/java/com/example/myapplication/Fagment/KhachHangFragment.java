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
import com.example.myapplication.Adapter.KhachHangAdapter;
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
 * Use the {@link KhachHangFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KhachHangFragment extends Fragment {
    List<TaiKhoan> listTKKhachHang;
    RecyclerView rcv_KhachHang;
    TaiKhoan taiKhoan;

    private FirebaseFirestore db;

    public KhachHangFragment() {
        // Required empty public constructor
    }

    public static KhachHangFragment newInstance() {
        KhachHangFragment fragment = new KhachHangFragment();
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
        View view = inflater.inflate(R.layout.fragment_khach_hang, container, false);
        rcv_KhachHang = view.findViewById(R.id.rcv_KhachHang);

        //gọi firebase
        db = FirebaseFirestore.getInstance();

        getAllKhachHang(getContext());

        return view;
    }
    public void getAllKhachHang(Context context) {
        listTKKhachHang = new ArrayList<>();

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

                            if (quyen == 2) {
                                taiKhoan = new TaiKhoan(maTK, hoTen, matKhau, soDT, diaChi, quyen, hinhAnh, soDu);
                                listTKKhachHang.add(taiKhoan);
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
    private void goiAdapter(){
        KhachHangAdapter adapter = new KhachHangAdapter(listTKKhachHang, getContext(), this);
        rcv_KhachHang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_KhachHang.setAdapter(adapter);
    }
    public void dialogXoaKhachHang(String _maTK){
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

    public void xoaChuNhaHangFirestore(String _maTK){
        db.collection("TAIKHOAN").document(_maTK)
                .delete();

        getAllKhachHang(getContext()); //Lấy danh tất cả danh sách tài khoản từ Firebase xuống, sau đó đưa danh sách tài khoản khách hàng lên rcv
    }
}