package com.example.liu.autotanslate.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liu.autotanslate.MyURLSpan;
import com.example.liu.autotanslate.R;
import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.Translate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


public class Message extends Activity {

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

        initView();

//      单词分类优化到翻译类里面
        readArticle(title);
    }

    private void initView() {
        ImageView imageView = (ImageView)findViewById(R.id.ret);
        imageView.setImageResource(R.drawable.ret);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mTitle = (TextView)findViewById(R.id.activity_title);
        mTitle.setText("Message");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        String time = simpleDateFormat.format(curDate);
        textView = (TextView)findViewById(R.id.date_message);
        textView.setText(time);
//      改为全局变量，其他方法也要用
        title = this.getIntent().getAction();
//        Log.d("Message报告","传过来的内容"+title);
//        TODO 增加内容的时候使用
        textView = (TextView) findViewById(R.id.title_message);
        textView.setText(title);

    }

    //  从数据库读取并翻译
    public String readArticle(String title){
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
//            Log.d("数据库查询的标题", title);
            Translate translate = new Translate(this);
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='Article'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                    Log.d("数据库查询的标题", title);
                    c = db.rawQuery("SELECT * FROM Article  WHERE title = ?", new String[]{title});
                    while (c.moveToNext()) {
                        title = c.getString(c.getColumnIndex("title"));
                        String body = c.getString(c.getColumnIndex("body"));
//                        Log.d("从数据库读取到的文章内容",body);
                        time = c.getString(c.getColumnIndex("time"));
                        String catalogy = c.getString(c.getColumnIndex("catalogy"));
                        StringBuilder sBody = new StringBuilder(body);
                        article = new Article(title,sBody,catalogy);
                        textView = (TextView) findViewById(R.id.date_message);
                        textView.setText("文章更新于"+time);
//                        Log.d("提示","开始翻译啦");
                        article = translate.translate(article,dbArticle);
//                        Log.d(getClass().getSimpleName()+"报告","翻译加词性结束");
//                      选中每个单词,开启线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                      TODO 想想为什么后面的两个类的字符串长度异常大
//                                Log.d("Message报告","传入处理Str传入之前大小为"+article.getBody().toString().length());
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
        }
        return body;

    }

    public void handlerStr(String str){
        mss = new SpannableString(str);
        List<Integer> enStrList= Message.getENPositionList(str);
//        Log.d(getClass().getSimpleName()+"报告","添加获取单词后的文章大小"+enStrList.size());
//      如果大小为0则不执行
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
                    Toast.makeText(Message.this,"获取内容为空！",Toast.LENGTH_SHORT).show();
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
                mss.setSpan(new MyURLSpan(Message.this,clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView = (TextView) findViewById(R.id.content);
//              注意如果点击事件没有响应看是不是赋值出错了
                textView.setText(mss);
//             特别注意是LinkMovementMehond方法获取实例，否则点击无响应
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });
    }

    //    英文字母在字符串中的位置，将每一个字符的位置存储到list
    public static List<Integer> getENPositionList(String str){
        List<Integer> list=new ArrayList<Integer>();
//        Log.d("报告标记后的文章大小",str.length()+"");
//        如果长度太大截取即可，防止长时间没有响应
        if (str.length() > 4000){
            str = str.substring(0,4000);
        }
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
