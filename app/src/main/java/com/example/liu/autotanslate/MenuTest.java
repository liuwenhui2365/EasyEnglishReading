package com.example.liu.autotanslate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MenuTest extends ActionBarActivity implements OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private HashMap<String,String> item = null;
    private ArrayList<HashMap<String,String>> menuLists = new ArrayList<>();
    private ListAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mTitle = "即将关闭";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.menu_test);

        mTitle = (String) getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout1);
        mDrawerList = (ListView) findViewById(R.id.left_drawer1);

        item = new HashMap<>();
        item.put("item","单词分类");
        menuLists.add(item);
        item = new HashMap<>();
        item.put("item","设置");
        menuLists.add(item);
    //    adapter = new ArrayAdapter<String>(this,R.layout.wordlistviewitem);
        adapter = new SimpleAdapter(MenuTest.this,menuLists,//需要绑定的数据
                R.layout.menulistitem,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[] {"item"},
                new int[] {R.id.menu}
        );
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
            R.drawable.ab, R.string.open,
            R.string.close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle("个人中心");
                    invalidateOptionsMenu(); // Call onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    getSupportActionBar().setTitle(mTitle);
                    invalidateOptionsMenu();
                    }
            };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //开启ActionBar上APP ICON的功能
        try {
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
             getSupportActionBar().setHomeButtonEnabled(true);
        }catch (NullPointerException w){
            w.printStackTrace();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_search1).setVisible(!isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       //将ActionBar上的图标与Drawer结合起来
        if (mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()) {
           case R.id.action_search:
               Intent intent = new Intent();
               intent.setAction("android.intent.action.VIEW");
               Uri uri = Uri.parse("http://www.baidu.com");
               intent.setData(uri);
               startActivity(intent);
               break;
           }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //需要将ActionDrawerToggle与DrawerLayout的状态同步
        //将ActionBarDrawerToggle中的drawer图标，设置为ActionBar中的Home-Button的Icon
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
        // 动态插入一个Fragment到FrameLayout当中(有问题暂时不用）
//        fragment_content contentFragment = new fragment_content();
//        Bundle args = new Bundle();
//        args.putString("text", menuLists.get(position).get("item"));
//        contentFragment.setArguments(args);
//
//        FragmentManager fm = getFragmentManager();
//        fm.beginTransaction().replace(R.id.content_frame, contentFragment)
//                                                     .commit();
//
//        mDrawerLayout.closeDrawer(mDrawerList);
//       点击进入第一行菜单栏对应的fragment
        if(position == 0){
            Intent intent = new Intent();
            intent.setClass(MenuTest.this,WordClassify.class);
            startActivity(intent);
        }
    }
}