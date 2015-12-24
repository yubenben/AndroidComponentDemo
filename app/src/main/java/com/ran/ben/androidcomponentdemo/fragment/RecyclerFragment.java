package com.ran.ben.androidcomponentdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.adapter.LoadAdapter;
import com.ran.ben.androidcomponentdemo.refresh.OnRecycleViewScrollListener;

import java.util.ArrayList;

public class RecyclerFragment extends Fragment {
    private RecyclerView recyclerView;
    private LoadAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LoadAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setHasMoreData(true);
        recyclerView.addOnScrollListener(new OnRecycleViewScrollListener() {
            @Override
            public void onLoadMore() {
                adapter.setHasFooter(true);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> dataList = new ArrayList<String>();
                        for (int i = 0; i < 10; i++) {
                            dataList.add("Item " + (i + 1));
                        }
                        if (adapter.getItemCount() > 30) {
                            adapter.setHasMoreDataAndFooter(false, true);
                        } else {
                            adapter.appendToList(dataList);
                            adapter.setHasMoreDataAndFooter(true, true);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, 2000);
            }
        });
        refresh();
    }


    public void refresh() {
        adapter.showHeader();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> dataList = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                    dataList.add("Item " + (i + 1));
                }
                adapter.getList().clear();
                adapter.appendToList(dataList);
                adapter.hideHeader();
            }
        }, 1000);
    }


}
