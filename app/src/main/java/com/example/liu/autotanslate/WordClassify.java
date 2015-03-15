package com.example.liu.autotanslate;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;

import java.util.ArrayList;
import java.util.HashMap;


public class WordClassify extends ActionBarActivity implements PageRefresh.OnLoadListener {

    ArrayList<HashMap<String,String>> wordsView = new ArrayList<HashMap<String, String>>();
    PageRefresh listView = null;
    private PageRefresh pageRefresh;
    private DbArticle dbArticle;
    private  int wordNum =0;
    private int loadIndex = 0;
    private int perReadNum = 30;
    LayoutInflater inflater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_classify);

        wordNum = getWordNum();
        Log.d("单词总个数",wordNum+"");
        readWord(loadIndex, perReadNum);
        pageRefresh = (PageRefresh)findViewById(R.id.page);
        pageRefresh.setOnLoadListener(this);
        pageRefresh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        inflater = LayoutInflater.from(this);
        showWordView();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_classify, menu);
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

    public void showWordView(){
//      调用定义好的ListView类的方法（debug了好长时间）
        listView = (PageRefresh) findViewById(R.id.page);
        listView.setOnLoadListener(this);
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(WordClassify.this,wordsView,//需要绑定的数据
                R.layout.wordlistviewitem,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"word"},
                new int[]{R.id.word}
        );
        if(!mSimpleAdapter.isEmpty()) {
            mSimpleAdapter.notifyDataSetChanged();
            listView.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        }
    }

    public void readWord(int firstIndex, int perReadNum){
        SQLiteDatabase db = null;
        String word = null;
        Cursor c = null;
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                 如果表存在
                    c = db.rawQuery("SELECT word FROM words limit ?,?", new String[]{firstIndex + "", perReadNum + ""});
                    while (c.moveToNext()) {
                        HashMap<String,String> wordMap = new HashMap<String,String>();
                        word = c.getString(c.getColumnIndex("word"));
//                        Log.d("从数据库中读取word", "word=>" + word);
                        wordMap.put("word", word);
                        wordsView.add(wordMap);
                    }

                    Log.d("单词个数",wordsView.size()+"");
                    for (int i=0; i<wordsView.size(); i++){
                        Log.d("第"+i+"个单词",wordsView.get(i).get("word"));
                    }
                }
            }else {
                //TODO 添加弹框提示数据库读取完毕
                Log.w("警告", "数据库读取完毕");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null){
                db.close();
            }
        }
    }

    @Override
    public void onLoad() {
//        header = inflater.inflate(R.layout.top, null);
        TextView lt = (TextView) findViewById(R.id.tv);
//      考虑索引问题
        loadIndex = loadIndex + perReadNum;
        int fromIndex = loadIndex;
        if(fromIndex >= wordNum)
        {
            fromIndex = wordNum;
            lt.setText("已读完。");

        }else if (fromIndex + perReadNum >= wordNum) {
            fromIndex = wordNum - perReadNum;
        }else {
            readWord(fromIndex, perReadNum);
//            Log.d("上拉刷新单词个数",wordsView.size()+"");
        }

        Log.d("上拉刷新 begin" + fromIndex, "end" + fromIndex+perReadNum);

        pageRefresh.loadComplete();

    }

    public int getWordNum(){
        SQLiteDatabase db = null;
        Cursor c = null;
        int count = 0;
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) from 'words'", null);
            if (c.moveToNext()) {
                count = c.getInt(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null){
                db.close();
            }

            return count;
        }

    }
}
