package ch.bretscherhochstrasser.android.poc.mytimetable.notification;

import android.app.Notification;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.R;
import ch.bretscherhochstrasser.android.poc.mytimetable.apiclient.Connection;
import ch.bretscherhochstrasser.android.poc.mytimetable.common.TextUtil;

/**
 * Created by marti_000 on 24.06.2014.
 */
public class ConnectionsNotificationManager {

    private static final String LOG_TAG = "ConnectionNotificationManager";

    private final Context mContext;
    private final Handler mHandler;

    public ConnectionsNotificationManager(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void showConnectionNotifiaction(Connection connection) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(mContext.getString(R.string.connection_notification_to_format, connection.to.station.name));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(new SpannableString(formatDuration(connection.duration) + "\n"));
        spannableStringBuilder.append(new SpannableString(mContext.getString(R.string.connection_notification_transfers_format, connection.transfers)) + "\n");
        builder.setContentText(spannableStringBuilder);
        builder.setSmallIcon(R.drawable.ic_stat_departures);
        builder.setPriority(Notification.PRIORITY_HIGH); // we set priority high, because we "requested" it

        List<Notification> notificationPages = new ArrayList<Notification>();
        for (Connection.Section section : connection.sections) {
            notificationPages.add(createSectionNotificationPage(section));
        }

        WearableNotifications.Builder wearableNotificationsBuilder = new WearableNotifications.Builder(builder);
        wearableNotificationsBuilder.addPages(notificationPages);
        final Notification connectionNotification = wearableNotificationsBuilder.build();

        Log.d(LOG_TAG, "Removing old notification");
        NotificationManagerCompat.from(mContext).cancel(NotificationUtil.TIME_TABLE_NOTIFICATION_ID);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "Posting connection notification");
                NotificationManagerCompat.from(mContext).notify(NotificationUtil.TIME_TABLE_NOTIFICATION_ID, connectionNotification);

            }
        }, 250); // delay a bit to prevent blocking issue
    }

    private Notification createSectionNotificationPage(Connection.Section section) {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(section.departure.station.name);

        SpannableStringBuilder sectionTextBuilder = new SpannableStringBuilder();

        if (section.journey != null) {
            sectionTextBuilder.append(TextUtil.createStyledText(section.journey.name + "\n", Typeface.BOLD));
            sectionTextBuilder.append(new SpannableString(mContext.getString(R.string.connection_notification_to_format, section.journey.to) + "\n"));
        } else if (section.walk != null) {
            sectionTextBuilder.append(new SpannableString(mContext.getString(R.string.connection_notification_walk_format, formatDuration(section.walk.duration)) + "\n"));
        }

        sectionTextBuilder.append(new SpannableString(mContext.getString(R.string.connection_notification_departing_format, section.departure.departure) + "\n"));
        if (section.departure.platform != null && !section.departure.platform.isEmpty()) {
            sectionTextBuilder.append(new SpannableString(mContext.getString(R.string.connection_notification_platform_format, section.departure.platform) + "\n"));
        }

        sectionTextBuilder.append(mContext.getString(R.string.connection_notification_departure_arrival_divider) + "\n");

        sectionTextBuilder.append(TextUtil.createStyledText(section.arrival.station.name + "\n", Typeface.BOLD));
        sectionTextBuilder.append(new SpannableString(mContext.getString(R.string.connection_notification_arriving_format, section.arrival.arrival) + "\n"));
        if (section.arrival.platform != null && !section.arrival.platform.isEmpty()) {
            sectionTextBuilder.append(new SpannableString(mContext.getString(R.string.connection_notification_platform_format, section.arrival.platform) + "\n"));
        }

        style.bigText(sectionTextBuilder);

        NotificationCompat.Builder sectionNotificationPageBuilder = new NotificationCompat.Builder(mContext);
        sectionNotificationPageBuilder.setStyle(style);
        return sectionNotificationPageBuilder.build();
    }

    private String formatDuration(String duration) {
        // duration should have format 00d00:00:00 or 00:00:00 transformed into 00:00
        final int indexOfD = duration.indexOf('d');
        final String durationTime = indexOfD == -1 ? duration : duration.substring(indexOfD + 1);
        return durationTime.substring(0, durationTime.lastIndexOf(':'));
    }

}