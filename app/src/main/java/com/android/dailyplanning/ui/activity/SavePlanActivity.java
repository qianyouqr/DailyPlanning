package com.android.dailyplanning.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.dailyplanning.R;
import com.android.dailyplanning.app.App;
import com.android.dailyplanning.entity.Plan;
import com.android.dailyplanning.utils.MyUtils;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SavePlanActivity extends AppCompatActivity {
    @BindView(R.id.spinner_label)
    Spinner spinnerLabel;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.et_key_word)
    EditText etKeyWord;
    @BindView(R.id.cb_notify)
    CheckBox cbNotify;
    private String day;
    private TimePickerDialog mDialogAll;
    private String label;
    private long startTime;
    private long endTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_plan);
        ButterKnife.bind(this);
        day = getIntent().getStringExtra("day");

        //原始string数组
        final String[] spinnerItems = {"生活", "数学", "英语", "专业课"};
        //简单的string数组适配器：样式res，数组
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);
        //下拉的样式res
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinnerLabel.setAdapter(spinnerAdapter);
        spinnerLabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                label = spinnerItems[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.tv_start_time, R.id.tv_end_time, R.id.btn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_start_time:
                mDialogAll = new TimePickerDialog.Builder()
                        .setCallBack(new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                startTime = millseconds;
                                tvStartTime.setText(MyUtils.getHM(millseconds));
                            }
                        })
                        .setCurrentMillseconds(System.currentTimeMillis())
                        .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                        .setType(Type.HOURS_MINS)
                        .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                        .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                        .setWheelItemTextSize(12)
                        .build();
                mDialogAll.show(SavePlanActivity.this.getSupportFragmentManager(), "");
                break;
            case R.id.tv_end_time:
                mDialogAll = new TimePickerDialog.Builder()
                        .setCallBack(new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                endTime = millseconds;
                                tvEndTime.setText(MyUtils.getHM(millseconds));
                            }
                        })
                        .setCurrentMillseconds(System.currentTimeMillis())
                        .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                        .setType(Type.HOURS_MINS)
                        .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                        .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                        .setWheelItemTextSize(12)
                        .build();
                mDialogAll.show(SavePlanActivity.this.getSupportFragmentManager(), "");
                break;
            case R.id.btn_ok:
                Plan plan = new Plan();
                plan.setContent(etContent.getText().toString());
                plan.setDay(day);
                plan.setLabel(label);
                plan.setStartTime(tvStartTime.getText().toString());
                plan.setEndTime(tvEndTime.getText().toString());
                plan.setDuration((int) ((endTime - startTime) / (1000 * 60)));
                plan.setOwnerId(App.getUser().getObjectId());
                plan.setKeyWord(etKeyWord.getText().toString());
                plan.setNotify(cbNotify.isChecked());
                plan.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e != null) {
                            MyUtils.showToast(App.app, e.getMessage());
                            return;
                        }
                        MyUtils.showToast(App.app, "保存成功");
                        finish();
                    }
                });
                break;
        }
    }
}
