package com.example.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        //for one time animation
        first = true;
        openFragment(BasicFragment.newInstance());

        //initialize bottom nav
        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.basic:
                                openFragment(BasicFragment.newInstance());
                                return true;
                            case R.id.tip:
                                openFragment(TipsFragment.newInstance());
                                return true;
                            case R.id.stocks:
                                openFragment(StocksFragment.newInstance());
                                return true;
                            case R.id.bitwise:
                                openFragment(BitwiseFragment.newInstance());
                                return true;
                            case R.id.convert:
                                openFragment(ConvertFragment.newInstance());
                                return true;
                        }
                        return false;
                    }
                });
    }

    /**
     * Handles opening fragments.
     *
     * @param fragment Fragment to open.
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //only animate first fragment
        if (first) {
            transaction.setCustomAnimations(R.anim.slide_from_bottom, 0);
            first = false;
        }

        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}