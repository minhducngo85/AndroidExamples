package com.android.tuto.ch6earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
    // A reference to the local object
    private EarthQuakeListFragment local;

    ArrayAdapter<Quake> adapter;
    ArrayList<Quake> earthquakes = new ArrayList<Quake>();
    private static final String TAG = "EARTHQUAKE";

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set reference to this activity
        local = this;

        int layoutID = android.R.layout.simple_list_item_1;
        adapter = new ArrayAdapter<Quake>(this.getActivity(), layoutID, earthquakes);
        setListAdapter(adapter);

        /** download rss feeds */
        GetRSSDataTask task = new GetRSSDataTask();
        String quakeFeed = getString(R.string.quake_feed);
        task.execute(quakeFeed);
        Log.i(TAG, Thread.currentThread().getName());
    }

    private class GetRSSDataTask extends AsyncTask<String, Void, List<Quake>> {
        @Override
        protected List<Quake> doInBackground(String... urls) {
            // Debug the task thread name
            Log.i(TAG, Thread.currentThread().getName());
            Log.i(TAG, urls[0]);
            // Parse RSS, get items
            return fetchEarthquakes(urls[0]);

        }

        @Override
        protected void onPostExecute(List<Quake> result) {
            // Create a list adapter
            ArrayAdapter<Quake> aa = new ArrayAdapter<Quake>(local.getActivity(), android.R.layout.simple_list_item_1, result);
            local.setListAdapter(aa);
        }
    }

    public List<Quake> fetchEarthquakes(String quakeFeed) {
        Log.i(TAG, "refreshEarthquakes() called");
        // Get the XML
        URL url;
        try {
            url = new URL(quakeFeed);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            Log.i(TAG, "Open connection");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "Response Code ok");
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                // Parse the earthquake feed.
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                Log.i(TAG, "DocELement");
                // Clear the old earthquakes
                earthquakes.clear();

                // Get a list of each earthquake entry.
                NodeList nl = docEle.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 1) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Log.d(TAG, "entry " + i);
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
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

                        Quake quake = new Quake(qdate, details, l, magnitude, linkString);
                        Log.i(TAG, quake.toString());
                        // Process a newly found earthquake
                        earthquakes.add(quake);
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
        return earthquakes;
    }

}
