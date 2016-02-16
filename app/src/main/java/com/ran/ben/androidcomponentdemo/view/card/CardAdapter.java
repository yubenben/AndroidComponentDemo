package com.ran.ben.androidcomponentdemo.view.card;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Collection;

import jp.wasabeef.fresco.processors.BlurPostprocessor;

/**
 * Created by yubenben
 * Date: 16-1-26.
 */
public class CardAdapter extends BaseCardAdapter {

    private final Object mLock = new Object();
    private ArrayList<CardDataItem> mData;
    private LayoutInflater inflater;
    private Postprocessor processor;

    private int mWidth;

    public CardAdapter(Context context, Collection<CardDataItem> items) {
        super(context);
        inflater = LayoutInflater.from(mContext);
        if (items != null && items.size() > 0) {
            mData = new ArrayList<>(items);
        } else {
            mData = new ArrayList<>();
        }

        //int displayHeigth = DensityUtil.gettDisplayHeight(context);
        int displayWidth = DensityUtil.gettDisplayWidth(context);

        mWidth = (int) (displayWidth -
                context.getResources().getDimension(R.dimen.match_user_card_padding) * 2);

        processor = new BlurPostprocessor(mContext, 25);
    }

    @Override
    public Object getItem(int position) {
        return getDataItem(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getCardView(int position, View convertView, ViewGroup parent) {


        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.card_item, parent, false);

            holder.mImageView = (SimpleDraweeView) convertView.findViewById(R.id.card_image_view);
            ViewGroup.LayoutParams imageParams = holder.mImageView.getLayoutParams();
            imageParams.width = mWidth;
            imageParams.height = mWidth;
            holder.mUserNameTv = (TextView) convertView.findViewById(R.id.card_user_name);
            holder.mImageNumTv = (TextView) convertView.findViewById(R.id.card_pic_num);
            holder.mLikeNumTv = (TextView) convertView.findViewById(R.id.card_like);
            holder.ignoreBtn = (ImageView) convertView.findViewById(R.id.ignore_btn);
            holder.likeBtn = (ImageView) convertView.findViewById(R.id.like_btn);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }


        final CardDataItem itemData = getDataItem(position);
        int type = getItemViewType(position);
        switch (type) {
            case CardDataItem.DATA_TYPE_NORMAL:
                holder.mImageView.setImageURI(Uri.parse(itemData.imagePath));
                break;
            case CardDataItem.DATA_TYPE_BLUR:
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(itemData.imagePath))
                        .setPostprocessor(processor)
                        .build();

                PipelineDraweeController controller =
                        (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(holder.mImageView.getController())
                                .build();
                holder.mImageView.setController(controller);
                break;
            default:
                holder.mImageView.setImageURI(Uri.parse(itemData.imagePath));
                break;
        }
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.itemClick(v, itemData);
                } else {
                    Toast.makeText(mContext, "卡片点击-" + itemData.userName,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.mUserNameTv.setText(itemData.userName);
        holder.mImageNumTv.setText(String.valueOf(itemData.imageNum));
        holder.mLikeNumTv.setText(String.valueOf(position));
        holder.ignoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.ignoreButtonClick(itemData);
                } else {
                    Toast.makeText(mContext, "点击无感",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.likeButtonClick(itemData);
                } else {
                    Toast.makeText(mContext, "点击喜欢",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    private static class Holder {
        SimpleDraweeView mImageView;
        TextView mUserNameTv;
        TextView mImageNumTv;
        TextView mLikeNumTv;
        ImageView likeBtn;
        ImageView ignoreBtn;
    }

    public CardDataItem getDataItem(int position) {
        synchronized (mLock) {
            if (mData.size() <= position) {
                return null;
            }
            return mData.get(position);
        }
    }

    public void clear() {
        synchronized (mLock) {
            mData.clear();
        }
        notifyDataSetInvalidated();
    }

    public void add(CardDataItem item) {
        synchronized (mLock) {
            mData.add(item);
        }
        notifyDataSetChanged();
    }

    public void addAll(Collection<CardDataItem> items) {
        synchronized (mLock) {
            mData.addAll(items);
        }
        notifyDataSetChanged();
    }

    private ICardItemOnClickListener mListener;

    public void setOnCardItemOnClickListener(ICardItemOnClickListener listener) {
        mListener = listener;
    }
    public interface ICardItemOnClickListener {
        void itemClick(View v, CardDataItem data);

        void likeButtonClick(CardDataItem data);

        void ignoreButtonClick(CardDataItem data);
    }
}
