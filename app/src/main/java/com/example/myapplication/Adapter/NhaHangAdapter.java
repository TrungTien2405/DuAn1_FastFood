package com.example.myapplication.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fagment.NhaHangFragment;
import com.example.myapplication.Model.LoaiNhaHang;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.Model.TaiKhoan;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NhaHangAdapter extends RecyclerView.Adapter<NhaHangAdapter.adapterNhaHang> {
    private List<NhaHang> list;
    private Context context;
    private NhaHangFragment fragment;
//    private List<String> listMaLoaiNH;
//    private List<String> listMaTK;

    //Firestore
    //FirebaseFirestore db;


    public NhaHangAdapter(List<NhaHang> list, Context context, NhaHangFragment fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public adapterNhaHang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);

        return new adapterNhaHang(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterNhaHang holder, int position) {
        NhaHang nhaHang =list.get(position);

        holder.tvTenNH.setText(nhaHang.getTenNH());
        holder.tvLoaiNH.setText(fragment.getTenLoaiNhaHang(nhaHang.getMaLoaiNH()));
        holder.tvDanhGiaTB.setText(nhaHang.getDanhGia()+"");
        holder.tvTenNH.setText(nhaHang.getTenNH());

        if(!nhaHang.getMaYT().isEmpty() && fragment.check_favorite(nhaHang.getMaYT())) holder.tgb_yeuThich.setChecked(true);

        if(nhaHang.getHinhAnh().isEmpty()){
            holder.imvHinh.setImageResource(R.drawable.im_food);
        }else{
            Picasso.with(context).load(nhaHang.getHinhAnh()).into(holder.imvHinh);
        }

        //Nhấn nút yêu thích nhà hàng
        holder.tgb_yeuThich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fragment.press_favorite(position);
                }else{
                    fragment.unpress_favorite(position);
                }
            }
        });

        //Nhấn sửa nhà hàng
        holder.tvSuaNH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Gọi Firebase xuống
                fragment.dialog_suaNH(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class adapterNhaHang extends RecyclerView.ViewHolder{
        TextView tvTenNH, tvLoaiNH, tvDanhGiaTB, tvSuaNH;
        ImageView imvHinh;
        ToggleButton tgb_yeuThich;

        public adapterNhaHang(@NonNull View itemView) {
            super(itemView);

            tvTenNH =itemView.findViewById(R.id.tv_itemTenNHRes);
            tvLoaiNH =itemView.findViewById(R.id.tv_itemLoaiNHRes);
            tvSuaNH =itemView.findViewById(R.id.tv_itemSuaNhaHangRes);
            tvDanhGiaTB =itemView.findViewById(R.id.tv_itemDanhGiaRes);
            imvHinh =  itemView.findViewById(R.id.imv_itemHinhRes);
            tgb_yeuThich = itemView.findViewById(R.id.tgb_favoriteRes);
        }
    }




}


