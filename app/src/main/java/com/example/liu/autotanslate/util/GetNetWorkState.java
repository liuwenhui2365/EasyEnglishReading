package com.example.liu.autotanslate.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.MyApplication;

/**
 * 优化判断网络状态的类，加到工具里面，方便其它类的调用
 * TODO 获取服务对象的时候会出现空指针异常！（升级版本的时候考虑和完善）
 * Created by Administrator on 2015/6/14.
 */
public class GetNetWorkState extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MyApplication myApplication = new MyApplication();
        ConnectivityManager connectivityManager = (ConnectivityManager)
                         myApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo =  networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {
                Toast.makeText(context, "连接失败，请检查网络!", Toast.LENGTH_LONG).show();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
//            Log.d("获取网络状态","失败");
        }
    }
}
