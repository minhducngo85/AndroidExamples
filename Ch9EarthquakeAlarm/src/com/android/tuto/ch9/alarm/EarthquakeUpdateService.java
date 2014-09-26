package com.android.tuto.ch9.alarm;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.android.tuto.ch9.alarm.data.Quake;
import com.android.tuto.ch9.alarm.pref.PreferencesActivity;
import com.android.tuto.ch9.alarm.util.DatabaseHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * The Update service. This time, the service uses Alarm isntead of Timmer.
 * 
 * @author minhducngo
 *
 */
public class EarthquakeUpdateService extends Service {

    /** Teh database helper */
    private DatabaseHelper dbHelper;

    private String TAG = "EARTHQUAKE_SERVICE";

    /** The alarm manager */
    private AlarmManager alarmManager;

    /** The alarm intent */
    private PendingIntent alarmIntent;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "oncreate()  wihtin service called");
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // create a alarm intent
        String ALARM_ACTION = EarthquakeAlarmReceiver.ACTION_REFRESH_QUAKE_ALARM;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentToFire, 0);
    }

    /**
     * Services can be bound to Activities, with the latter maintaining a reference to an instance of the former, enabling you to make method calls on
     * the running Service as you would on any other instantiated class.
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     * START_STICKY is used for services that are explicitly started and stopped as needed, while START_NOT_STICKY or START_REDELIVER_INTENT are used
     * for services that should only remain running while processing any commands sent to them.<br/>
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

        if (autoUpdateChecked) {
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq * 60 * 1000;
            alarmManager.setInexactRepeating(alarmType, timeToRefresh, updateFreq * 60 * 1000, alarmIntent);
        } else {
            alarmManager.cancel(alarmIntent);
        }

        /** background thread */
        String quakeFeed = getString(R.string.quake_feed);
        FeedReader task = new FeedReader();
        task.execute(quakeFeed);
        return Service.START_NOT_STICKY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "destroy service");
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
            saveNewQuakes(result);
            // stop service itself
            stopSelf();
        }

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
         * save list of quakes to db if they doesn't exist in db.
         * 
         * @param quakes
         *            list of quakes
         */
        private void saveNewQuakes(List<Quake> quakes) {
            Log.i(TAG, "saveNewQuakes");
            for (Quake quake : quakes) {
                dbHelper = DatabaseHelper.getInstance(getApplicationContext());
                if (dbHelper.findQuakeByDate(quake.getDate().getTime()) == null) {
                    dbHelper.addQuake(quake);
                }
            }
        }
    }

}
