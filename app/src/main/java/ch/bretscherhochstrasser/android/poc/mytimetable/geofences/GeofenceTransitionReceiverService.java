package ch.bretscherhochstrasser.android.poc.mytimetable.geofences;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

public class GeofenceTransitionReceiverService extends IntentService {

    StationFenceStateManager mStationFenceStateManager;

    public GeofenceTransitionReceiverService() {
        super("GeofenceTransitionReceiverService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStationFenceStateManager = new StationFenceStateManager(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // First check for errors
            if (LocationClient.hasError(intent)) {
                // Get the error code with a static method
                int errorCode = LocationClient.getErrorCode(intent);
                // Log the error
                Log.e("GeofenceTransitionReceiverService",
                        "Location Services error: " +
                                Integer.toString(errorCode)
                );
            } else {
                // Get the type of transition (entry, exit or dwelling)
                int transitionType =
                        LocationClient.getGeofenceTransition(intent);
                List<Geofence> triggerList =
                        LocationClient.getTriggeringGeofences(intent);
                Log.d("GeofenceTransitionReceiverService", String.format("Got %1$d triggerd geofences for transition type %2$d.", triggerList.size(), transitionType));
                for (Geofence geofence : triggerList) {
                    try {
                        long stationId = Long.parseLong(geofence.getRequestId());
                        switch (transitionType) {
                            case Geofence.GEOFENCE_TRANSITION_ENTER:
                                mStationFenceStateManager.enterStation(stationId);
                                break;
                            case Geofence.GEOFENCE_TRANSITION_EXIT:
                                mStationFenceStateManager.exitStation(stationId);
                                break;
                            case Geofence.GEOFENCE_TRANSITION_DWELL:
                                mStationFenceStateManager.stayAtStation(stationId);
                                break;
                            default:
                                Log.e("GeoFencefransitionReceiverService",
                                        "Invalid geofence transition: " +
                                                Integer.toString(transitionType)
                                );
                        }
                    } catch (NumberFormatException e) {
                        Log.e("GeofenceTransitionReceiverService", "Got invalid station ID: " + geofence.getRequestId());
                    }
                }
            }
        }
    }

}
