package com.example.liu.autotanslate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class PaginationActivity extends Activity implements PaginationListView.OnLoadListener {

    private PaginationListView paginationLv;
    private DemoAdapter paginationAdapter;
    private List<ApkBean> datas = new ArrayList<ApkBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagination);
        initData();
        paginationLv = (PaginationListView) this
                .findViewById(R.id.pagination_lv);
        paginationLv.setOnLoadListener(this);

        paginationLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

        showView();
    }

    @SuppressLint("SimpleDateFormat")
    private void initData() {
        ApkBean apkBean;
        String dateString;
        long dateLong = new Date().getTime();
        for (int i = 0; i < 20; i++) {
            apkBean = new ApkBean();
            apkBean.setContent("这是一个美好的夜晚    " + i);
            dateLong = dateLong + i * 1000 * 6;
            dateString = new SimpleDateFormat("yyyy-MM-dd HHmmss")
                    .format(new Date(dateLong));
            apkBean.setDateString(dateString);
            datas.add(apkBean);
        }
    }

    /**
     * 加载适配器
     */
    private void showView() {
        if (paginationAdapter == null) {
            paginationAdapter = new DemoAdapter(this, datas);
            paginationLv.setAdapter(paginationAdapter);
        } else {
            paginationAdapter.updateView(datas);
        }
    }

    @Override
    public void onLoad() {
        // 为了显示效果，采用延迟加载
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                initLoadData();
                showView();
                paginationLv.loadComplete();
            }
        }, 3000);
    }

    @SuppressLint("SimpleDateFormat")
    private void initLoadData() {
        ApkBean apkBean;
        String dateString;
        long dateLong = new Date().getTime();
        for (int i = 0; i < 20; i++) {
            apkBean = new ApkBean();
            apkBean.setContent("这是一个新的开始    " + i);
            dateLong = dateLong + i * 1000 * 6 * 60;
            dateString = new SimpleDateFormat("yyyy-MM-dd HHmmss")
                    .format(new Date(dateLong));
            apkBean.setDateString(dateString);
            datas.add(apkBean);
        }
    }

}
