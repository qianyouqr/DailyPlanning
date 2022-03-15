package com.android.dailyplanning;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.android.dailyplanning.app.App;
import com.android.dailyplanning.entity.Plan;
import com.android.dailyplanning.ui.activity.MainActivity;
import com.android.dailyplanning.ui.activity.NotifyActivity;
import com.android.dailyplanning.utils.MyUtils;

import java.util.List;

import androidx.annotation.Nullable;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class NotifyService extends Service {

    private IntentFilter mFilter;    //意图过滤器
    private NotifyService.TimeChangeReceiver timeChangeReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_TIME_TICK); //每分钟变化的action，广播动作是以每分钟一次的形式发送
        timeChangeReceiver = new TimeChangeReceiver();//创建  实例
        registerReceiver(timeChangeReceiver, mFilter);//注册，这样TimeChangeReceiver就会收到所有的值为ACTION_TIME_TICK的广播
    }
    //动态注册的广播接收器一定要取消注册
    @Override
    public void onDestroy() {
        unregisterReceiver(timeChangeReceiver);
        super.onDestroy();
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    String day = MyUtils.formatDate(System.currentTimeMillis());//获取系统当前日期，并转化成一定形式
                    String hm = MyUtils.getHM(System.currentTimeMillis());//获取时间
                    //查询day是
                    BmobQuery<Plan> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("ownerId", App.getUser().getObjectId());
                    bmobQuery.addWhereEqualTo("day", day);
                    bmobQuery.addWhereEqualTo("endTime", hm);
                    bmobQuery.findObjects(new FindListener<Plan>() {
                        @Override
                        public void done(List<Plan> list, BmobException e) {
                            if (e != null) {
                                MyUtils.printLog(e.getMessage());
                                return;
                            }
                            for (Plan plan : list) {
                                Intent intentMainActivity = new Intent(context, NotifyActivity.class);
                                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intentMainActivity.putExtra("plan", plan);
                                context.startActivity(intentMainActivity);
                            }
                        }
                    });

                    break;
            }
        }
    }
}
