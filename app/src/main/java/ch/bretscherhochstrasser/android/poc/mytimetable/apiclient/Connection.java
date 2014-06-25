package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import java.util.Date;
import java.util.List;

import ch.bretscherhochstrasser.android.poc.mytimetable.data.Station;

/**
 * Created by marti_000 on 24.06.2014.
 */
public class Connection {

    public From from;
    public To to;
    public String duration;
    public int transfers;
    public List<Section> sections;

    public static class From {
        public Station station;
        public Date departure;
        public String platform;
    }

    public static class To {
        public Station station;
        public Date arrival;
        public String platform;
    }

    public static class Section {
        public Walk walk;
        public Journey journey;
        public From departure;
        public To arrival;

        public static final class Journey {
            public String name;
            public String to;
        }
    }

    public static class Walk {
        public String duration;
    }
}
