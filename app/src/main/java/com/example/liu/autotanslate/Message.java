package com.example.liu.autotanslate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class Message extends ActionBarActivity {

    private Article article = null;
    private TextView textView = null;
    private DbArticle dbArticle;
    private String title = null;
    private String body = null;
    private String time = null;
    SQLiteDatabase db = null;
    Cursor c = null;

    private SpannableString mss = null;
//    private String clickStr = null;
//    private int start = 0;
//    private int end = 0;

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
        String time = simpleDateFormat.format(curDate);
        textView = (TextView)findViewById(R.id.date_message);
        textView.setText(time);

        final String title = this.getIntent().getAction();
//        TODO 增加内容的时候使用
        textView = (TextView) findViewById(R.id.title_message);
        textView.setText(title);

//      单词分类优化到翻译类里面
        readArticle(title);
    }

//  从数据库读取并翻译
    public String readArticle(String title){
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            Translate translate = new Translate(this);
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
                        textView.setText("文章更新于"+time);
//                        Log.d("提示","开始翻译啦");
                        article = translate.translate(article,dbArticle);
//                      选中每个单词,开启线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handlerStr(article.getBody().toString());
                            }
                        }).start();


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

    public void handlerStr(String str){
        mss = new SpannableString(str);
        List<Integer> enStrList= Message.getENPositionList(str);
        String tempStr=str.charAt(enStrList.get(0))+"";
        for(int i=0;i<enStrList.size()-1;i++){
            if(enStrList.get(i+1)-enStrList.get(i)==1){
                tempStr=tempStr+str.charAt(enStrList.get(i+1));
            }else{
                setLink(enStrList.get(i)-tempStr.length()+1, enStrList.get(i)+1,tempStr);//因为此时i在循环中已经自加了
                tempStr=str.charAt(enStrList.get(i+1))+"";
            }
        }
        setLink(enStrList.get(enStrList.size()-1)-tempStr.length()+1, enStrList.get(enStrList.size()-1)+1,tempStr);
    }

    /**
     * 给指定的[start,end)字符串设置链接
     * @param start 设置链接的开始位置
     * @param end  设置链接的结束位置
     * @param clickStr 点击的字符串
     */
    public void setLink(final int start, final int end, final String clickStr) {
//      msp.setSpan(new URLSpan("http://www.baidu.com"), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//      特别注意传入上下文的时候，把该类作为上下文传进去，不能传全局的，否则报空指针异常
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mss.setSpan(new MyURLSpan(Message.this,clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView = (TextView) findViewById(R.id.content);
                textView.setText(mss);
//             特别注意是LinkMovementMehond方法获取实例
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });
    }

    //    英文字母在字符串中的位置，将每一个字符的位置存储到list
    public static List<Integer> getENPositionList(String str){
        List<Integer> list=new ArrayList<Integer>();
        for(int i=0;i<str.length();i++){
            char mchar=str.charAt(i);
            //('a' <= mchar && mchar <= 'z')||('A' <= mchar && mchar <='Z')
            if(Pattern.matches("[A-Za-z]", mchar + "")){
                list.add(i);
//              System.out.println(i+"位置为英文字符："+mchar);
            }
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("quit")) {
                String message = intent.getStringExtra("remind");
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
