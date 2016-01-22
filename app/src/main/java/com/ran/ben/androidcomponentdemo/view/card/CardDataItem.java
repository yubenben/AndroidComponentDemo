package com.ran.ben.androidcomponentdemo.view.card;

/**
 * 卡片数据装载对象
 *
 * @author xmuSistone
 */
public class CardDataItem {
    public String imagePath;
    public String userName;
    public int likeNum;
    public int imageNum;

    @Override
    public String toString() {
        return "CardDataItem{" +
                "imagePath='" + imagePath + '\'' +
                ", userName='" + userName + '\'' +
                ", likeNum=" + likeNum +
                ", imageNum=" + imageNum +
                '}';
    }
}
