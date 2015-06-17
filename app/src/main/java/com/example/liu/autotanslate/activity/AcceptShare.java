package com.example.liu.autotanslate.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
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
import com.wenhuiliu.EasyEnglishReading.MSpiderChinaDaily;
import com.wenhuiliu.EasyEnglishReading.MyHttpPost;
import com.wenhuiliu.EasyEnglishReading.SpiderEconomicArticle;
import com.wenhuiliu.EasyEnglishReading.Translate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class AcceptShare extends Activity {
    public static final int SHOW_DATA = 0;
    private final String CHINADAILY_URL = "http://m.chinadaily.com.cn";
    private final String ECONOMIST_URL = "http://www.economist.com/";
    private TextView textView;
    DbArticle dbArticle;
    SQLiteDatabase db = null;
    private SpannableString mss = null;

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
//      标题栏设置
        ImageView imageView = (ImageView)findViewById(R.id.ret);
        imageView.setImageResource(R.drawable.tubiao);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mTitle = (TextView)findViewById(R.id.activity_title);
        mTitle.setText("Message");
    }

    /**
     * 用TextView显示文本
     * 可以打开一般的文本文件
     * @param intent
     */
    private void handleSendText(final Intent intent){
          new Thread(new Runnable() {
               @Override
               public void run() {
                   String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    //                String sharedText = "http://m.chinadaily.com.cn/en/trending/2015-04/04/content_20000872.htm";
                   if(sharedText != null) {
                       if (sharedText.contains(CHINADAILY_URL)
                               || sharedText.contains(ECONOMIST_URL)) {
                           try {
                               writeShareArticle(sharedText);
                           } catch (Exception e) {
                               Log.e("写入分享文章出现异常", "查看异常");
                           }
                       } else {
                           Toast.makeText(AcceptShare.this, "暂时不支持哦！", Toast.LENGTH_SHORT).show();
                       }
                   }else {
                       Toast.makeText(AcceptShare.this,"没有获取到网址，请重试！",Toast.LENGTH_SHORT).show();
                   }
                }
            }).start();
    }

//        接受子线程发过来的对象

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
//            Log.d("handler","接受到子线程传过来的对象了");
            switch (msg.what){
                case SHOW_DATA:
                    Article article = (Article)msg.obj;
                    textView = (TextView)findViewById(R.id.title_message);
                    textView.setText( article.getTitle());
                    textView = (TextView)findViewById(R.id.date_message);
//                    Log.d(getClass().getSimpleName()+"报告","线程传过来的文章时间"+article.getBody());
                    textView.setText(article.getTime());
//                    textView = (TextView)findViewById(R.id.content);
//                    textView.setText("正在努力加载文章。。。");
//                    Log.d(getClass().getSimpleName()+"报告","线程传过来的文章内容"+article.getBody());
//                    textView.setText( article.getBody());
                break;
            }
        }
    };



    public void writeShareArticle(String url){
//        关于数据库的操作
        Article article = null;
        Cursor myCursor = null;
        Translate translate = new Translate(this);
        MSpiderChinaDaily mSpiderChinaDaily = new MSpiderChinaDaily();
        SpiderEconomicArticle spiderEconomicArticle = new SpiderEconomicArticle();

//        Log.d("获取到的网址网址",url);

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
                        if (url.contains(CHINADAILY_URL)) {
                            article = mSpiderChinaDaily.getPassage(url);
                        }else if (url.contains(ECONOMIST_URL)){
                            article = spiderEconomicArticle.getPassage(url);
                        }

                        if (article != null) {
                            String title = article.getTitle();
//                            Log.d("标题", title);
                            String catalogy = article.getCatalogy();
                            String level = article.getLevel();
                            String time = article.getTime();
                            //                        Log.d("从网络获取文章时间", time);
                            int difficultRatio = article.getDifficultRatio();
                            StringBuilder body = article.getBody();
   //                        Log.d("网络获取文章内容",body+"");
//                          标记词性
                            String contentTagged = MyHttpPost.post(body.toString());
//                            Log.d(getClass().getSimpleName()+"报告","标记后的内容"+contentTagged);
                            db.execSQL("INSERT INTO ShareArticle VALUES (?,?,?,?,?,?,?)", new Object[]{url, title,
                                    catalogy, contentTagged, level, difficultRatio, time});
                            StringBuilder sbody = new StringBuilder(contentTagged);
//                            记得要new一个对象（不需要new也可以）
//                            article = new Article(title,sbody,catalogy);
//                            body内容必须要替换
                            article.setBody(contentTagged);
                            article = translate.translate(article, dbArticle);
                            handlerStr(article.getBody().toString());

                            Message message = new Message();
                            message.what = SHOW_DATA;
                            message.obj = article;
//                            Log.d("报告向主线程发送翻译好的文章",article.getBody()+"");
    //                      特别注意handler是全局对象，如果new一个则收不到对象了
                            handler.sendMessage(message);
                        }else {
//                            Log.e("警告","获取文章为空");
//                          必须在主线程运行才能执行Toast
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AcceptShare.this,"获取内容为空，请重试！",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e("警告", "article arguments is illegal!");
                    } catch (SQLiteConstraintException e1) {
                        Log.e("警告", "Share表中已经存在！");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                             Toast.makeText(AcceptShare.this, "数据已经分享过了哦，去已分享看看吧。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                             Toast.makeText(AcceptShare.this,"获取失败，重新分享试试吧！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    db.execSQL("CREATE TABLE ShareArticle (url VARCHAR PRIMARY KEY,title VARCHAR," +
                            "catalogy VARCHAR, body VARCHAR, level VARCHAR, difficultRatio INT, time VARCHAR)");
                    Log.e("数据库", "share表创建成功");
                    //                    Log.i("网络获取文章数目", Integer.toString(urls.size()));
                    try {
                        article = mSpiderChinaDaily.getPassage(url);
                        String title = article.getTitle();
//                        Log.d("获取到的标题",title);
                        String catalogy = article.getCatalogy();
                        String level = article.getLevel();
                        int difficultRatio = article.getDifficultRatio();
                        StringBuilder body = article.getBody();
                        String time = article.getTime();
//                      标记词性
                        String contentTagged = MyHttpPost.post(body.toString());

                        db.execSQL("INSERT INTO ShareArticle VALUES (?,?,?,?,?,?,?)", new Object[]{url, title, catalogy, contentTagged
                                , level, difficultRatio, time});
//                        Log.d("开始翻译","ooppoo");
                        article = translate.translate(article,dbArticle);
//                      Log.d("向主线程发送对象","ggg");
//                      Log.d("报告向主线程发送翻译好的文章",article.getBody()+"");
//                      调用点击单词选中事件
                        handlerStr(article.getBody().toString());
                        Message message = new Message();
                        message.what = SHOW_DATA;
                        message.obj = article;

//                       注意handler是全局的此处不能再new
                        handler.sendMessage(message);
                    } catch (IllegalArgumentException e) {
                        Log.e("警告", "article arguments is illegal!");
                    } catch (SQLiteConstraintException e1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AcceptShare.this,"数据已经分享过了哦，去已分享看看吧。",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }catch (Exception w){
//                        防止程序挂掉接住所有异常
                        w.printStackTrace();
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("捕获","异常");
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

    public void handlerStr(String str){
        mss = new SpannableString(str);
        List<Integer> enStrList= com.example.liu.autotanslate.activity.Message.getENPositionList(str);
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
                    Toast.makeText(AcceptShare.this,"分享到的文章内容为空！",Toast.LENGTH_SHORT).show();
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
                Log.d("开始赋值文章内容","......");
                mss.setSpan(new MyURLSpan(AcceptShare.this,clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
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
//        if (str.length() > 4000){
//            str = str.substring(0,4000);
//        }
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
