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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.SpiderArticle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Message.this,WordClassify.class);
                startActivity(intent);
            }
        });

//        Toast.makeText();
        readArticle(title);
        getWordsMeaningByFile();


    }

    public void getWordsMeaningByFile() {
//		从文章中获取每一个单词
        HashMap<String, String> wordMeaning = new HashMap<String, String>();
        String [] words = new String[]{};
        BufferedReader reader = null;
        String line = null;
        InputStream input = getResources().openRawResource(R.raw.words);
        BufferedReader read = new BufferedReader(new InputStreamReader(input));
        try {
            while((line= read.readLine()) != null){
                words =  line.split(" ");
                wordMeaning.put(words[0],words[1]);
//                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                 如果表存在

                } else {
                    db.execSQL("CREATE TABLE words (word VARCHAR PRIMARY KEY,meaning VARCHAR,type VARCHAR)");
                    Log.e("数据库", "表创建成功");
                    Iterator iter = wordMeaning.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        //            往数据库里放数据
                        db.execSQL("INSERT INTO words values(?,?,?)", new String[]{entry.getKey().toString(), entry.getValue().toString(), "unknown"});
                        //                Log.d("单词：", entry.getKey().toString());
                        //                Log.d("意思：", entry.getValue().toString());
                    }
                }
            }
            if(db != null){
                db.close();
            }

        }
    }

    public void readArticle(String title){

        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='Article'", null);

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
