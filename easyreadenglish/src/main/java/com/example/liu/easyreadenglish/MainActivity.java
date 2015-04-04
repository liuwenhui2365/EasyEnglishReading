package com.example.liu.easyreadenglish;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.lang.reflect.Array;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private VideoView videoView;
    private Button play;
    private Button pause;
    private Button replay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        研究fragment布局
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }
        TextView textView = (TextView)findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Nav.class);
                startActivity(intent);
            }
        });

        play = (Button)findViewById(R.id.play);
        pause = (Button)findViewById(R.id.pause);
        replay = (Button)findViewById(R.id.replay);
        videoView = (VideoView)findViewById(R.id.video);

        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        replay.setOnClickListener(this);

        initVideoPath();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                if (!videoView.isPlaying()){
                    videoView.start();
                }
                break;
            case R.id.pause:
                if(videoView.isPlaying()){
                    videoView.pause();
                }
                break;
            case R.id.replay:
                if(videoView.isPlaying()){
                    videoView.resume();
                }else{
                    videoView.start();
                }
                break;
        }
    }

    private void initVideoPath(){
//        ListDir("/sdcard/Download");
//        ListDir(Environment.get());
        videoView.setVideoPath("/sdcard/Download/ddd.mp4");
//        Log.d("文件路径",file.getPath());
    }

    public void ListDir(String path){
        File file = new File(path);
        File [] files  = null;
        if (file.isDirectory()){
            files = file.listFiles();
        }
//        Log.d("根文件路径",file.getAbsolutePath());
        try {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    Log.d("子文件夹", file1.getAbsolutePath());
//                    ListDir(file1.getPath());
                }else{
//                    Log.d("文件名字",file1.getName());
                }
            }
        }catch (NullPointerException e){
            //e.printStackTrace();
        }
    }
//   记得销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView!=null){
            videoView.suspend();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container, false);
            return rootView;
        }
    }
}
