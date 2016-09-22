package com.ran.ben.androidcomponentdemo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.DensityUtil;
import com.ran.ben.androidcomponentdemo.view.ProgressCoasterView;

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
                            progress.showCircleView(new ProgressCoasterView.IOnShowViewEndListener() {
                                @Override
                                public void onEnd() {

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
//                                    scrollView.smoothScrollBy(0, DensityUtil.gettDisplayWidth(CoasterCircleProgressActivity.this));

//                                    PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY"
//                                            , 0, 0
//                                            , 0, -DensityUtil.gettDisplayWidth(CoasterCircleProgressActivity.this));
//                                    ObjectAnimator listObjectAnimator
//                                            = ObjectAnimator.ofPropertyValuesHolder(progress, pvhY).setDuration(1000);
//                                    listObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//                                    listObjectAnimator.start();
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
