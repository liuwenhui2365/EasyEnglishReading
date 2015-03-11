package com.example.liu.autotanslate;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.SpiderArticle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Listview extends ActionBarActivity{

    private  ListView plv = null;
    private  ListViewExt lv = null;

    private  ListView rlv = null;
    Article article = null;
    SpiderArticle spiderArticle = new SpiderArticle();
    Handler mHandler = null;
    final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
    private ArrayList<String> mList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(Listview.this,listItem,//需要绑定的数据
                R.layout.lsitviewitem,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"ItemImage","title", "date"},
                new int[] {R.id.ItemImage,R.id.title,R.id.date}
        );
        mSimpleAdapter.notifyDataSetChanged();
        lv.setAdapter(mSimpleAdapter);//为ListView绑定适配器


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
//                Log.d("TAG", "arg2 = " + arg2);
                if (arg2 > 0) {
                    lv.stopRefresh();
                    lv.stopLoad();
                }

                lv.setFooterMode(arg2 % 2);
            }
        });



        //      获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ");
        Date curDate = new Date(System.currentTimeMillis());
        final String str = formatter.format(curDate);


        //获取类别和级别

//          final String message = this.getIntent().getAction();

//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Article article = spiderArticle.getPassage("http://www.51voa.com/VOA_Special_English/animal-weapons-offer-lesons-for-human-arms-race-61556.html");
//                }catch (NullPointerException e){
//                    Log.e("---------empty!-----------------","network wroong");
////                    //TODO 增加网络错误提示窗口
//                    Intent intent = new Intent();
//                    intent.setClass(Listview.this,Classify.class);
//                    startActivity(intent);
//                }catch (Exception e){
//                    e.printStackTrace();
//                    Log.e("-------error------",e.toString());
//                }
//
//                android.os.Message msg = new android.os.Message();
//                msg.what = 1;
//                msg.obj = article != null ? article : new Article();
//                mHandler.sendMessage(msg);
//            }
//        }.start();

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
