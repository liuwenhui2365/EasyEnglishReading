package com.example.liu.autotanslate;

import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class WordClassify extends ActionBarActivity implements PageRefresh.OnLoadListener,ContentAdapter.MyClickListener{

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
    private ContentAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_classify);

        wordNum = getWordNum();
//        Log.d("单词总个数",wordNum+"");
        readWord(loadIndex, perReadNum);
        inflater = LayoutInflater.from(this);
        showWordView();

        contentAdapter = new ContentAdapter(this,wordsView);
        View view = contentAdapter.getView(1,null,listView);

        // 如何获取到控件
//        Log.d("行数",listView.getCount()+"");
//        View view = listView.getAdapter().getView(2,null,null);
//        Log.d("获取到的View",view+"");
//        mTogBtn = (Button)view.findViewById(R.id.TogButton);
//        Log.d("按钮", mTogBtn + "");
//        mTogBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("你点击了","呵呵");
//            }
//        });
//
//
//        TextView textView = (TextView)view.findViewById(R.id.word);
//        textView.setText("hhhhhh");
//            if(mTogBtn != null) {
//        mTogBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // TODO Auto-generated method stub
//
//                if (isChecked) {
//                    //选中
//                    Log.d("选中", "正确");
//                } else {
//                    //未选中
//                    Log.d("没选中", "正确");
//
//                }
//            }
//        });// 添加监听事




//        TextView textView = (TextView)findViewById(R.id.dis_word);
//        Log.d("获取到的单词",textView.getText()+"");
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
        SimpleAdapter mSimpleAdapter = mSimpleAdapter =  new SimpleAdapter(WordClassify.this,wordsView,//需要绑定的数据
                R.layout.wordlistviewitem,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"word"},
                new int[]{R.id.word}
        );



        if(!mSimpleAdapter.isEmpty()) {
            mSimpleAdapter.notifyDataSetChanged();
            listView.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        }



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//              这里的View就是ListView中item的 View
                Log.d("click事件的View",view+"");
                Button button = (Button)findViewById(R.id.TogButton);
                button.setText("哈哈哈哈");

                String word = wordsView.get((int) id).get("word");
                Intent intent = new Intent();
                intent.setAction(word);
                intent.setClass(WordClassify.this, WordMeaning.class);
                startActivity(intent);
            }

        });


    }


        public View getViewByPosition(int pos, ListView listView) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition ) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }


    public void readWord(int firstIndex, int perReadNum){
        HashMap<String, String> wordMeaning = new HashMap<String, String>();
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

//                    Log.d("单词个数",wordsView.size()+"");
//                    for (int i=0; i<wordsView.size(); i++){
//                        Log.d("第"+i+"个单词",wordsView.get(i).get("word"));
//                    }
                }else {
                    db.execSQL("CREATE TABLE words (word VARCHAR PRIMARY KEY,meaning VARCHAR,type VARCHAR)");
                    Log.e("数据库", "表创建成功");
                    Iterator iter = wordMeaning.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        //            往数据库里放数据
                        db.execSQL("INSERT INTO words values(?,?,?)", new String[]{entry.getKey().toString(), entry.getValue().toString(), "unknown"});
                        //                Log.d("单词：", entry.getKey().toString());
                        //                Log.d("意思：", entry.getValue().toString());
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
//        Toast的用法

    @Override
    public void myOnClick(String word) {
        Toast.makeText(WordClassify.this,
                      "listview的内部的按钮被点击了！内容是-->"+word
                                    , Toast.LENGTH_SHORT).show();
    }
}
