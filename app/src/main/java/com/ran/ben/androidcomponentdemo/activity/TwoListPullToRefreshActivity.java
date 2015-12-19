package com.ran.ben.androidcomponentdemo.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.DensityUtil;
import com.ran.ben.androidcomponentdemo.view.custom.ZZPullToRefreshScrollView;
import com.ran.ben.androidcomponentdemo.view.custom.ZZScrollView;

import java.util.ArrayList;


/**
 * Created by yubenben on 15-12-19.
 */
public class TwoListPullToRefreshActivity extends FragmentActivity
        implements PullToRefreshBase.OnRefreshListener2<ZZScrollView>,
        View.OnClickListener, AdapterView.OnItemClickListener {


    private static final int REFRESH_ONE_LIST_DATA = 100;
    private static final int GET_MORE_ONE_LIST_DATA = 101;
    private static final int REFRESH_TWO_LIST_DATA = 102;
    private static final int GET_MORE_TWO_LIST_DATA = 103;

    private ZZPullToRefreshScrollView mScrollView;

    private LinearLayout mTabLayout;
    private RelativeLayout mOneTab;
    private RelativeLayout mTwoTab;
    private View mOneSelectLine;
    private View mTwoSelectLine;

    private ListView mOneListView;
    private ListView mTwoListView;

    private StringAdapter mOneAdapter;
    private StringAdapter mTwoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_list_pull_to_refresh_layout);
        initViews();
        setTab(1);
    }

    private void initViews() {

        mScrollView  = (ZZPullToRefreshScrollView) findViewById(R.id.root_scroll_view);
        mScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mScrollView.setOnRefreshListener(this);
        mScrollView.setOnScrollChangedListener(new ZZPullToRefreshScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                Log.d("ScrollView", "ZZPullToRefreshScrollView onScrollChanged " +
                        "top = " + t + ", odlt = " + oldt);
            }
        });

        mScrollView.getRefreshableView().setOnScrollChangedListener(new ZZScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                Log.d("ScrollView", "ZZScrollView onScrollChanged " +
                        "top = " + t + ", odlt = " + oldt);
            }
        });

        mTabLayout = (LinearLayout) findViewById(R.id.tab_layout);
        mOneTab = (RelativeLayout) findViewById(R.id.one_rl);
        mOneTab.setOnClickListener(this);
        mTwoTab = (RelativeLayout) findViewById(R.id.two_rl);
        mTwoTab.setOnClickListener(this);
        mOneSelectLine = findViewById(R.id.one_underline);
        mTwoSelectLine = findViewById(R.id.two_underline);

        mOneListView = (ListView) findViewById(R.id.one_list);
        ViewGroup.LayoutParams params = mOneListView.getLayoutParams();
        params.height = DensityUtil.gettDisplayHeight(this) / 3;
        mOneListView.setOnItemClickListener(this);
        mTwoListView = (ListView) findViewById(R.id.two_list);
        ViewGroup.LayoutParams params2 = mTwoListView.getLayoutParams();
        params2.height = DensityUtil.gettDisplayHeight(this);
        mTwoListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.one_rl:
                setTab(1);
                break;
            case R.id.two_rl:
                setTab(2);
                break;
        }
    }

    private void setTab(int i) {
        switch (i) {
            case 1:
                mOneSelectLine.setVisibility(View.VISIBLE);
                mTwoSelectLine.setVisibility(View.GONE);
                mOneListView.setVisibility(View.VISIBLE);
                mTwoListView.setVisibility(View.GONE);
                break;
            case  2:
                mOneSelectLine.setVisibility(View.GONE);
                mTwoSelectLine.setVisibility(View.VISIBLE);
                mOneListView.setVisibility(View.GONE);
                mTwoListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private int getCurrentTab() {
        if (mOneSelectLine.getVisibility() == View.VISIBLE) {
            return 1;
        } else if (mTwoSelectLine.getVisibility() == View.VISIBLE) {
            return 2;
        }

        return 1;
    }

    private void requestMoreData(final boolean refresh) {
        Log.w("ScrollView", "one listview height = " + mOneListView.getHeight());
        Log.w("ScrollView", "two listview height = " + mTwoListView.getHeight());
        if (getCurrentTab() == 1) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    ArrayList<String>  data = new ArrayList<>();

                    for (int i = 0; i < 10; i++) {
                        data.add("message  ");
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = Message.obtain(mHandler);
                    if (refresh) {
                        msg.what = REFRESH_ONE_LIST_DATA;
                    } else {
                        msg.what = GET_MORE_ONE_LIST_DATA;
                    }
                    msg.obj = data;
                    msg.sendToTarget();
                    return null;
                }
            }.execute();
        } else if (getCurrentTab() == 2) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    ArrayList<String> data = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        data.add("message  ");
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = Message.obtain(mHandler);
                    if (refresh) {
                        msg.what = REFRESH_TWO_LIST_DATA;
                    } else {
                        msg.what = GET_MORE_TWO_LIST_DATA;
                    }
                    msg.obj = data;
                    msg.sendToTarget();
                    return null;
                }
            }.execute();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        requestMoreData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        requestMoreData(false);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_ONE_LIST_DATA:
                    if (mOneAdapter == null) {
                        mOneAdapter = new StringAdapter(TwoListPullToRefreshActivity.this
                                , (ArrayList<String>) msg.obj);
                        mOneListView.setAdapter(mOneAdapter);
                    } else  {
                        mOneAdapter.clear();
                        mOneAdapter.addAll((ArrayList<String>) msg.obj);
                        mOneAdapter.notifyDataSetChanged();
                    }
                    break;

                case GET_MORE_ONE_LIST_DATA:
                    if (mOneAdapter == null) {
                        mOneAdapter = new StringAdapter(TwoListPullToRefreshActivity.this
                                , (ArrayList<String>) msg.obj);
                        mOneListView.setAdapter(mOneAdapter);
                    } else  {
                        mOneAdapter.addAll((ArrayList<String>) msg.obj);
                        mOneAdapter.notifyDataSetChanged();
                    }

                    break;

                case REFRESH_TWO_LIST_DATA:
                    if (mTwoAdapter == null) {
                        mTwoAdapter = new StringAdapter(TwoListPullToRefreshActivity.this
                                , (ArrayList<String>) msg.obj);
                        mTwoListView.setAdapter(mTwoAdapter);
                    } else  {
                        mTwoAdapter.clear();
                        mTwoAdapter.addAll((ArrayList<String>) msg.obj);
                        mTwoAdapter.notifyDataSetChanged();
                    }
                    break;

                case GET_MORE_TWO_LIST_DATA:
                    if (mTwoAdapter == null) {
                        mTwoAdapter = new StringAdapter(TwoListPullToRefreshActivity.this
                                , (ArrayList<String>) msg.obj);
                        mTwoListView.setAdapter(mTwoAdapter);
                    } else  {
                        mTwoAdapter.addAll((ArrayList<String>) msg.obj);
                        mTwoAdapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    break;
            }

            mScrollView.onRefreshComplete();
        }

    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mOneListView) {
            Toast.makeText(this,  "mOneListView click item "  + position,
                    Toast.LENGTH_SHORT).show();
        } else if (parent == mTwoListView) {
            Toast.makeText(this,  "mTwoListView  click item "  + position,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
