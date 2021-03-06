package com.android.dailyplanning.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dailyplanning.R;
import com.android.dailyplanning.adapter.CompleteAdapter;
import com.android.dailyplanning.app.App;
import com.android.dailyplanning.entity.Plan;
import com.android.dailyplanning.utils.MyUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ShowFragment extends Fragment {
    @BindView(R.id.line_chart)
    LineChart lineChart;
    Unbinder unbinder;
    @BindView(R.id.tv_info_1)  //??????
    TextView tvInfo1;
    @BindView(R.id.tv_info_2)
    TextView tvInfo2;
    @BindView(R.id.tv_info_3)
    TextView tvInfo3;
    @BindView(R.id.share_view)
    LinearLayout shareView;
    @BindView(R.id.btn_share)
    Button btnShare;
    @BindView(R.id.tv_info_4)
    TextView tvInfo4;
    @BindView(R.id.complete_list_view)
    RecyclerView completeListView;
    private int[] yOne = {0, 0, 0, 0, 0, 0, 0};
    private int[] yTwo = {0, 0, 0, 0, 0, 0, 0};
    private int[] yThree = {0, 0, 0, 0, 0, 0, 0};
    private int[] yFour = {0, 0, 0, 0, 0, 0, 0};
    private int signInOne = 0;
    private int signInTwo = 0;
    private int signInThree = 0;
    private int signInFour = 0;
    private float CompleteOne = 0F;
    private float CompleteTwo = 0F;
    private float CompleteThree = 0F;
    private float CompleteFour = 0F;
    private int day1;
    private int day2;
    private int day3;
    private int day4;
    private List<Plan> plans1 = new ArrayList<>();
    private List<Plan> plans2 = new ArrayList<>();
    private List<Plan> plans3 = new ArrayList<>();
    private List<Plan> plans4 = new ArrayList<>();
    private CompleteAdapter completeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        completeListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        completeAdapter = new CompleteAdapter(new ArrayList<Plan>());
        completeListView.setAdapter(completeAdapter);
        completeListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        lineChart.getDescription().setEnabled(false);//???????????????

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);   //???????????????Y ???

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(7);
        //??????????????????????????????x????????????
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {       //?????????x?????????
                if (value >0){
                    value=value+1;
                }
                String day = "";
                //System.err.println("qiurui?????????"+value);
                switch ((int) value) {
                    case 0:
                        day = "???";
                        break;
                    case 1:
                        day = "???";
                        break;
                    case 2:
                        day = "???";
                        break;
                    case 3:
                        day = "???";
                        break;
                    case 4:
                        day = "???";
                        break;
                    case 5:
                        day = "???";
                        break;
                    case 6:
                        day = "???";
                        break;
                }
                return "??????" + day;
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(180);
    }

    @Override
    public void onResume() {
        super.onResume();
        BmobQuery<Plan> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("ownerId", App.getUser().getObjectId());
        bmobQuery.findObjects(new FindListener<Plan>() {
            @Override
            public void done(List<Plan> list, BmobException e) {
                if (e != null) {
                    MyUtils.showToast(App.app, e.getMessage());
                    return;
                }
                for (Plan plan : list) {
                    for (int i = 0; i < MyUtils.getThisWeek().size(); i++) {
                        System.err.print("????????????"+MyUtils.getThisWeek().get(i));
                        String day = MyUtils.getThisWeek().get(i);
                        if (plan.getDay().equals(day)) {
                            switch (plan.getLabel()) {
                                case "??????":
                                    yOne[i] = plan.getDuration();
                                    if (plan.isSignIn())
                                        signInOne++;
                                    if (plan.isNotify() && !TextUtils.isEmpty(plan.getCompleteInfo())) {
                                        CompleteOne += Float.parseFloat(plan.getCompleteInfo());
                                        day1++;
                                        plans1.add(plan);
                                    }
                                    break;
                                case "??????":
                                    yTwo[i] = plan.getDuration();
                                    if (plan.isSignIn())
                                        signInTwo++;
                                    if (plan.isNotify() && !TextUtils.isEmpty(plan.getCompleteInfo())) {
                                        CompleteTwo += Float.parseFloat(plan.getCompleteInfo());
                                        day2++;
                                        plans2.add(plan);
                                    }
                                    break;
                                case "??????":
                                    yThree[i] = plan.getDuration();
                                    if (plan.isSignIn())
                                        signInThree++;
                                    if (plan.isNotify() && !TextUtils.isEmpty(plan.getCompleteInfo())) {
                                        CompleteThree += Float.parseFloat(plan.getCompleteInfo());
                                        day3++;
                                        plans3.add(plan);
                                    }
                                    break;
                                case "?????????":
                                    yFour[i] = plan.getDuration();
                                    if (plan.isSignIn())
                                        signInFour++;
                                    if (plan.isNotify() && !TextUtils.isEmpty(plan.getCompleteInfo())) {
                                        CompleteFour += Float.parseFloat(plan.getCompleteInfo());
                                        day4++;
                                        plans4.add(plan);
                                    }
                                    break;
                            }
                        }
                    }
                }
                List<Entry> entries = new ArrayList<>();
                //??????????????????
                for (int i = 0; i < 7; i++) {
                    entries.add(new Entry(i, yOne[i]));
                }
                List<Entry> entries2 = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    entries2.add(new Entry(i, yTwo[i]));
                }
                List<Entry> entries3 = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    entries3.add(new Entry(i, yThree[i]));
                }
                List<Entry> entries4 = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    entries4.add(new Entry(i, yFour[i]));
                }

                LineDataSet dataSet = new LineDataSet(entries, "??????");
                dataSet.setColor(getResources().getColor(R.color.colorAccent));
                LineDataSet dataSet2 = new LineDataSet(entries2, "??????");
                dataSet2.setColor(getResources().getColor(R.color.holoBlue));
                LineDataSet dataSet3 = new LineDataSet(entries3, "??????");
                dataSet3.setColor(getResources().getColor(R.color.orange));
                LineDataSet dataSet4 = new LineDataSet(entries4, "?????????");
                dataSet4.setColor(getResources().getColor(R.color.colorPrimary));
                lineChart.setData(new LineData(dataSet, dataSet2, dataSet3, dataSet4));
                lineChart.invalidate();

                int avg1 = 0;
                int avg2 = 0;
                int avg3 = 0;
                int avg4 = 0;

                for (int i : yOne) {
                    avg1 += i;
                }
                for (int i : yTwo) {
                    avg2 += i;
                }
                for (int i : yThree) {
                    avg3 += i;
                }
                for (int i : yFour) {
                    avg4 += i;
                }

                tvInfo1.setText("????????????" + (avg1 / 7) + "??????,?????????" + signInOne + "???");
                tvInfo2.setText("????????????" + (avg2 / 7) + "??????,?????????" + signInTwo + "???");
                tvInfo3.setText("????????????" + (avg3 / 7) + "??????,?????????" + signInThree + "???");
                tvInfo4.setText("????????????" + (avg4 / 7) + "??????,?????????" + signInFour + "???");

                try {
                    if (CompleteOne / (float) day1 < 0.5F){
                        completeAdapter.addData(plans1);
                    }
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                try {
                    if (CompleteTwo / (float) day2 < 0.5F){
                        completeAdapter.addData(plans2);
                    }
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                try {
                    if (CompleteThree / (float) day3 < 0.5F){
                        completeAdapter.addData(plans3);
                    }
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                try {
                    if (CompleteFour / (float) day4 < 0.5F){
                        completeAdapter.addData(plans4);
                    }
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_share)
    public void onViewClicked() {
        goWeChat();
    }

    private void goWeChat() {
       // Resources res = getResources();
        Bitmap bmp = loadBitmapFromView(shareView);
        String filePrefix = null;
        try {
            filePrefix = saveImageToGallery(getActivity(), bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        //ComponentName??????????????????????????????Activity???Service.
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.putExtra(Intent.EXTRA_SUBJECT, "??????");
        intent.putExtra(Intent.EXTRA_TEXT, "?????? ");
        intent.putExtra(Intent.EXTRA_TITLE, "????????????");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse(filePrefix);

        Log.i("uri", "" + uri.getScheme());

        intent.setAction(Intent.ACTION_SEND);

        intent.setType("image");
        Log.i("image", " " + uri);
//????????????????????????????????????
////                ArrayList<Uri> imageUris = new ArrayList<>();
////                    imageUris.add(uri);
//
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);
    }

    public static String saveImageToGallery(Context context, Bitmap bmp) throws IOException {
        // ??????????????????
        File appDir = new File(Environment.getExternalStorageDirectory(), "DailyPlanning");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        String fileNameTemp;
        File file = new File(appDir, fileName);
        file.createNewFile();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ??????????????????????????????
        try {
            fileNameTemp = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileNameTemp = "";
        }
        // ????????????????????????
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(Environment.getExternalStorageDirectory().toString() + "/Boohee")));
        return fileNameTemp;//??????????????? content://?????????????????????
    }

    private Bitmap loadBitmapFromView(View v) {    //??????
        v.setDrawingCacheEnabled(true);
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }
}
