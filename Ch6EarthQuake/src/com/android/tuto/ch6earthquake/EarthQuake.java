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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class EarthQuake extends Activity {

    /** The list adapter */
    private EarthquakeItemDapter adapter;

    /** The list resources */
    private ArrayList<Quake> earthquakes = new ArrayList<Quake>();

    /** The constant string tag */
    private static final String TAG = "EARTHQUAKE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get reference to the Fragment
        FragmentManager fm = getFragmentManager();
        EarthQuakeListFragment earthquakesFragment = (EarthQuakeListFragment) fm.findFragmentById(R.id.EarthQuakeListFragment);

        // Create the array adapter to bind the array to the ListView
        int resID = R.layout.earthquakelist_item;
        adapter = new EarthquakeItemDapter(this, resID, earthquakes);
        
        earthquakesFragment.setListAdapter(adapter);

        Thread t = new Thread(new Runnable() {
            public void run() {
                refreshEarthquakes();
            }
        });
        t.start();
    }

    /**
     * gets earth quakes from internet resources.
     */
    private void refreshEarthquakes() {
        Log.i(TAG, "refreshEarthquakes()");
        earthquakes.clear();
        URL url;
        String quakeFeed = getString(R.string.quake_feed);
        try {
            url = new URL(quakeFeed);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                // Parse the earth quake feed
                Document dom = db.parse(in);
                Element docElement = dom.getDocumentElement();
                Log.i(TAG, "DocELement");

                // gets list of earthquake entry
                NodeList nl = docElement.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        // element.getTextContent() = element.getFirstChild().getNodeValue()
                        Log.i(TAG, "Entry(" + i + ")");

                        Element entry = (Element) nl.item(i);
                        Element title = (Element) entry.getElementsByTagName("title").item(0);
                        Log.i(TAG, "Title: " + title.getTextContent());
                        Element g = (Element) entry.getElementsByTagName("georss:point").item(0);
                        if (g != null) {
                            Log.i(TAG, "Geolocation: " + g.getTextContent());
                        }

                        Element when = (Element) entry.getElementsByTagName("updated").item(0);
                        Log.i(TAG, "When: " + when.getTextContent());
                        String dt = when.getFirstChild().getNodeValue();
                        Log.i(TAG, "dt: " + dt);

                        Element link = (Element) entry.getElementsByTagName("link").item(0);

                        // String details = title.getFirstChild().getNodeValue();
                        Element detailsE = (Element) entry.getElementsByTagName("summary").item(0);

                        String linkString = link.getAttribute("href");
                        Log.i(TAG, "linkString: " + linkString);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                        Date qdate = new GregorianCalendar(0, 0, 0).getTime();
                        try {
                            qdate = sdf.parse(dt);
                        } catch (ParseException e) {
                            Log.d(TAG, "Date parsing exception.", e);
                        }
                        Location l = new Location("dummyGPS");
                        l.setLatitude(0d);
                        l.setLongitude(0d);
                        if (g != null) {
                            String point = g.getFirstChild().getNodeValue();
                            String[] location = point.split(" -");
                            l.setLatitude(Double.parseDouble(location[0]));
                            l.setLongitude(Double.parseDouble(location[1]));
                        }

                        double magnitude = 2.5d;
                        String details = detailsE.getFirstChild().getNodeValue();
                        Log.i(TAG, "Details: " + details);

                        Quake quake = new Quake(qdate, details, l, magnitude, linkString);
                        Log.i(TAG, "quake:  " + quake);
                        addNewQuake(quake);
                        Log.i(TAG, "added entry " + i);
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException", e);
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "ParserConfigurationException", e);
        } catch (SAXException e) {
            Log.d(TAG, "SAXException", e);
        }
    }

    private void addNewQuake(Quake quake) {
        // Add the new quake to our list of earthquakes.
        earthquakes.add(quake);

        // Notify the array adapter of a change.
        adapter.notifyDataSetChanged();
    }
}
