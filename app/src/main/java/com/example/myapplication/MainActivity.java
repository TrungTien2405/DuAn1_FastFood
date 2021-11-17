package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        loadFragment(new NhaHangFragment());

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mn_main:
                    fragment =new NhaHangFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.mn_users:
                    fragment = new QLTaiKhoanFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.mn_basket:
                    fragment = new GioHangFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.mn_setting:
                    fragment = new CaiDatFragment();
                    loadFragment(fragment);
                    return true;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_FrameFragment, fragment).commit();
            return false;
        }

    };

    private void loadFragment(Fragment fragment) {
        // load Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_FrameFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}