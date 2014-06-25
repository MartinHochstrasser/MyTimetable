package ch.bretscherhochstrasser.android.poc.mytimetable.geofences;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.TextView;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.CupboardProvider;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;

/**
 * Created by marti_000 on 19.06.2014.
 */
public class StationAdapter extends ResourceCursorAdapter {

    private Location mLocation;

    public StationAdapter(Context context, Cursor c) {
        super(context, android.R.layout.simple_list_item_2, c, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.line1 = (TextView) view.findViewById(android.R.id.text1);
            viewHolder.line2 = (TextView) view.findViewById(android.R.id.text2);
            view.setTag(viewHolder);
        }
        final Station station = CupboardProvider.cupboard().withCursor(cursor).get(Station.class);
        viewHolder.line1.setText(station.name);
        if (mLocation != null) {
            final Location stationLocation = new Location("DATA");
            stationLocation.setLongitude(station.longitude);
            stationLocation.setLatitude(station.latitude);
            final float distance = stationLocation.distanceTo(mLocation);
            viewHolder.line2.setText(String.format("%.0f m", distance));
        } else {
            viewHolder.line2.setText("-");
        }
        switch (station.fenceState) {
            case ENTERED:
                view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_bright));
                break;
            case ON_SITE:
                view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
            default:
                view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }

    public void setLocation(Location location) {
        this.mLocation = location;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView line1;
        TextView line2;
    }
}
