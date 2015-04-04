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
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.Translate;
import com.wenhuiliu.EasyEnglishReading.Words;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Message extends ActionBarActivity {

    private Article article = null;
    private TextView textView = null;
    private DbArticle dbArticle;
    private String title = null;
    private String body = null;
    private String time = null;
    SQLiteDatabase db = null;
    Cursor c = null;

//  翻译使用
    private HashMap<String,String> knownWords = new HashMap<>();
    private HashMap<String,String> unknownWords = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置没有标题
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
//      显示返回菜单
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
//        String time = simpleDateFormat.format(curDate);

        final String title = this.getIntent().getAction();

//        TODO 增加内容的时候使用
        textView = (TextView) findViewById(R.id.title_message);
        textView.setText(title);
//      注意顺序
        getWordsMeaningByFile();
        readArticle(title);


    }

    public void getWordsMeaningByFile() {
//		从文章中获取每一个单词
        HashMap<String, String> wordMeaning = new HashMap<String, String>();
        String [] words = new String[]{};
        BufferedReader reader = null;
        InputStream input = null;
        String line = null;

        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            String word = null;
            String meaning = null;
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                 如果表存在
                    c = db.rawQuery("select word,meaning from words where type = ?", new String[]{"know"});
                    while (c.moveToNext()) {
                        word = c.getString(c.getColumnIndex("word"));
                        meaning = c.getString(c.getColumnIndex("meaning"));
//                        Log.d("知道的单词和意思",word+meaning);
                        knownWords.put(word, meaning);
                    }

                    c = db.rawQuery("select word,meaning from words where type = ?", new String[]{"unknown"});
                    while (c.moveToNext()) {
                        word = c.getString(c.getColumnIndex("word"));
                        meaning = c.getString(c.getColumnIndex("meaning"));
//                        Log.d("bu知道的单词和意思",word+meaning);
                        unknownWords.put(word, meaning);
                    }

                } else {
                    wordMeaning = Words.getWord();
                    db.execSQL("CREATE TABLE words (word VARCHAR PRIMARY KEY,meaning VARCHAR,type VARCHAR)");
                    Log.e("数据库", "单词表创建成功");
//                   从map中读取单词和意思
                    Iterator iter = wordMeaning.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        //            往数据库里放数据
                        db.execSQL("INSERT INTO words values(?,?,?)", new String[]{entry.getKey().toString(), entry.getValue().toString(), "unknown"});
                        //                Log.d("单词：", entry.getKey().toString());
                        //                Log.d("意思：", entry.getValue().toString());
                    }

                    c = db.rawQuery("select word,meaning from words where type = ?", new String[]{"know"});
                    while (c.moveToNext()) {
                        word = c.getString(c.getColumnIndex("word"));
                        meaning = c.getString(c.getColumnIndex("meaning"));
//                        Log.d("知道的单词和意思", word + meaning);
                        knownWords.put(word, meaning);
                    }

                    c = db.rawQuery("select word,meaning from words where type = ?", new String[]{"unknown"});
                    while (c.moveToNext()) {
                        word = c.getString(c.getColumnIndex("word"));
                        meaning = c.getString(c.getColumnIndex("meaning"));
//                        Log.d("bu知道的单词和意思", word + meaning);
                        unknownWords.put(word, meaning);
                    }
                }
            }
        }catch (Exception w) {
            w.printStackTrace();
        }finally {
            if (c != null){
                c.close();
            }

            if(db != null){
                db.close();
            }

        }
    }

//  从数据库读取并翻译
    public String readArticle(String title){
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            Translate translate = new Translate();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='Article'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                    Log.d("数据库获取到的标题", title);
                    c = db.rawQuery("SELECT * FROM Article  WHERE title = ?", new String[]{title});
                    while (c.moveToNext()) {
                        title = c.getString(c.getColumnIndex("title"));
                        String body = c.getString(c.getColumnIndex("body"));
                        time = c.getString(c.getColumnIndex("time"));
                        String catalogy = c.getString(c.getColumnIndex("catalogy"));
                        StringBuilder sBody = new StringBuilder(body);
                        article = new Article(title,sBody,catalogy);
                        textView = (TextView) findViewById(R.id.date_message);
                        textView.setText(time);
//                        Log.d("提示","开始翻译啦");
//                        Log.d("知道map大小",""+knownWords.size());
//                        Log.d("不知道map大小",unknownWords.size()+"");
                        article = translate.translate(article,knownWords,unknownWords);
                        textView = (TextView) findViewById(R.id.content);
                        textView.setText(article.getBody());
                        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                    }
                }
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
            return body;
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
//        if (id == R.id.action_settings) {
////           添加相关事件
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
