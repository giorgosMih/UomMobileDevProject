package com.giorgosmih.flightitineraries;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONparser {

    public static void getCountryDataFromJson(String countryJsonStr, DBHandler db)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "response";
        final String OWM_COUNTRY_NAME = "name";

        JSONObject countryJson = new JSONObject(countryJsonStr);
        JSONArray countriesArray = countryJson.getJSONArray(OWM_LIST);

        int size = countriesArray.length();
        FlightSearchFragment.countries = size;
        Log.v("DBug","Countries: " + size);
        for(int i = 0; i < size; i++) {
            JSONObject country = countriesArray.getJSONObject(i);

            db.addCountry(country.getString(OWM_COUNTRY_NAME));
        }
    }

    public static String[] getCitiesAndAirportsDataFromJson(String JsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_AIRPORTS_LIST = "airports_by_countries";
        final String OWM_NAME = "name";
        final String OWN_CODE = "code";

        JSONObject countryJson = new JSONObject(JsonStr).getJSONObject("response");
        JSONArray airportsArray = countryJson.getJSONArray(OWM_AIRPORTS_LIST);

        int size = airportsArray.length();
        String[] resultStrs = new String[size];
        for(int i = 0; i < size; i++) {
            JSONObject airport = airportsArray.getJSONObject(i);
            resultStrs[i] = airport.getString(OWM_NAME) + ", " + airport.getString(OWN_CODE);
        }

        return resultStrs;

    }

    public static void getAirlinesDataFromJson(String JsonStr, DBHandler db)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_NAME = "name";
        final String OWN_CODE = "code";

        JSONArray array = new JSONObject(JsonStr).getJSONArray("response");

        int size = array.length();
        for(int i = 0; i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            db.addAirline(new String[]{obj.getString(OWN_CODE),obj.getString(OWM_NAME)});
        }
    }

    public static Flight[] getFlightsDataFromJson(String jsonStr)
            throws JSONException{

        JSONObject root = new JSONObject(jsonStr);
        JSONArray result = root.getJSONArray("results");

        ArrayList<Flight> list = new ArrayList<Flight>();
        for(int i=0;i<result.length();i++){
            JSONArray itinerary = result.getJSONObject(i).getJSONArray("itineraries");

            JSONObject fare = result.getJSONObject(i).getJSONObject("fare");
            String price = fare.getString("total_price");
            boolean refundable = fare.getJSONObject("restrictions").getBoolean("refundable");
            boolean penalties = fare.getJSONObject("restrictions").getBoolean("change_penalties");

            for(int j=0;j<itinerary.length();j++){
                JSONObject flight = itinerary.getJSONObject(j);
                if(flight.has("inbound"))
                    list.add(new Flight(flight.getJSONObject("outbound"),flight.getJSONObject("inbound"),price,refundable,penalties));
                else
                    list.add(new Flight(flight.getJSONObject("outbound"),price,refundable,penalties));
            }
        }

        Flight[] array = list.toArray(new Flight[0]);
        return array;
    }
}