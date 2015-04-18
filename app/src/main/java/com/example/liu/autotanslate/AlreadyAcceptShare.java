package com.example.liu.autotanslate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.Words;

import java.io.BufferedReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class AlreadyAcceptShare extends ActionBarActivity implements PageRefresh.OnLoadListener{

    ArrayList<HashMap<String, Object>> itemEntities = new ArrayList<HashMap<String, Object>>();
    private LayoutInflater inflater = null;
    private PageRefresh listview;
    private SimpleAdapter mSimpleAdapter;
    private final int REQ_CODE = 1;
    private DbArticle dbArticle;
//   删除位置
    int location = 0;
    private  int loadIndex =0;
    //  每个页面显示的行数
    private int perReadNum = 6;
    private int shareArticleNum = 0;
    View footer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_accept_share);

        shareArticleNum = GetShareArticleNum();
        InitView();

    }


    public int GetShareArticleNum(){
        SQLiteDatabase db = null;
        Cursor c = null;
        int count = 0;
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) from ShareArticle", null);
            if (c.moveToNext()) {
                count = c.getInt(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (c != null){
                c.close();
            }

            if(db != null){
                db.close();
            }

            return count;
        }

    }

    public void InitView(){
        loadIndex = shareArticleNum;
        readShareArticle(shareArticleNum - perReadNum, shareArticleNum);
        //        如果数据库为空 添加默认页面
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        if (itemEntities.isEmpty()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
//            TODO 以后可以加图片
//            map.put("ItemImage", R.drawable.ic_launcher);//加入图片
            map.put("title", "还没有分享到的文章");
            map.put("date", time);
            itemEntities.add(map);
        }
        inflater = LayoutInflater.from(this);

        showListView(itemEntities);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                try {
                    String title = (String) itemEntities.get((int) id).get("title");
                    Intent intent = new Intent();
                    intent.setClass(AlreadyAcceptShare.this, ShareMessage.class);
                    intent.setAction(title);
                    startActivity(intent);

                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                    Toast.makeText(AlreadyAcceptShare.this, "点其他哦！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //      长按删除如果为false就会执行itemClick事件
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                location =(int)id;
                Intent intent = new Intent();
                intent.setClass(AlreadyAcceptShare.this, Web.class);
                startActivityForResult(intent, REQ_CODE);
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE){
            if (resultCode == RESULT_OK){
//                Log.d(TAG,"删除"+location);
//              注意先后顺序
                deleteArticle(location);
                itemEntities.remove(location);
                mSimpleAdapter.notifyDataSetChanged();
                Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteArticle(int pos) {
        //        想想为什么要减一
        String title = (String) itemEntities.get(pos).get("title");
//        Log.d(TAG,"删除标题"+title);
        SQLiteDatabase db = null;
        Cursor myCursor = null;
        try {
            db = dbArticle.getWritableDatabase();
//          判断表是否存在
            myCursor = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='ShareArticle'", null);
            if (myCursor.moveToNext()) {
                int count = myCursor.getInt(0);
                if (count > 0) {
                    db.execSQL("DELETE FROM ShareArticle WHERE title = ?", new Object[]{title});
                }
            }
        }catch (Exception e) {
            Log.d(AlreadyAcceptShare.class.getSimpleName(),"捕获异常");
        }finally {
            if(myCursor!=null){
                myCursor.close();
            }

            if (db!=null){
                db.close();
            }
        }
    }

    private void showListView(ArrayList<HashMap<String, Object>> itemEntities) {
        listview = (PageRefresh) findViewById(R.id.sharepage);
        listview.setOnLoadListener(this);
        mSimpleAdapter = new SimpleAdapter(AlreadyAcceptShare.this,itemEntities,//需要绑定的数据
                R.layout.lsitviewitem,//每一行的布局//动态数组中的数据源的键对应到定义布局的View中(图片先不加）
                new String[] {"title", "date"},
                new int[] {R.id.title,R.id.date}
        );
        if(!mSimpleAdapter.isEmpty()) {
            mSimpleAdapter.notifyDataSetChanged();
            listview.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        }
    }

    public void readShareArticle(int firstIndex, int perReadNum){
        ArrayList<Article> dbal = new ArrayList<Article>();
        SQLiteDatabase db = null;
        Article article = null;
        Cursor c = null;
        String title = null;
        String time = null;
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            if (shareArticleNum > 0) {
                c = db.rawQuery("SELECT * FROM ShareArticle limit " +firstIndex +"," + perReadNum, null);
                while (c.moveToNext()) {
                    String url = c.getString(c.getColumnIndex("url"));
//                    Log.d("分享数据库","开始读取。。。");
                    title = c.getString(c.getColumnIndex("title"));
                    String catalogy = c.getString(c.getColumnIndex("catalogy"));
                    String body = c.getString(c.getColumnIndex("body"));
                    String level = c.getString(c.getColumnIndex("level"));
                    int difficultRatio = c.getInt(c.getColumnIndex("difficultRatio"));
                    time = c.getString(c.getColumnIndex("time"));
                    StringBuilder bodys = new StringBuilder(body);
                    article = new Article(title, bodys, catalogy);
                    article.setDifficultRatio(100);
                    article.setLevel(level);
                    article.setTime(time);
                    dbal.add(article);
                }
            }else {
                Toast.makeText(this,"数据读取完成",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null){
                db.close();
            }
            if(!dbal.isEmpty()) {
                for (int i = dbal.size() - 1; i>= 0; i-- ) {
                    article = dbal.get(i);
                    title = article.getTitle();
                    time = article.getTime();
                    HashMap<String, Object> map = new HashMap<String, Object>();
//                    map.put("ItemImage", R.drawable.ic_launcher);//加入图片
                    map.put("title", title);
                    map.put("date", time);
                    itemEntities.add(map);
                    //Log.d("从dbal中读取db", "title=>" + title + ",time" + time);
                }
                dbal.clear();
            }else{
                Toast.makeText(this,"当前数据库为空！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //  上拉获取数据
    @Override
    public void onLoad() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run () {
                // 获取更多数据
                loadData();
                // 更新listview显示；
                showListView(itemEntities);
                // 通知listview加载完毕
                listview.loadComplete();
            }
        }, 1000);
    }

    private void loadData() {
        footer = inflater.inflate(R.layout.footview, null);
        TextView lt = (TextView) findViewById(R.id.loader);

        loadIndex = loadIndex - perReadNum;
        int fromIndex = loadIndex - perReadNum + 1;
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (loadIndex < 0) {
            loadIndex = 0;
        }
        if (loadIndex != 0) {
            readShareArticle(fromIndex, perReadNum);
        } else {
            Toast.makeText(this, "已读完了哦！", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_already_accept_share, menu);
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
