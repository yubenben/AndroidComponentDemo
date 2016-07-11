package com.ran.ben.androidcomponentdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.adapter.MyRecyclerAdapter;
import com.ran.ben.androidcomponentdemo.view.DividerItemDecoration;
import com.ran.ben.androidcomponentdemo.view.SuperSwipeRefreshLayout;

import java.util.ArrayList;

import static com.ran.ben.androidcomponentdemo.view.SuperSwipeRefreshLayout.OnPullRefreshListener;

public class LinearRecyclerFragment extends Fragment
        implements OnPullRefreshListener, SuperSwipeRefreshLayout.OnPushLoadMoreListener {
    private RecyclerView recyclerView;
    private MyRecyclerAdapter adapter;
    private SuperSwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout = (SuperSwipeRefreshLayout) getView().findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnPullRefreshListener(this);
        mSwipeRefreshLayout.setOnPushLoadMoreListener(this);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));
        adapter = new MyRecyclerAdapter();
        recyclerView.setAdapter(adapter);
//        adapter.setHasMoreData(true);
//        recyclerView.addOnScrollListener(new OnRecycleViewScrollListener() {
//            @Override
//            public void onLoadMore() {
//                adapter.setHasFooter(true);
//                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                recyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        ArrayList<String> dataList = new ArrayList<String>();
//                        for (int i = 0; i < 10; i++) {
//                            dataList.add("Item " + (i + 1));
//                        }
//                        if (adapter.getItemCount() > 30) {
//                            adapter.setHasMoreDataAndFooter(false, true);
//                        } else {
//                            adapter.appendToList(dataList);
//                            adapter.setHasMoreDataAndFooter(true, true);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }, 2000);
//            }
//        });
        refresh();
    }


    public void refresh() {

        adapter.notifyDataSetChanged();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.getList().clear();
                ArrayList<String> dataList = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                    dataList.add("Item " + (i + 1));
                }

                adapter.appendToList(dataList);
            }
        }, 1000);
    }

    public void loadMore() {

        adapter.notifyDataSetChanged();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> dataList = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                    dataList.add("Item " + (i + 1));
                }

                adapter.appendToList(dataList);
            }
        }, 1000);
    }


    @Override
    public void onRefresh() {
        refresh();
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onPullDistance(int distance) {

    }

    @Override
    public void onPullEnable(boolean enable) {

    }

    @Override
    public void onLoadMore() {
        loadMore();
    }

    @Override
    public void onPushDistance(int distance) {

    }

    @Override
    public void onPushEnable(boolean enable) {

    }
}
