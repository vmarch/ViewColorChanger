package devtolife.viewcolorchanger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class DB {
    static final String KEY_ID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_COLORED = "colored";
    private static final int DATABASE_VERSION = 3;
    public static SQLiteDatabase database;
    private static String DATABASE_NAME = "SHOPINGLIST";
    private static String nameOfTable = "Example";
    private Context context;
    private DBHelper dbHelper;


    DB(Context ctx) {
        context = ctx;
    }

    public static String getNameOfTable() {
        return nameOfTable;
    }

    public void open() {
        dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    void upDateCheck(long id, int checked) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_COLORED, checked);
        database.update("\'" + getNameOfTable() + "\'", cv, "_id = " + id, null);
    }

    Cursor getAllData() {
        return database.query("\'" + getNameOfTable() + "\'", null, null, null, null, null, null);
    }

    protected void addRec(String name, int checked) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_COLORED, checked);
        database.insert("\'" + getNameOfTable() + "\'", null, cv);
    }

    void delRec(long id) {
        database.delete("\'" + getNameOfTable() + "\'", KEY_ID + " = " + id, null);
    }

    Cursor toGetCheckedItem(long id) {
        return database.rawQuery("SELECT colored FROM " + "\'" + DB.getNameOfTable() + "\'" + " WHERE _id = " + id, null);
    }

    void deleteTable(String tabl) {
        database.execSQL("DROP TABLE " + "\'" + tabl + "\'");
    }

    void close() {
        if (dbHelper != null) dbHelper.close();
    }
}