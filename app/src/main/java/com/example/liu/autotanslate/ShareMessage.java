package com.example.liu.autotanslate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.LoggingPermission;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/4/1.
 * 用来显示已分享到的文章内容
 */
public class ShareMessage extends ActionBarActivity {
    private TextView textView;
    private DbArticle dbArticle = null;
    private SQLiteDatabase db = null;
    private Article article;
    private SpannableString mss = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置没有标题
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share_message);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        String time = simpleDateFormat.format(curDate);

        final String title = this.getIntent().getAction();
        textView = (TextView)findViewById(R.id.sharemessage_title);
        textView.setText(title);
        textView = (TextView)findViewById(R.id.sharemessage_time);
        textView.setText(time);
//      单词分类优化到翻译类中
        readArticle(title);

    }


    public void readArticle(String title){
        Cursor c = null;
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            Translate translate = new Translate(this);
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
//                        Log.d("--分享到的文章内容",body);
                        String time = c.getString(c.getColumnIndex("time"));
                        String catalogy = c.getString(c.getColumnIndex("catalogy"));
                        StringBuilder sBody = new StringBuilder(body);
                        article = new Article(title,sBody,catalogy);
                        textView = (TextView)findViewById(R.id.sharemessage_title);
                        textView.setText(title);
                        textView = (TextView) findViewById(R.id.sharemessage_time);
                        textView.setText("文章更新于"+time);
                        article = translate.translate(article, dbArticle);
//                      选中每个单词,开启线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                               handlerStr(article.getBody().toString());
                            }
                        }).start();
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

    public void handlerStr(String str){
        mss = new SpannableString(str);
        List<Integer> enStrList= Message.getENPositionList(str);
//        防止越界异常
        if (enStrList.size() > 0) {
            String tempStr = String.valueOf(str.charAt(enStrList.get(0)));
            for (int i = 0; i < enStrList.size() - 1; i++) {
                if (enStrList.get(i + 1) - enStrList.get(i) == 1) {
                    tempStr = tempStr + str.charAt(enStrList.get(i + 1));
                } else {
                    setLink(enStrList.get(i) - tempStr.length() + 1, enStrList.get(i) + 1, tempStr);//因为此时i在循环中已经自加了
                    tempStr = str.charAt(enStrList.get(i + 1)) + "";
                }
            }
            setLink(enStrList.get(enStrList.size() - 1) - tempStr.length() + 1, enStrList.get(enStrList.size() - 1) + 1, tempStr);
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ShareMessage.this,"分享到的文章内容为空！",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
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
                mss.setSpan(new MyURLSpan(ShareMessage.this,clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView = (TextView) findViewById(R.id.sharemessage_content);
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
