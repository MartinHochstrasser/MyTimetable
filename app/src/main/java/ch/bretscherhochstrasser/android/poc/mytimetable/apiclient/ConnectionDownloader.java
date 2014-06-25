package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import android.util.Log;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * Created by marti_000 on 18.06.2014.
 */
public class ConnectionDownloader {

    private static final String LOG_TAG = "ConnectionDownloader";

    private static final String GET_CONNECTIONS_URL_FORMAT = "http://transport.opendata.ch/v1/connections?from=%1$s&to=%2$s&limit=1";

    final JsonDownloader mJsonDownloader;
    final ConnectionParser mConnectionParser;

    public ConnectionDownloader(JsonDownloader jsonDownloader, ConnectionParser connectionParser) {
        mJsonDownloader = jsonDownloader;
        mConnectionParser = connectionParser;
    }

    public List<Connection> getConnections(String fromStatoinId, String toStationName) throws IOException {
        final String toStation = URLEncoder.encode(toStationName, "UTF-8");
        final String getNearbyStationsUrl = String.format(Locale.US, GET_CONNECTIONS_URL_FORMAT, fromStatoinId, toStation);
        Log.d(LOG_TAG, "Request URL: " + getNearbyStationsUrl);
        try {
            final String json = mJsonDownloader.downloadJson(getNearbyStationsUrl);
            final List<Connection> connections = mConnectionParser.parseStations(json);
            Log.d("ConnectionDownloader", String.format("Got %d connections from server.", connections.size()));
            return connections;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error downloading stations json", e);
            throw e;
        } catch (JsonParseException e) {
            Log.e(LOG_TAG, "Error parsing connections json", e);
            throw new IOException("Error parsing connections json", e);
        }
    }
}
