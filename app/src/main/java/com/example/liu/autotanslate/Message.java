package com.example.liu.autotanslate;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Message extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
//      显示返回菜单
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        String time = simpleDateFormat.format(curDate);

        TextView textView = (TextView)findViewById(R.id.date);
        textView.setText(time);

        textView = (TextView)findViewById(R.id.title);
        textView.setText("显示文章标题");

        textView = (TextView)findViewById(R.id.message);
        StringBuilder sbd = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../passage.txt"));
            String line = null;
            while((line = reader.readLine()) != null){
                sbd.append(line);
            }
            reader.close();

        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView.setText(sbd);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
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
//           添加相关事件
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
