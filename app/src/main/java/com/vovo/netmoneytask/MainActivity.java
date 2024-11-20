package com.vovo.netmoneytask;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vovo.netmoneytask.controller.CommonController;

public class MainActivity extends AppCompatActivity {

    private CommonController commonController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 通用
        commonController = new CommonController(this, this);
        commonController.checkUpdate();


        // 获取 TabLayout 和 ViewPager2, 设置 ViewPager 的适配器
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new TabAdapter(this));

        // 将 TabLayout 和 ViewPager2 关联起来
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("抖赚✨");
                    } else {
                        tab.setText("小红薯");
                    }
                }).attach();


    }

    // 创建适配器，返回 Fragment 对象
    private static class TabAdapter extends FragmentStateAdapter {

        public TabAdapter(MainActivity mainActivity) {
            super(mainActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            // 根据 tab 的位置返回不同的 Fragment
            if (position == 0) {
                return new DyFragment();
            } else {
                return new XhsFragment();
            }
        }

        @Override
        public int getItemCount() {
            // Tab 的数量
            return 2;
        }
    }

}
