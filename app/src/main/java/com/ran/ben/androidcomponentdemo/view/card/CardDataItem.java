package com.ran.ben.androidcomponentdemo.view.card;

/**
 * 卡片数据装载对象
 *
 */
public class CardDataItem {

    public static final int  DATA_TYPE_NORMAL  = 0;
    public static final int  DATA_TYPE_BLUR  = 1;

    public String imagePath;
    public String userName;
    public int likeNum;
    public int imageNum;

    private int type;

    @Override
    public String toString() {
        return "CardDataItem{" +
                "imagePath='" + imagePath + '\'' +
                ", userName='" + userName + '\'' +
                ", likeNum=" + likeNum +
                ", imageNum=" + imageNum +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
