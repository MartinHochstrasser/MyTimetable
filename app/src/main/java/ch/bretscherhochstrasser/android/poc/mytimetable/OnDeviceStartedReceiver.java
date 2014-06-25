package ch.bretscherhochstrasser.android.poc.mytimetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ch.bretscherhochstrasser.android.poc.mytimetable.common.PreferenceUtil;
import ch.bretscherhochstrasser.android.poc.mytimetable.locationupdates.RequestLocationUpdatesService;

public class OnDeviceStartedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PreferenceUtil.departureNotificationsEnabled(context)) {
            Log.d("OnDeviceStartedReceiver", "start requesting for location updates");
            RequestLocationUpdatesService.startActionRequestLocationUpdates(context);
        }
    }

}
