package com.example.liu.autotanslate;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class Refresh extends ActionBarActivity implements MyListView.OnLoaderListener {

    ArrayList<HashMap<String, Object>> itemEntities;
    MyListView listview = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        getData();
        showListView(itemEntities);
    }

    private void showListView(ArrayList<HashMap<String, Object>> itemEntities) {
        listview = (MyListView) findViewById(R.id.mylist);
        listview.setLoaderListener(this);
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(Refresh.this,itemEntities,//需要绑定的数据
                R.layout.lsitviewitem,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"ItemImage","title", "date"},
                new int[] {R.id.ItemImage,R.id.title,R.id.date}
        );
        mSimpleAdapter.notifyDataSetChanged();
        listview.setAdapter(mSimpleAdapter);//为ListView绑定适配器
    }
    private void getData() {
        itemEntities = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("ItemImage", R.drawable.ic_launcher);//加入图片
            map.put("title", "标题" + i);
            map.put("date", "2015.3.04");
            itemEntities.add(0,map);

        }
    }
    private void loadData() {
        for (int i = 100; i < 115; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.ic_launcher);//加入图片
            map.put("title", "上拉加载" + i);
            map.put("date", "2015.3.04");
            itemEntities.add(map);
        }

    }
    private void setReflashData() {
        for (int i = 10; i < 15; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.ic_launcher);//加入图片
            map.put("title", "下拉刷新" + i);
            map.put("date", "2015.3.04");
            itemEntities.add(0, map);
        }
    }
    @Override
    public void onLoad() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取更多数据
                loadData();
                // 更新listview显示；
                showListView(itemEntities);
                // 通知listview加载完毕
                listview.loadComplete();
            }
        }, 2000);
    }
    @Override
    public void onReflash() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取最新数据
                setReflashData();
                // 通知界面显示
                showListView(itemEntities);
                // 通知listview 刷新数据完毕；
                listview.reflashComplete();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
