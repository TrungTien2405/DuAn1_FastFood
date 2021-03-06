package com.example.myapplication.Fagment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.GioHangAdapter;
import com.example.myapplication.Adapter.LoaiNhaHangAdapter;
import com.example.myapplication.Adapter.NhaHangAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.DanhGiaNH;
import com.example.myapplication.Model.GioHang;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.Model.MonAnNH;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GioHangFragment extends Fragment {
    private RecyclerView rcv_GioHang; // hi???n th??n tin c??c m??n ??n trong gi??? h??ng
    public TextView tvTongTienGH; // Hi???n th??ng tin t???ng ti???n t??? c??c m??n kh??ch h??ng ???? ch???n
    private TextView tvXoaGH; // Hi???n th??ng tin t???ng ti???n t??? c??c m??n kh??ch h??ng ???? ch???n
    private Button btnThanhToanGH; // Nh???n thanh to??n gi??? h??ng
    private ImageView btnTroVe; // Nh???n thanh to??n gi??? h??ng

    //List ch???a danh s??ch
    private List<MonAnNH> listMonAn;
    private List<GioHang> listGioHang;
    private List<GioHangCT> listGioHangCT;

    //Model
    private GioHang gioHang;
    private MonAnNH monAnNH;
    private GioHangCT gioHangCT;

    private int soDuTK; //S??? ti???n c???a ng?????i ????ng nh???p

    //S??? ti???n ???????c t??nh t???ng t??? c??c m??n ??n ???? ch???n checkbox trong gi??? h??ng
    public int TongTienGH = 0;

    //Firestore
    private FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gio_hang, container, false);
        anhxa(view);


        //G???i Firebase xu???ng
        db = FirebaseFirestore.getInstance();

        getAllGioHang(getContext()); //L???y danh t???t c??? danh s??ch gi??? h??ng t??? Firebase xu???ng
        getAllMonAn(getContext()); // L???y t???t c??? m??n ??n t??? Firebase xu???ng
        getSoDuND(getContext()); // L???y s??? ti???n c???a ng?????i d??ng

        //Nh???n x??a
        tvXoaGH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickXoa();
            }
        });

        //Nh???n n??t thanh to??n
        btnThanhToanGH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickThanhToan();
            }
        });

        //Tr??? v??? m??n h??nh
        btnTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_out, R.anim.fade_out, R.anim.fade_in, R.anim.slide_in)
                        .replace(R.id.nav_FrameFragment, new NhaHangFragment())
                        .addToBackStack(null)
                        .commit();

                try {
                    ((MainActivity) getActivity()).bottomNavigation.setSelectedItemId(R.id.mn_main);
                }catch (Exception e){
                    Log.d("===> ", "Bottom navigaion :" + e.getMessage());

                }
            }
        });
        return view;
    }

    private void anhxa(View v){
        tvTongTienGH = v.findViewById(R.id.tv_tongTienGH);
        tvXoaGH = v.findViewById(R.id.tv_xoaGioHang);
        btnThanhToanGH = v.findViewById(R.id.btn_thanhToanGH);
        btnTroVe = v.findViewById(R.id.imv_TroveTrongGH);
        rcv_GioHang = v.findViewById(R.id.rcv_GioHang);
    }

    //T??nh t???ng ti???n trong gi??? h??ng
    public void tinhTongTien(){
        try {
            TongTienGH = 0;
            for (GioHangCT gh : listGioHangCT) {
                if (gh.getTrangThaiCheckbox()) TongTienGH += gh.getSoLuong() * gh.getGiaMA();
            }
            if(TongTienGH == 0){
                tvTongTienGH.setText("00.00");
            }else tvTongTienGH.setText(formatNumber(TongTienGH));
        }catch (Exception e){


        }
    }

    //T??nh t???ng gi?? c??c m??n ??n trong gi??? h??ng, khi nh???n ch???n
    public void checkedGioHang(int positon, int giaMon){
        TongTienGH += giaMon;
        tvTongTienGH.setText(formatNumber(TongTienGH)); // Hi???n th??? gi?? ti???n tr??n TextView

        listGioHangCT.get(positon).setTrangThaiCheckbox(true); // l??u l???i c??c m??n ??n ???? nh???n ch???n
    }

    //T??nh hi???u gi?? c??c m??n ??n trong gi??? h??ng, khi nh???n ch???n
    public void uncheckedGioHang(int positon, int giaMon){
        TongTienGH -= giaMon;
        tvTongTienGH.setText(formatNumber(TongTienGH)); // Hi???n th??? gi?? ti???n tr??n TextView

        listGioHangCT.get(positon).setTrangThaiCheckbox(false); // l??u l???i c??c m??n ??n ???? nh???n ch???n
    }


    // ?????nh d???ng sang s??? ti???n
    private String formatNumber(int number){
        // t???o 1 NumberFormat ????? ?????nh d???ng s??? theo ti??u chu???n c???a n?????c Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }


    // Duy???t danh s??ch ki???m tra xem item n??o c?? ch???n Checkbox th?? ch???n thanh to??n
    public void clickThanhToan(){
        int duyet = 0;
        Intent intent = getActivity().getIntent();
        String maTK = intent.getStringExtra("MaTK");

        List<GioHangCT> listThanhToan = new ArrayList<>();

        for(int i=0; i<listGioHangCT.size(); i++){
            if(listGioHangCT.get(i).getTrangThaiCheckbox()) {
                listThanhToan.add(listGioHangCT.get(i));

                duyet = 1; //x??c nh???n ???? c?? checkbox ch???n

            }

        }

        if(duyet == 0){
            Toast.makeText(getContext(), "B???n ch??a ch???n m??n ??n n??o!!", Toast.LENGTH_SHORT).show();
        }else {

            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("listThanhToan", (ArrayList<? extends Parcelable>) listThanhToan);
            bundle.putSerializable("listThanhToan", (Serializable) listThanhToan);
            ThanhToanFragment thanhToanFragment = new ThanhToanFragment();
            thanhToanFragment.setArguments(bundle);

            //getFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, monAnFragment).commit();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .replace(R.id.nav_FrameFragment,  thanhToanFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    //L???y danh s??ch ng?????i d??ng t??? Firebase xu???ng, ????? ki???m tra s??? d?? t??i kho???n
    public void getSoDuND(Context context){
        Intent intent = getActivity().getIntent();
        String _maTK = intent.getStringExtra("MaTK");

        final CollectionReference reference = db.collection("TAIKHOAN");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String soDu = doc.get("SoDu").toString();
                            String maTK = doc.get("MaTK").toString();

                            if(maTK.equals(_maTK)) {
                                soDuTK  = Integer.parseInt(soDu);
                                return;
                            }
                        }
                    }else{
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //L???y danh s??ch gi??? h??ng t??? Firebase xu???ng
    public void getAllGioHang(Context context){
        listGioHang = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        String _maTK = intent.getStringExtra("MaTK");

        final CollectionReference reference = db.collection("GIOHANG");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String maGH = doc.get("MaGH").toString();
                            String maTK = doc.get("MaTK").toString();

                            if(maTK.equals(_maTK)) {
                                gioHang = new GioHang(maGH, maTK);
                                listGioHang.add(gioHang);

                                // L???y list gi??? h??ng chi ti???t
                                getAllGioHangCT(getContext(), maGH);
                                break;
                            }
                        }
                    }else{
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Error getAllGioHang: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // L???y danh s??ch m??n ??n t??? Firebase xu???ng
    public void getAllMonAn(Context context){
        listMonAn = new ArrayList<>();

        final CollectionReference reference = db.collection("MONANNH");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String maMA = doc.get("MaMA").toString();
                            String maNH = doc.get("MaNH").toString();
                            String tenMon = doc.get("TenMon").toString();
                            String maMenuNH = doc.get("MaMenuNH").toString();
                            String chiTiet = doc.get("ChiTiet").toString();
                            int gia = Integer.parseInt(doc.get("Gia").toString());
                            String hinhAnh = doc.get("HinhAnh").toString();

                            monAnNH = new MonAnNH(maMA, maNH, maMenuNH, tenMon, chiTiet, gia, hinhAnh);
                            listMonAn.add(monAnNH);
                        }

                    }else{
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // L???y danh s??ch gi??? h??ng chi ti???t t??? Firebase xu???ng
    public void getAllGioHangCT(Context context, String _maGH){
        listGioHangCT = new ArrayList<>();

        final CollectionReference reference = db.collection("GIOHANGCT");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot) {
                            String maMA = doc.get("MaMA").toString();
                            String maGHCT = doc.get("MaGHCT").toString();
                            String maGH = doc.get("MaGH").toString();
                            int soLuong = Integer.parseInt(doc.get("SoLuong").toString());
                            String tenMonThem = doc.get("TenMonThem").toString();
                            String thoiGian = doc.get("ThoiGian").toString();
                            int trangThai = Integer.parseInt(doc.get("TrangThai").toString());
//                            long tongGiaDH = Long.parseLong(doc.get("TongTien").toString());

                            if(_maGH.equals(maGH) && trangThai==0) {
                                gioHangCT = new GioHangCT(maGH, maGHCT, maMA, "", soLuong, 0, "", tenMonThem, thoiGian, trangThai, "", false, 0);
                                listGioHangCT.add(gioHangCT);
                            }
                        }

                        // Th??m ?????y ????? th??ng tin v??o gi??? h??ng chi ti??t
                        getAllDetail_gioHang();

                    }else{
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Error getAllGioHangCT"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("=====>", e.getMessage());
                }
            }
        });
    }

    //Set Adapter gi??? h??ng
    private void adapter_gioHang(){
        GioHangAdapter adapter  = new GioHangAdapter(listGioHangCT, getContext(), this);
        rcv_GioHang.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv_GioHang.setAdapter(adapter);
        tinhTongTien();
    }

    //C???p nh???t ?????y ????? th??ng tin gi??? h??ng l??n listGioHangCT;
    private void getAllDetail_gioHang(){
        for(int i=0; i<listGioHangCT.size(); i++){
            addDetail_gioHang(listGioHangCT.get(i).getMaMA(), i);
        }

        //?????y list l??n adapter gi??? h??ng
        adapter_gioHang();
    }

    //T??m ki???m m??n ??n b???ng m?? m??n ??n, n???u c?? th??m m??n ??n v??o list gi??? h??ng
    private void addDetail_gioHang(String maMA, int positon){
        for(MonAnNH ma: listMonAn){
            if(maMA.equals(ma.getMaMA())){
                listGioHangCT.get(positon).setTenMA(ma.getTenMon());
                listGioHangCT.get(positon).setGiaMA(ma.getGia());
                listGioHangCT.get(positon).setHinhAnh(ma.getHinhAnh());

                //C???ng d???n v??o t???ng ti???n gi??? h??ng

                Intent intent = getActivity().getIntent();
                //Th??m m?? t??i kho???n v??o list gi??? h??ng
                listGioHangCT.get(positon).setMaTK(intent.getStringExtra("MaTK"));
            }
        }
    }

    public void clickXoa(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Th??ng b??o")
                .setMessage("B???n ch???n ch???n mu???n x??a m??n h??ng kh??ng?")
                .setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            int ktCheck = 0;
                            for(GioHangCT gh: listGioHangCT){
                                if(gh.getTrangThaiCheckbox()){
                                    ktCheck = 1;
                                    deleteGioHangGioHangCTFirestore(gh.getMaGH(), gh.getMaGHCT());
                                }
                            }

                            if(ktCheck == 0) Toast.makeText(getContext(), "B???n ch??a ch???n m??n ??n n??o!!", Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(getContext(), "B???n ???? x??a th??nh c??ng", Toast.LENGTH_SHORT).show();

                                getAllGioHang(getContext()); //L???y danh t???t c??? danh s??ch gi??? h??ng t??? Firebase xu???ng, sau ???? ????a danh s??ch gi??? h??ng l??n rcv
                                getAllMonAn(getContext()); // L???y t???t c??? m??n ??n t??? Firebase xu???ng
                            }
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

    // X??a m??n ??n trong gi??? h??ng
    public void deleteGioHangGioHangCTFirestore(String _maGH, String _maGHCT){
        try {
            db.collection("GIOHANGCT").document(_maGHCT)
                    .delete();

        }catch (Exception e){
            Log.d("===> ", e.getMessage());
        }
    }


    public void updateSoLuongGH(String _maGHCT, int _soLuong, int position){
        db.collection("GIOHANGCT").document(_maGHCT)
                .update(
                        "SoLuong" , _soLuong
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listGioHangCT.get(position).setSoLuong(_soLuong); //update s??? l?????ng trong list gi??? h??ng chi ti???t
//                listGioHangCT.get(position).setGiaMA(_soLuong * listGioHangCT.get(position).getGiaMA());

            }
        });

    }
}










