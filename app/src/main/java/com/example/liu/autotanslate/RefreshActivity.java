package com.example.liu.autotanslate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class RefreshActivity extends Activity implements RefreshListView.OnRefreshListener {

    private RefreshListView refreshLv;
    private DemoAdapter refreshAdapter;
    private List<ApkBean> datas = new ArrayList<ApkBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        initData();
        refreshLv =(RefreshListView) this.findViewById(R.id.refresh_lv);
        refreshLv.setOnRefreshListener(this);
        refreshLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

        showView();
    }

    @SuppressLint("SimpleDateFormat")
    private void initData(){
        ApkBean apkBean;
        String dateString;
        long dateLong = new Date().getTime();
        for (int i = 0; i < 20; i++) {
            apkBean = new ApkBean();
            apkBean.setContent("这是一个刷新的早晨      " + i);
            dateLong = dateLong + i * 1000 * 6;
            dateString = new SimpleDateFormat("yyyy-MM-dd HHmmss")
                    .format(new Date(dateLong));
            apkBean.setDateString(dateString);
            datas.add(apkBean);
        }
    }

    private void showView(){
        if(refreshAdapter == null){
            refreshAdapter = new DemoAdapter(this, datas);
            refreshLv.setAdapter(refreshAdapter);
        }else{
            refreshAdapter.updateView(datas);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                initLoadData();
                refreshAdapter.updateView(datas);
                refreshLv.refreshComplete();
            }
        }, 3000);
    }

    @SuppressLint("SimpleDateFormat")
    private void initLoadData() {
        ApkBean apkBean;
        String dateString;
        long dateLong = new Date().getTime();
        for (int i = 0; i < 5; i++) {
            apkBean = new ApkBean();
            apkBean.setContent("刷新新的的数据  " + i);
            dateLong = dateLong + i * 1000 * 6 * 60;
            dateString = new SimpleDateFormat("yyyy-MM-dd HHmmss")
                    .format(new Date(dateLong));
            apkBean.setDateString(dateString);
            datas.add(apkBean);
        }
    }

}