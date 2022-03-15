package com.android.dailyplanning.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.dailyplanning.R;
import com.android.dailyplanning.app.App;
import com.android.dailyplanning.entity.User;
import com.android.dailyplanning.ui.activity.LoginActivity;
import com.android.dailyplanning.utils.MyUtils;
import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static cn.bmob.v3.Bmob.getApplicationContext;

public class FragmentMe extends Fragment {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;   //圆形图片控件，继承于ImageView
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.btn_out)
    Button btnOut;
    Unbinder unbinder;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    private String nick;
    private String picturePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);   //动态加载布局
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(App.getUser().getIcon())) {
            Glide.with(FragmentMe.this).load(App.getUser().getIcon()).into(profileImage);   //Glide图片加载库
        }
        if (!TextUtils.isEmpty(App.getUser().getNick())) {
            tvNick.setText(App.getUser().getNick());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.profile_image, R.id.tv_nick, R.id.btn_out, R.id.btn_upload})
    public void onViewClicked(View view) {     //多个事件绑定使用onViewClicked()
        switch (view.getId()) {
            case R.id.profile_image:
                //激活系统图库，选择一张图片
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Media是android系统提供的一个多媒体数据库
                startActivityForResult(i, 0010);
                break;
            case R.id.tv_nick:
                final EditText editText = new EditText(getActivity());
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(getActivity());
                inputDialog.setTitle("请输入昵称").setView(editText);
                inputDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(editText.getText().toString())) {
                                    nick = editText.getText().toString();
                                    tvNick.setText(nick);
                                }
                            }
                        }).show();
                break;
            case R.id.btn_out:
                BmobUser.getCurrentUser(User.class).logOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.btn_upload:
                if((" ").equals(picturePath) || null == picturePath)
                {
                    MyUtils.showToast(App.app, "请修改图片！");
                }
                else{
                    final BmobFile bmobFile = new BmobFile(new File(picturePath));
                    bmobFile.upload(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null) {
                                MyUtils.showToast(App.app, e.getMessage());
                                return;
                            }
                            User user = App.getUser();
                            user.setIcon(bmobFile.getFileUrl());
                            user.setNick(nick);
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e != null) {
                                        MyUtils.showToast(App.app, e.getMessage());
                                        return;
                                    }
                                    MyUtils.showToast(App.app, "上传成功");
                                }
                            });
                        }
                    });
                }
                break;
        }
    }
    //接收Actvity回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0010 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};//获取手机内图片的绝对路径
            //由于系统版本的不同，返回content或者file_path这两者不同的url，需要将content转换为file进行处理
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            File scal = MyUtils.scal(Uri.fromFile(new File(picturePath)), getApplicationContext());
            //绝地路径
            picturePath = scal.getAbsolutePath();

            Glide.with(FragmentMe.this).load(picturePath).into(profileImage);
        }
    }
}
