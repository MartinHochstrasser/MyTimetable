package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import android.util.Log;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.Departure;
import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;

/**
 * Created by marti_000 on 21.06.2014.
 */
public class DepartureDownloader {

    private static final String LOG_TAG = "DepartureDownloader";

    private static final String GET_DEPARTURES_FILTERED_URL_FORMAT = "http://transport.opendata.ch/v1/stationboard?id=%1$d&limit=%2$d&fields[]=stationboard/to&fields[]=stationboard/stop/departure&fields[]=stationboard/name&fields[]=stationboard/stop/platform";

    final JsonDownloader mJsonDownloader;
    final DepartureParser mDepartureParser;

    public DepartureDownloader(JsonDownloader jsonDownloader, DepartureParser departureParser) {
        mJsonDownloader = jsonDownloader;
        mDepartureParser = departureParser;
    }

    public List<Departure> getDeparturesForStation(final Station station, final int limit) throws IOException {
        long stationId = station._id;
        final String getNearbyStationsUrl = String.format(Locale.US, GET_DEPARTURES_FILTERED_URL_FORMAT, stationId, limit);
        Log.d(LOG_TAG, "Request URL: " + getNearbyStationsUrl);
        try {
            final String json = mJsonDownloader.downloadJson(getNearbyStationsUrl);
            final List<Departure> departures = mDepartureParser.parseDepartures(json);
            Log.d("DepartureDownloader", String.format("Got %d departures from server.", departures.size()));
            return departures;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error downloading stations json", e);
            throw e;
        } catch (JsonParseException e) {
            Log.e(LOG_TAG, "Error parsing stations json", e);
            throw new IOException("Error parsing stations json", e);
        }
    }

}
