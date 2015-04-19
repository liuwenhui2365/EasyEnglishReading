package com.example.liu.autotanslate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.Words;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class WordClassify extends ActionBarActivity implements PageRefresh.OnLoadListener{

    ArrayList<HashMap<String,String>> wordsView = new ArrayList<HashMap<String, String>>();
    PageRefresh listView = null;
    ListView lv = null;
    TextView textView = null;
    private LinearLayout linearLayout = null;
    Button mTogBtn = null;
    private DbArticle dbArticle;
    private  int wordNum =0;
    private int loadIndex = 0;
    private int perReadNum = 30;
    LayoutInflater inflater = null;
    private WordAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_classify);
//      注意顺序，否则在初始化时只能读取一次，只有再进去才能读
        readWord(loadIndex, perReadNum);
        wordNum = getWordNum();
//        Log.d("单词总个数",wordNum+"");
        inflater = LayoutInflater.from(this);
        showWordView();

        // 如何获取到控件(测试了很多方法比如inflatr\byId\Click事件找到需要的View\最终原因么有调用自己写的适配器）
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

//      刚开始一直是SimpleAdapter没有改适配器
//      最关键的是要把自定义的适配器加载到ListView这样才能按钮生效！（研究四天）
        contentAdapter = new WordAdapter(WordClassify.this,//需要绑定的数据
                R.layout.wordlistviewitem,wordsView,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"word"},
                new int[]{R.id.word}
        );
//      注意是不为空的时候
        if(!contentAdapter.isEmpty()){
            contentAdapter.notifyDataSetChanged();
            listView.setAdapter(contentAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//              这里的View就是ListView中item的 View
                String word = wordsView.get((int) id).get("word");
                Intent intent = new Intent();
                intent.setAction(word);
                intent.setClass(WordClassify.this, WordMeaning.class);
                startActivity(intent);
            }

        });
    }

    public void readWord(int firstIndex, int perReadNum){
        HashMap<String, String> wordMeaning = new HashMap<String, String>();
        SQLiteDatabase db = null;

        String [] words = new String[]{};
        BufferedReader reader = null;
        InputStream input = null;
        BufferedWriter writer = null;
        OutputStream output = null;
        String word = null;
        Cursor c = null;
//        获取系统默认文件路径
//        Log.d("路径：",this.getFilesDir()+"");
        String fileList [] =  this.fileList();

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
                }else {
//                  从变量中获取（放弃不要了）
                    db.execSQL("CREATE TABLE words (word VARCHAR PRIMARY KEY,meaning VARCHAR,type VARCHAR)");
                    Log.d("数据库", "单词表创建成功");
        //        TODO 从文件中获取
        //            往数据库里放数据(默认为认识的）
                    InputStream inputStream = getResources().openRawResource(R.raw.sortwordlist);
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String[] type = line.split(":");
//                      有些单词没有意思
                        if (type.length == 2) {
                            db.execSQL("INSERT INTO words values(?,?,?)", new String[]{type[0], type[1], "unknow"});
                        }
                    }

                    c = db.rawQuery("SELECT word FROM words limit ?,?", new String[]{firstIndex + "", perReadNum + ""});
                    while (c.moveToNext()) {
                        HashMap<String,String> wordMap = new HashMap<String,String>();
                        word = c.getString(c.getColumnIndex("word"));
//                        Log.d("从数据库中读取word", "word=>" + word);
                        wordMap.put("word", word);
                        wordsView.add(wordMap);
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
        TextView lt = (TextView) findViewById(R.id.loader);
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

//        Log.d("上拉刷新 begin" + fromIndex, "end" + fromIndex+perReadNum);

        listView.loadComplete();

    }

    public int getWordNum() {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }

            return count;
        }
    }
}
