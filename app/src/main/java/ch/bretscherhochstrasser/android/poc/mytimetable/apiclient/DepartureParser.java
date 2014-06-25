package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.Departure;

/**
 * Created by marti_000 on 21.06.2014.
 */
public class DepartureParser {

    private static final String LOG_TAG = "DepartureParser";

    private static final String ELEMENT_NAME_STATIONBOARD = "stationboard";
    private static final String ELEMENT_NAME_STOP = "stop";

    public List<Departure> parseDepartures(String json) throws JsonParseException {
        try {
            final JsonObject jsonResponse = new JsonParser().parse(json).getAsJsonObject();
            final JsonArray stationJsonArray = jsonResponse.getAsJsonArray(ELEMENT_NAME_STATIONBOARD);
            final Gson gson = GsonProvider.gson();
            final List<Departure> departures = new ArrayList<Departure>();
            for (JsonElement stationJsonElement : stationJsonArray) {
                final JsonObject stationJson = stationJsonElement.getAsJsonObject();
                final Departure departure = gson.fromJson(stationJson, Departure.class);
                final Stop stop = gson.fromJson(stationJson.getAsJsonObject(ELEMENT_NAME_STOP), Stop.class);
                departure.time = stop.departure;
                departure.platform = stop.platform;
                departures.add(departure);
            }
            return departures;
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

    private static class Stop {
        Date departure;
        String platform;
    }

}
