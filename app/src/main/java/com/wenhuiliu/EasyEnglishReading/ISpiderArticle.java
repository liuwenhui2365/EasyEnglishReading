package com.wenhuiliu.EasyEnglishReading;

import java.util.ArrayList;

/**
 * 获取文章链表并解析
 * Created by Administrator on 2015/3/4.
 */
public interface ISpiderArticle {

    /**
     * 根据提供的网址抓取对应文章的地址
     * @param url  文章列表网址
     * @return  多个文章地址构成的链表
     */
    public ArrayList<String> getUrl(String url);

    /**
     * 根据提供网页内容获取文章的标题
     * @param message 网页内容
     * @return  文章标题
     */
    public String getTitle(String message);

    /**
     * 根据提供网页内容获取文章的内容
     * @param message 网页内容
     * @return 文章内容
     */
    public StringBuilder getContent(String message);

    /**
     * 根据提供网页内容获取文章的时间
     * @param message 网页内容
     * @return 写文章时间
     */
    public String getTime(String message);

    /**
     * 根据提供网页内容获取文章的分类
     * @param message 网页内容
     * @return  文章类别
     */
    public String getCatalogy(String message);
}
