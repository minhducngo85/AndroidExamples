package com.android.tuto.ch6earthquake;

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

import android.app.ListFragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class EarthQuakeListFragment extends ListFragment {

    /** A reference to the current instance */
    private EarthQuakeListFragment currentInstance;

    /** The list adapter */
    private ArrayAdapter<Quake> adapter;

    /** List resources */
    private List<Quake> earthquakes = new ArrayList<Quake>();

    /** String tag for debug mode */
    private static final String TAG = "EARTHQUAKE";

    /**
     * Create view once activity created
     */
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set reference to this activity
        currentInstance = this;

        int layoutID = android.R.layout.simple_list_item_1;
        adapter = new ArrayAdapter<Quake>(this.getActivity(), layoutID, earthquakes);
        setListAdapter(adapter);

        /** download rss feeds */
        GetRSSDataTask task = new GetRSSDataTask();
        String quakeFeed = getString(R.string.quake_feed);
        task.execute(quakeFeed);
        Log.i(TAG, "Current Thread: " + Thread.currentThread().getName());
    }

    /**
     * Network operations should always be performed in a background thread — a requirement that is enforced in API level 11 onwards.
     * 
     * AsyncTask to read RSS feed data
     * 
     * @author minhducngo
     *
     */
    private class GetRSSDataTask extends AsyncTask<String, Void, List<Quake>> {
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
            currentInstance.earthquakes.clear();
            currentInstance.earthquakes.addAll(result);
            // adapter = new ArrayAdapter<Quake>(local.getActivity(), android.R.layout.simple_list_item_1, result);
            // local.setListAdapter(adapter);
            currentInstance.adapter.notifyDataSetChanged();
        }
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
                        // Log.d(TAG, "entry " + i);
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
                        sdf.applyPattern("yyyy-MM-dd'T'hh:mm:ss'Z'");

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
                        int end = magnitudeString.length() - 1;
                        double magnitude = 0d;
                        try {
                            magnitude = Double.parseDouble(magnitudeString.substring(0, end));
                        } catch (NumberFormatException e) {

                        }

                        if (details.contains(",")) {
                            details = details.split(",")[1].trim();
                        }
                        //
                        // Log.d(TAG, "link: " + linkString);
                        // Log.d(TAG, "Details: " + details);
                        // Log.d(TAG, "Point: " + point);
                        // Log.d(TAG, "When: " + sdf.format(qdate));
                        // Log.d(TAG, "Location" + l.toString());
                        // Log.d(TAG, "Magnitude: " + magnitude);

                        if (!point.equals("0 -0")) {
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

}
