package com.example.liu.autotanslate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HeaderView extends LinearLayout {

    public final static int STATE_NORMAL = 0;
    public final static int STATE_WILL_RELEASE = 1;
    public final static int STATE_REFRESHING = 2;
    private int mState = STATE_NORMAL;

    private View mHeader = null;
    private ImageView mArrow = null;
    private ProgressBar mProgressBar = null;
    private TextView mRefreshTips = null;
    //private TextView mRefreshLastTime = null;
    private RotateAnimation mRotateUp = null;
    private RotateAnimation mRotateDown = null;
    private final static int ROTATE_DURATION = 250;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView(context);
    }

    private void initHeaderView(Context context){
        LinearLayout.LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        mHeader = LayoutInflater.from(context).inflate(R.layout.headerview, null);
        addView(mHeader, lp);
        setGravity(Gravity.BOTTOM);

        mArrow = (ImageView) mHeader.findViewById(R.id.ivArrow);
        mProgressBar = (ProgressBar) mHeader.findViewById(R.id.pbWaiting);
        mRefreshTips = (TextView) mHeader.findViewById(R.id.refresh_tips);
        //mRefreshLastTime = (TextView) mHeader.findViewById(R.id.refresh_last_time);

        mRotateUp = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUp.setDuration(ROTATE_DURATION);
        mRotateUp.setFillAfter(true);

        mRotateDown = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDown.setDuration(ROTATE_DURATION);
        mRotateDown.setFillAfter(true);
    }

    public void setHeaderState(int state){
        if(mState == state){
            return;
        }

        mArrow.clearAnimation();
        if(state == STATE_REFRESHING){
            mProgressBar.setVisibility(View.VISIBLE);
            mArrow.setVisibility(View.GONE);
        }else{
            mProgressBar.setVisibility(View.GONE);
            mArrow.setVisibility(View.VISIBLE);
        }

        switch(state){
            case STATE_NORMAL:
                mArrow.startAnimation(mRotateDown);
                mRefreshTips.setText(R.string.refresh);
                break;

            case STATE_WILL_RELEASE:
                mArrow.startAnimation(mRotateUp);
                mRefreshTips.setText(R.string.refresh);
                break;

            case STATE_REFRESHING:
                mRefreshTips.setText(R.string.refresh);
                break;

            default:
                break;
        }

        mState = state;
    }

    public int getCurrentState(){
        return mState;
    }

    public void setHeaderHeight(int height){
        if(height <= 0){
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
        lp.height = height;
        mHeader.setLayoutParams(lp);
    }
    public int getHeaderHeight(){
        return mHeader.getHeight();
    }
}