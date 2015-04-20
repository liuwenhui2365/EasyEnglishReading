package com.example.liu.autotanslate;

import com.example.liu.autotanslate.util.SystemUiHider;
import com.wenhuiliu.EasyEnglishReading.MyApplication;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Web extends Activity{

    private TextView textView;
    private MyGifView myGifView;

    private String str = null;
    private SpannableString mss = null;
    private String clickStr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        textView = (TextView) findViewById(R.id.delete);
        textView.setText("Hello ! 呵呵 what about you?");
        str = (String)textView.getText();
        handlerStr(str);
//        WebView webView = (WebView)findViewById(R.id.web1);

//        InputStream ins = getResources().openRawResource(R.raw.welcome);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
//        String line = null;
//        String data = null;
//        try {
//            while ((line = reader.readLine()) != null){
//                data = data + line;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(Web.class.getSimpleName(),"读出异常");
//        }
//        webView.loadData(data,"text/html","utf-8");

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
    public void setLink(int start,int end,String clickStr) {
//      msp.setSpan(new URLSpan("http://www.baidu.com"), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//      特别注意传入上下文的时候，把该类作为上下文传进去，不能传全局的，否则报空指针异常
        Log.d(getClass().getSimpleName()+"报告","开始创建点击事件的对象");
        mss.setSpan(new MyURLSpan(this,clickStr), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView = (TextView) findViewById(R.id.delete);
        textView.setText(mss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
//            }
//        });
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}

