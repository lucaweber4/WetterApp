package com.example.luc.wetterapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainWetterApp extends AppCompatActivity {

    TextView sunset;
    TextView sunrise;
    String contentAsString;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wetter_app);

        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.button);
    }
    public void myClickHandler(View view)
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
        {
            new DownloadWebpageTask().execute("http://api.openweathermap.org/data/2.5/forecast?zip=");
        }
        else
        {
            Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT);
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            try
            {
                return downloadUrl(urls[0]);
            }
            catch (IOException e)
            {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        protected void onPostExecute(String result)
        {
            String[] results = result.split(";");
            sunrise.setText(results[0]);
            sunset.setText(results[1]);
        }
    }

    private String downloadUrl(String myurl) throws IOException
    {
        InputStream is = null;
        int len = 500;

        try
        {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            //Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            if(contentAsString.contains("sun"))
            {
                int riseBegin = contentAsString.indexOf("rise=\"");
                riseBegin += 6;
                int riseEnd = contentAsString.indexOf("\" set");
                String riseing = contentAsString.substring(riseBegin, riseEnd);
                int setBegin = riseEnd + 7;
                int setEnd = setBegin + 20;
                String seting = contentAsString.substring(setBegin, setEnd);
                return riseing + ";" + seting;
            }

            String contentAsString = readIt(is, len);
            return contentAsString;
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_wetter_app, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
