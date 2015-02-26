package com.example.liu.autotanslate;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Listview extends ActionBarActivity {

    private  ListView lv = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //      获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);

//        获取类别和级别
        String message = this.getIntent().getAction();

//          实现每行显示两行文本和图片

        lv = (ListView)findViewById(R.id.lv);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
        /*在数组中存放数据*/
        for(int i=0;i<10;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if(i==1 || i==3 || i==5){
                map.put("ItemImage", null);//加入图片
                map.put("title", message);
                map.put("date", str);
                listItem.add(map);
            }else {
                map.put("ItemImage", R.drawable.ic_launcher);//加入图片
                map.put("title", "标题" + i);
                map.put("date", str);
                listItem.add(map);
            }
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,listItem,//需要绑定的数据
                R.layout.lsitviewitem,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"ItemImage","title", "date"},
                new int[] {R.id.ItemImage,R.id.title,R.id.date}
        );

        lv.setAdapter(mSimpleAdapter);//为ListView绑定适配器

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("MyListViewBase", "你点击了ListView条目" + position);//在LogCat中没有输出信息
                Intent intent = new Intent();
                intent.setClass(Listview.this,Message.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        //当点击不同的menu item 是执行不同的操作
        switch (id) {
            case R.id.action_search:
//                openSearch();

            case R.id.action_settings:
//                openSettings();

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
