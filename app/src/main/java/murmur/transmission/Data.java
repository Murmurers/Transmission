package murmur.transmission;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Data extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private String te;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        text1=(TextView) findViewById(R.id.text1);
        text2=(TextView) findViewById(R.id.text2);
        text3=(TextView) findViewById(R.id.text3);

        text2.setText("包裹编号:741741\n时间:2018-01-09 12:34:05\n地点:太原市小店区");
        text3.setText("包裹编号:741741\n时间:2018-01-07 15:41:09\n地点:上海市浦东区");

        dbHelper = new MyDatabaseHelper(getApplicationContext());
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cur = db.query("Location",null,null,null,null,null,null,null);
        if (cur.moveToFirst()){
            while(!cur.isAfterLast())
            {
                te="包裹编号:"+cur.getString(0)+"\n"+"时间:"+cur.getString(1)+"\n"+"地点:"+cur.getString(2);
                text1.setText(te);
                cur.moveToNext();
            }
        }
        db.close();
    }
}
