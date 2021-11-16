package com.example.myapplication.Fagment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.GioHangAdapter;
import com.example.myapplication.Adapter.LoaiNhaHangAdapter;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GioHangFragment extends Fragment {
    private RecyclerView rcv_GioHang;

    private List<GioHang> listGioHang;
    private GioHang gioHang;

    FirebaseAuth mAuth;
    //Firestore
    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gio_hang, container, false);
        rcv_GioHang = view.findViewById(R.id.rcv_GioHang);

        //Gọi Firebase xuống
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
//        getAllGioHang(getContext());

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllGioHang(getContext());
    }

        public void getAllGioHang(Context context){
        listGioHang = new ArrayList<>();

        final CollectionReference reference = db.collection("GIOHANG");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String maGH = doc.get("MaGH").toString();
                            String maMA = doc.get("MaMA").toString();
                            String maTK = doc.get("MaTK").toString();
//                          String tenMonAn = doc.get("TenNH").toString();
                            String tenMonThem = doc.get("TeMonThem").toString();
                            int soLuong = Integer.parseInt(doc.get("SoLuong").toString());
                            int tongTien = Integer.parseInt(doc.get("TongTien").toString());
//                          String HinhAnh = doc.get("HinhAnh").toString();

                            gioHang = new GioHang(maGH, maMA, maTK, soLuong, tenMonThem, tongTien);
                            listGioHang.add(gioHang);
                        }
                            GioHangAdapter adapter  = new GioHangAdapter(listGioHang, getContext());
                            rcv_GioHang.setLayoutManager(new LinearLayoutManager(getContext()));
                            rcv_GioHang.setAdapter(adapter);

                            //Xuat danh sach gio hang len recycleview
//                          getAllGioHang(getContext());
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}