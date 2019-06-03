package org.xutils.sample;

import android.app.Application;
import android.content.Context;

import org.xutils.sample.vo.TStudent;
import org.xutils.x;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by wyouflf on 15/10/28.
 */
public class MyApplication extends Application {

    private TStudent loginInfo;

    private static Context context;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MyApplication.context = this.getApplicationContext();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能

        // 全局默认信任所有https域名 或 仅添加信任的https域名
        // 使用RequestParams#setHostnameVerifier(...)方法可设置单次请求的域名校验
        x.Ext.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 获取上下文 Context对象
     * @return Context
     */
    public static Context getAppContext(){
        return MyApplication.context;
    }


    public TStudent getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(TStudent loginInfo) {
        this.loginInfo = loginInfo;
    }
}
