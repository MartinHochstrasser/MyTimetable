package ch.bretscherhochstrasser.android.poc.mytimetable;

import android.app.Activity;
import android.os.Bundle;

import ch.bretscherhochstrasser.android.poc.mytimetable.settings.SettingsFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the settings fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
