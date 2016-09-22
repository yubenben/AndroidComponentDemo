package com.ran.ben.androidcomponentdemo.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.DensityUtil;
import com.ran.ben.androidcomponentdemo.view.ProgressCoasterView;

import java.util.Random;

public class CoasterCircleProgressActivity extends AppCompatActivity {

    private View mContainer;
    private ScrollView  scrollView;

    private ProgressCoasterView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaster_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContainer  = findViewById(R.id.cost_container);
        scrollView = (ScrollView) findViewById(R.id.cost_scrollview);
        scrollView.setVerticalScrollBarEnabled(true);

        final TextView textView = (TextView)  findViewById(R.id.job_seeker_num_tv);

        progress = (ProgressCoasterView) findViewById(R.id.progress_bar);
        ViewGroup.LayoutParams params = progress.getLayoutParams();
        params.width  = (DensityUtil.gettDisplayWidth(this));
        params.height = (DensityUtil.gettDisplayWidth(this));

        SimpleDraweeView mImage = (SimpleDraweeView) findViewById(R.id.image);
        mImage.setImageURI(
                Uri.parse("http://h.hiphotos.baidu.com/image/h%3D300/sign=97028a6b4a086e0675a8394b32097b5a/023b5bb5c9ea15ce1948f653b0003af33b87b2c1.jpg"));

        SimpleDraweeView mImage2 = (SimpleDraweeView) findViewById(R.id.image2);
        mImage2.setImageURI(
                Uri.parse("http://b.hiphotos.baidu.com/image/h%3D300/sign=592f8030ac18972bbc3a06cad6cd7b9d/267f9e2f0708283896096030bf99a9014c08f18a.jpg"));

        SimpleDraweeView mImage3 = (SimpleDraweeView) findViewById(R.id.image3);
        mImage3.setImageURI(
                Uri.parse("res://com.ran.ben.androidcomponentdemo/"+R.drawable.ic_loading_caster));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progress.getVisibility() != View.VISIBLE) {
                    progress.setVisibility(View.VISIBLE);
                    progress.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final ValueAnimator animator = ValueAnimator.ofInt(0, 10);
                            animator.setRepeatCount(ValueAnimator.INFINITE);
                            animator.setInterpolator(new LinearInterpolator());
                            animator.setDuration(1000);
                            final  Random random = new Random();
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textView.setText(String.valueOf(random.nextInt(80000) + 10000));
                                }
                            });
                            animator.start();
                            progress.showCircleView(new ProgressCoasterView.IOnShowViewEndListener() {
                                @Override
                                public void onEnd() {
                                    animator.cancel();
                                    TranslateAnimation translateAnimation = new TranslateAnimation(0, 0
                                            , 0, -DensityUtil.gettDisplayWidth(CoasterCircleProgressActivity.this) );
                                    translateAnimation.setDuration(1000);
                                    translateAnimation.setFillAfter(true);
                                    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            progress.setVisibility(View.INVISIBLE);

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    mContainer.startAnimation(translateAnimation);
                                }
                            });
                        }
                    },  1000);
                } else {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

}
