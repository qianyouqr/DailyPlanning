package com.android.dailyplanning.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.dailyplanning.R;
import com.android.dailyplanning.entity.Plan;
import com.android.dailyplanning.utils.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class NotifyActivity extends AppCompatActivity {

    @BindView(R.id.tv_key_word)
    TextView tvKeyWord;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;
    @BindView(R.id.rb_four)
    RadioButton rbFour;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private Plan plan;
    private Vibrator vibrator;  //手机震动器
    private float info = 1F;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        ButterKnife.bind(this);
        plan = (Plan) getIntent().getSerializableExtra("plan");

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000, 50, 1000, 50, 1000}, 0);//隔1秒震动一次

        tvKeyWord.setText("关键词：" + plan.getKeyWord());
        tvContent.setText("任务内容：" + plan.getContent());

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        info = 0F;
                        break;
                    case R.id.rb_two:
                        info = 0.25F;
                        break;
                    case R.id.rb_three:
                        info = 0.5F;
                        break;
                    case R.id.rb_four:
                        info = 0.75F;
                        break;
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        vibrator.cancel();
        super.onDestroy();
    }

    @OnClick(R.id.btn_ok)
    public void onViewClicked() {
        plan.setCompleteInfo(String.valueOf(info));
        plan.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    MyUtils.showToast(NotifyActivity.this, e.getMessage());
                    return;
                }
                MyUtils.showToast(NotifyActivity.this, "操作成功");
                finish();
            }
        });
    }
}
