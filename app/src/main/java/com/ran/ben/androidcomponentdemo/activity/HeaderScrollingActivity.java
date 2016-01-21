package com.ran.ben.androidcomponentdemo.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.adapter.HeadViewAdapter;
import com.ran.ben.androidcomponentdemo.adapter.MainTabAdapter;
import com.ran.ben.androidcomponentdemo.fragment.GridRecyclerFragment;
import com.ran.ben.androidcomponentdemo.fragment.NestedScrollFragment;
import com.ran.ben.androidcomponentdemo.fragment.LinearRecyclerFragment;

import java.util.ArrayList;
import java.util.List;

public class HeaderScrollingActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener,
        AppBarLayout.OnOffsetChangedListener {

    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private LinearRecyclerFragment mLinearRecyclerFragment;
    private GridRecyclerFragment mGridRecyclerFragment;

    private MainTabAdapter mAdapter;

    private ViewPager mHeadViewPager;
    private HeadViewAdapter mHeadViewAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the toolbar min height
//        int toolbar_height = PlatformUtils.getToolbarHeight(this) * 2;
//        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
//        params.height = toolbar_hight;
//        toolbar.setLayoutParams(params);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        List<String> titles = new ArrayList<>();
        titles.add(" One ");
        titles.add(" Two ");
        titles.add(" Three ");

        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);

        List<Fragment> fragments = new ArrayList<>();
        mLinearRecyclerFragment = new LinearRecyclerFragment();
        fragments.add(mLinearRecyclerFragment);
        fragments.add(new NestedScrollFragment());
        mGridRecyclerFragment = new GridRecyclerFragment();
        fragments.add(mGridRecyclerFragment);

        mAdapter = new MainTabAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);

        mHeadViewPager = (ViewPager) findViewById(R.id.header_viewpager);
        mHeadViewAdapter = new HeadViewAdapter(this, null);
        ArrayList<String> headList = new ArrayList<String>();
        headList.add("1111");
        headList.add("1111");
        mHeadViewAdapter.setmLists(headList);
        mHeadViewPager.setAdapter(mHeadViewAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        if(mViewPager.getCurrentItem() == 0) {
            mLinearRecyclerFragment.refresh();
        } else if (mViewPager.getCurrentItem() == 2) {
            mGridRecyclerFragment.refresh();
        }
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSwipeRefreshLayout.setEnabled(verticalOffset== 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }
}
