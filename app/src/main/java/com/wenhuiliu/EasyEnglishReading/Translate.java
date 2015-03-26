package com.wenhuiliu.EasyEnglishReading;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liu.autotanslate.Message;
import com.example.liu.autotanslate.R;

import org.apache.http.HttpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate {

    private int unknownWordsNum;
    private HashMap<String,String> knownWords;
    private HashMap<String,String> unknownWords;
    //	初级 < 20%
    final private int  PRIMARY = 20;
    //	中级 < 50%
    //final private int INTERMEDIATE = 50;
    // 高级 > 50%
    final private int ADVANCED = 50;

    public ArrayList<Article> translate(ArrayList<Article> passages){
//	        存放翻译好的文章链表
        ArrayList<Article> objPassages = new ArrayList<Article>();
        for(int i=0;i<passages.size();i++){
//		    把文章的内容转成字符串
            ArrayList<String> objBody = passages.get(i).getWords();
//			翻译文章内容中的每一个不认识的单词
            for (int j = 0; j < objBody.size(); j++) {
                String word = objBody.get(j);
                Pattern expression = Pattern.compile("[a-zA-Z]+");  //定义正则表达式匹配单词
                Matcher matcher = expression.matcher(word);
                if(!knownWords.containsKey(word) && matcher.find()){
                    if(unknownWords.containsKey(word)){
                        unknownWordsNum++;
                        objBody.set(i, word+"("+unknownWords.get(word)+")");
                    }else{
//			ToDo 	以后完善网络获取并加入单词表中
                        unknownWordsNum++;
//                        String unmean = HttpClient.ClientRun(word);
//                        objBody.set(i, unmean);
                    }
                }
            }
            passages.get(i).setBody(objBody.toString());
            int difficultRatio = unknownWordsNum / objBody.size() * 100;
            if( difficultRatio <= PRIMARY ){
                passages.get(i).setLevel("初级");
            }else if(difficultRatio <= ADVANCED){
                passages.get(i).setLevel("中级");
            }else{
                passages.get(i).setLevel("高级");
            }
            objPassages.set(i,passages.get(i));
        }

        return objPassages;
    }

    public Article translate(Article article,HashMap<String,String> knownWords,HashMap<String,String> unknownWords){
//	        存放翻译好的文章链表
//		    把文章的内容转成字符串
        ArrayList<String> bodyWords = article.getWords();
        StringBuilder body = new StringBuilder();
//			翻译文章内容中的每一个不认识的单词
        for (int j = 0; j < bodyWords.size(); j++) {
            String word = bodyWords.get(j);
            Pattern expression = Pattern.compile("[a-zA-Z]+");  //定义正则表达式匹配单词
            Matcher matcher = expression.matcher(word);
            if(!knownWords.containsKey(word) && matcher.find()){
                if(unknownWords.containsKey(word)){
//                    Log.d("提示","从不认识的单词表中获取");
                    unknownWordsNum++;
                    bodyWords.set(j, word+"("+unknownWords.get(word)+")");
                    body.append(bodyWords.get(j)+" ");
                }else{
//                    TODO 以后完善
//                    Log.d("提示","从网络获取");
                    unknownWordsNum++;
//
//                        String wordMeaning = HttpClient.ClientRun(word);
//                        bodyWords.set(j, word+"("+wordMeaning+")");
//
////                      Toast.makeText(context,"网络异常",Toast.LENGTH_SHORT).show();
//                        Log.d("提示","网络异常");
                    body.append(bodyWords.get(j)+" ");
                }
            }else if (word.equalsIgnoreCase("^")){
                System.out.println("找到符号了"+word);
                body.append("\n\n");
            }else {
                body.append(bodyWords.get(j) + " ");
            }
        }

        article.setBody(body.toString());
        int difficultRatio = unknownWordsNum / bodyWords.size() * 100;
        if( difficultRatio <= PRIMARY ){
            article.setLevel("初级");
        }else if(difficultRatio <= ADVANCED){
            article.setLevel("中级");
        }else{
            article.setLevel("高级");
        }

        return article;
    }

//    ToDo 完善该方法
    public void insertWord(String word,String meaning){
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
//            DbArticle dbArticle = new DbArticle(this, "Articles.db", null, 1);
//            db = dbArticle.getReadableDatabase();
            Translate translate = new Translate();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='Article'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
                    db.execSQL("INSERT INTO words values(?,?,?)", new String[]{word, meaning, "unknown"});
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("数据库异常","ERROR");
        }finally {
            if(c != null){
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }

    }
    //	翻译好的文章进行分类
    public ArrayList<Article>  getPassages (ArrayList<Article> objPassages,String level,String catalogy){
        ArrayList<Article> classifiedPassages = new ArrayList<Article>();
        for (int i = 0; i < objPassages.size(); i++) {
            //			测试一下比较方法
            if(objPassages.get(i).getCatalogy().equalsIgnoreCase(catalogy) && objPassages.get(i).getLevel().equalsIgnoreCase(level)){
                //			   用的是add方法不是set方法
                classifiedPassages.add(objPassages.get(i));
            }
        }

        return classifiedPassages;
    }
}
