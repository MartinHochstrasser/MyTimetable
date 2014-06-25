package ch.bretscherhochstrasser.android.poc.mytimetable.common;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

/**
 * Created by marti_000 on 18.06.2014.
 */
public abstract class LocationClientIntentService extends DelayedStartIntentService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    protected LocationClient mLocationClient;

    public LocationClientIntentService(String name) {
        super("LocationClientIntentService[" + name + "]");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("RequestLocationUpdateService", "LocationClient connected. Starting intent delivery.");

        startIntentDelivery();
    }

    @Override
    public void onDisconnected() {
        Log.i("RequestLocationUpdateService", "LocationClient disconnected.");
        mLocationClient = null;
        stopSelf();
    }

    @Override
    public void onDestroy() {
        mLocationClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("RequestLocationUpdateService", "LocationClient connection failed.");
    }

}

