package com.ran.ben.androidcomponentdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ran.ben.androidcomponentdemo.R;

import java.util.ArrayList;

/**
 * Created by yubenben on 15-12-11.
 */
public class AllActivityViewAdapter extends RecyclerView.Adapter<AllActivityViewAdapter.NormalTextViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<ActivityModel> activityModels;

    public AllActivityViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        activityModels = new ArrayList<>();
        String[] mTitles = context.getResources().getStringArray(R.array.titles);

        for(String text : mTitles) {
            try {
                activityModels.add(new ActivityModel(text,
                        (Class<? extends Activity>) Class.forName("com.ran.ben.androidcomponentdemo.activity." + text)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_text, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, final int position) {
        holder.mTextView.setText(activityModels.get(position).getTitle());
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, activityModels.get(
                        position).getActivityClass()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityModels == null ? 0 : activityModels.size();
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        NormalTextViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text_view);

        }
    }


    private class ActivityModel {

        private String title;
        private Class<? extends Activity> activityClass;

        public ActivityModel(String title,
                             Class<? extends Activity> activityClass) {
            this.title = title;
            this.activityClass = activityClass;
        }

        public String getTitle() {
            return title;
        }

        public Class<? extends Activity> getActivityClass() {
            return activityClass;
        }

    }
}
