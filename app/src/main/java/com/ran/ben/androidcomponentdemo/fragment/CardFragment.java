package com.ran.ben.androidcomponentdemo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.view.card.CardAdapter;
import com.ran.ben.androidcomponentdemo.view.card.CardDataItem;
import com.ran.ben.androidcomponentdemo.view.card.CardSlidePanel;
import com.ran.ben.androidcomponentdemo.view.card.CardSlidePanel.CardSwitchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡片Fragment
 *
 */
@SuppressLint({"HandlerLeak", "NewApi", "InflateParams"})
public class CardFragment extends Fragment {

    private CardSwitchListener cardSwitchListener;

    CardSlidePanel slidePanel;
    private CardAdapter mCardAdapter;

    private List<CardDataItem> dataList = new ArrayList<CardDataItem>();


    private boolean onLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_layout, null);
        initView(rootView);
        initData();

        return rootView;
    }

    private void initView(View rootView) {
        slidePanel = (CardSlidePanel) rootView
                .findViewById(R.id.image_slide_panel);
        cardSwitchListener = new CardSwitchListener() {

            @Override
            public void onShow(int index) {
                CardDataItem item  = (CardDataItem) mCardAdapter.getItem(index);
                if (item != null) {
                    Log.d("CardFragment", "正在显示-" + item.userName);
                }
            }

            @Override
            public void onCardVanish(int index, int type) {
                CardDataItem item  = (CardDataItem) mCardAdapter.getItem(index);
                if (item != null) {
                    Log.d("CardFragment", "正在消失-" + item.userName + " 消失type=" + type);
                }
            }

            @Override
            public void onItemClick(int index) {
                CardDataItem item  = (CardDataItem) mCardAdapter.getItem(index);
                if (item != null) {
                    Log.d("CardFragment", "卡片点击-" + item.userName);
                    Toast.makeText(getActivity(), "卡片点击-" + item.userName,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        slidePanel.setCardSwitchListener(cardSwitchListener);
        slidePanel.setOnLastItemVisible(new CardSlidePanel.OnLastItemVisible() {
            @Override
            public void onVisible() {
                if (!onLoading) {
                    onLoading = true;
                    Toast.makeText(getActivity(), "加载更多。。。",
                            Toast.LENGTH_SHORT).show();
                    slidePanel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCardAdapter.addAll(dataList);
                            mCardAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "加载完成。。。",
                                    Toast.LENGTH_SHORT).show();
                            onLoading  = false;

                        }
                    }, 2000);
                }

            }
        });

    }

    private void initData() {
        prepareDataList();
        mCardAdapter = new CardAdapter(getActivity(), dataList);
        slidePanel.setAdapter(mCardAdapter);
    }

    private void prepareDataList() {
        int num = imagePaths.length;

        for (int i = 0; i < num; i++) {
            CardDataItem dataItem = new CardDataItem();
            dataItem.userName = names[i % 24] + ":" + i;
            dataItem.imagePath = imagePaths[i];
            dataItem.likeNum = (int) (Math.random() * 10);
            dataItem.imageNum = (int) (Math.random() * 6);
            dataList.add(dataItem);
        }

    }


    private String imagePaths[] =
            {
                    "http://b.hiphotos.baidu.com/image/h%3D300/sign=592f8030ac18972bbc3a06cad6cd7b9d/267f9e2f0708283896096030bf99a9014c08f18a.jpg",
                    "http://f.hiphotos.baidu.com/image/h%3D300/sign=4c454181d643ad4bb92e40c0b2025a89/03087bf40ad162d967a5de3816dfa9ec8a13cd4f.jpg",
                    "http://e.hiphotos.baidu.com/image/h%3D300/sign=11f7bfc7d239b60052ce09b7d9503526/f2deb48f8c5494eef426b33d2af5e0fe99257e89.jpg",
                    "http://h.hiphotos.baidu.com/image/h%3D300/sign=97028a6b4a086e0675a8394b32097b5a/023b5bb5c9ea15ce1948f653b0003af33b87b2c1.jpg",
                    "http://g.hiphotos.baidu.com/image/h%3D200/sign=bf47ff3e504e9258b93481eeac83d1d1/b7fd5266d01609241d7cb541d20735fae6cd340a.jpg",
                    "http://g.hiphotos.baidu.com/image/h%3D200/sign=0834a8e8bf014a909e3e41bd99773971/472309f790529822642d2e99d1ca7bcb0a46d449.jpg",
                    "http://img0.imgtn.bdimg.com/it/u=950613993,2010759747&fm=11&gp=0.jpg",
                    "http://img4.imgtn.bdimg.com/it/u=3055967711,608528141&fm=11&gp=0.jpg",
                    "http://img2.imgtn.bdimg.com/it/u=3594948319,3518785373&fm=21&gp=0.jpg",
                    "http://img0.imgtn.bdimg.com/it/u=2247699704,405672166&fm=23&gp=0.jpg",
                    "http://b.hiphotos.baidu.com/image/h%3D300/sign=592f8030ac18972bbc3a06cad6cd7b9d/267f9e2f0708283896096030bf99a9014c08f18a.jpg",
                    "http://f.hiphotos.baidu.com/image/h%3D300/sign=4c454181d643ad4bb92e40c0b2025a89/03087bf40ad162d967a5de3816dfa9ec8a13cd4f.jpg",
                    "http://e.hiphotos.baidu.com/image/h%3D300/sign=11f7bfc7d239b60052ce09b7d9503526/f2deb48f8c5494eef426b33d2af5e0fe99257e89.jpg",
                    "http://h.hiphotos.baidu.com/image/h%3D300/sign=97028a6b4a086e0675a8394b32097b5a/023b5bb5c9ea15ce1948f653b0003af33b87b2c1.jpg",
                    "http://g.hiphotos.baidu.com/image/h%3D200/sign=bf47ff3e504e9258b93481eeac83d1d1/b7fd5266d01609241d7cb541d20735fae6cd340a.jpg",
                    "http://g.hiphotos.baidu.com/image/h%3D200/sign=0834a8e8bf014a909e3e41bd99773971/472309f790529822642d2e99d1ca7bcb0a46d449.jpg",
                    "http://img0.imgtn.bdimg.com/it/u=950613993,2010759747&fm=11&gp=0.jpg",
                    "http://img4.imgtn.bdimg.com/it/u=3055967711,608528141&fm=11&gp=0.jpg",
                    "http://img2.imgtn.bdimg.com/it/u=3594948319,3518785373&fm=21&gp=0.jpg",
                    "http://img0.imgtn.bdimg.com/it/u=2247699704,405672166&fm=23&gp=0.jpg",
                    "http://b.hiphotos.baidu.com/image/h%3D300/sign=592f8030ac18972bbc3a06cad6cd7b9d/267f9e2f0708283896096030bf99a9014c08f18a.jpg",
                    "http://f.hiphotos.baidu.com/image/h%3D300/sign=4c454181d643ad4bb92e40c0b2025a89/03087bf40ad162d967a5de3816dfa9ec8a13cd4f.jpg",
                    "http://e.hiphotos.baidu.com/image/h%3D300/sign=11f7bfc7d239b60052ce09b7d9503526/f2deb48f8c5494eef426b33d2af5e0fe99257e89.jpg",
                    "http://h.hiphotos.baidu.com/image/h%3D300/sign=97028a6b4a086e0675a8394b32097b5a/023b5bb5c9ea15ce1948f653b0003af33b87b2c1.jpg",
                    "http://g.hiphotos.baidu.com/image/h%3D200/sign=bf47ff3e504e9258b93481eeac83d1d1/b7fd5266d01609241d7cb541d20735fae6cd340a.jpg",
                    "http://g.hiphotos.baidu.com/image/h%3D200/sign=0834a8e8bf014a909e3e41bd99773971/472309f790529822642d2e99d1ca7bcb0a46d449.jpg",
                    "http://img0.imgtn.bdimg.com/it/u=950613993,2010759747&fm=11&gp=0.jpg",
                    "http://img4.imgtn.bdimg.com/it/u=3055967711,608528141&fm=11&gp=0.jpg",
                    "http://img2.imgtn.bdimg.com/it/u=3594948319,3518785373&fm=21&gp=0.jpg",
                    "http://img0.imgtn.bdimg.com/it/u=2247699704,405672166&fm=23&gp=0.jpg",
                    "asset:///wall12.jpg"
            }; // 24个图片资源名称

    private String names[] =
            {
                    "郭富城",
                    "刘德华",
                    "张学友",
                    "李连杰",
                    "成龙",
                    "谢霆锋",
                    "李易峰",
                    "霍建华",
                    "胡歌",
                    "曾志伟",
                    "吴孟达",
                    "梁朝伟",
                    "周星驰",
                    "赵本山",
                    "郭德纲",
                    "周润发",
                    "邓超",
                    "王祖蓝",
                    "王宝强",
                    "黄晓明",
                    "张卫健",
                    "徐峥",
                    "李亚鹏",
                    "郑伊健"
            }; // 24个人名

}
