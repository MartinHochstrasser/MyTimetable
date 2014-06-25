package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;

/**
 * Created by marti_000 on 16.06.2014.
 */
public class GsonProvider {

    private static Gson instance;

    public static synchronized Gson gson() {
        if (instance == null) {
            final GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            builder.registerTypeAdapter(Station.class, new StationDeserializer());
            instance = builder.create();
        }
        return instance;
    }

}
