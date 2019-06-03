package org.xutils.sample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.sample.MyApplication;
import org.xutils.sample.R;
import org.xutils.sample.vo.ResultObject;
import org.xutils.sample.vo.TStudent;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.List;


public class Account_Login extends AppCompatActivity implements View.OnClickListener {
    private Button sendRequest;
    private TextView account;
    private TextView password;
    private CheckBox remembPwdCheck;
    protected Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        password = (EditText) findViewById(R.id.pwdEdit);
        account = (EditText) findViewById(R.id.userNameEdit);
        sendRequest = (Button) findViewById(R.id.loginBtn);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginBtn) {
            //发送账号和密码至后台验证
            String userName = account.getText().toString();
            String password1 = password.getText().toString();
            RequestParams params = new RequestParams("http://192.168.1.109:8088/user/studentLogin");
            params.addBodyParameter("userName",userName);
            params.addBodyParameter("password",password1);
            params.addBodyParameter("type","2");
            Callback.Cancelable post = x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    Type userType=new TypeToken<ResultObject<List<TStudent>>>(){}.getType();
                    ResultObject<List<TStudent>> userResult=gson.fromJson(result,userType);
                    List<TStudent> tStudent =userResult.getData();
                    TStudent tStudent1 = tStudent.get(0);
                    MyApplication.getInstance().setLoginInfo(tStudent1);
                    Intent intent = new Intent();
                    intent.putExtra("tStudent1", tStudent1);
                    setResult(RESULT_OK,intent);
                    finish();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }

    }

}

