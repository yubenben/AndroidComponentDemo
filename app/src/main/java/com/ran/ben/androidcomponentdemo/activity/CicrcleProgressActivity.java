package com.ran.ben.androidcomponentdemo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ran.ben.androidcomponentdemo.R;

public class CicrcleProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cicrcle_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SimpleDraweeView mImage = (SimpleDraweeView) findViewById(R.id.image);
        mImage.setImageURI(
                Uri.parse("http://h.hiphotos.baidu.com/image/h%3D300/sign=97028a6b4a086e0675a8394b32097b5a/023b5bb5c9ea15ce1948f653b0003af33b87b2c1.jpg"));

        SimpleDraweeView mImage2 = (SimpleDraweeView) findViewById(R.id.image2);
        mImage2.setImageURI(
                Uri.parse("http://b.hiphotos.baidu.com/image/h%3D300/sign=592f8030ac18972bbc3a06cad6cd7b9d/267f9e2f0708283896096030bf99a9014c08f18a.jpg"));

        SimpleDraweeView mImage3 = (SimpleDraweeView) findViewById(R.id.image3);
        mImage3.setImageURI(
                Uri.parse("http://b.hiphotos.baidu.com/image/h%3D300/sign=592f8030ac18972bbc3a06cad6cd7b9d/267f9e2f0708283896096030bf99a9014c08f18a.jpg"));

        final View progress = findViewById(R.id.progress_bar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progress.getVisibility() != View.VISIBLE) {
                    progress.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

}