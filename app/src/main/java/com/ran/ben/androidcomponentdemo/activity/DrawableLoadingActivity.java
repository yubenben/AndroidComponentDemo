package com.ran.ben.androidcomponentdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.view.loading.LoadingDrawable;
import com.ran.ben.androidcomponentdemo.view.loading.SwapCircleLoadingRenderer;
import com.ran.ben.androidcomponentdemo.view.loading.SwapLoadingRenderer;

public class DrawableLoadingActivity extends AppCompatActivity {

    private LoadingDrawable mSwapDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_loading);

        ImageView mIvGear = (ImageView) findViewById(R.id.loading_view);
        mSwapDrawable = new LoadingDrawable(new SwapCircleLoadingRenderer(this));

        mIvGear.setImageDrawable(mSwapDrawable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSwapDrawable.start();
    }

    @Override
    protected void onStop() {
        mSwapDrawable.stop();
        super.onStop();
    }
}
