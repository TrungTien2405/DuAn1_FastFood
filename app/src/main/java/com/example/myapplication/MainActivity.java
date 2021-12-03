package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Fagment.CaiDatFragment;
import com.example.myapplication.Fagment.GioHangFragment;
import com.example.myapplication.Fagment.MonAnFragment;
import com.example.myapplication.Fagment.NhaHangFragment;
import com.example.myapplication.Fagment.QLTaiKhoanFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity{
    public DrawerLayout drawerLayout;
    public BottomNavigationView bottomNavigation;

    TextView tvNameNavigation;
    ImageView imgAvatarNavigation;

    private int viTriThuc = 1;
    private int viTriBam = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        loadFragment(new NhaHangFragment());

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        hideNavigation();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mn_main:
                    fragment =new NhaHangFragment();
                    viTriThuc = viTriBam;
                    viTriBam = 1;
                    loadFragment(fragment);
                    return true;
                case R.id.mn_users:
                    fragment = new QLTaiKhoanFragment();
                    viTriThuc = viTriBam;
                    viTriBam = 2;
                    loadFragment(fragment);
                    return true;
                case R.id.mn_basket:
                    fragment = new GioHangFragment();
                    viTriThuc = viTriBam;
                    viTriBam = 3;
                    loadFragment(fragment);
                    return true;
                case R.id.mn_setting:
                    fragment = new CaiDatFragment();
                    viTriThuc = viTriBam;
                    viTriBam = 4;
                    loadFragment(fragment);
                    return true;

            }

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .replace(R.id.nav_FrameFragment, fragment)
                    .addToBackStack(null)
                    .commit();
            return false;
        }

    };

    private void loadFragment(Fragment fragment) {
        // load Fragment

        if(viTriThuc<viTriBam) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .replace(R.id.nav_FrameFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_out, R.anim.fade_out, R.anim.fade_in, R.anim.slide_in)
                    .replace(R.id.nav_FrameFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // Ẩn các tác vụ khách hàng và chủ nhà hàng không có quyền
    private void hideNavigation(){
        Intent intent = getIntent();
        int quyen = intent.getIntExtra("Quyen", 2);

        if(quyen==1 || quyen == 2) {
            bottomNavigation.getMenu().removeItem(R.id.mn_users);
        }
    }


}