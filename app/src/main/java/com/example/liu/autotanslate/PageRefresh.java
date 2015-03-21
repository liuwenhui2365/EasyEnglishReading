package com.example.liu.autotanslate;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

public class PageRefresh extends ListView implements OnScrollListener{
    //底部View
    private View footerView;
    private TextView textView;
    //ListView item个数
    int totalItemCount;// = 0;
    //最后可见的Item
    int lastVisibleItem;// = 0;
    //是否加载标示
    boolean isLoading = false;

    public PageRefresh(Context context) {
        super(context);
        initView(context);
    }

    public PageRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public PageRefresh(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化ListView
     */
    private void initView(Context context){
        LayoutInflater mInflater = LayoutInflater.from(context);
        footerView = mInflater.inflate(R.layout.footer, null);
        footerView.findViewById(R.id.wlist).setVisibility(View.GONE);
        //添加底部View
        this.addFooterView(footerView);
        this.setOnScrollListener(this);
//        Log.d("setOnScrollListener", this.toString());

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        Log.d("状态改变","准备改变");
        //当滑动到底端，并滑动状态为 not scrolling
        if(lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE){
            if(!isLoading){
                isLoading = true;
                //设置可见
                footerView.setVisibility(View.VISIBLE);
//                Log.d("状态改变","已经改变");

                //加载数据
                onLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
//        Log.d("onScroll lastVisibaleItem", String.valueOf(lastVisibleItem));

    }

    OnLoadListener onLoadListener;
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    /**
     * 加载数据接口
     * @author Administrator
     *
     */
    public interface OnLoadListener {
        void onLoad();
    }

    /**
     * 数据加载完成
     */
    public void loadComplete(){
        footerView.setVisibility(View.GONE);
        isLoading = false;
        this.invalidate();
    }

}
