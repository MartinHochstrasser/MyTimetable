package ch.bretscherhochstrasser.android.poc.mytimetable.locationupdates;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

import java.io.IOException;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.JsonDownloader;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.StationDownloader;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.StationParser;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.LocationClientIntentService;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;
import ch.bretscherhochstrasser.android.poc.mytimetable.geofences.StationGeofenceManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class UpdateStationsService extends LocationClientIntentService {

    private static final String ACTION_UPDATE_STATIONS = "example.com.locationtest.action.UPDATE_STATIONS";

    private StationDownloader mStationDownloader;
    private StationGeofenceManager mStationGeofenceManager;

    public UpdateStationsService() {
        super("UpdateStationsService");
    }

    /**
     * Creates an Intent to start the UpdateStationsService
     */
    public static Intent startActionUpdateStations(Context context) {
        Intent intent = new Intent(context, UpdateStationsService.class);
        intent.setAction(ACTION_UPDATE_STATIONS);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStationDownloader = new StationDownloader(new JsonDownloader(), new StationParser());
        mStationGeofenceManager = new StationGeofenceManager(this, mLocationClient);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_STATIONS.equals(action)) {
                final Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
                Log.d("UpdateStationsService", "Got location " + location);
                handleUpdateStations(location);
            }
        }
    }

    private void handleUpdateStations(Location location) {
        try {
            List<Station> nearbyStations = mStationDownloader.getNearbyStations(location);
            Log.d("UpdateStationsService", String.format("Downloaded %d nearby stations.", nearbyStations.size()));
            mStationGeofenceManager.setNewStationsNearby(nearbyStations);
        } catch (IOException e) {
            Log.e("UpdateStationsService", "Error downloading stations", e);
        }
    }

}
