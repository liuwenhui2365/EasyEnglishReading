package com.example.liu.autotanslate.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liu.autotanslate.MyURLSpan;
import com.example.liu.autotanslate.R;
import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.MyHttpPost;
import com.wenhuiliu.EasyEnglishReading.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserTranslate extends Activity {
    private TextView mTitle;
    private ClipboardManager mClipboard;
    private Article mArticle;
    private SpannableString mss;
    private Article article;
    private IntentFilter intentFilter;

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_translate);
//        获取网络的状态
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
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
        mTitle = (TextView)findViewById(R.id.activity_title);
        mTitle.setText("自定义翻译");

        final EditText editText = (EditText)findViewById(R.id.translateContent);
//        这里需要注意不能在这里获取，否则一直为空。

        Button button = (Button)findViewById(R.id.translate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
//                Log.d("UserTranslate报告获取到文本编辑框的内容：",content);
                final Translate translate = new Translate(UserTranslate.this);
                if(content != null) {
                    if (content.length() > 0) {
                        String contentTagged = MyHttpPost.post(content);
                        mArticle = new Article("unknow title", new StringBuilder(contentTagged), "默认");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                注意传入文章的对象的内容为已经标记过词性的。
                                article = translate.translate(mArticle, new DbArticle(UserTranslate.this, "Articles.db", null, 1));
//                                Log.d("UserTranslate报告", "传入处理Str传入之前大小为" + article.getBody().toString().length());
                                handlerStr(article.getBody().toString());
                            }
                        }).start();
                    }else{
                        Toast.makeText(UserTranslate.this,"粘贴的内容为空！！！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(UserTranslate.this,"粘贴的内容为空！！！",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
           Log.e("UerTranslate处理的字符长度","0");
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
                mss.setSpan(new MyURLSpan(UserTranslate.this,clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                TextView textView = (TextView) findViewById(R.id.result);
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

    class NetworkChangeReceiver extends BroadcastReceiver{
        ////  必须要在主配置文件中设置否则会挂掉,优化到工具包中
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            try {
                NetworkInfo networkInfo =  networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    Toast.makeText(context, "连接失败，请检查网络!", Toast.LENGTH_LONG).show();
                }
            }catch (NullPointerException e){
                e.printStackTrace();
//                Log.d("获取网络状态","失败");
            }
        }
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
