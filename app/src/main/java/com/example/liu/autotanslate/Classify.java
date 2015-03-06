package com.example.liu.autotanslate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class Classify extends ActionBarActivity {

    private ActionBar actionbar;
    private ViewPager Viewpage;
    private Button button;
    /**ViewPager包含的Fragment集合**/
//    private ArrayList<Fragment> fragments;
    /**ActionBar上的Tab集合**/
    private ArrayList<ActionBar.Tab> tabs;
    /**当前页**/
    protected int currentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

//        Utils.showOverflowMenu(this);//如果手机有menu键也显示flowMenu
        initViewPager();//初始化ViewPager要在初始化initTab之前，否则会出错
        initTab();
        //   ton.setOnClickListener(listener);
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
//            Utils.showToast(this, "您点击了刷新菜单", Toast.LENGTH_SHORT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        // TODO Auto-generated method stub
        LayoutInflater inflater=getLayoutInflater();
        Viewpage = (ViewPager) findViewById(R.id.vpage);

//      为每个Tab标签添加相应的布局文件
        ArrayList<View> views=new ArrayList<View>();
        views.add(inflater.inflate(R.layout.classifyitem1, null));
        views.add(inflater.inflate(R.layout.classifyitem2, null));
        views.add(inflater.inflate(R.layout.classifyitem3, null));
        Viewpage.setAdapter(new ViewPagerAdapter(views));
//        viewPager.setOnPageChangeListener(new ViewPagerChangeListener());
        //初始化ViewPager显示的页面集合
//        fragments = new ArrayList<Fragment>();
//        BaseFragment fragment1=BaseFragment.newInstance(BaseFragment.LOAD_FRAGMENT_1);
//        BaseFragment fragment2=BaseFragment.newInstance(BaseFragment.LOAD_FRAGMENT_2);
//        fragments.add(fragment1);
//        fragments.add(fragment2);
//        //设置ViewPager adapter
//        BaseFragmentPagerAdapter adapter=new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//        vpContent.setAdapter(adapter);
        Viewpage.setCurrentItem(0);//默认显示第一个页面
        //监听ViewPager事件
        Viewpage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            position表示第几页从0开始
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                if(position==0) {
                    getLayoutInflater().inflate(R.layout.classifyitem1, null);
                    button = (Button) findViewById(R.id.button1);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button2);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button3);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button4);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button5);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button6);
                    button.setOnClickListener(listener);
                }else if(position==1){
                    getLayoutInflater().inflate(R.layout.classifyitem2, null);
                    button = (Button) findViewById(R.id.button21);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button22);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button23);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button24);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button25);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button26);
                    button.setOnClickListener(listener);
                }else{
//                    获取当前页面的VIew
                    getLayoutInflater().inflate(R.layout.classifyitem3, null);
                    button = (Button) findViewById(R.id.button31);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button32);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button33);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button34);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button35);
                    button.setOnClickListener(listener);
                    button = (Button) findViewById(R.id.button36);
                    button.setOnClickListener(listener);
                }

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                actionbar.selectTab(tabs.get(position));//当滑动页面结束让ActionBar选择指定的Tab

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 初始化Tab
     */
    private void initTab() {
        tabs = new ArrayList<ActionBar.Tab>();
        actionbar = getSupportActionBar();//获取v7兼容包中的ActionBar
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab tab1 = actionbar.newTab();//创建一个tab实例
        ActionBar.Tab tab2 = actionbar.newTab();
        ActionBar.Tab tab3 = actionbar.newTab();
        tab1.setTag(0);//为Tab设置Tag，用于标识Tab的位置
        tab1.setText("初级");
        tab2.setTag(1);
        tab2.setText("中级");
        tab3.setTag(2);
        tab3.setText("高级");
        tab1.setTabListener(tabListener);//为Tab设置监听(这一步是必须的，不然系统会不报错)
        tab2.setTabListener(tabListener);
        tab3.setTabListener(tabListener);
//      添加到链表里面
        tabs.add(tab1);
        tabs.add(tab2);
        tabs.add(tab3);
        actionbar.addTab(tab1);//将Tab添加ActionBar上
        actionbar.addTab(tab2);
        actionbar.addTab(tab3);
    }

    /**
     * ActionBar的Tab监听器
     */
    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            //当选中了指定的Tab后让VeiwPager滑动到指定页面
            Viewpage.setCurrentItem((Integer) tab.getTag());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }
    };

    View.OnClickListener listener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            button = (Button)v;
            switch(button.getId())
            {
                case R.id.button1:
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级科技");
                    startActivity(intent);
                    break;
                case R.id.button2:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级健康");
                    startActivity(intent);
                    break;
                case R.id.button3:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级经济");
                    startActivity(intent);
                    break;
                case R.id.button4:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级历史");
                    startActivity(intent);
                    break;
                case R.id.button5:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级理财");
                    startActivity(intent);
                    break;
                case R.id.button6:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("初级娱乐");
                    startActivity(intent);
                    break;


                case R.id.button21:
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("中级科技");
                    startActivity(intent);
                    break;
                case R.id.button22:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("中级健康");
                    startActivity(intent);
                    break;
                case R.id.button23:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("中级经济");
                    startActivity(intent);
                    break;
                case R.id.button24:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("中级历史");
                    startActivity(intent);
                    break;
                case R.id.button25:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("中级理财");
                    startActivity(intent);
                    break;
                case R.id.button26:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("中级娱乐");
                    startActivity(intent);
                    break;

                case R.id.button31:
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("高级科技");
                    startActivity(intent);
                    break;
                case R.id.button32:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("高级健康");
                    startActivity(intent);
                    break;
                case R.id.button33:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("高级经济");
                    startActivity(intent);
                    break;
                case R.id.button34:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("高级历史");
                    startActivity(intent);
                    break;
                case R.id.button35:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("高级理财");
                    startActivity(intent);
                    break;
                case R.id.button36:
                    intent = new Intent();
                    intent.setClass(Classify.this,Refresh.class);
                    intent.setAction("高级娱乐");
                    startActivity(intent);
                    break;
            }
        }
    };
}
