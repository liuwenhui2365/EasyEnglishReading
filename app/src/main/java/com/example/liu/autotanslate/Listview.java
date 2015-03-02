package com.example.liu.autotanslate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.SpiderArticle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Listview extends ActionBarActivity {

    private  ListView lv = null;

    Article article = null;
    SpiderArticle spiderArticle = new SpiderArticle();
    Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //      获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ");
        Date curDate = new Date(System.currentTimeMillis());
        final String str = formatter.format(curDate);

        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        //StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectAll().penaltyLog().penaltyDeath().build());

    //        获取类别和级别

          final String message = this.getIntent().getAction();


        Thread articleTask = new Thread() {
            @Override
            public void run() {
//                try {
                Article article = spiderArticle.getPassage("http://www.51voa.com/VOA_Special_English/animal-weapons-offer-lesons-for-human-arms-race-61556.html");
//                }catch (NullPointerException e){
//                    Log.e("empty!","network wroong");
//                    //TODO 增加网络错误提示窗口
//                    Intent intent = new Intent();
//                    intent.setClass(Listview.this,Classify.class);
//                    startActivity(intent);
//                }

                android.os.Message msg = new android.os.Message();
                msg.what = 1;
//                mDownloadCount ++;
                msg.obj = article;
                mHandler.sendMessage(msg);
            }
        };
        articleTask.start();

        //        实现每行显示两行文本和图片

        lv = (ListView)findViewById(R.id.lv);
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();

        mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    article = (Article) msg.obj;
                    Log.d("article title", article.getTitle());
                    /*在数组中存放数据*/
                    for(int i=0;i<10;i++)
                    {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        if(i==1 || i==3 || i==5){
                            if(article != null && message != null && message.equalsIgnoreCase("初级科技")) {
                                map.put("ItemImage", null);//加入图片
                                map.put("title", article.getTitle());
                                map.put("date", article.getTime());
                                listItem.add(map);
                            }
                        }else {
                            map.put("ItemImage", R.drawable.ic_launcher);//加入图片
                            map.put("title", "标题" + i);
                            map.put("date", str);
                            listItem.add(map);
                        }
                    }

                    SimpleAdapter mSimpleAdapter = new SimpleAdapter(Listview.this,listItem,//需要绑定的数据
                            R.layout.lsitviewitem,//每一行的布局
                            //动态数组中的数据源的键对应到定义布局的View中
                            new String[] {"ItemImage","title", "date"},
                            new int[] {R.id.ItemImage,R.id.title,R.id.date}
                    );

                    lv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
                }
            }
        };


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("MyListViewBase", "你点击了ListView条目" + position);//在LogCat中没有输出信息
                Intent intent = new Intent();
                intent.setClass(Listview.this, Message.class);
                startActivity(intent);
            }
        });
    }

//    private class ProgressThread extends Thread {
//
//        private Handler handler;
//        private ArrayList<String> data;
//
//        public ProgressThread(Handler handler, ArrayList<String> data) {
//            this.handler = handler;
//            this.data = data;
//        }
//
//    // 此处甚至可以不需要设置Looper，因为Handler默认就使用当前线程的Looper
//    private final Handler handler = new Handler(Looper.getMainLooper()) {
//
//        private ArrayAdapter<String> adapter;
//        private ArrayList<String> data;
//
//        private static final int PROGRESS_DIALOG = 1;
//        private static final int STATE_FINISH = 1;
//        private static final int STATE_ERROR = -1;
//        public void handleMessage(Message msg) { // 处理Message，更新ListView
//            int state = msg.getData().getInt("state");
//            switch(state){
//                case STATE_FINISH:
//                    dismissDialog(PROGRESS_DIALOG);
//                    Toast.makeText(getApplicationContext(),
//                            "加载完成!",
//                            Toast.LENGTH_LONG)
//                            .show();
//
//                    adapter = new ArrayAdapter<String>(getApplicationContext(),
//                            android.R.layout.simple_list_item_1,
//                            data );
//
//                    setListAdapter(adapter);
//
//                    break;
//
//                case STATE_ERROR:
//                    dismissDialog(PROGRESS_DIALOG);
//                    Toast.makeText(getApplicationContext(),
//                            "处理过程发生错误!",
//                            Toast.LENGTH_LONG)
//                            .show();
//
//                    adapter = new ArrayAdapter<String>(getApplicationContext(),
//                            android.R.layout.simple_list_item_1,
//                            data );
//
//                    setListAdapter(adapter);
//
//                    break;
//
//                default:
//
//            }
//        }
//    };

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
