package atlas.atlas;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ReverseGeocoding {

    double OldLatitude = 0;
    double  OldLongitude = 0;


    double NewLatitude;
    double  NewLongitude;

    double distance = 0;
    double speed = 0;


    long OldPositionUpdateTime = 0;
    long CurrentPositionUpdateTime;
    double TimeBetweenUpdates = 0;

    Marker trackerMarker;

    //MarkerOptions trackerMarker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps");


    String address;

    DecimalFormat numberFormat = new DecimalFormat("#.00");




    public Geocoder geocoder;// = new Geocoder(getApplicationContext(), Locale.getDefault());





    ReverseGeocoding ( Activity activity, Marker trackerMarker)
    {
        geocoder = new Geocoder(activity,Locale.getDefault());
        this.trackerMarker = trackerMarker;

    }






    public void UpdateGeocoder (double Latitude, double Longitude)
    {
        this.OldLatitude = this.NewLatitude;
        this.OldLongitude = this.NewLongitude;
        this.NewLatitude = Latitude;
        this.NewLongitude = Longitude;
        this.OldPositionUpdateTime = this.CurrentPositionUpdateTime;
        this.CurrentPositionUpdateTime = System.currentTimeMillis() / 1000l;
        TimeBetweenUpdates = CurrentPositionUpdateTime - OldPositionUpdateTime;



        if(OldLatitude !=0 && OldLongitude !=0 && OldPositionUpdateTime !=0)
        {
            //distance between the last 2 points in meters
            distance = getDistance(OldLatitude, OldLongitude, NewLatitude, NewLongitude);


            //speed in kilometers
            speed = (distance/(1000*TimeBetweenUpdates))*3600;

            //Define a format with 2 digits after the decimals to be passed to the speed
            numberFormat = new DecimalFormat("#.00");


        }







    }



    private static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // for haversine use R = 6372.8 km instead of 6371 km
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        //double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        // simplify haversine:
        //return 2 * R * 1000 * Math.asin(Math.sqrt(a));
    }







    public void getAddress(Activity activity)
    {
        try {
            //Code for Reverse Geocoding (tranforms latitude and longitude coordiantes into street address)
            List<Address> listAddresses = geocoder.getFromLocation(NewLatitude, NewLongitude, 1);

            if(listAddresses != null && listAddresses.size() >0){
                // log returning the whole address (index is zero because we want only one address)
                Log.i("PlaceInfo", listAddresses.get(0).toString());

                 address = "";







                if (listAddresses.get(0).getSubThoroughfare() != null) {
                    //returns the street number of the address
                    address += listAddresses.get(0).getSubThoroughfare() + " ";

                }



                if (listAddresses.get(0).getThoroughfare() != null) {
                    //returns the thoroughfare name of the address
                    address += listAddresses.get(0).getThoroughfare() + ", ";

                }

                if (listAddresses.get(0).getLocality() != null) {
                    //returns the locality of the address
                    address += listAddresses.get(0).getLocality() + ", ";

                }

                if (listAddresses.get(0).getPostalCode() != null) {
                    //returns the postal code
                    address += listAddresses.get(0).getPostalCode() + ", ";

                }

                if (listAddresses.get(0).getCountryName() != null) {
                    //returns the country name
                    address += listAddresses.get(0).getCountryName()+" ";

                }

                if(OldLatitude !=0 && OldLongitude !=0)
                {   //returns the speed of the address in #.00 format
                    address += numberFormat.format(speed)+" Km/h";
                }

                Toast.makeText(activity, address, Toast.LENGTH_SHORT).show();

            }

        } catch (
                IOException e) {

            e.printStackTrace();

        }
    }














}
