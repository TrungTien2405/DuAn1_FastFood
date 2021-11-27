package com.example.myapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fagment.MonAnCTFragment;
import com.example.myapplication.Model.MonAnThem;
import com.example.myapplication.R;

import java.util.List;

import static com.example.myapplication.R.color.mauchinh;
import static com.example.myapplication.R.color.white;

public class MonAnThemAdapter extends RecyclerView.Adapter<MonAnThemAdapter.MyViewHolder> {
    private Context context;
    private List<MonAnThem> list;
    private MonAnCTFragment fragment;

    public MonAnThemAdapter(Context context, List<MonAnThem> list, MonAnCTFragment fragment) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monanthem, parent, false);
        return new MonAnThemAdapter.MyViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MonAnThem ma = list.get(position);
        holder.tvTenMonThem.setText(ma.getTenMonThem());
        holder.imv_hinhAnh.setImageResource(ma.getHinhAnh());


        holder.cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMonAnThem(ma, holder);
            }
        });

    }

    private void clickMonAnThem(MonAnThem ma, MyViewHolder holder){
        if(ma.getCheckBox() == 0) {
            holder.cons.setBackgroundResource(R.drawable.item_clickloainh);
            holder.tvTenMonThem.setTextColor(Color.WHITE);
            ma.setCheckBox(1);
        }else{
            holder.cons.setBackgroundResource(R.drawable.item_loainhahang);
            holder.tvTenMonThem.setTextColor(Color.GRAY);
            ma.setCheckBox(0);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMonThem;
        ImageView imv_hinhAnh;
        ConstraintLayout cons;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTenMonThem = itemView.findViewById(R.id.tv_itemTenMonAnThem);
            imv_hinhAnh = itemView.findViewById(R.id.imv_itemMonAnThem);
            cons = itemView.findViewById(R.id.cons_itemMonAnThem);
        }
    }
}
