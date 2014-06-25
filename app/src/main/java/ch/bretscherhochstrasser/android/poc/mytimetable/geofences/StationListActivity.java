package ch.bretscherhochstrasser.android.poc.mytimetable.geofences;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.StationContentProvider;

public class StationListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private static final int LOADER_ID = 100;

    private static final long REQUEST_INTERVAL = 1000; // 1 second
    private static final long MIN_REQUEST_INTERVAL = 300; // 300 milliseconds

    private StationAdapter mStationAdapter;

    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStationAdapter = new StationAdapter(this, null);
        setListAdapter(mStationAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, StationContentProvider.CONTENT_URI_STATION, null, null, null, BaseColumns._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        mStationAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mStationAdapter.swapCursor(null);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("StationListActivity", "LocationClient connected. Requesting updates");
        mStationAdapter.setLocation(mLocationClient.getLastLocation());
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(REQUEST_INTERVAL);
        locationRequest.setFastestInterval(MIN_REQUEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Log.d("StationListActivity", "LocationClient disconnected.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("StationListActivity", "Failed to get LocationClient connection.");
        Toast.makeText(this, "LocationClient connection failed.", Toast.LENGTH_LONG);
    }

    @Override
    public void onLocationChanged(Location location) {
        mStationAdapter.setLocation(location);
    }
}
