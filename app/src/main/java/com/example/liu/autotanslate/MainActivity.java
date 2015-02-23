package com.example.liu.autotanslate;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


public class MainActivity extends Activity {


    private ArrayList<ImageView> images = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置无标题窗口
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        LayoutInflater inflater=getLayoutInflater();
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this,Classify.class);
//        MainActivity.this.startActivity(intent);
//       添加图片页
        ArrayList<View> views=new ArrayList<View>();
        views.add(inflater.inflate(R.layout.item1, null));
        views.add(inflater.inflate(R.layout.item2, null));
        views.add(inflater.inflate(R.layout.item3, null));

//      添加圆点
        images.add((ImageView)findViewById(R.id.iv_01));
        images.add((ImageView)findViewById(R.id.iv_02));
        images.add((ImageView)findViewById(R.id.iv_03));

        ViewPager viewPager=(ViewPager)findViewById(R.id.vpage);
        viewPager.setAdapter(new ViewPagerAdapter(views));
        viewPager.setOnPageChangeListener(new ViewPagerChangeListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < images.size(); i++) {
                images.get(i).setImageResource(R.drawable.gray);
                if (arg0==i) {
                    images.get(i).setImageResource(R.drawable.blue);
                }
            }

            if(arg0==images.size()-1){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Classify.class);
                startActivity(intent);
                finish();
            }
        }

    }
}
