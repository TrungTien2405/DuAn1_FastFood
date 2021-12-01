package com.example.myapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.LoaiNhaHang;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LoaiNhaHangAdapter extends RecyclerView.Adapter<LoaiNhaHangAdapter.loaiNhaHangAdapter> {
    public List<LoaiNhaHang> list;
    public Context context;
    public int vitri = 0;

    public LoaiNhaHangAdapter(List<LoaiNhaHang> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public LoaiNhaHangAdapter.loaiNhaHangAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_loainhahang, parent, false);
        return new LoaiNhaHangAdapter.loaiNhaHangAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoaiNhaHangAdapter.loaiNhaHangAdapter holder, int position) {
        LoaiNhaHang loaiNhaHang = list.get(position);

        holder.tv_ten.setText(loaiNhaHang.getTenLoaiNH());
        if(loaiNhaHang.getHinhAnh().isEmpty()){
            holder.imv_hinh.setImageResource(R.drawable.ic_location);
        }else Picasso.with(context).load(loaiNhaHang.getHinhAnh()).resize(2048, 1600).centerCrop().onlyScaleDown().into(holder.imv_hinh);



        holder.constrain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                vitri = position;

                notifyDataSetChanged();
            }
        });

        click_itemLoaiNH(position, holder);
    }

    void click_itemLoaiNH(int position, @NonNull LoaiNhaHangAdapter.loaiNhaHangAdapter holder){
        if(position == vitri) {
            holder.constrain.setBackgroundResource(R.drawable.item_clickloainh);
            holder.tv_ten.setTextColor(Color.WHITE);
        }else{
            holder.constrain.setBackgroundResource(R.drawable.item_loainhahang);
            holder.tv_ten.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class loaiNhaHangAdapter extends RecyclerView.ViewHolder {
        ImageView imv_hinh;
        TextView tv_ten;
        ConstraintLayout constrain;

        public loaiNhaHangAdapter(@NonNull View itemView) {
            super(itemView);

            imv_hinh = itemView.findViewById(R.id.imv_itemLoaiNH);
            tv_ten = itemView.findViewById(R.id.tv_itemTenLoaiNH);
            constrain = itemView.findViewById(R.id.view_itemLoaiNH);
        }
    }
}
