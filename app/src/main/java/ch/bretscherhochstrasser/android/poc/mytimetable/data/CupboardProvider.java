package ch.bretscherhochstrasser.android.poc.mytimetable.data;

import nl.qbusict.cupboard.Cupboard;

/**
 * Created by marti_000 on 15.06.2014.
 */
public class CupboardProvider {
    private static Cupboard instance;

    public static synchronized Cupboard cupboard() {
        if (instance == null) {
            instance = new Cupboard();
            instance.register(Station.class);
        }
        return instance;
    }

}
