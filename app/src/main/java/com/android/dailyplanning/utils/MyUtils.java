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
     * 打印log，tag名是Altria
     *
     * @param content 内容
     */
    public static void printLog(String content) {
        Log.e("Altria", content);
    }


    /**
     * dip 转换成 px
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
     * 用户名字符串匹配
     */
    public static boolean isusername(String str){
        String zhongyin="^[A-Za-z\u4e00-\u9fa5]+$";//中英文
        Pattern p = Pattern.compile(zhongyin);
        return p.matcher(str).matches(); }
    /**
     * 密码字符匹配
     */
    public static boolean ispassw(String str){
        String yinshuzi="^[A-Za-z0-9_@]+$";//字母数字_ @
         Pattern p = Pattern.compile(yinshuzi);
         return p.matcher(str).matches(); }


    /**
     * 时间戳 时间格式化 yyyy-MM-dd HH:mm:ss
     */
    public static String formatDate(long oldString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());  //Locale.getDefault()获取当前的语言环境，把返回值放进SimpleDateFormat的构造里，就能实现通用化，
        // 因此format.format(date)方法返回的值也会根据当前语言来返回对应的值
        String dateString = dateFormat.format(oldString);
        return dateString;
    }

    public static String formatDateFull(long oldString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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
     * 时间格式化
     *
     * @param oldString
     * @param how       制定格式
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
     * 得到本周周一
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
        //通过将给定路径名字符串转换为抽象路径名来创建一个新File实例
        File outputFile = new File(path);
        //获取文件或者文件夹的长度
        long fileSize = outputFile.length();
        final long fileMaxSize = 200 * 1024;
        if (fileSize >= fileMaxSize) {  //如果图片太大，需要进行压缩处理
            BitmapFactory.Options options = new BitmapFactory.Options();
            //这个值置为true，在解码的时候将不会返回bitmap，只会返回这个bitmap的尺寸
            options.inJustDecodeBounds = true;
            //从文件中解码生成一个位图
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
           // 大于1，那么就会按照比例（1 / inSampleSize）缩小bitmap的宽和高、降低分辨率
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(createImageFile(context).getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                //压缩手机里的图片
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);  //压缩了50%
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);  //string转成对象
            two = df.parse(str2);
            long time1 = one.getTime();  //getTiem()返回long类型的毫秒数
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
                day = diff / (24 * 60 * 60 * 1000);
                hour = (diff / (60 * 60 * 1000) - day * 24);
                min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                return day + "天" + hour + "小时" + min + "分" + sec + "秒";
            } else {
                diff = time1 - time2;
                return "已提醒";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
