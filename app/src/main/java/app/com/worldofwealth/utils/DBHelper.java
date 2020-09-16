package app.com.worldofwealth.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.com.worldofwealth.models.User;

import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myvoice";
    public static final String USER_TBL = "user";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table if not exists " + USER_TBL + " (data text)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TBL);
        return numRows;
    }

    public boolean insertdata(String data, String tblname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", data);

        db.delete(tblname, "", null);
        db.insert(tblname, null, contentValues);
        return true;
    }


    public boolean deletetable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, "", null);
        return true;
    }

    public User getUserData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery("select * from " + USER_TBL, null);
        User user = new User();
        if (rs.moveToFirst()) {
            try {
                String data = rs.getString(rs.getColumnIndex("data"));
                JSONObject jsonObject = new JSONObject(data);
                user.setUid(jsonObject.getString("id"));
                user.setUsertype(jsonObject.getString("usertype"));
                user.setFirstname(jsonObject.getString("firstname"));
                user.setLastname(jsonObject.getString("lastname"));
                user.setEmail(jsonObject.getString("email"));
                user.setPhone(jsonObject.getString("phone"));
                user.setDob(jsonObject.getString("dob"));
                user.setGender(jsonObject.getString("gender"));
                user.setUserimageurl(jsonObject.getString("userimageurl"));
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (!rs.isClosed()) {
                rs.close();
            }
        }
        return user;
    }
}
