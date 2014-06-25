package ch.bretscherhochstrasser.android.poc.mytimetable.apiclient;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by marti_000 on 15.06.2014.
 */
public class JsonDownloader {

    public String downloadJson(String urlString) throws IOException {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e("JsonDownloader", "Invalid URL: " + urlString);

            throw new IOException("Invalid URL: " + urlString, e);
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("JsonDownloader", "Unexpected HTTP status: " + connection.getResponseCode());
                throw new IOException("Unexpected HTTP code");
            } else {
                return CharStreams.toString(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));
            }
        } finally {
            if (connection != null) {
                connection.getResponseCode();
            }
        }
    }

}
