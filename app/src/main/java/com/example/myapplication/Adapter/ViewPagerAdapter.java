package com.example.myapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Fagment.ChuNhaHangFragment;
import com.example.myapplication.Fagment.KhachHangFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ChuNhaHangFragment();
            case 1:
                return new KhachHangFragment();
            default:
                return new ChuNhaHangFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
