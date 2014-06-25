package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;

/**
 * Created by marti_000 on 16.06.2014.
 */
public class StationParser {

    public static final String LOG_TAG = "StationParser";

    private static final String ELEMENT_NAME_STATIONS = "stations";

    public List<Station> parseStations(String json) throws JsonParseException {
        try {
            final JsonObject jsonResponse = new JsonParser().parse(json).getAsJsonObject();
            final JsonArray stationJsonArray = jsonResponse.getAsJsonArray(ELEMENT_NAME_STATIONS);
            final Gson gson = GsonProvider.gson();
            Type stationListType = new TypeToken<List<Station>>() {
            }.getType();
            final List<Station> stations = gson.fromJson(stationJsonArray, stationListType);
            return stations;
        } catch (JsonParseException e) {
            Log.e(LOG_TAG, "Error parsing json: " + e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "Unexpected json: " + e.getMessage());
            throw new JsonParseException("Unexpected json", e);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Json element not found.");
            throw new JsonParseException("Json element not found", e);
        }
    }

}
