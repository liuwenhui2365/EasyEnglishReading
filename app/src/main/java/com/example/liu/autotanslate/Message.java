package com.example.liu.autotanslate;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
//      显示返回菜单
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        String time = simpleDateFormat.format(curDate);

        textView = (TextView)findViewById(R.id.date);
        textView.setText(time);

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    article = (Article) msg.obj;
                    textView = (TextView) findViewById(R.id.title);
                    //        textView.setText("显示文章标题");
                    textView.setText(article.getTitle());
                    textView = (TextView) findViewById(R.id.content);
                    StringBuilder sbd = new StringBuilder(article.getBody());
                    //      从文件读取涉及到路径问题

                    textView.setText(sbd);
                    textView.setMovementMethod(ScrollingMovementMethod.getInstance());

                }
            }
        };

        final SpiderArticle spiderArticle = new SpiderArticle();
        Thread articleTask = new Thread() {
            @Override
            public void run() {
                try {
                      article = spiderArticle.getPassage("http://www.51voa.com/VOA_Special_English/animal-weapons-offer-lesons-for-human-arms-race-61556.html");
                }catch (NullPointerException e){
                    Log.e("empty!", "network wroong");
//                    //TODO 增加网络错误提示窗口
                    Intent intent = new Intent();
                    intent.setClass(Message.this,Listview.class);
                    startActivity(intent);
                }

                android.os.Message msg = new android.os.Message();
                msg.what = 1;
//                mDownloadCount ++;
                msg.obj = article;
                mHandler.sendMessage(msg);
            }
        };
        articleTask.start();

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
