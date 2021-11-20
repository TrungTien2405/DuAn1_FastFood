package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.Fagment.LichSuMuaFragment;
import com.example.myapplication.Model.GioHangCT;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LichSuMHAdapter extends RecyclerView.Adapter<LichSuMHAdapter.MyViewholder> {
    public List<GioHangCT> list;
    public Context context;
    public LichSuMuaFragment fragment;

    public LichSuMHAdapter(List<GioHangCT> list, Context context, LichSuMuaFragment fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lichsumua, parent, false);
        return new LichSuMHAdapter.MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        try {
            GioHangCT gioHangCT = list.get(position);

            Intent intent = fragment.getActivity().getIntent();
            String hoTen = intent.getStringExtra("HoTen");
            String diaChi = intent.getStringExtra("DiaChi");
            String sdt = intent.getStringExtra("SDT");

            holder.tvTenND.setText(hoTen);
            holder.tvSDT.setText(sdt);
            holder.tvDiaChi.setText(diaChi);
            holder.tvTenMA.setText(gioHangCT.getTenMA());
            holder.tvTenNH.setText("");
            holder.tvGia.setText(formatNumber(gioHangCT.getGiaMA()) + " VND");

            if (gioHangCT.getHinhAnh().isEmpty()) {
                holder.imvHinhMA.setImageResource(R.drawable.im_food);
            } else Picasso.with(context).load(gioHangCT.getHinhAnh()).into(holder.imvHinhMA);

            holder.chkChon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        fragment.checkedGioHang(position);
                    } else fragment.checkedGioHang(position);
                }
            });
        }catch (Exception e){
            Log.d("===>", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {
        TextView tvTenND, tvDiaChi, tvSDT, tvGia, tvTenNH, tvTenMA;
        ImageView imvHinhMA;
        CheckBox chkChon;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            tvTenMA = itemView.findViewById(R.id.tv_itemTenMonNHLS);
            tvDiaChi = itemView.findViewById(R.id.tv_itemDiaChiLS);
            tvGia = itemView.findViewById(R.id.tv_itemGiaLS);
            tvTenNH = itemView.findViewById(R.id.tv_itemTenNHLS);
            tvTenND = itemView.findViewById(R.id.tv_itemTenNguoiLS);
            tvSDT = itemView.findViewById(R.id.tv_itemSDTLS);
            imvHinhMA = itemView.findViewById(R.id.imv_itemHinhLS);
            chkChon = itemView.findViewById(R.id.chk_itemLSMH);
        }
    }

    // Định dạng sang số tiền
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }
}
