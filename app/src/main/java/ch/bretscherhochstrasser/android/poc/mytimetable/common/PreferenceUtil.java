package ch.bretscherhochstrasser.android.poc.mytimetable.common;

import android.content.Context;
import android.preference.PreferenceManager;

import ch.bretscherhochstrasser.android.poc.mytimetable.R;

/**
 * Created by marti_000 on 23.06.2014.
 */
public class PreferenceUtil {

    public static int getIntPreference(int preferenceKeyResource, int defaultValueResource, Context context) {
        final String key = context.getString(preferenceKeyResource);
        final String defaultValue = context.getString(defaultValueResource);
        final String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
        return Integer.parseInt(value);
    }

    public static boolean departureNotificationsEnabled(Context context) {
        final String key = context.getString(R.string.settings_fragment_enable_key);
        final boolean enabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, true);
        return enabled;
    }
}
