package com.android.tuto.ch13whereami;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 1) detect where am i by using location <br/>
 * 2) add update location service <br/>
 * 3) Geocoder: to detect the address
 * 
 * @author minhducngo
 *
 */
public class WhereAmI extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // String provider = LocationManager.GPS_PROVIDER;

        // rather then selecting a provider, we update the application to use set of criteria and let Android find the best provider
        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        crit.setPowerRequirement(Criteria.POWER_LOW);
        crit.setAltitudeRequired(false);
        crit.setSpeedRequired(false);
        crit.setCostAllowed(true);

        String provider = locationManager.getBestProvider(crit, true);
        Location l = locationManager.getLastKnownLocation(provider);
        updateLocation(l);

        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    /**
     * update the location text on the ui
     * 
     * @param location
     */
    private void updateLocation(Location location) {
        TextView myLocationText = (TextView) findViewById(R.id.myLocationText);

        String latLongString = "No location found";
        String addressString = "No address found";
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "Lat:" + lat + "\nLong:" + lng;

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(lat, lng, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                }
                addressString = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        myLocationText.setText("Your Current Position is:\n" + latLongString + "\n\n" + addressString);
    }

    /**
     * the location listener
     */
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
    };
}
