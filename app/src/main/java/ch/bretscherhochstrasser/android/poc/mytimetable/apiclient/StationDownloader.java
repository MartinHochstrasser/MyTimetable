package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import android.location.Location;
import android.util.Log;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;

/**
 * Created by marti_000 on 18.06.2014.
 */
public class StationDownloader {

    private static final String LOG_TAG = "StationDownloader";

    private static final String GET_NEARBY_STATIONS_URL_FORMAT = "http://transport.opendata.ch/v1/locations?x=%1$f&y=%2$f";

    final JsonDownloader mJsonDownloader;
    final StationParser mStationParser;

    public StationDownloader(JsonDownloader jsonDownloader, StationParser stationParser) {
        mJsonDownloader = jsonDownloader;
        mStationParser = stationParser;
    }

    public List<Station> getNearbyStations(Location location) throws IOException {
        final String getNearbyStationsUrl = String.format(Locale.US, GET_NEARBY_STATIONS_URL_FORMAT, location.getLatitude(), location.getLongitude());
        Log.d(LOG_TAG, "Request URL: " + getNearbyStationsUrl);
        try {
            final String json = mJsonDownloader.downloadJson(getNearbyStationsUrl);
            final List<Station> stations = mStationParser.parseStations(json);
            Log.d("StationDownloader", String.format("Got %d stations from server.", stations.size()));
            return stations;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error downloading stations json", e);
            throw e;
        } catch (JsonParseException e) {
            Log.e(LOG_TAG, "Error parsing stations json", e);
            throw new IOException("Error parsing stations json", e);
        }
    }
}
