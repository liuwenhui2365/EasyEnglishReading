package com.example.liu.autotanslate;

import com.example.liu.autotanslate.util.SystemUiHider;
import com.wenhuiliu.EasyEnglishReading.Translate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Web extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        textView = (TextView)findViewById(R.id.delete);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_OK);
                finish();
            }
        });

        textView = (TextView)findViewById(R.id.cancel);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

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

//        FileOutputStream out = null;
//        byte []  buffer = new byte[1024];
//        int len = 0;
//        try {
//            out = new FileOutputStream(OBJ+"welcome.html");
//            while ((len = ins.read(buffer)) != -1){
//                out.write(buffer,0,len);
//            }
//
//            ins = getResources().openRawResource(R.raw.welcome1);
//            out = new FileOutputStream(OBJ+"welcome1");
//            while ((len = ins.read(buffer))!= -1){
//                out.write(buffer,0,len);
//            }
//        } catch (IOException e) {
//            Log.e(Web.class.getSimpleName(),"拷贝异常");
//        }
//        Log.d(Web.class.getSimpleName(),"拷贝完成");
//        webView.loadDataWithBaseURL(null,"<img src="+OBJ+
//                "welcome1"+"/>", "text/html", "utf-8", null);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return  false;
    }
}
