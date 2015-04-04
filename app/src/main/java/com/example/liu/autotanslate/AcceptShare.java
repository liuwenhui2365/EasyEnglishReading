package com.example.liu.autotanslate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.*;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.HttpClient;
import com.wenhuiliu.EasyEnglishReading.MSpiderChinaDaily;
import com.wenhuiliu.EasyEnglishReading.SpiderArticle;
import com.wenhuiliu.EasyEnglishReading.SpiderChinaDaily;
import com.wenhuiliu.EasyEnglishReading.Translate;
import com.wenhuiliu.EasyEnglishReading.Words;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class AcceptShare extends ActionBarActivity {

    private HashMap<String,String> knownWords = new HashMap<>();
    private HashMap<String,String> unknownWords = new HashMap<>();
    public static final int SHOW_DATA = 0;
    DbArticle dbArticle;
    SQLiteDatabase db = null;

    String title = null;
    String time = null;
    StringBuilder content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //获得Intent的Action
        String action = intent.getAction();
        //获得Intent的MIME type
        String type = intent.getType();

//      接受到网址才能触发
        if(Intent.ACTION_SEND.equals(action) && type != null){
            //我们这里处理所有的文本类型
            if(type.startsWith("text/")){
                //处理获取到的文本，这里我们用TextView显示
                handleSendText(intent);
//          handleSendText();
            }
        }else {
            Toast.makeText(this,"还没有接受到网址哦！",Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_message);
    }

    /**
     * 用TextView显示文本
     * 可以打开一般的文本文件
     * @param intent
     */
    private void handleSendText(final Intent intent){
//   public void handleSendText(){
//        final TextView textView = new TextView(this);

        //一般的文本处理，我们直接显示字符


            //文本文件处理，从Uri中获取输入流，然后将输入流转换成字符串
//        Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//        if(textUri != null){
//            try {
//                InputStream inputStream = this.getContentResolver().openInputStream(textUri);
//                textView.setText(inputStream2Byte(inputStream));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WordClassifyByFile();
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
//                String sharedText = "http://m.chinadaily.com.cn/en/trending/2015-04/04/content_20000872.htm";
                if(sharedText != null) {
                    try {
//                        Log.d("已经进入执行了","哈哈哈");
                        writeShareArticle(sharedText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                }
            }).start();
    }

    //        接受子线程发过来的对象

   private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Log.d("handler","接受到子线程传过来的对象了");
            switch (msg.what){
                case SHOW_DATA:
                    Article article = (Article)msg.obj;
                    TextView textView = (TextView)findViewById(R.id.title_message);
                    article.getTitle();
                    textView.setText( article.getTitle());
                    textView = (TextView)findViewById(R.id.date_message);
                    textView.setText( article.getTime());
                    textView = (TextView)findViewById(R.id.content);
                    textView.setText( article.getBody());
            }
        }
    };

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

    public void writeShareArticle(String url){
//        关于数据库的操作
        Article article = null;
        Cursor myCursor = null;
        Translate translate = new Translate();
        MSpiderChinaDaily mSpiderChinaDaily = new MSpiderChinaDaily();

        Log.d("获取到的网址网址",url);


        //插入数据(逆序写入保证读取到最新的）
//        这样在多次插入数据再读取的时候会出现顺序不匹配问题
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getWritableDatabase();
//            db.execSQL("DROP TABLE IF EXISTS Article");
//            Log.i("提示","表删除成功");
            myCursor = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='ShareArticle'", null);
            if (myCursor.moveToNext()) {
                int count = myCursor.getInt(0);
                if (count > 0) {
                    try {
                        article = mSpiderChinaDaily.getPassage(url);
                        String title = article.getTitle();
//                        Log.d("标题", title);
                        String catalogy = article.getCatalogy();
                        String level = article.getLevel();
                        String time = article.getTime();
//                        Log.d("从网络获取文章时间", time);
                        int difficultRatio = article.getDifficultRatio();
                        StringBuilder body = article.getBody();
//                        Log.d("网络获取文章内容",body+"");
                        db.execSQL("INSERT INTO ShareArticle VALUES (?,?,?,?,?,?,?)", new Object[]{url, title,
                                catalogy, body, level, difficultRatio, time});
                        article = translate.translate(article, knownWords, unknownWords);
//                        Log.d("known",knownWords.size()+"");
//                        Log.d("unknowm",unknownWords.size()+"");
//                        Log.d("翻译完成","...");
                        Message message = new Message();
                        message.what = SHOW_DATA;
                        message.obj = article;
//                      注意handler是全局对象
                        handler.sendMessage(message);

                    } catch (IllegalArgumentException e) {
                        Log.e("警告", "article arguments is illegal!");
                    } catch (SQLiteConstraintException e1) {
                        Log.e("警告", "Share表中已经存在！");
//                        Log.d("发送","子线程向主线程发送");
                        Toast.makeText(this,"数据已经分享过了哦，去已分享看看吧。",Toast.LENGTH_SHORT).show();
                    } catch (IOException e2){
                        Toast.makeText(this,"获取失败，重新分享试试",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    db.execSQL("CREATE TABLE ShareArticle (url VARCHAR PRIMARY KEY,title VARCHAR," +
                            "catalogy VARCHAR, body VARCHAR, level VARCHAR, difficultRatio INT, time VARCHAR)");
                    Log.e("数据库", "share表创建成功");
                    //                    Log.i("网络获取文章数目", Integer.toString(urls.size()));
                    try {
                        article = mSpiderChinaDaily.getPassage(url);
                        String title = article.getTitle();
                        Log.d("获取到的标题",title);
                        String catalogy = article.getCatalogy();
                        String level = article.getLevel();
                        int difficultRatio = article.getDifficultRatio();
                        StringBuilder body = article.getBody();
                        String time = article.getTime();
                        db.execSQL("INSERT INTO ShareArticle VALUES (?,?,?,?,?,?,?)", new Object[]{url, title, catalogy, body
                                , level, difficultRatio, time});
                        Log.d("开始翻译","ooppoo");
                        article = translate.translate(article, knownWords, unknownWords);
                        Log.d("向主线程发送对象","ggg");
                        Log.d("翻译好的文章",article.getBody()+"");
                        Message message = new Message();
                        message.what = SHOW_DATA;
                        message.obj = article;
                        Handler handler = new Handler();
                        handler.sendMessage(message);
                    } catch (IllegalArgumentException e) {
                        Log.w("警告", "article arguments is illegal!");
                    } catch (SQLiteConstraintException e1) {
                        Log.w("警告", "share表中数据已经存在！");
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("捕获","异常");
//            Toast.makeText(Refresh.this,"获取数据异常",Toast.LENGTH_SHORT).show();
        }finally {
            if(myCursor!=null){
                myCursor.close();
            }

            if (db!=null){
                db.close();
            }
        }

    }

    /**
     * 将输入流转换成字符串
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String inputStream2Byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte [] buffer = new byte[1024];
        int len = -1;

        while((len = inputStream.read(buffer)) != -1){
            bos.write(buffer, 0, len);
        }

        bos.close();

        //指定编码格式为UIT-8
        return new String(bos.toByteArray(), "UTF-8");
    }
}
