package com.android.tuto.data.util;

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

import com.android.tuto.EarthquakeListFragment;
import com.android.tuto.data.Quake;
import com.android.tuto.database.EarthquakeDatabaseHelper;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class FeedReader extends AsyncTask<String, Void, List<Quake>> {

    private EarthquakeListFragment listFragmentInstance = null;

    /** The minimum of magnitude */
    private int minMagnitude = 0;

    /** String tag for debug mode */
    private static final String TAG = "EARTHQUAKE";

    public FeedReader(EarthquakeListFragment instance, int minMagnitude) {
        super();
        this.listFragmentInstance = instance;
        this.minMagnitude = minMagnitude;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Quake> doInBackground(String... urls) {
        // Debug the task thread name
        Log.i(TAG, "Current Thread: " + Thread.currentThread().getName());
        Log.i(TAG, "Fetching information from the url: " + urls[0]);
        // Parse RSS, get items
        return fetchEarthquakes(urls[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostExecute(List<Quake> result) {
        listFragmentInstance.getEarthquakes().clear();
        listFragmentInstance.getEarthquakes().addAll(result);
        listFragmentInstance.getAdapter().notifyDataSetChanged();

        saveQuakeIntoDB();
        readAllQuakes();
    }

    /**
     * fetching earthquake within the day from the feed on the Internet.
     * 
     * @param quakeFeed
     *            the feed link
     * @return list of earth quakes
     */
    public List<Quake> fetchEarthquakes(String quakeFeed) {
        Log.i(TAG, "refreshEarthquakes() called");
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

                        if (!point.equals("0 -0") && magnitude >= minMagnitude) {
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
     * save new quakes into db
     */
    public void saveQuakeIntoDB() {
        EarthquakeDatabaseHelper dbHelper = new EarthquakeDatabaseHelper(listFragmentInstance.getActivity().getApplicationContext());
        for (Quake item : listFragmentInstance.getEarthquakes()) {
            if (dbHelper.findQuakeByDate(item.getDate().getTime()) == null) {
                dbHelper.addQuake(item);
            }
        }
    }

    public void readAllQuakes() {
        EarthquakeDatabaseHelper dbHelper = new EarthquakeDatabaseHelper(listFragmentInstance.getActivity().getApplicationContext());
        for (Quake item : dbHelper.getAllQuakes()) {
            Log.i(TAG, item.getId() + " -> " + item.toString());
        }
    }
}
