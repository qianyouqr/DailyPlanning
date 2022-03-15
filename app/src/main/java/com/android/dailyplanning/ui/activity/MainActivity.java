package com.android.dailyplanning.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.dailyplanning.NotifyService;
import com.android.dailyplanning.R;
import com.android.dailyplanning.ui.fragment.DailyFragment;
import com.android.dailyplanning.ui.fragment.FragmentMe;
import com.android.dailyplanning.ui.fragment.ShowFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.content_view)
    FrameLayout contentView;
    @BindView(R.id.rb_daily)
    RadioButton rbDaily;
    @BindView(R.id.rb_show)
    RadioButton rbShow;
    @BindView(R.id.rb_me)
    RadioButton rbMe;
    @BindView(R.id.rg_function)
    RadioGroup rgFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startService(new Intent(this, NotifyService.class));
        init();
    }

    private void init() {
        rgFunction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                switch (checkedId) {
                    case R.id.rb_daily:
                        fragment = new DailyFragment();
                        break;
                    case R.id.rb_show:
                        fragment = new ShowFragment();
                        break;
                    case R.id.rb_me:
                        fragment = new FragmentMe();
                        break;
                }
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_view, fragment);
                    ft.commit();
                }
            }
        });
        //默认选中日程规划
        rbDaily.setChecked(true);
    }

}
