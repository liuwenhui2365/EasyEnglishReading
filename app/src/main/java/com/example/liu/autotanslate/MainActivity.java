package com.example.liu.autotanslate;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.MyHttpPost;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<ImageView> images = new ArrayList<ImageView>();
    private MyGifView myGifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置无标题窗口
//      requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
//      开启新线程执行初始化单词
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();

//        Toast.makeText(this,"初始化完成",Toast.LENGTH_SHORT).show();
    }

    private void initView(){
//       添加图片页
        ArrayList<View> views=new ArrayList<View>();
        LayoutInflater inflater=getLayoutInflater();
        views.add(inflater.inflate(R.layout.item1, null));
        views.add(inflater.inflate(R.layout.item2, null));
        views.add(inflater.inflate(R.layout.item3, null));

        View view = inflater.inflate(R.layout.item1,null);



//      添加圆点
        images.add((ImageView)findViewById(R.id.iv_01));
        images.add((ImageView)findViewById(R.id.iv_02));
        images.add((ImageView)findViewById(R.id.iv_03));

        ViewPager viewPager=(ViewPager)findViewById(R.id.vpage);
        viewPager.setAdapter(new ViewPagerAdapter(views));
        viewPager.setOnPageChangeListener(new ViewPagerChangeListener());
    }

//    初始化单词表
    private void initData() {
        DbArticle dbArticle = null;
        SQLiteDatabase db = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
//        计时统计耗时
        long start = System.currentTimeMillis();

        Cursor c = null;
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
//            Log.d(TAG, "开始初始化单词表");
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count <= 0) {
                    //                Log.d("获取个数：",""+wordMeaning.size());
                    db.execSQL("CREATE TABLE words (word VARCHAR PRIMARY KEY,meaning VARCHAR,type VARCHAR)");
//                    Log.e(TAG, "单词表创建成功");
//                  记住这种读取方式以后会用到！！！
                    inputStream = getResources().openRawResource(R.raw.sortwordlist);
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String[] type = line.split(":");
//                      有些单词没有意思
                        if (type.length == 2) {
                            db.execSQL("INSERT INTO words values(?,?,?)", new String[]{type[0], type[1], "unknow"});
                        }
                    }
                }
            }

//            Log.d(TAG, "开始初始化词性表");
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='wordsTag'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count <= 0) {
                    //                Log.d("获取个数：",""+wordMeaning.size());
                    db.execSQL("CREATE TABLE wordsTag (tag VARCHAR PRIMARY KEY,type VARCHAR)");
//                    Log.e("数据库", "单词词性编码表创建成功");

                    inputStream = getResources().openRawResource(R.raw.wordtag);
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String[] type = line.split(" ");
//                      防止越界！
                        if (type.length == 2) {
//                            Log.d("词性" + type[0], "代码" + type[1]);
                            db.execSQL("INSERT INTO wordsTag values(?,?)", new String[]{type[0], type[1]});
                        }
                    }
                }
            }
//            标记单词需要拷贝后来还是不能用，放弃从网络获取
        }catch (FileNotFoundException w) {
            Log.e("文件没有找到", "-------");
        }catch (IOException e1){
            Log.e("IOException","hh ");
        }catch (Exception e){
//            Toast.makeText(this,"表初始化异常",Toast.LENGTH_SHORT).show();
            Log.e("初始化异常","-------");
            e.printStackTrace();
        }finally {
            if (c != null) {
                c.close();
            }

            if (db != null) {
                db.close();
            }
        }
//        Log.d("数据初始化", "完成");

        long end = System.currentTimeMillis();
        Log.d(TAG+"报告","总耗时"+(end - start)/1000);
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
