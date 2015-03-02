package com.wenhuiliu.EasyEnglishReading;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderArticle {

    public String getMessage(String url){
        String message = null;
        HttpClient hc = new HttpClient(url);
        message = hc.getResponse();
        return message;
    }

    Pattern pat = null;
    Matcher mat = null;

    public String getTitle(String message){
        String title = null;
        if(message != null) {

            pat = Pattern.compile("<div id=\"title\">(.*?)</div>");
            mat = pat.matcher(message);
            if (mat.find()) {
                title = mat.group(1);
            }
        }
        return title;
    }

    public StringBuilder getContent(String message){
        StringBuilder content = new StringBuilder();
        pat = Pattern.compile("<P>([^<_]*?)</P>", Pattern.MULTILINE|Pattern.DOTALL);
        mat = pat.matcher(message);
        while(mat.find()){
            content.append(mat.group(1));
            content.append("\n\n");
        }
        return content;
    }

    public String getTime(String message){
        String time = null;
        pat = Pattern.compile("<SPAN class=datetime>(.*)</SPAN>", Pattern.CASE_INSENSITIVE);
        mat = pat.matcher(message);
        if(mat.find()){
            time = mat.group(1);
        }
        return time;
    }

    public String getCatalogy(String message){
        String catalogy = null;
        pat = Pattern.compile("<div id=\"nav\">.*title=.*?title=\"(.*?)\">");
        mat = pat.matcher(message);
        if(mat.find()){
            catalogy = mat.group(1).substring(0,2);
        }
        return catalogy;
    }

    public Article getPassage(String url){
        String message = getMessage(url);
        String title = getTitle(message);
        StringBuilder content = getContent(message);
        String catalogy= getCatalogy(message);
        String time = getTime(message);
        Article article = new Article(title, content, catalogy);
        article.setTime(time);
        return article;
    }
}
