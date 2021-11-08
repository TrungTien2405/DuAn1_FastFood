package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NhaHangAdapter extends RecyclerView.Adapter<NhaHangAdapter.adapterNhaHang> {
    private List<NhaHang> list;
    private Context context;

    public NhaHangAdapter(List<NhaHang> list, Context context) {
        this.list = list;
        this.context = context;
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
        holder.tvLoaiNH.setText(nhaHang.getMaLoaiNH());
        holder.tvDanhGiaTB.setText(nhaHang.getDanhGia()+"");
        holder.tvTenNH.setText(nhaHang.getTenNH());

        Picasso.with(context).load(nhaHang.getHinhAnh()).into(holder.imvHinh);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class adapterNhaHang extends RecyclerView.ViewHolder{
        TextView tvTenNH, tvLoaiNH, tvDanhGiaTB;
        ImageView imvHinh;
        ToggleButton tgb_yeuThich;

        public adapterNhaHang(@NonNull View itemView) {
            super(itemView);

            tvTenNH =itemView.findViewById(R.id.tv_itemTenNHRes);
            tvLoaiNH =itemView.findViewById(R.id.tv_itemLoaiNHRes);
            tvDanhGiaTB =itemView.findViewById(R.id.tv_itemDanhGiaRes);
            imvHinh =  itemView.findViewById(R.id.imv_itemHinhRes);
            tgb_yeuThich = itemView.findViewById(R.id.tgb_favoriteRes);
        }
    }
}
