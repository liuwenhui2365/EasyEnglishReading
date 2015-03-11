package com.example.liu.autotanslate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.SpiderArticle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Message extends ActionBarActivity {

    private Article article = null;
    private TextView textView = null;
    private DbArticle dbArticle;
    private String title = null;
    private String body = null;
    private String time = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
//      显示返回菜单
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
//        String time = simpleDateFormat.format(curDate);

        final String title = this.getIntent().getAction();

        textView = (TextView) findViewById(R.id.title);
        textView.setText(title);

        readArticle(title);


//        final Handler mHandler = new Handler() {
//            @Override
//            public void handleMessage(android.os.Message msg) {
//                if (msg.what == 1) {
//                    article = (Article) msg.obj;
//                    textView = (TextView) findViewById(R.id.title);
//                    textView.setText(title);
////                  textView.setText(article.getTitle());
//                    textView = (TextView) findViewById(R.id.content);
//                    StringBuilder sbd = new StringBuilder(body);
//                    //      从文件读取涉及到路径问题
//
//                    textView.setText(sbd);
//                    textView.setMovementMethod(ScrollingMovementMethod.getInstance());
//
//                }
//            }
//        };
//
//        final SpiderArticle spiderArticle = new SpiderArticle();
//        Thread articleTask = new Thread() {
//            @Override
//            public void run() {
//                try {
//                      article = spiderArticle.getPassage("http://www.51voa.com/VOA_Special_English/animal-weapons-offer-lesons-for-human-arms-race-61556.html");
//                }catch (NullPointerException e){
//                    Log.e("empty!", "network wroong");
////                    //TODO 增加网络错误提示窗口
//                    Intent intent = new Intent();
//                    intent.setClass(Message.this,Listview.class);
//                    startActivity(intent);
//                }
//
//                android.os.Message msg = new android.os.Message();
//                msg.what = 1;
////                mDownloadCount ++;
//                msg.obj = article;
//                mHandler.sendMessage(msg);
//            }
//        };
//        articleTask.start();

    }

    public void readArticle(String title){
        SQLiteDatabase db = null;
//        Article article = null;
        Cursor c = null;

        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            Log.d("数据库获取到的标题",title);
            c = db.rawQuery("SELECT * FROM Article  WHERE title = ?",new String[]{title});
            while (c.moveToNext()) {
                title = c.getString(c.getColumnIndex("title"));
                String body = c.getString(c.getColumnIndex("body"));
//                String level = c.getString(c.getColumnIndex("level"));
//                int difficultRatio = c.getInt(c.getColumnIndex("difficultRatio"));
                time = c.getString(c.getColumnIndex("time"));
                Log.d("从数据库中读取db", "title=>" + title + ",time" + time);
//                StringBuilder bodys = new StringBuilder(body);
//                article = new Article(title, bodys, catalogy);
//                article.setDifficultRatio(100);
//                article.setLevel(level);
//                article.setTime(time);
//                dbal.add(article);

                textView = (TextView) findViewById(R.id.content);
                StringBuilder sbd = new StringBuilder(body);
                textView.setText(sbd);
                textView.setMovementMethod(ScrollingMovementMethod.getInstance());

                textView = (TextView) findViewById(R.id.date);
                textView.setText(time);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("数据库异常","ERROR");
        }finally {
            if(c != null){
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
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
//           添加相关事件
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
