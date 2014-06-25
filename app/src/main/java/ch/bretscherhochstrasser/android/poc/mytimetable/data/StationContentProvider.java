package ch.bretscherhochstrasser.android.poc.mytimetable.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by marti_000 on 17.06.2014.
 */
public class StationContentProvider extends SQLiteContentProvider {

    public static final String AUTHORITY = "example.com.locationtest";

    private static final String PATH_STATION = "station";
    private static final String PATH_STATION_ID = PATH_STATION + "/#";

    public static final Uri CONTENT_URI_STATION = Uri.parse("content://" + AUTHORITY + "/" + PATH_STATION);

    private static final int MATCH_STATION = 100;
    private static final int MATCH_STATION_ID = 110;


    private UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public StationContentProvider() {
        mMatcher.addURI(AUTHORITY, PATH_STATION, MATCH_STATION);
        mMatcher.addURI(AUTHORITY, PATH_STATION_ID, MATCH_STATION_ID);
    }

    @Override
    public SQLiteOpenHelper getDatabaseHelper(Context context) {
        return new StationDbOpenHelper(context);
    }

    @Override
    public Uri insertInTransaction(Uri uri, ContentValues values, boolean callerIsSyncAdapter) {
        final SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
        long id;
        Uri insertUri;
        switch (mMatcher.match(uri)) {
            case MATCH_STATION:
                id = CupboardProvider.cupboard().withDatabase(db).put(Station.class, values);
                insertUri = ContentUris.withAppendedId(uri, id);
                break;
            case MATCH_STATION_ID:
                values.put(BaseColumns._ID, ContentUris.parseId(uri));
                CupboardProvider.cupboard().withDatabase(db).put(Station.class, values);
                insertUri = uri;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri " + uri);
        }
        postNotifyUri(insertUri);
        return insertUri;
    }

    @Override
    public int updateInTransaction(Uri uri, ContentValues values, String selection, String[] selectionArgs, boolean callerIsSyncAdapter) {
        final SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
        int updated;
        switch (mMatcher.match(uri)) {
            case MATCH_STATION:
                updated = CupboardProvider.cupboard().withDatabase(db).update(Station.class, values, selection, selectionArgs);
                break;
            case MATCH_STATION_ID:
                values.put(BaseColumns._ID, ContentUris.parseId(uri));
                updated = CupboardProvider.cupboard().withDatabase(db).update(Station.class, values);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri " + uri);
        }
        if (updated > 0) {
            postNotifyUri(uri);
        }
        return updated;
    }

    @Override
    public int deleteInTransaction(Uri uri, String selection, String[] selectionArgs, boolean callerIsSyncAdapter) {
        final SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
        int deleted;
        switch (mMatcher.match(uri)) {
            case MATCH_STATION:
                deleted = CupboardProvider.cupboard().withDatabase(db).delete(Station.class, selection, selectionArgs);
                break;
            case MATCH_STATION_ID:
                deleted = CupboardProvider.cupboard().withDatabase(db).delete(Station.class, ContentUris.parseId(uri)) ? 1 : 0;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri " + uri);
        }
        if (deleted > 0) {
            postNotifyUri(uri);
        }
        return deleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getDatabaseHelper().getReadableDatabase();
        Cursor cursor;
        switch (mMatcher.match(uri)) {
            case MATCH_STATION:
                cursor = CupboardProvider.cupboard().withDatabase(db).query(Station.class).withProjection(projection).withSelection(selection, selectionArgs).orderBy(sortOrder).getCursor();
                break;
            case MATCH_STATION_ID:
                cursor = CupboardProvider.cupboard().withDatabase(db).query(Station.class).byId(ContentUris.parseId(uri)).getCursor();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (mMatcher.match(uri)) {
            case MATCH_STATION:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ch.bretscherhochstrasser.android.poc.mytimetable.data.Station";
            case MATCH_STATION_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ch.bretscherhochstrasser.android.poc.mytimetable.data.Station";
            default:
                throw new UnsupportedOperationException("Unsupported uri " + uri);
        }
    }
}
