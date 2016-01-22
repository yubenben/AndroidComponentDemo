package com.ran.ben.androidcomponentdemo.view.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.DensityUtil;

/**
 * 卡片View项
 * @author xmuSistone
 */
@SuppressLint("NewApi")
public class CardItemView extends LinearLayout {

    private static final String TAG = "CardItemView";
    private int mWidth;
    public SimpleDraweeView mImageView;
    private TextView mUserNameTv;
    private TextView mImageNumTv;
    private TextView mLikeNumTv;

    public CardItemView(Context context) {
        this(context, null);
    }

    public CardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        int displayHeigth  = DensityUtil.gettDisplayHeight(context);
        int displayWidth = DensityUtil.gettDisplayWidth(context);

        mWidth  = (int) (displayWidth -
                context.getResources().getDimension(R.dimen.match_user_card_padding) * 2);


        inflate(context, R.layout.card_item, this);
        mImageView = (SimpleDraweeView) findViewById(R.id.card_image_view);
        ViewGroup.LayoutParams imageParams = mImageView.getLayoutParams();
        imageParams.width = mWidth;
        imageParams.height = mWidth;
        mUserNameTv = (TextView) findViewById(R.id.card_user_name);
        mImageNumTv = (TextView) findViewById(R.id.card_pic_num);
        mLikeNumTv = (TextView) findViewById(R.id.card_like);
    }

    public void fillData(CardDataItem itemData) {
        //ImageLoader.getInstance().displayImage(itemData.imagePath, mImageView);
        Log.d(TAG, "fillData: itemData = " +  itemData);

        mImageView.setImageURI(Uri.parse(itemData.imagePath));
        mUserNameTv.setText(itemData.userName);
        mImageNumTv.setText(itemData.imageNum + "");
        mLikeNumTv.setText(itemData.likeNum + "");
    }
}
