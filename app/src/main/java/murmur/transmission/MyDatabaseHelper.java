package murmur.transmission;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by apple on 2018/1/10.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    //数据库版本号
    private static final int DATABASE_VERSION=4;

    //数据库名称
    private static final String DATABASE_NAME="mylocation.db";

    public MyDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String CREATE_TABLE_LOCATION="CREATE TABLE Location(ID VARCHAR(20) PRIMARY KEY,TIME varchar(20),LOCATION varchar(80))";
        db.execSQL(CREATE_TABLE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果旧表存在，删除，所以数据将会消失
        db.execSQL("DROP TABLE IF EXISTS Location");

        //再次创建表
        onCreate(db);
    }
}

