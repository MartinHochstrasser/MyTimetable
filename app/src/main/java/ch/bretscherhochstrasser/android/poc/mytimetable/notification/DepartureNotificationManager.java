package ch.bretscherhochstrasser.android.poc.mytimetable.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.RemoteInput;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.R;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.DepartureDownloader;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.PreferenceUtil;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.TextUtil;
import ch.bretscherhochstrasser.android.poc.mytimetable.connections.LoadConnectionsService;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.CupboardProvider;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.Departure;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.StationContentProvider;

/**
 * Created by marti_000 on 21.06.2014.
 */
public class DepartureNotificationManager {

    private static final String LOG_TAG = "DepartureNotificationManager";

    private final Context mContext;
    private final DepartureDownloader mDepartureDownloader;

    public DepartureNotificationManager(final Context context, final DepartureDownloader departureDownloader) {
        mContext = context;
        mDepartureDownloader = departureDownloader;
    }

    public void showTimeTableNotifiaction(final long stationId) {
        final Uri stationUri = ContentUris.withAppendedId(StationContentProvider.CONTENT_URI_STATION, stationId);
        final Station station = CupboardProvider.cupboard().withContext(mContext).get(stationUri, Station.class);
        if (station != null) {
            try {
                int limit = PreferenceUtil.getIntPreference(R.string.settings_fragment_limit_departures_key, R.string.settings_fragment_limit_departures_default, mContext);
                List<Departure> departures = mDepartureDownloader.getDeparturesForStation(station, limit);
                showDepartureNotification(station, departures);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error getting departures for station " + station);
            }
        }

    }

    private void showDepartureNotification(Station station, List<Departure> departures) {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (Departure departure : departures) {
            spannableStringBuilder.append(getTimteTableLine(departure));
        }
        style.bigText(spannableStringBuilder);
        style.setBigContentTitle(station.name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(mContext.getString(R.string.departure_notification_title_format, station.name));
        builder.setContentText(mContext.getString(R.string.departure_notification_content_text_format, departures.size(), station.name));
        builder.setSmallIcon(R.drawable.ic_stat_departures);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setStyle(style);

        WearableNotifications.Builder wearableBuilder = new WearableNotifications.Builder(builder);
        RemoteInput remoteInput = new RemoteInput.Builder(LoadConnectionsService.EXTRA_VOICE_REPLY_STATION)
                .setLabel(mContext.getString(R.string.departure_notification_action_connection_voice_input_title))
                .build();
        final PendingIntent findConnectionPendingIntent = LoadConnectionsService.getPendingLoadConnectionsServiceIntent(Long.toString(station._id), mContext);
        WearableNotifications.Action findConnectionAction = new WearableNotifications.Action.Builder(R.drawable.ic_action_connection,
                mContext.getString(R.string.departure_notification_action_connection_title), findConnectionPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        wearableBuilder.addAction(findConnectionAction);
        Notification notification = wearableBuilder.build();

        NotificationManagerCompat.from(mContext).notify(NotificationUtil.TIME_TABLE_NOTIFICATION_ID, notification);
    }

    private Spannable getTimteTableLine(Departure departure) {
        String deltaMinutes = mContext.getString(R.string.departure_notification_delta_time_format, getDeltaMinutesFromNow(departure));
        Spannable deltaMinutesStyled = TextUtil.createStyledText(deltaMinutes, Typeface.BOLD);
        Spannable nameStyled = TextUtil.createStyledText(departure.name, Typeface.BOLD);
        String destination = mContext.getString(R.string.departure_notification_destination_format, departure.to);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(deltaMinutesStyled).append(" ").append(nameStyled).append(" ").append(destination).append("\n");
        return spannableStringBuilder;
    }

    private int getDeltaMinutesFromNow(Departure departure) {
        long deltaMillis = Math.max(departure.time.getTime() - new Date().getTime(), 0);
        return (int) (deltaMillis / 60000);
    }

    public void closeDepartureNotification() {
        NotificationManagerCompat.from(mContext).cancel(NotificationUtil.TIME_TABLE_NOTIFICATION_ID);
    }

}
