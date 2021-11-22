package com.example.myapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.MonAnNH;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MonAnAdapter extends BaseAdapter{
    public List<MonAnNH> list;
    public Context context;

    public MonAnAdapter(List<MonAnNH> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MonAnNH monAnNH = list.get(position);
        if(convertView == null){
            holder = new MonAnAdapter.ViewHolder();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_monan,null);

            holder.imv_hinhMonAn = convertView.findViewById(R.id.imv_HinhMA);
            holder.imv_ChonMA = convertView.findViewById(R.id.imv_ChonMA);
            holder.tv_ChinhSuaMA = convertView.findViewById(R.id.tv_ChinhSuaMA);
            holder.tv_TenMonAn = convertView.findViewById(R.id.tv_TenMonAn);
            holder.tv_ChiTietMA = convertView.findViewById(R.id.tv_ChiTietMA);
            holder.tv_GiaMA = convertView.findViewById(R.id.tv_GiaMA);
            holder.constrain = convertView.findViewById(R.id.view_itemLoaiNH);

            convertView.setTag(holder);
        }else{
            holder = (MonAnAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_TenMonAn.setText(monAnNH.getTenMon());
        holder.tv_ChiTietMA.setText(monAnNH.getChiTiet());
        holder.tv_GiaMA.setText(formatNumber(monAnNH.getGia())+" VND");

        if(monAnNH.getHinhAnh().isEmpty()){
            holder.imv_hinhMonAn.setImageResource(R.drawable.im_food);
        }else{
            Picasso.with(context).load(monAnNH.getHinhAnh()).into(holder.imv_hinhMonAn);
        }
        holder.tv_ChinhSuaMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    private class ViewHolder{
        ImageView imv_hinhMonAn, imv_ChonMA;
        TextView tv_ChinhSuaMA, tv_TenMonAn, tv_ChiTietMA, tv_GiaMA;
        ConstraintLayout constrain;
    }
    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }
}
