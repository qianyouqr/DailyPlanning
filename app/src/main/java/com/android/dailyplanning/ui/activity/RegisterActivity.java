package com.android.dailyplanning.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.dailyplanning.R;
import com.android.dailyplanning.entity.User;
import com.android.dailyplanning.utils.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by altria on 17-3-23.
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_pwd_conf)
    EditText etPwdConf;
    @BindView(R.id.btn_register)
    Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        ButterKnife.bind(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                final String pwd = etPwd.getText().toString().trim();
                String pwdConf = etPwdConf.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdConf)){
                    MyUtils.showToast(RegisterActivity.this, "请填写完整");
                }else if (!pwd.equals(pwdConf)){
                    MyUtils.showToast(RegisterActivity.this, "两次密码输入不一致");
                }
                else if (!MyUtils.isusername(username))
                {MyUtils.showToast(RegisterActivity.this,"请输入中英文");}
                else if (!MyUtils.ispassw(pwd)){
                    MyUtils.showToast(RegisterActivity.this,"密码格式：数字密码");
                }
                    else {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(pwd);
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e != null){
                                MyUtils.showToast(RegisterActivity.this, "帐号已被注册");
                            }else {
                                MyUtils.showToast(RegisterActivity.this, "注册成功");
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}
