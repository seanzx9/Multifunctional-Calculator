package com.example.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bnv;
    private Stack<Integer> fragments;
    private int curFragmentId;
    private boolean backPressed;
    private boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        //for one time animation
        first = true;

        fragments = new Stack<>();
        openFragment(BasicFragment.newInstance());
        curFragmentId = R.id.basic;
        backPressed = false;

        //initialize bottom nav
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.basic:
                                openFragment(BasicFragment.newInstance());
                                curFragmentId = R.id.basic;
                                return true;
                            case R.id.tip:
                                openFragment(TipsFragment.newInstance());
                                curFragmentId = R.id.tip;
                                return true;
                            case R.id.stocks:
                                openFragment(StocksFragment.newInstance());
                                curFragmentId = R.id.stocks;
                                return true;
                            case R.id.bitwise:
                                openFragment(BitwiseFragment.newInstance());
                                curFragmentId = R.id.bitwise;
                                return true;
                            case R.id.convert:
                                openFragment(ConvertFragment.newInstance());
                                curFragmentId = R.id.convert;
                                return true;
                        }
                        return false;
                    }
                });
    }

    /**
     * Navigate through app with back button.
     */
    @Override
    public void onBackPressed() {
        if (fragments.isEmpty()) {
            finish();
        }
        else {
            int id = fragments.pop();
            backPressed = true;
            bnv.setSelectedItemId(id);
            curFragmentId = id;
        }
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

        if (!backPressed) fragments.push(curFragmentId);
        else backPressed = false;
    }
}
