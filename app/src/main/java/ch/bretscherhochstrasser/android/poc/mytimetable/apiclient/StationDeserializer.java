package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;

/**
 * Created by marti_000 on 23.06.2014.
 */
public class StationDeserializer implements JsonDeserializer<Station> {

    private static final String ELEMENT_NAME_ID = "id";
    private static final String ELEMENT_NAME_NAME = "name";
    private static final String ELEMENT_NAME_COORDIANTE = "coordinate";

    @Override
    public Station deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject stationJson = jsonElement.getAsJsonObject();
        final Station station = new Station();
        station._id = stationJson.get(ELEMENT_NAME_ID).getAsLong();
        station.name = stationJson.get(ELEMENT_NAME_NAME).getAsString();
        final Coordinate coordinate = jsonDeserializationContext.deserialize(stationJson.getAsJsonObject(ELEMENT_NAME_COORDIANTE), Coordinate.class);
        station.latitude = coordinate.x;
        station.longitude = coordinate.y;
        return station;
    }

    private static class Coordinate {
        String type;
        float x;
        float y;
    }

}
