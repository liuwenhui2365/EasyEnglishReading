package com.example.liu.autotanslate;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class Classify extends ActionBarActivity {

    private MyImageButton btDataConfig = null;
    private MyImageButton btDataConfig1 = null;
    private MyImageButton btDataConfig2 = null;
    private MyImageButton btDataConfig3 = null;
    private MyImageButton btDataConfig4 = null;
    private MyImageButton btDataConfig5 = null;
    private MyImageButton btDataConfig6 = null;
    private MyImageButton btDataConfig7 = null;




    private LinearLayout llbtDataConfig = null;  //main布局中包裹本按钮的容器
    private LinearLayout llbtDataConfig1 = null;
    private LinearLayout llbtDataConfig2 = null;
    private LinearLayout llbtDataConfig3 = null;
    private LinearLayout llbtDataConfig4 = null;
    private LinearLayout llbtDataConfig5 = null;
    private LinearLayout llbtDataConfig6 = null;
    private LinearLayout llbtDataConfig7 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

        btDataConfig = new MyImageButton(this, R.drawable.ic_launcher, R.string.classify);
        btDataConfig1 = new MyImageButton(this,R.drawable.ic_launcher,R.string.classify);
        btDataConfig2 = new MyImageButton(this, R.drawable.ic_launcher, R.string.classify);
        btDataConfig3 = new MyImageButton(this,R.drawable.ic_launcher,R.string.classify);
        btDataConfig4 = new MyImageButton(this, R.drawable.ab, R.string.classify);
        btDataConfig5 = new MyImageButton(this,R.drawable.ab,R.string.classify);
        btDataConfig6 = new MyImageButton(this, R.drawable.ab, R.string.classify);
        btDataConfig7 = new MyImageButton(this,R.drawable.ab,R.string.classify);
        //获取包裹本按钮的容器
        llbtDataConfig = (LinearLayout) findViewById(R.id.textView);
        llbtDataConfig1 = (LinearLayout) findViewById(R.id.textView1);
        llbtDataConfig2 = (LinearLayout) findViewById(R.id.textView2);
        llbtDataConfig3 = (LinearLayout) findViewById(R.id.textView3);
        llbtDataConfig4 = (LinearLayout) findViewById(R.id.textView5);
        llbtDataConfig5 = (LinearLayout) findViewById(R.id.textView6);
        llbtDataConfig6 = (LinearLayout) findViewById(R.id.textView7);
        llbtDataConfig7 = (LinearLayout) findViewById(R.id.textView8);


        //将我们自定义的Button添加进这个容器
        llbtDataConfig.addView(btDataConfig);
        llbtDataConfig1.addView(btDataConfig1);
        llbtDataConfig2.addView(btDataConfig2);
        llbtDataConfig3.addView(btDataConfig3);
        llbtDataConfig4.addView(btDataConfig4);
        llbtDataConfig5.addView(btDataConfig5);
        llbtDataConfig6.addView(btDataConfig6);
        llbtDataConfig7.addView(btDataConfig7);
        //设置按钮的监听
        btDataConfig.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig.setText("按钮被点击过了");
                Intent intent = new Intent();
                intent.setClass(Classify.this,Listview.class);
                startActivity(intent);
            }
        });

        btDataConfig1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig1.setText("按钮被点击过了");
            }
        });
        btDataConfig2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig2.setText("按钮被点击过了");
            }
        });

        btDataConfig3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig3.setText("按钮被点击过了");
            }
        });
        btDataConfig4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig4.setText("按钮被点击过了");
            }
        });

        btDataConfig5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig5.setText("按钮被点击过了");
            }
        });
        btDataConfig6.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig6.setText("按钮被点击过了");
            }
        });

        btDataConfig7.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataConfig7.setText("按钮被点击过了");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_classify, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
