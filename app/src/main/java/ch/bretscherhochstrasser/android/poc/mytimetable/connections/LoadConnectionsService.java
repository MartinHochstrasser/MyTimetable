package ch.bretscherhochstrasser.android.poc.mytimetable.connections;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.Connection;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.ConnectionDownloader;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.ConnectionParser;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.JsonDownloader;
import ch.bretscherhochstrasser.android.poc.mytimetable.notification.ConnectionsNotificationManager;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class LoadConnectionsService extends IntentService {

    public static final String ACTION_LOAD_CONNECTIONS = "ch.bretscherhochstrasser.android.poc.mytimetable.connections.action.LOAD_CONNECTIONS";

    public static final String EXTRA_VOICE_REPLY_STATION = "extra_voice_reply_station";
    private static final String EXTRA_FROM_STATION_ID = "from_station_id";

    private ConnectionDownloader mConnectionDownloader;
    private ConnectionsNotificationManager mConnectionsNotificationManager;

    public LoadConnectionsService() {
        super("LoadConnectionsService");
        mConnectionDownloader = new ConnectionDownloader(new JsonDownloader(), new ConnectionParser());
        mConnectionsNotificationManager = new ConnectionsNotificationManager(this);
    }

    public static PendingIntent getPendingLoadConnectionsServiceIntent(final String fromStationId, final Context context) {
        Intent startIntent = new Intent();
        startIntent.setClass(context, LoadConnectionsService.class);
        startIntent.setAction(ACTION_LOAD_CONNECTIONS);
        startIntent.putExtra(EXTRA_FROM_STATION_ID, fromStationId);
        return PendingIntent.getService(context, 0, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD_CONNECTIONS.equals(action) && intent.hasExtra(EXTRA_VOICE_REPLY_STATION)) {
                final String destinationStation = intent.getStringExtra(EXTRA_VOICE_REPLY_STATION);
                final String fromStation = intent.getStringExtra(EXTRA_FROM_STATION_ID);
                if (fromStation != null) {
                    try {
                        List<Connection> connectionList = mConnectionDownloader.getConnections(fromStation, destinationStation);
                        if (!connectionList.isEmpty()) {
                            mConnectionsNotificationManager.showConnectionNotifiaction(connectionList.get(0));
                        }
                    } catch (IOException e) {
                        Log.e("LoadConnectionsService", "Error downloading connections", e);
                    }
                }
            }
        }
    }

}
