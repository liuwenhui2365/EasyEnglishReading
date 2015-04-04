package com.wenhuiliu.EasyEnglishReading;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderChinaDaily implements ISpiderArticle {

	final private Pattern titlePat = Pattern.compile("<div id=\"Title_e\"><h1.*?>(.*?)</h1>");
	final private Pattern timePat = Pattern.compile("<div id=\"Title_e\">.*?<span class=\"greyTxt6 block mb15\">(.*?)</span>",Pattern.MULTILINE|Pattern.DOTALL);
//	final private Pattern catalogyPat = Pattern.compile("<div class=\"w980 greyTxt9 titleTxt22 pt20 pb10\"><a.*?>(.*?)</a>",Pattern.MULTILINE|Pattern.DOTALL);
	final private Pattern contentPat = Pattern.compile("<p.*>([^<_]*?)</p>");

	@Override
	public ArrayList<String> getUrlList(String catalogy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessage(String url) {
		// TODO Auto-generated method stub
		HttpClient httpClient = new HttpClient(url);
		return httpClient.getResponse();
	}

	@Override
	public String getTitle(String message) {
		// TODO Auto-generated method stub
		String title = null;
		Matcher mat = titlePat.matcher(message);
		if(mat.find()){
			title = mat.group(1);
		}
		return title;
	}

	@Override
	public StringBuilder getContent(String message) {
		// TODO Auto-generated method stub
		StringBuilder content = new StringBuilder();
		Matcher mat = contentPat.matcher(message);
		while(mat.find()){	
			content.append(mat.group(1));
			content.append("^");
		}
		return content;
	}

	@Override
	public String getTime(String message) {
		// TODO Auto-generated method stub
		String time = null;
		Matcher mat = timePat.matcher(message);
		if(mat.find()){
			time = mat.group(1);
		}
		return time;
	}

	@Override
	public String getCatalogy(String message) {
		// TODO Auto-generated method stub
		String catalogy = "默认";
//		Matcher mat = catalogyPat.matcher(message);
//		if(mat.find()){
//			catalogy = mat.group(1).substring(0,2);
//		}
		return catalogy;
	}

//    初始化文章对象
    public Article getPassage(String url){
        String message = getMessage(url);
        String title = getTitle(message);
//        Log.d("标题完成",title);
        StringBuilder content = getContent(message);
//        Log.d("内容完成",content.toString());
        String catalogy = getCatalogy(message);
//        Log.d("分类完成",catalogy);
        String time = getTime(message);
//        Log.d("时间完成","....");
        Article article = new Article(title, content, catalogy);
        article.setTime(time);
//        Log.d("文章对象初始化完成！","恭喜");
        return article;
    }

}
