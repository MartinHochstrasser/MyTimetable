package ch.bretscherhochstrasser.android.poc.mytimetable.locationupdates;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import ch.bretscherhochstrasser.android.poc.mytimetable.R;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.LocationClientIntentService;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.PreferenceUtil;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class RequestLocationUpdatesService extends LocationClientIntentService {

    private static final String ACTION_REQUEST_LOCATION_UPDATES = "example.com.locationtest.action.REQUEST_LOCATION_UPDATES";
    private static final String ACTION_STOP_LOCATION_UPDATES = "example.com.locationtest.action.STOP_LOCATION_UPDATES";

    private static final long MILLIS_IN_SECOND = 1000;

    public RequestLocationUpdatesService() {
        super("RequestLocationUpdatesService");
    }

    /**
     * Starts this service to perform action REQUEST_LOCATION_UPDATES. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRequestLocationUpdates(Context context) {
        Intent intent = new Intent(context, RequestLocationUpdatesService.class);
        intent.setAction(ACTION_REQUEST_LOCATION_UPDATES);
        context.startService(intent);
    }

    public static void startActionStopLocationUpdates(Context context) {
        Intent intent = new Intent(context, RequestLocationUpdatesService.class);
        intent.setAction(ACTION_STOP_LOCATION_UPDATES);
        context.startService(intent);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REQUEST_LOCATION_UPDATES.equals(action)) {
                if (mLocationClient != null) {
                    requestLocationUpdates(mLocationClient);
                }
            } else if (ACTION_STOP_LOCATION_UPDATES.equals(action)) {
                if (mLocationClient != null) {
                    stopLocationUpdates(mLocationClient);
                }
            }
        }
    }

    private void requestLocationUpdates(LocationClient locationClient) {
        Log.d("RequestLocationupdateService", "requesting location updates");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(MILLIS_IN_SECOND * PreferenceUtil.getIntPreference(R.string.settings_fragment_location_updates_interval_key, R.string.settings_fragment_location_updates_interval_default, this));
        locationRequest.setFastestInterval(MILLIS_IN_SECOND * PreferenceUtil.getIntPreference(R.string.settings_fragment_location_updates_min_interval_key, R.string.settings_fragment_location_updates_min_interval_default, this));
        locationRequest.setSmallestDisplacement(PreferenceUtil.getIntPreference(R.string.settings_fragment_location_updates_min_distance_key, R.string.settings_fragment_location_updates_min_distance_default, this));
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationClient.requestLocationUpdates(locationRequest, createPendingIntent());
    }

    private void stopLocationUpdates(LocationClient mLocationClient) {
        Log.d("RequestLocationupdateService", "stopping location updates");
        mLocationClient.removeLocationUpdates(createPendingIntent());
    }

    private PendingIntent createPendingIntent() {
        return PendingIntent.getService(this, 0, UpdateStationsService.startActionUpdateStations(this), 0);
    }

}
