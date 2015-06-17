package com.example.liu.autotanslate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.MyApplication;

public class MyURLSpan extends ClickableSpan {
    private Context ctx;
    private String clickStr;

    public MyURLSpan(Context ctx,String clickStr){
        this.ctx=ctx;
        this.clickStr=clickStr;
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View widget) {
//        Log.d("点击获取到",clickStr);
        try {
//            Toast.makeText(ctx,"获取到的内容"+clickStr,Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);

//          防止单词首字母大写获取不到类型转换为小写
            final String type = getWordType(clickStr.toLowerCase());
            String flag = null;
            if (type != null) {
                if (type.equalsIgnoreCase("unknow")) {
                    flag = "认识";
                } else {
                    flag = "不认识";
                }
                dialog.setTitle("修改单词类型");
                dialog.setMessage("确定要将" + clickStr+"转为"+flag+"的单词吗？");
                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String typeMod = changeWordType(clickStr.toLowerCase());
                        if (typeMod.equalsIgnoreCase("know")){
                            typeMod = "认识";
                            Toast.makeText(ctx,"已经成功将该词归类到"+typeMod+"的单词库中！",Toast.LENGTH_SHORT).show();
                        }else if (typeMod.equalsIgnoreCase("unknow")){
                            typeMod = "不认识";
                            Toast.makeText(ctx,"已经成功将该词归类到"+typeMod+"的单词库中！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }else {
                Toast.makeText(ctx,"非常抱歉，该词还没有收录本词库，以后会更新哦！",Toast.LENGTH_SHORT).show();
            }


        } catch (NullPointerException w) {
            w.printStackTrace();
        }
    }

    private String getWordType(String word) {
//        Log.d("按钮获取到的单词",word);
        DbArticle dbArticle;
        SQLiteDatabase db = null;
        String type = null;
        Cursor c = null;
        try {
            dbArticle = new DbArticle(ctx, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                 如果表存在
                    c = db.rawQuery("SELECT type FROM words where word = ?", new String[]{word});
                    while (c.moveToNext()) {
                        type = c.getString(c.getColumnIndex("type"));
//                        Log.d("从数据库中读取type", "type=>" + type);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (c != null) {
                c.close();
            }

            if (db != null) {
                db.close();
            }
        }
            return type;

    }

    private String changeWordType(String word) {
//        Log.d("按钮获取到的单词",word);
        DbArticle dbArticle;
        SQLiteDatabase db = null;
        String type = null;
        Cursor c = null;
        try {
            dbArticle = new DbArticle(ctx, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                 如果表存在
                    c = db.rawQuery("SELECT type FROM words where word = ?", new String[]{word});
                    while (c.moveToNext()) {
                        type = c.getString(c.getColumnIndex("type"));
//                        Log.d("从数据库中读取type", "type=>" + type);
                    }
                }

                if (type != null) {
                    if (type.equalsIgnoreCase("unknow")) {
                        type = "know";
                        db.execSQL("UPDATE words SET type = ? where word = ?", new String[]{type, word});
//                       Toast没反应！！！
                    } else {
                        type = "unknow";
                        db.execSQL("UPDATE words SET type = ? where word = ?", new String[]{type, word});
                    }
                } else {
                    Log.e("警告", "没有获取到类型");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (c != null) {
                c.close();
            }

            if (db != null) {
                db.close();
            }
        }
            return type;

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ctx.getResources().getColor(R.color.black));
        ds.setUnderlineText(false); //去掉下划线
    }
}
