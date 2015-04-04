package com.example.liu.autotanslate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.Translate;
import com.wenhuiliu.EasyEnglishReading.Words;

import java.io.BufferedReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.LoggingPermission;

/**
 * Created by Administrator on 2015/4/1.
 * 用来显示已分享到的文章内容
 */
public class ShareMessage extends ActionBarActivity {
    TextView textView;
    DbArticle dbArticle = null;
    SQLiteDatabase db = null;

    //  翻译使用
    private HashMap<String,String> knownWords = new HashMap<>();
    private HashMap<String,String> unknownWords = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置没有标题
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share_message);
//      显示返回菜单
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        String time = simpleDateFormat.format(curDate);

        final String title = this.getIntent().getAction();
        textView = (TextView)findViewById(R.id.sharemessage_title);
        textView.setText(title);
        textView = (TextView)findViewById(R.id.sharemessage_time);
        textView.setText(time);
//      注意顺序
        WordClassifyByFile();
        readArticle(title);

    }

    //    单词分类
    public void WordClassifyByFile() {
//		从文章中获取每一个单词
        HashMap<String, String> wordMeaning = new HashMap<String, String>();
        String [] words = new String[]{};
        BufferedReader reader = null;
        InputStream input = null;
        Cursor c = null;
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
//                    Log.e("数据库", "单词表创建成功");
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

    public void readArticle(String title){

        Article article = null;
        Cursor c = null;
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            Translate translate = new Translate();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='ShareArticle'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                    Log.d("读取数据库文章开始","gogo");
//                    Log.d("known"+knownWords.size(),"unknown"+unknownWords.size());
                    c = db.rawQuery("SELECT * FROM ShareArticle  WHERE title = ?", new String[]{title});
                    while (c.moveToNext()) {
                        title = c.getString(c.getColumnIndex("title"));
                        String body = c.getString(c.getColumnIndex("body"));
                        String time = c.getString(c.getColumnIndex("time"));
                        String catalogy = c.getString(c.getColumnIndex("catalogy"));
                        StringBuilder sBody = new StringBuilder(body);
                        article = new Article(title,sBody,catalogy);

                        article = translate.translate(article, knownWords, unknownWords);

                        textView = (TextView)findViewById(R.id.sharemessage_title);
                        textView.setText(title);
                        textView = (TextView) findViewById(R.id.sharemessage_time);
                        textView.setText(time);
                        textView = (TextView) findViewById(R.id.sharemessage_content);
                        textView.setText(article.getBody());
                        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                    }
                }else {
                    Toast.makeText(this,"还没有分享到的文章哦！",Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("数据库异常", "ERROR");
        }finally {
            if(c != null){
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}
