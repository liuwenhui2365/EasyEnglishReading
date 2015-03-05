package com.example.liu.autotanslate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.SpiderArticle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Listview extends ActionBarActivity implements FooterView.OnLoadListener,ListViewExt.OnRefreshListener { // implements PaginationListView.OnLoadListener, RefreshListView.OnRefreshListener{

    private  ListView plv = null;
    private  ListViewExt lv = null;

    private  ListView rlv = null;
    private DbArticle dbArticle;
    Article article = null;
    SpiderArticle spiderArticle = new SpiderArticle();
    private PaginationListView paginationLv;
    private RefreshListView refreshLv;

    Handler mHandler = null;
//    private ListView mainLv;
//    private DemoAdapter demoAdapter;
    final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
    private List<ApkBean> datas = new ArrayList<ApkBean>();
    private ArrayList<String> mList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh2);

        initData();
        lv = (ListViewExt) findViewById(R.id.lv);
//        lv.setAdapter(new ArrayAdapter<HashMap<String, Object>>(this,
//                android.R.layout.simple_list_item_1,listItem));

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

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            paginationLv = (PaginationListView) this.findViewById(R.id.refresh);
//            paginationLv.setOnLoadListener(this);
//
//            refreshLv =(RefreshListView) this.findViewById(R.id.refresh);
//            refreshLv.setOnRefreshListener(this);
//            refreshLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//            }
//        });


        //      获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ");
        Date curDate = new Date(System.currentTimeMillis());
        final String str = formatter.format(curDate);


        //获取类别和级别

          final String message = this.getIntent().getAction();

        //实现每行显示两行文本和图片
//        plv = (ListView)findViewById(R.id.refresh);
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(android.os.Message msg) {
//                if (msg.what == 1) {
//                    article = (Article) msg.obj;
//
//                    /*在数组中存放数据*/
//                    if(article != null) {
//                        for (int i = 0; i < 10; i++) {
//                            HashMap<String, Object> map = new HashMap<String, Object>();
//                            if (i == 1 || i == 3 || i == 5) {
//                                if (article != null && message != null && message.equalsIgnoreCase("初级科技")) {
//                                    map.put("ItemImage", null);//加入图片
//                                    map.put("title", article.getTitle());
//                                    map.put("date", article.getTime());
//                                    listItem.add(map);
//                                }
//                            } else {
//                                map.put("ItemImage", R.drawable.ic_launcher);//加入图片
//                                map.put("title", "标题" + i);
//                                map.put("date", str);
//                                listItem.add(map);
//                            }
//                        }
//                    }else{
//                        Log.e("------ERROR----------","article is empty");
//                    }
//
//                    SimpleAdapter mSimpleAdapter = new SimpleAdapter(Listview.this,listItem,//需要绑定的数据
//                            R.layout.lsitviewitem,//每一行的布局
//                            //动态数组中的数据源的键对应到定义布局的View中
//                            new String[] {"ItemImage","title", "date"},
//                            new int[] {R.id.ItemImage,R.id.title,R.id.date}
//                    );
////
//                    plv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
//                }
//            }
//        };
//
//        rlv = (ListView)findViewById(R.id.refresh);
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(android.os.Message msg) {
//                if (msg.what == 1) {
//                    article = (Article) msg.obj;
//
//                    /*在数组中存放数据*/
//                    if(article != null) {
//                        for (int i = 0; i < 10; i++) {
//                            HashMap<String, Object> map = new HashMap<String, Object>();
//                            if (i == 1 || i == 3 || i == 5) {
//                                if (article != null && message != null && message.equalsIgnoreCase("初级科技")) {
//                                    map.put("ItemImage", null);//加入图片
//                                    map.put("title", article.getTitle());
//                                    map.put("date", article.getTime());
//                                    listItem.add(map);
//                                }
//                            } else {
//                                map.put("ItemImage", R.drawable.ic_launcher);//加入图片
//                                map.put("title", "标题" + i);
//                                map.put("date", str);
//                                listItem.add(map);
//                            }
//                        }
//                    }else{
//                        Log.e("------ERROR----------","article is empty");
//                    }
//
//                    SimpleAdapter mSimpleAdapter = new SimpleAdapter(Listview.this,listItem,//需要绑定的数据
//                            R.layout.lsitviewitem,//每一行的布局
//                            //动态数组中的数据源的键对应到定义布局的View中
//                            new String[] {"ItemImage","title", "date"},
//                            new int[] {R.id.ItemImage,R.id.title,R.id.date}
//                    );
//
//                    rlv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
//                }
//            }
//        };
//
//
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
//
//        plv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("MyListViewBase", "你点击了ListView条目" + position);//在LogCat中没有输出信息
//                Intent intent = new Intent();
//                intent.setClass(Listview.this, RefreshActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        rlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("MyListViewBase", "你点击了ListView条目" + position);//在LogCat中没有输出信息
//                Intent intent = new Intent();
//                intent.setClass(Listview.this, RefreshActivity.class);
//                startActivity(intent);
//            }
//        });
////        关于数据库的操作
//        dbArticle = new DbArticle(this,"Articles.db",null,1);
//        SQLiteDatabase db = dbArticle.getWritableDatabase();
//
//        //插入数据
////        db.execSQL("INSERT INTO Article VALUES (?,?,?,?,?,?,?)", new Object[]{4,"title","科技","body"
////                ,"初级","50","2015.03.03"});
//        Cursor c = db.rawQuery("SELECT * FROM Article",null);
//        while (c.moveToNext()) {
//            int id = c.getInt(c.getColumnIndex("id"));
//            String title = c.getString(c.getColumnIndex("title"));
//            String catalogy = c.getString(c.getColumnIndex("catalogy"));
//            String body = c.getString(c.getColumnIndex("body"));
//            String level = c.getString(c.getColumnIndex("level"));
//            int difficultRatio = c.getInt(c.getColumnIndex("difficultRatio"));
//            Log.i("db", "id=>" + id + ", title=>" + title + ", catalogy=>" + catalogy
//                    + ", body=>" + body + ", level=>" + level + "difficultRatio=>" + difficultRatio);
//        }
//        c.close();
//
//        //关闭当前数据库
//        db.close();
//
////        //删除test.db数据库
////      deleteDatabase("test.db");
//
//    }
//
//
////    private void showView(){
////        if(demoAdapter == null){
////            demoAdapter = new DemoAdapter(this, datas);
////            mainLv.setAdapter(demoAdapter);
////        }else{
////            demoAdapter.updateView(datas);
////        }
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

    @Override
    public void onLoad() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
//                initLoadData();
                showView();
//                lv.getFootView().loadComplete();
            }
        }, 3000);
    }

    private void showView() {

        for (int i = 10; i < 20; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();

                map.put("ItemImage", R.drawable.ic_launcher);//加入图片
                map.put("title", "标题" + i);
                map.put("date", "2015.3.04");
                listItem.add(map);

        }
//        SimpleAdapter mSimpleAdapter = new SimpleAdapter(Listview.this,listItem,//需要绑定的数据
//                R.layout.lsitviewitem,//每一行的布局
//                //动态数组中的数据源的键对应到定义布局的View中
//                new String[] {"ItemImage","title", "date"},
//                new int[] {R.id.ItemImage,R.id.title,R.id.date}
//        );
//
//        plv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
    }

    private void showView1() {

        for (int i = 100; i < 120; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("ItemImage", R.drawable.ic_launcher);//加入图片
            map.put("title", "标题" + i);
            map.put("date", "2015.3.04");
            listItem.add(0,map);

        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(Listview.this,listItem,//需要绑定的数据
                R.layout.lsitviewitem,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"ItemImage","title", "date"},
                new int[] {R.id.ItemImage,R.id.title,R.id.date}
        );
        mSimpleAdapter.notifyDataSetChanged();
        lv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                showView1();
//                lv.refreshComplete();
            }
        }, 3000);
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (i == 1 || i == 3 || i == 5) {
                map.put("ItemImage", null);//加入图片
                map.put("title", "标题");
                map.put("date", "2015.03.05");
                listItem.add(map);
            } else {
                map.put("ItemImage", R.drawable.ic_launcher);//加入图片
                map.put("title", "标题" + i);
                map.put("date", "2015.03.06");
                listItem.add(map);
            }
        }
    }
}
