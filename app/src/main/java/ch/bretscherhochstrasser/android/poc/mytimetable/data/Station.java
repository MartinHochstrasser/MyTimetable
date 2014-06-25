package ch.bretscherhochstrasser.android.poc.mytimetable.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marti_000 on 15.06.2014.
 */
public class Station {

    @SerializedName("id")
    public Long _id;
    public String name;
    public float latitude;
    public float longitude;
    public StationFenceState fenceState = StationFenceState.NOT_THERE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (_id != null ? !_id.equals(station._id) : station._id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _id != null ? _id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Station [%1$d, %2$s, %3$f, %4$f]", _id, name, latitude, longitude);
    }
}
