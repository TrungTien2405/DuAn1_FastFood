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

        //g???i firebase
        db = FirebaseFirestore.getInstance();

        getAllChuNhaHang(getContext());

        return view;
    }

    //l???y d??? li???u c???a b???ng TAIKHOAN t??? firestore v???, l??u v??o listTKChuNhaHang
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
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("===> ", "getAllChuNhaHang " + e.getMessage());
                }
            }
        });
    }

    //g???i adapter, xu???t danh s??ch listTKChuNhaHang l??n rcv
    private void goiAdapter() {
        ChuNhaHangAdapter adapter = new ChuNhaHangAdapter(listTKChuNhaHang, getContext(), this);
        rcv_ChuNhaHang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_ChuNhaHang.setAdapter(adapter);
    }

    //dialog th??ng b??o x??a t??i kho???n ch??? nh?? h??ng
    public void dialogXoaChuNhaHang(String _maTK){
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

        getAllChuNhaHang(getContext()); //L???y danh t???t c??? danh s??ch t??i kho???n t??? Firebase xu???ng, sau ???? ????a danh s??ch t??i kho???n ch??? nh?? h??ng l??n rcv
    }
}