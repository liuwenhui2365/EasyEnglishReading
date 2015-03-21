package com.example.liu.autotanslate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/3/19.
 */
public class ContentAdapter extends ArrayAdapter<HashMap<String,String>> {

    LayoutInflater inflater;
    Context context;
    private MyClickListener mListener;
    ArrayList<HashMap<String,String>> contentList = null;

    public ContentAdapter(Context context, ArrayList<HashMap<String,String>> arList){
        super(context,R.layout.wordlistviewitem, arList);
        this.context=context;
        contentList = arList;
    }

    @Override
    public int getCount()
    {
        return contentList.size();
    }

    @Override
    public HashMap<String,String> getItem(int position)
    {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.wordlistviewitem, null);
            Log.d("调试View","getview进去了啊"+convertView);

            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView
                    .findViewById(R.id.word);
            viewHolder.text.setText("改正了");
            viewHolder.button = (Button) convertView
                    .findViewById(R.id.TogButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position).get("word");
        viewHolder.text.setText("hhh");
        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String word = null;
                    mListener.myOnClick(word);
                }

            }
        });

        return convertView;

    }

    public interface MyClickListener{

        void myOnClick(String word);


    }

    public void setMyClickListener(MyClickListener listener) {
        this.mListener  = listener;
    }


    public class ViewHolder {
        TextView text;
        Button button;
    }

}
