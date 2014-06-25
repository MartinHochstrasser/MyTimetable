package ch.bretscherhochstrasser.android.poc.mytimetable.settings;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import ch.bretscherhochstrasser.android.poc.mytimetable.R;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.PreferenceUtil;
import ch.bretscherhochstrasser.android.poc.mytimetable.geofences.StationGeofenceManager;
import ch.bretscherhochstrasser.android.poc.mytimetable.locationupdates.RequestLocationUpdatesService;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private LocationClient mLocationClient;
    private StationGeofenceManager mStationGeofenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        if (PreferenceUtil.departureNotificationsEnabled(getActivity())) {
            RequestLocationUpdatesService.startActionRequestLocationUpdates(getActivity());
        }
        mLocationClient = new LocationClient(getActivity(), this, this);
        mStationGeofenceManager = new StationGeofenceManager(getActivity(), mLocationClient);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //do nothing
    }

    @Override
    public void onDisconnected() {
        //do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //do nothing
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (isGeofencePreference(s)) {
            if (mLocationClient.isConnected() && PreferenceUtil.departureNotificationsEnabled(getActivity())) {
                mStationGeofenceManager.refreshCurrentGeofences();
            }
        } else if (isLocationUpdatePreference(s)) {
            if (PreferenceUtil.departureNotificationsEnabled(getActivity())) {
                RequestLocationUpdatesService.startActionRequestLocationUpdates(getActivity());
            }
        } else if (isPreferenceKey(s, R.string.settings_fragment_enable_key)) {
            boolean isEnabled = sharedPreferences.getBoolean(s, true);
            if (isEnabled) {
                RequestLocationUpdatesService.startActionRequestLocationUpdates(getActivity());
            } else {
                RequestLocationUpdatesService.startActionStopLocationUpdates(getActivity());
                mStationGeofenceManager.clearStationGeofences();
            }
        }
    }

    private boolean isLocationUpdatePreference(String s) {
        return isPreferenceKey(s, R.string.settings_fragment_location_updates_interval_key) || isPreferenceKey(s, R.string.settings_fragment_location_updates_min_interval_key) || isPreferenceKey(s, R.string.settings_fragment_location_updates_min_distance_key);
    }

    private boolean isGeofencePreference(String s) {
        return isPreferenceKey(s, R.string.settings_fragment_geofence_radius_key) || isPreferenceKey(s, R.string.settings_fragment_geofence_dwelling_key) ||
                isPreferenceKey(s, R.string.settings_fragment_geofence_responsiveness_key);
    }

    private boolean isPreferenceKey(final String key, final int keyResource) {
        return key.equals(getString(keyResource));
    }

}
