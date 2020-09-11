package com.java.guoshiguang.data;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FS {
    private final String TABLE_NAME_READ = "newsReadTable";
    private final String TABLE_NAME_SIMPLE = "newsSimpleTable";
    private final String TABLE_NAME_DETAIL = "newsDetailTable";
    private final String KEY_ID = "newsId";
    private final String KEY_SIMPLE = "simpleJson";
    private final String KEY_DETAIL = "detailJson";
    private final String KEY_TYPE = "type";
    private String dbPath;
    private SQLiteDatabase db;

    //很多私有变量

    FS(Context context) throws IOException {
        dbPath = context.getFilesDir().getPath() + "/data.db";
        db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
        //Fixme
        //dropTables();
        createTables();
    }

    void createTables() {
        String readTable = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)", TABLE_NAME_READ, KEY_ID, KEY_DETAIL);
        db.execSQL(readTable);

        String detailTable = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)", TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL);
        db.execSQL(detailTable);

        String simpleTable = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string, %s text, %s string, PRIMARY KEY(%s, %s))", TABLE_NAME_SIMPLE, KEY_ID, KEY_TYPE, KEY_SIMPLE, KEY_ID, KEY_TYPE);
        db.execSQL(simpleTable); //FIXME
    }

    void dropTables() {
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_READ));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_DETAIL));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_SIMPLE));

    }

    void insertSimple(SimpleNews simpleNews, String type) {
        String command = String.format("INSERT OR REPLACE INTO `%s`(%s, %s, %s) VALUES(%s, %s, %s)",
                TABLE_NAME_SIMPLE, KEY_ID, KEY_TYPE, KEY_SIMPLE,
                DatabaseUtils.sqlEscapeString(simpleNews.id),
                DatabaseUtils.sqlEscapeString(type),
                DatabaseUtils.sqlEscapeString(simpleNews.plainJson)
        );
        db.execSQL(command);
    }

    List<SimpleNews> fetchSimple(final String type, int page, int size) throws JSONException {
        //TODO
        String command = String.format("SELECT * FROM `%s` WHERE %s='%s' ORDER BY %s DESC LIMIT %s OFFSET %s",
                TABLE_NAME_SIMPLE, KEY_TYPE, type, KEY_ID, size, (page * size - size));//FIXME, 按照ID查询

        Cursor cursor = db.rawQuery(command, null);
        List<SimpleNews> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            list.add(API.getDetailNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_SIMPLE))), true));

        }
        return list;

    }

    List<SimpleNews> fetchRead() throws JSONException {
        String command = String.format("SELECT * FROM `%s`", TABLE_NAME_READ);
        Cursor cursor = db.rawQuery(command, null);
        List<SimpleNews> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(API.getDetailNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL))), true));
        }
        return list;
    }


    void insertDetail(DetailNews detailNews) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s) VALUES(%s, %s)",
                TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL,
                DatabaseUtils.sqlEscapeString(detailNews.id),
                DatabaseUtils.sqlEscapeString(detailNews.plainJson));
        db.execSQL(cmd);

    }

    DetailNews fetchDetail(final String newsId) throws JSONException {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_DETAIL, KEY_ID, DatabaseUtils.sqlEscapeString(newsId));
        Cursor cursor = db.rawQuery(cmd, null);
        DetailNews detailNews;
        if (cursor.moveToFirst()) {
            detailNews = API.getDetailNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL))), true);
        } else {
            detailNews = DetailNews.NULL;
        }
        cursor.close();
        return detailNews;
    }

    void insertRead(final String newsId, DetailNews news) {
        String command = String.format("INSERT OR REPLACE INTO `%s`(%s,%s) VALUES(%s,%s)",
                TABLE_NAME_READ, KEY_ID, KEY_DETAIL,
                DatabaseUtils.sqlEscapeString(news.id),
                DatabaseUtils.sqlEscapeString(news.plainJson));

        db.execSQL(command);
    }

    boolean hasRead(final String newsId) {
        String command = String.format("SELECT * FROM `%s` WHERE %s='%s'",
                TABLE_NAME_READ, KEY_ID, newsId);
        Cursor cursor = db.rawQuery(command, null);
        boolean read = cursor.moveToFirst();
        cursor.close();
        return read;
    }


}
