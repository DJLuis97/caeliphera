package com.djluis.caeliphera;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLite extends SQLiteOpenHelper {
  public SQLite (
      @Nullable Context context, @Nullable String name,
      @Nullable SQLiteDatabase.CursorFactory factory, int version
  )
  {
    super(context, name, factory, version);
  }

  @Override public void onCreate (SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL("CREATE TABLE users (" +
      "id INT PRIMARY KEY, " +
      "full_name VARCHAR(255) NOT NULL," +
      "email VARCHAR(255) NOT NULL," +
      "token VARCHAR(255) NOT NULL" +
    ")");
  }

  @Override public void onUpgrade (SQLiteDatabase sqLiteDatabase, int i, int i1) {

  }
}
