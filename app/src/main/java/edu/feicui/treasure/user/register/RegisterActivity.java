package edu.feicui.treasure.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.feicui.treasure.Constants;
import edu.feicui.treasure.R;
import edu.feicui.treasure.commons.ActivityUtils;
import edu.feicui.treasure.commons.RegexUtils;
import edu.feicui.treasure.components.AlertDialogFragment;
import edu.feicui.treasure.treasure.HomeActivity;
import edu.feicui.treasure.user.User;

public class RegisterActivity extends MvpActivity<RegisterView,RegisterPresenter> implements RegisterView{
    @Bind(R.id.toolbar)
    Toolbar  toolbar;
    @Bind(R.id.et_Username)
    EditText etUsername;
    @Bind(R.id.et_Password)
    EditText etPassword;
    @Bind(R.id.et_Confirm)
    EditText etConfirm;
    @Bind(R.id.btn_Register)
    Button   btnRegister;

    private String username; // 用来保存编辑框内的用户名
    private String password; // 用来保存编辑框内的密码

    private ActivityUtils activityUtils;// Activity常用工具集

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_register);
    }

    // 当布局发生改变时来回调的方法(如每次setContentView后)
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getTitle());
        }
        etConfirm.addTextChangedListener(mTextWatcher); // EditText监听
        etPassword.addTextChangedListener(mTextWatcher); // EditText监听
        etUsername.addTextChangedListener(mTextWatcher); // EditText监听
    }

    @NonNull
    @Override
    public RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();
            boolean canRegister = !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
                    && password.equals(confirm);
            btnRegister.setEnabled(canRegister);// 注意：在布局内注册按钮默认是不可用的
        }
    };

    @OnClick(R.id.btn_Register)
    public void register() {
        // 正则进行判断输入的用户名是否有效
        if (RegexUtils.verifyUsername(username) != RegexUtils.VERIFY_SUCCESS) {
            showUsernameError();
            return;
        }
        // 正则进行判断输入的密码是否有效
        if (RegexUtils.verifyPassword(password) != RegexUtils.VERIFY_SUCCESS) {
            showPasswordError();
            return;
        }
        getPresenter().register(new User(username,password));
    }

    private void showUsernameError() {
        String msg = getString(R.string.username_rules);
        AlertDialogFragment fragment = AlertDialogFragment.newInstance(R.string.username_error, msg);
        fragment.show(getSupportFragmentManager(), "showUsernameError");
    }

    private void showPasswordError() {
        String msg = getString(R.string.password_rules);
        AlertDialogFragment fragment = AlertDialogFragment.newInstance(R.string.password_error, msg);
        fragment.show(getSupportFragmentManager(), "showPasswordError");
    }

    private ProgressDialog progressDialog;

    @Override
    public void showProgress() {
        activityUtils.hideSoftKeyboard();
        progressDialog = ProgressDialog.show(this, "", "注册中,请稍后...");
    }

    @Override
    public void hideProgress() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void navigateToHome() {
        activityUtils.startActivity(HomeActivity.class);
        finish();
        // 发送广播
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.ACTION_ENTER_HOME));
    }


    @Override
    public void clearEditView() {
        etPassword.setText("");
    }
}
