package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.go.kemdikbud.pkpberbasiszonasi.Master.TokenMaster;

public class DatabaseHelper extends SQLiteOpenHelper {
    // static variable
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "TokenManager";

    // table name
    private static final String TABLE_TOKEN = "tokenTable";

    // column tables
    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PRIVATETOKEN = "privatetoken";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_TOKEN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TOKEN + " TEXT,"
                + KEY_PRIVATETOKEN + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN);
        onCreate(db);
    }

    public void addRecord(TokenMaster tokenMaster){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TOKEN, tokenMaster.getToken());
        values.put(KEY_PRIVATETOKEN, tokenMaster.getPrivatetoken());

        db.insert(TABLE_TOKEN, null, values);
        db.close();
    }

    public TokenMaster getTokenMaster() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TOKEN, new String[] { KEY_ID,
                        KEY_TOKEN, KEY_PRIVATETOKEN }, null,
                null, null, null, null, "1");
        TokenMaster token = new TokenMaster();
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            token.setId(cursor.getInt(0));
            token.setToken(cursor.getString(1));
            token.setPrivatetoken(cursor.getString(2));
        }

        // return contact
        return token;
    }

    public void deleteTokenMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_TOKEN);
        db.close();
    }
}
