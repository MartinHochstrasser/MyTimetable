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

/**
 * Created by marti_000 on 16.06.2014.
 */
public class ConnectionParser {

    public static final String LOG_TAG = "ConnectionParser";

    private static final String ELEMENT_NAME_CONNECTIONS = "connections";

    public List<Connection> parseStations(String json) throws JsonParseException {
        try {
            final JsonObject jsonResponse = new JsonParser().parse(json).getAsJsonObject();
            final JsonArray stationJsonArray = jsonResponse.getAsJsonArray(ELEMENT_NAME_CONNECTIONS);
            final Gson gson = GsonProvider.gson();
            Type connectionListType = new TypeToken<List<Connection>>() {
            }.getType();
            return gson.fromJson(stationJsonArray, connectionListType);
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
