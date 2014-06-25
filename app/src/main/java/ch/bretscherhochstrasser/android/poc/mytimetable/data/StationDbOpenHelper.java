package ch.bretscherhochstrasser.android.poc.mytimetable.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marti_000 on 15.06.2014.
 */
public class StationDbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "StationDb";
    private static final int DB_VERSION = 1;


    public StationDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CupboardProvider.cupboard().withDatabase(sqLiteDatabase).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        CupboardProvider.cupboard().withDatabase(sqLiteDatabase).upgradeTables();
    }
}
