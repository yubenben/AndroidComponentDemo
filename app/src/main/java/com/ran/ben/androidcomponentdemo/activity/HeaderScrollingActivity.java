package com.ran.ben.androidcomponentdemo.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.adapter.MainTabAdapter;
import com.ran.ben.androidcomponentdemo.fragment.NestedScrollFragment;
import com.ran.ben.androidcomponentdemo.fragment.RecyclerFragment;

import java.util.ArrayList;
import java.util.List;

public class HeaderScrollingActivity extends AppCompatActivity {

    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private RecyclerFragment fragment;

    private MainTabAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the toolbar min height
//        int toolbar_hight = PlatformUtils.getToolbarHeight(this) * 2;
//        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
//        params.height = toolbar_hight;
//        toolbar.setLayoutParams(params);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        List<String> titles = new ArrayList<>();
        titles.add(" One ");
        titles.add(" Two ");

        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        List<Fragment> fragments = new ArrayList<>();
        fragment = new RecyclerFragment();
        fragments.add(fragment);
        fragments.add(new NestedScrollFragment());

        mAdapter = new MainTabAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
    }
}
