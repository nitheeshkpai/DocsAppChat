package com.example.nitheeshkpai.docsappchat.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nitheeshkpai.docsappchat.model.Message;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 2/18/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DocsAppChat";

    // Chats table name
    private static final String TABLE_ITEMS = "chats";

    // Chats Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_SENDER_NAME = "username";
    private static final String KEY_PENDING = "isPending";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHAT_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE + " TEXT,"
                + KEY_SENDER_NAME + " TEXT," + KEY_PENDING + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_CHAT_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public void saveMessage(Message messageItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, messageItem.getMessage());
        values.put(KEY_SENDER_NAME, messageItem.getName());

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public ArrayList<Message> getAllItems() {
        ArrayList<Message> itemList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message item = new Message();
                item.setMessage(cursor.getString(1));
                item.setName(cursor.getString(2));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public void addPending(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message.getMessage());
        values.put(KEY_SENDER_NAME, message.getName());
        values.put(KEY_PENDING, 1);

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public ArrayList<Message> getPendingMessages() {
        ArrayList<Message> itemList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE isPending = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message item = new Message();
                item.setMessage(cursor.getString(1));
                item.setName(cursor.getString(2));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public void removePendingMessage(Message pendingMessage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PENDING, 0);

        db.update(TABLE_ITEMS, values, "username=? AND message=?", new String[]{pendingMessage.getName(), pendingMessage.getMessage()});
        db.close();
    }
}