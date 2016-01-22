package com.ran.ben.androidcomponentdemo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.view.card.CardDataItem;
import com.ran.ben.androidcomponentdemo.view.card.CardSlidePanel;
import com.ran.ben.androidcomponentdemo.view.card.CardSlidePanel.CardSwitchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡片Fragment
 *
 * @author xmuSistone
 */
@SuppressLint({"HandlerLeak", "NewApi", "InflateParams"})
public class CardFragment extends Fragment {

    private CardSwitchListener cardSwitchListener;

    private String imagePaths[] = {
            "asset:///wall01.jpg",
            "asset:///wall03.jpg",
            "asset:///wall04.jpg",
            "asset:///wall02.jpg",
            "asset:///wall05.jpg",
            "asset:///wall06.jpg",
            "asset:///wall07.jpg",
            "asset:///wall08.jpg",
            "asset:///wall09.jpg",
            "asset:///wall10.jpg",
            "asset:///wall11.jpg",
            "asset:///wall12.jpg",
            "asset:///wall01.jpg",
            "asset:///wall02.jpg",
            "asset:///wall03.jpg",
            "asset:///wall04.jpg",
            "asset:///wall05.jpg",
            "asset:///wall06.jpg",
            "asset:///wall07.jpg",
            "asset:///wall08.jpg",
            "asset:///wall09.jpg",
            "asset:///wall10.jpg",
            "asset:///wall11.jpg",
            "asset:///wall12.jpg"}; // 24个图片资源名称

    private String names[] = {"郭富城", "刘德华", "张学友", "李连杰", "成龙", "谢霆锋", "李易峰",
            "霍建华", "胡歌", "曾志伟", "吴孟达", "梁朝伟", "周星驰", "赵本山", "郭德纲", "周润发", "邓超",
            "王祖蓝", "王宝强", "黄晓明", "张卫健", "徐峥", "李亚鹏", "郑伊健"}; // 24个人名

    private List<CardDataItem> dataList = new ArrayList<CardDataItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_layout, null);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        CardSlidePanel slidePanel = (CardSlidePanel) rootView
                .findViewById(R.id.image_slide_panel);
        cardSwitchListener = new CardSwitchListener() {

            @Override
            public void onShow(int index) {
                Log.d("CardFragment", "正在显示-" + dataList.get(index).userName);
            }

            @Override
            public void onCardVanish(int index, int type) {
                Log.d("CardFragment", "正在消失-" + dataList.get(index).userName + " 消失type=" + type);
            }

            @Override
            public void onItemClick(View cardView, int index) {
                Log.d("CardFragment", "卡片点击-" + dataList.get(index).userName);
            }
        };
        slidePanel.setCardSwitchListener(cardSwitchListener);

        prepareDataList();
        slidePanel.fillData(dataList);
    }

    private void prepareDataList() {
        int num = imagePaths.length;

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < num; i++) {
                CardDataItem dataItem = new CardDataItem();
                dataItem.userName = names[i];
                dataItem.imagePath = imagePaths[i];
                dataItem.likeNum = (int) (Math.random() * 10);
                dataItem.imageNum = (int) (Math.random() * 6);
                dataList.add(dataItem);
            }
        }
    }

}
