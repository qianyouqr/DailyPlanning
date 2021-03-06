package com.android.dailyplanning.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.dailyplanning.R;
import com.android.dailyplanning.adapter.PlanAdapter;
import com.android.dailyplanning.app.App;
import com.android.dailyplanning.entity.Plan;
import com.android.dailyplanning.ui.activity.MainActivity;
import com.android.dailyplanning.ui.activity.SelectDayActivity;
import com.android.dailyplanning.utils.MyUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class DailyFragment extends Fragment {

    @BindView(R.id.plan_list)
    RecyclerView planList;
    @BindView(R.id.btn_add_plan)
    FloatingActionButton btnAddPlan;
    Unbinder unbinder;
    @BindView(R.id.btn_pdf)
    FloatingActionButton btnPdf;
    private PlanAdapter planAdapter;
    private float BORDER_WIDTH = MyUtils.dip2Dimension(1, App.app);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, null);
        unbinder = ButterKnife.bind(this, view);
        //??????RecyclerView
        planList.setLayoutManager(new LinearLayoutManager(getActivity()));
        planList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        planAdapter = new PlanAdapter();
        planList.setAdapter(planAdapter);

        planAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                String hh = MyUtils.formatDate(System.currentTimeMillis(), "HH");
                if (Integer.parseInt(hh) >= 22) {
                    if (planAdapter.getData().get(position).isSignIn()) {
                        MyUtils.showToast(App.app, "??????????????????????????????");
                    } else {
                        Plan plan = planAdapter.getData().get(position);
                        plan.setSignIn(true);
                        plan.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    MyUtils.showToast(App.app, e.getMessage());
                                    return;
                                }
                                planAdapter.notifyItemChanged(position);
                                MyUtils.showToast(App.app, "????????????");
                            }
                        });
                    }
                } else {
                    MyUtils.showToast(App.app, "??????10????????????????????????");
                }
            }
        });
        planAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showNormalDialog(position);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BmobQuery<Plan> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("ownerId", App.getUser().getObjectId());
        bmobQuery.order("-day");//??????????????????????????????
        bmobQuery.findObjects(new FindListener<Plan>() {
            @Override
            public void done(List<Plan> list, BmobException e) {
                if (e != null) {
                    MyUtils.showToast(App.app, e.getMessage());
                    return;
                }
                int index = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getDay().equals(MyUtils.formatDate(System.currentTimeMillis()))) {
                        index = i;  //???????????????????????????????????????????????????
                        break;
                    }
                }
                planAdapter.setNewData(list);

                final int finalIndex = index;
                //timer???????????????????????????
                Observable.timer(500, TimeUnit.MILLISECONDS)  // 0.5???????????????smoothMoveToPosition?????????????????????????????????
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                smoothMoveToPosition(planList, finalIndex);
                            }
                        });

            }
        });
    }

    //?????????????????????????????????????????????
    private boolean mShouldScroll;
    //?????????????????????
    private int mToPosition;

    /**
     * ?????????????????????
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // ?????????????????????
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // ????????????????????????
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // ???????????????:???????????????????????????????????????????????????smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position);  //???????????????????????????
        } else if (position <= lastItem) {
            // ???????????????:????????????????????????????????????????????????????????????????????????
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition ??????????????????????????????smoothScrollBy????????????????????????
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // ???????????????:???????????????????????????????????????????????????smoothScrollToPosition??????????????????????????????????????????
            // ?????????onScrollStateChanged??????????????????smoothMoveToPosition????????????????????????????????????
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.btn_add_plan, R.id.btn_pdf})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_plan:
                startActivity(new Intent(getActivity(), SelectDayActivity.class));
                break;
            case R.id.btn_pdf:
                try {
                    createPdf();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MyUtils.showToast(App.app, "PDF???????????????");
                break;
        }
    }

    private void showNormalDialog(final int position){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("??????");
        normalDialog.setMessage("????????????????????????????");
        normalDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planAdapter.getData().get(position).delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    MyUtils.showToast(getActivity(), e.getMessage());
                                    return;
                                }
                                MyUtils.showToast(getActivity(), "????????????");
                                planAdapter.getData().remove(position);
                                planAdapter.notifyItemRemoved(position);
                            }
                        });
                    }
                });
        // ??????
        normalDialog.show();
    }

    public class PdfBackground extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            //??????pdf??????????????????
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle rect = document.getPageSize();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
            canvas.fill();

            //??????pdf???????????????
            PdfContentByte canvasBorder = writer.getDirectContent();
            Rectangle rectBorder = document.getPageSize();
            rectBorder.setBorder(Rectangle.BOX);
            rectBorder.setBorderWidth(BORDER_WIDTH);
            rectBorder.setBorderColor(BaseColor.WHITE);
            rectBorder.setUseVariableBorders(true);
            canvasBorder.rectangle(rectBorder);
        }
    }

    private void createPdf() throws IOException, DocumentException {
        File appDir = new File(Environment.getExternalStorageDirectory(), "DailyPlanning");  //Gets the Android external storage directory.??????????????????????????????(SDCard)
        if (!appDir.exists()) {
            appDir.mkdir();//???????????????????????????????????????
        }

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(appDir, System.currentTimeMillis()+".pdf")));

        //??????pdf??????
        PdfBackground event = new PdfBackground();
        writer.setPageEvent(event);

        document.open();

        ByteArrayOutputStream output = new ByteArrayOutputStream();//????????????????????????
        loadBitmapFromView(planList).compress(Bitmap.CompressFormat.JPEG, 100, output);//???bitmap100%??????????????? ??? output?????????
        byte[] result = output.toByteArray();//???????????????
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.newPage();
        Image img = Image.getInstance(result);
        //?????????????????????A4????????????
        img.scaleToFit(PageSize.A4.getWidth() - BORDER_WIDTH * 2, PageSize.A4.getHeight() - BORDER_WIDTH * 2);
        //???????????????????????????????????????
        img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2);
        document.add(img);

        document.close();
    }

    private Bitmap loadBitmapFromView(View v) {  //????????????????????????
        v.setDrawingCacheEnabled(true);   //??????
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);  //????????????DrawingCache??????????????????DrawingCache???????????????????????????????????????
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }
}
