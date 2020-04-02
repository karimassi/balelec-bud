package ch.epfl.balelecbud.util.http;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

public class FetchDataTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        InputStream in = null;
        String result = null;
        HttpClient client = new DefaultHttpClient()
    }
}
