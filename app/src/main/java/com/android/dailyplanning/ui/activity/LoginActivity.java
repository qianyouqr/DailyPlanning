package com.android.dailyplanning.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.dailyplanning.R;
import com.android.dailyplanning.entity.User;
import com.android.dailyplanning.utils.MyUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import io.reactivex.functions.Consumer;

/**
 * Created by altria on 17-3-23.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_go_register)
    TextView tvGoRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (BmobUser.getCurrentUser(User.class) != null){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            return;
                        }
                    }
                });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
                    MyUtils.showToast(LoginActivity.this, "请填写完整");
                }else {
                    User.loginByAccount(username, pwd, new LogInListener<User>() {

                        @Override
                        public void done(User user, BmobException e) {
                            if(user!=null){
                                MyUtils.showToast(LoginActivity.this, "登录成功");
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }else {
                                MyUtils.showToast(LoginActivity.this, "登录失败");
                            }
                        }
                    });
                }
            }
        });

        tvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}
