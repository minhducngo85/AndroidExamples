package com.android.tuto;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.android.tuto.data.Quake;
import com.android.tuto.pref.PreferencesActivity;
import com.android.tuto.util.DatabaseHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class EarthquakeUpdateService extends Service {

    private DatabaseHelper dbHelper;

    private String TAG = "EARTHQUAKE_SERVICE";

    private Timer updateTimer;

    private static final String SERVICE_NAME = "earthquakeUpdates";

    /**
     * Services can be bound to Activities, with the latter maintaining a reference to an instance of the former, enabling you to make method calls on
     * the running Service as you would on any other instantiated class.
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        Log.i(TAG, "oncreate()  wihtin service called");
        updateTimer = new Timer(SERVICE_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        boolean autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);

        updateTimer.cancel();
        if (autoUpdateChecked) {
            updateTimer = new Timer(SERVICE_NAME);
            updateTimer.schedule(doRefesh, 0, updateFreq * 60 * 1000);
        } else {
            FeedReader task = new FeedReader();
            String quakeFeed = getString(R.string.quake_feed);
            task.execute(quakeFeed);
        }
        return Service.START_STICKY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addNewQuake(Quake quake) {
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        if (dbHelper.findQuakeByDate(quake.getDate().getTime()) == null) {
            dbHelper.addQuake(quake);
        }
    }

    private TimerTask doRefesh = new TimerTask() {
        @Override
        public void run() {
            Log.i(TAG, "doRefesh() within service called");
            String quakeFeed = getString(R.string.quake_feed);
            List<Quake> quakes = fetchEarthquakes(quakeFeed);
            for (Quake quake : quakes) {
                addNewQuake(quake);
            }
        }
    };

    /**
     * fetching earthquake within the day from the feed on the Internet.
     * 
     * @param quakeFeed
     *            the feed link
     * @return list of earth quakes
     */
    public List<Quake> fetchEarthquakes(String quakeFeed) {
        Log.i(TAG, "fetchEarthquakes(String quakeFeed) called");
        Log.i(TAG, "Internet source: " + quakeFeed);
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int minMag = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        List<Quake> results = new ArrayList<Quake>();
        URL url;
        try {
            url = new URL(quakeFeed);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                // Parse the earthquake feed.
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                // Get a list of each earthquake entry.
                NodeList nl = docEle.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 1) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        // Log.d(TAG, "------------------entry " + i);
                        Element entry = (Element) nl.item(i);
                        Element title = (Element) entry.getElementsByTagName("title").item(0);
                        Element g = (Element) entry.getElementsByTagName("georss:point").item(0);

                        Element when = (Element) entry.getElementsByTagName("updated").item(0);
                        Element link = (Element) entry.getElementsByTagName("link").item(0);

                        String details = title.getFirstChild().getNodeValue();
                        String linkString = link.getAttribute("href");
                        String point = "0 -0";
                        if (g != null) {
                            point = g.getFirstChild().getNodeValue();
                        }
                        String dt = when.getFirstChild().getNodeValue();

                        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance();
                        sdf.applyPattern("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

                        Date qdate = new GregorianCalendar(0, 0, 0).getTime();
                        try {
                            qdate = sdf.parse(dt);
                        } catch (ParseException e) {
                            Log.d(TAG, "Date parsing exception.", e);
                        }

                        String[] location = point.split(" -");
                        Location l = new Location("dummyGPS");
                        try {
                            l.setLatitude(Double.parseDouble(location[0]));
                            l.setLongitude(Double.parseDouble(location[1]));
                        } catch (NumberFormatException e) {
                            l.setLatitude(0);
                            l.setLongitude(0);
                        }
                        String magnitudeString = details.split(" ")[1];
                        int end = magnitudeString.length();
                        double magnitude = 0d;
                        try {
                            magnitude = Double.parseDouble(magnitudeString.substring(0, end));
                        } catch (NumberFormatException e) {

                        }

                        if (details.contains(" -")) {
                            details = details.split(" -")[1].trim();
                        }
                        //
                        // Log.d(TAG, "link: " + linkString);
                        // Log.d(TAG, "Details: " + details);
                        // Log.d(TAG, "Point: " + point);
                        // Log.d(TAG, "When: " + sdf.format(qdate));
                        // Log.d(TAG, "Location" + l.toString());
                        // Log.d(TAG, "Magnitude: " + magnitude);

                        if (!point.equals("0 -0") && magnitude >= minMag) {
                            Quake quake = new Quake(qdate, details, l, magnitude, linkString);
                            // Log.i(TAG, quake.toString());
                            results.add(quake);
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Parser Configuration Exception", e);
        } catch (SAXException e) {
            Log.e(TAG, "SAX Exception", e);
        } finally {

        }
        return results;
    }

    /**
     * 
     * read quakes from Internet
     * 
     * @author minhducngo
     *
     */
    private class FeedReader extends AsyncTask<String, Void, List<Quake>> {

        public FeedReader() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<Quake> doInBackground(String... urls) {
            // Debug the task thread name
            Log.i(TAG, "Current Thread: " + Thread.currentThread().getName());
            // Parse RSS, get items
            return fetchEarthquakes(urls[0]);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(List<Quake> result) {
            Log.i(TAG, "onPostExecute(List<Quake> result)");
            for (Quake quake : result) {
                addNewQuake(quake);
            }
        }
    }

}
