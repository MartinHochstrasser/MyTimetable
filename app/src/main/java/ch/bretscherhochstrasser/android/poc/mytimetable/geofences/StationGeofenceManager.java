package ch.bretscherhochstrasser.android.poc.mytimetable.geofences;

import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.R;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.PreferenceUtil;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.CupboardProvider;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.StationContentProvider;

/**
 * Created by marti_000 on 20.06.2014.
 */
public class StationGeofenceManager implements LocationClient.OnAddGeofencesResultListener, LocationClient.OnRemoveGeofencesResultListener {

    private final Context mContext;
    private final LocationClient mLocationClient;

    public StationGeofenceManager(final Context context, final LocationClient locationClient) {
        mContext = context;
        mLocationClient = locationClient;
    }

    public void setNewStationsNearby(List<Station> nearbyStations) {
        final List<Station> previousStations = CupboardProvider.cupboard().withContext(mContext).query(StationContentProvider.CONTENT_URI_STATION, Station.class).list();
        addNewStationGeofences(nearbyStations, previousStations);
        removeObsoleteStationGeofences(nearbyStations, previousStations);
    }

    private void addNewStationGeofences(final List<Station> nearbyStations, final List<Station> previousStations) {
        List<Station> newStationGeofences = new ArrayList<Station>();
        for (Station nearbyStation : nearbyStations) {
            if (!previousStations.contains(nearbyStation)) {
                newStationGeofences.add(nearbyStation);
            }
        }
        Log.d("StationGeofenceManager", String.format("%d new stations to add", newStationGeofences.size()));
        final ArrayList<ContentProviderOperation> updateOperations = new ArrayList<ContentProviderOperation>();
        for (Station station : newStationGeofences) {
            CupboardProvider.cupboard().withOperations(updateOperations).put(StationContentProvider.CONTENT_URI_STATION, station);
        }
        try {
            mContext.getContentResolver().applyBatch(StationContentProvider.AUTHORITY, updateOperations);
            Log.d("StationGeofenceManager", String.format("Added %d stations.", newStationGeofences.size()));
        } catch (RemoteException e) {
            Log.e("StationGeofenceManager", "Error inserting station data.", e);
        } catch (OperationApplicationException e) {
            Log.e("StationGeofenceManager", "Error inserting station data.", e);
        }
        final List<Geofence> geofencesToAdd = createGeofences(nearbyStations); // <- create geofences for all nearby stations, even if we recerate
        Log.d("StationGeofenceManager", String.format("Adding %d geofences", geofencesToAdd.size()));
        if (!geofencesToAdd.isEmpty()) {
            mLocationClient.addGeofences(geofencesToAdd, getPendingIntent(), this);
        }
    }

    private void removeObsoleteStationGeofences(final List<Station> nearbyStations, final List<Station> previousStations) {
        List<Station> obsoleteStationGeofences = new ArrayList<Station>();
        for (Station previousStation : previousStations) {
            if (!nearbyStations.contains(previousStation)) {
                obsoleteStationGeofences.add(previousStation);
            }
        }
        Log.d("StationGeofenceManager", String.format("%d obsolete stations to remove", obsoleteStationGeofences.size()));
        final ArrayList<ContentProviderOperation> updateOperations = new ArrayList<ContentProviderOperation>();
        for (Station station : obsoleteStationGeofences) {
            CupboardProvider.cupboard().withOperations(updateOperations).delete(StationContentProvider.CONTENT_URI_STATION, station);
        }
        try {
            mContext.getContentResolver().applyBatch(StationContentProvider.AUTHORITY, updateOperations);
            Log.d("StationGeofenceManager", String.format("Deleted %d stations.", obsoleteStationGeofences.size()));
        } catch (RemoteException e) {
            Log.e("StationGeofenceManager", "Error deleting station data.", e);
        } catch (OperationApplicationException e) {
            Log.e("StationGeofenceManager", "Error deleting station data.", e);
        }
        final List<String> geofencesToRemove = createGeofenceIdList(obsoleteStationGeofences);
        Log.d("StationGeofenceManager", String.format("Removing %d geofences", geofencesToRemove.size()));
        if (!geofencesToRemove.isEmpty()) {
            mLocationClient.removeGeofences(geofencesToRemove, this);
        }
    }

    private List<String> createGeofenceIdList(List<Station> stationGeoFences) {
        final List<String> geofenceIds = new ArrayList<String>();
        for (Station stationGeoFence : stationGeoFences) {
            geofenceIds.add(Long.toString(stationGeoFence._id));
        }
        return geofenceIds;
    }

    private List<Geofence> createGeofences(final List<Station> stationGeofences) {
        final List<Geofence> geofences = new ArrayList<Geofence>(stationGeofences.size());
        for (Station station : stationGeofences) {
            final float radius = PreferenceUtil.getIntPreference(R.string.settings_fragment_geofence_radius_key, R.string.settings_fragment_geofence_radius_default, mContext);
            final int dwellingTime = 1000 * PreferenceUtil.getIntPreference(R.string.settings_fragment_geofence_dwelling_key, R.string.settings_fragment_geofence_dwelling_default, mContext);
            final int responsiveness = 1000 * PreferenceUtil.getIntPreference(R.string.settings_fragment_geofence_responsiveness_key, R.string.settings_fragment_geofence_responsiveness_default, mContext);
            Geofence geofence = new Geofence.Builder()
                    .setCircularRegion(station.latitude, station.longitude, radius)
                    .setLoiteringDelay(dwellingTime)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(responsiveness)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setRequestId(Long.toString(station._id))
                    .build();
            geofences.add(geofence);
        }
        return geofences;
    }

    private PendingIntent getPendingIntent() {
        final Intent geoFenceIntent = new Intent(mContext, GeofenceTransitionReceiverService.class);
        return PendingIntent.getService(mContext, 0, geoFenceIntent, 0);
    }


    @Override
    public void onAddGeofencesResult(int statusCode, String[] geofenceIds) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Log.d("StationGeofenceManager", String.format("Added %1$d geofences: %2$s", geofenceIds.length, Arrays.toString(geofenceIds)));
        } else {
            Log.e("StationGeofenceManager", "Could not add geofences.");
        }
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceIds) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Log.d("StationGeofenceManager", String.format("Removed %1$d geofences: %2$s", geofenceIds.length, Arrays.toString(geofenceIds)));
        } else {
            Log.e("StationGeofenceManager", "Could not remove geofences.");
        }
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int i, PendingIntent pendingIntent) {
        // nothing to do here
    }

    public void refreshCurrentGeofences() {
        final List<Station> currentStations = CupboardProvider.cupboard().withContext(mContext).query(StationContentProvider.CONTENT_URI_STATION, Station.class).list();
        final List<Geofence> refreshedGeofences = createGeofences(currentStations);
        Log.d("StationGeofenceManager", String.format("Refreshing %d geofences", refreshedGeofences.size()));
        if (!refreshedGeofences.isEmpty()) {
            mLocationClient.addGeofences(refreshedGeofences, getPendingIntent(), this);
        }
    }

    public void clearStationGeofences() {
        Log.d("StationGeofenceManager", "Removing all geofences");
        if (mLocationClient.isConnected()) {
            mLocationClient.removeGeofences(getPendingIntent(), this);
        }
        int deleted = CupboardProvider.cupboard().withContext(mContext).delete(StationContentProvider.CONTENT_URI_STATION, null, null);
        Log.d("StationGeofenceManager", String.format("Deleted %d stations", deleted));
    }
}
