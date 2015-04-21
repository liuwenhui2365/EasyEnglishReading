package com.example.liu.autotanslate;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.MyApplication;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/2/21.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> viewList;

    private MyGifView myGifView;

    public ViewPagerAdapter(ArrayList<View> viewList) {
        this.viewList=viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
//      初始化动画界面注意添加在后面 TODO由于适配问题暂时放弃

//        if (position == 0){
////            Log.d(getClass().getSimpleName(),"适配器进来了");
//            myGifView = (MyGifView)viewList.get(position).findViewById(R.id.gifView);
////          防止空指针异常，在运行完之后会再次进来
//            if (myGifView != null) {
//                myGifView.setGif(R.drawable.welcome1);
//            }
//        }

        ((ViewPager) container).addView(viewList.get(position));
        return viewList.get(position);
    }

}
