package com.wenhuiliu.EasyEnglishReading;

import java.util.ArrayList;
import java.util.HashMap;
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
//							网络获取
                        unknownWordsNum++;
                        String unmean = HttpClient.ClientRun(word);
                        objBody.set(i, unmean);
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
