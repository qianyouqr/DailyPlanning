package com.android.dailyplanning.adapter;

import android.app.Activity;

import com.android.dailyplanning.R;
import com.android.dailyplanning.entity.Plan;
import com.android.dailyplanning.utils.MyUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class PlanAdapter extends BaseQuickAdapter<Plan, BaseViewHolder> {

    public PlanAdapter() {
        super(R.layout.item_plan);

    }

    @Override
    protected void convert(BaseViewHolder helper, Plan item) {
        helper.setText(R.id.tv_day, item.getDay());
        helper.setText(R.id.tv_label, item.getLabel());
        helper.setText(R.id.tv_start_time, "开始时间："+item.getStartTime());
        helper.setText(R.id.tv_end_time, "结束时间:"+item.getEndTime());
        helper.setText(R.id.tv_duration, "时长："+item.getDuration());
        helper.setText(R.id.tv_key_word, "关键词："+item.getKeyWord());
        helper.setText(R.id.tv_content, "规划内容："+item.getContent());
        helper.setText(R.id.tv_status, item.isSignIn() ? "状态：已签到" : "状态：未签到");
        if (item.isNotify()){
            helper.setVisible(R.id.tv_notify, true);
            //倒计时

            helper.setText(R.id.tv_notify, "设置提醒："+
                    MyUtils.getDistanceTime(MyUtils.formatDateFull(System.currentTimeMillis()), item.getDay()+" "+item.getStartTime()+":00"));  //计算当前时间与计划开始时间的时间间隔
            helper.setBackgroundRes(R.id.bg, R.color.orange);
        }else {
            helper.setGone(R.id.tv_notify, false);
            helper.setBackgroundRes(R.id.bg, R.color.white);
        }
    }


//    /**
//     * 列表倒计时
//     */
//    private void startTime() {
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < mTimeDownBeanList.size(); i++) {
//                            long useTime = mTimeDownBeanList.get(i).getUseTime();
//                            if (useTime > 1000) {
//                                useTime -= 1000;
//                                mTimeDownBeanList.get(i).setUseTime(useTime);
//                                if (useTime <= 1000 && mTimeDownBeanList.get(i).isTimeFlag()) {
//                                    mTimeDownBeanList.get(i).setTimeFlag(false);
//                                    PlanAdapter.this.notifyItemChanged(i);
//                                }else {
//                                    mTimeDownBeanList.get(i).setTimeFlag(true);
//                                    PlanAdapter.this.notifyItemChanged(i);
//                                }
//
//                            }
//
//                        }
//
//                    }
//                });
//
//
//            }
//        }, 0, 1000);
//    }
//
//    private void setTime(BaseViewHolder holder, int position) throws ParseException {
//        TimeDownBean timeDownBean = mTimeDownBeanList.get(position);
//        if (timeDownBean.getUseTime() > 1000) {
//            holder.timeTv.setVisibility(View.VISIBLE);
//            long useTime = timeDownBean.getUseTime();
//            useTime = useTime / 1000;
//            setTimeShow(useTime, holder);
//
//        }else {
//            holder.timeTv.setVisibility(View.GONE);
//        }
//
//    }
//
//    private void setTimeShow(long useTime, BaseViewHolder holder) {
//        int hour = (int) (useTime / 3600 );
//        int min = (int) (useTime / 60 % 60);
//        int second = (int) (useTime % 60);
//        int day = (int) (useTime / 3600 / 24);
//        String mDay, mHour, mMin, mSecond;//天，小时，分钟，秒
//        second--;
//        if (second < 0) {
//            min--;
//            second = 59;
//            if (min < 0) {
//                min = 59;
//                hour--;
//            }
//        }
//        if (hour < 10) {
//            mHour = "0" + hour;
//        } else {
//            mHour = "" + hour;
//        }
//        if (min < 10) {
//            mMin = "0" + min;
//        } else {
//            mMin = "" + min;
//        }
//        if (second < 10) {
//            mSecond = "0" + second;
//        } else {
//            mSecond = "" + second;
//        }
//        String strTime = "上架倒计时 " +mHour + ":" + mMin + ":" + mSecond + "";
//        holder.timeTv.setText(strTime);
//
//    }

}
