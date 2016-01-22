package com.ran.ben.androidcomponentdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.fragment.CardFragment;

/**
 * Created by yubenben
 * Date: 16-1-22.
 */
public class CardSlideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_slide_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CardFragment())
                    .commitAllowingStateLoss();
        }
    }
}
