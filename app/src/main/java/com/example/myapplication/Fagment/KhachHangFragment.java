package com.example.myapplication.Fagment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

        //g???i firebase
        db = FirebaseFirestore.getInstance();

        getAllKhachHang(getContext());

        return view;
    }

    //l???y d??? li???u c???a b???ng TAIKHOAN t??? firestore v???, l??u v??o listTKKhachHang
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
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("===> ", "getAllKhachHang " + e.getMessage());
                }
            }
        });
    }

    //g???i adapter, xu???t danh s??ch listTKKhachHang l??n rcv
    private void goiAdapter(){
        KhachHangAdapter adapter = new KhachHangAdapter(listTKKhachHang, getContext(), this);
        rcv_KhachHang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_KhachHang.setAdapter(adapter);
    }

    //dialog th??ng b??o x??a t??i kho???n kh??ch h??ng
    public void dialogXoaKhachHang(String _maTK){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Th??ng b??o")
                .setMessage("B???n ch???n ch???n mu???n x??a t??i kho???n kh??ng?")
                .setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            xoaChuNhaHangFirestore(_maTK);
                        }catch (Exception e){
                            Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


    //c??u l???nh x??a t??i kho???n theo m?? t??i kho???n c???a Firebase
    public void xoaChuNhaHangFirestore(String _maTK){
        db.collection("TAIKHOAN").document(_maTK)
                .delete();

        getAllKhachHang(getContext()); //L???y danh t???t c??? danh s??ch t??i kho???n t??? Firebase xu???ng, sau ???? ????a danh s??ch t??i kho???n kh??ch h??ng l??n rcv
    }
}