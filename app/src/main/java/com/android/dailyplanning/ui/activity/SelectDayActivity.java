package com.android.dailyplanning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.dailyplanning.R;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

public class SelectDayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_day);

        DatePicker picker = (DatePicker) findViewById(R.id.main_dp);
        picker.setDate(2019, 5);
        picker.setMode(DPMode.SINGLE);
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                String[] split = date.split("-");
                String month = split[1].length() == 1 ? "0"+split[1] : split[1];
                String day = split[2].length() == 1 ? "0"+split[2] : split[2];
                Intent intent = new Intent(SelectDayActivity.this, SavePlanActivity.class);
                intent.putExtra("day", split[0]+"-"+month+"-"+day);
                startActivity(intent);
                finish();
            }
        });
    }
}
