package com.android.dailyplanning.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyUtils {
    public static Toast mToast;

    public static void showToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    public static void showToastLong(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_LONG);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * ??????log???tag??????Altria
     *
     * @param content ??????
     */
    public static void printLog(String content) {
        Log.e("Altria", content);
    }


    /**
     * dip ????????? px
     *
     * @param dip
     * @param context
     * @return
     */
    public static float dip2Dimension(float dip, Context context) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                displayMetrics);
    }

    /**
     * ????????????????????????
     */
    public static boolean isusername(String str){
        String zhongyin="^[A-Za-z\u4e00-\u9fa5]+$";//?????????
        Pattern p = Pattern.compile(zhongyin);
        return p.matcher(str).matches(); }
    /**
     * ??????????????????
     */
    public static boolean ispassw(String str){
        String yinshuzi="^[A-Za-z0-9_@]+$";//????????????_ @
         Pattern p = Pattern.compile(yinshuzi);
         return p.matcher(str).matches(); }


    /**
     * ????????? ??????????????? yyyy-MM-dd HH:mm:ss
     */
    public static String formatDate(long oldString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());  //Locale.getDefault()????????????????????????????????????????????????SimpleDateFormat???????????????????????????????????????
        // ??????format.format(date)???????????????????????????????????????????????????????????????
        String dateString = dateFormat.format(oldString);
        return dateString;
    }

    public static String formatDateFull(long oldString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd??HH:mm:ss", Locale.getDefault());
        String dateString = dateFormat.format(oldString);
        return dateString;
    }

    public static String getHM(long oldString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        String dateString = dateFormat.format(oldString);
        return dateString;
    }

    /**
     * ???????????????
     *
     * @param oldString
     * @param how       ????????????
     * @return
     */
    public static String formatDate(long oldString, String how) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                how, Locale.getDefault());
        String dateString = dateFormat.format(oldString);
        return dateString;
    }

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * ??????????????????
     * @return yyyy-MM-dd
     */
    public static List<String> getThisWeek() {
        List<String> dayList = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            Calendar c = Calendar.getInstance();
            int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (day_of_week == 0)
                day_of_week = 7;
            c.add(Calendar.DATE, -day_of_week + i);
            dayList.add(format.format(c.getTime()));

        }
        return dayList;
    }


    public static File scal(Uri fileUri, Context context) {
        String path = fileUri.getPath();
        //???????????????????????????????????????????????????????????????????????????File??????
        File outputFile = new File(path);
        //????????????????????????????????????
        long fileSize = outputFile.length();
        final long fileMaxSize = 200 * 1024;
        if (fileSize >= fileMaxSize) {  //?????????????????????????????????????????????
            BitmapFactory.Options options = new BitmapFactory.Options();
            //???????????????true????????????????????????????????????bitmap?????????????????????bitmap?????????
            options.inJustDecodeBounds = true;
            //????????????????????????????????????
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
           // ??????1??????????????????????????????1 / inSampleSize?????????bitmap??????????????????????????????
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(createImageFile(context).getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                //????????????????????????
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);  //?????????50%
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(createImageFile(context).getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;
    }

    public static Uri createImageFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory(), "DailyPlanning");
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }

    public static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd??HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);  //string????????????
            two = df.parse(str2);
            long time1 = one.getTime();  //getTiem()??????long??????????????????
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
                day = diff / (24 * 60 * 60 * 1000);
                hour = (diff / (60 * 60 * 1000) - day * 24);
                min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                return day + "???" + hour + "??????" + min + "???" + sec + "???";
            } else {
                diff = time1 - time2;
                return "?????????";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
