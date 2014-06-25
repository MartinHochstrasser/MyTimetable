package ch.bretscherhochstrasser.android.poc.mytimetable.geofences;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.DepartureDownloader;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.DepartureParser;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.JsonDownloader;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.CupboardProvider;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.StationContentProvider;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.StationFenceState;
import ch.bretscherhochstrasser.android.poc.mytimetable.notification.DepartureNotificationManager;

/**
 * Created by marti_000 on 20.06.2014.
 */
public class StationFenceStateManager {

    private static final String CONTENT_VALUE_KEY_FENCE_STATE = "fenceState";

    private Context mContext;
    private DepartureNotificationManager mDepartureNotificationManager;

    public StationFenceStateManager(Context context) {
        mContext = context;
        mDepartureNotificationManager = new DepartureNotificationManager(mContext, new DepartureDownloader(new JsonDownloader(), new DepartureParser()));
    }

    public void enterStation(final long stationId) {
        Log.i("StationFenceStateManager", "Entered station " + stationId);
        updateStationFenceState(stationId, StationFenceState.ENTERED);
    }

    public void stayAtStation(final long stationId) {
        Log.i("StationFenceStateManager", "Staying at station " + stationId);
        updateStationFenceState(stationId, StationFenceState.ON_SITE);
        mDepartureNotificationManager.showTimeTableNotifiaction(stationId);
    }

    public void exitStation(final long stationId) {
        Log.i("StationFenceStateManager", "Left station " + stationId);
        updateStationFenceState(stationId, StationFenceState.NOT_THERE);
        mDepartureNotificationManager.closeDepartureNotification();
    }

    private void updateStationFenceState(long stationId, StationFenceState fenceState) {
        final Uri stationUri = ContentUris.withAppendedId(StationContentProvider.CONTENT_URI_STATION, stationId);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTENT_VALUE_KEY_FENCE_STATE, fenceState.toString());
        CupboardProvider.cupboard().withContext(mContext).update(stationUri, contentValues);
    }
}
