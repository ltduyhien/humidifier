package com.example.hien.humidifier;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hien on 04/05/2017.
 */

public class TemperatureFragment extends Fragment implements SensorEventListener {
    // Store instance variables
    private String title;
    private int page;

    private SensorManager mSensorManager;
    private Sensor mTemp;
    private boolean checkTempSensor;

    private TrackGPS gps;

    String longitude;
    String latitude;

    PackageManager packageManager;

    TextView longt;
    TextView latt;
    TextView tempValueIndoor;
    TextView tempValueOutdoor;
    TextView city;
    TextView time;
    TextView temp_tips;

    // newInstance constructor for creating fragment with arguments
    public static TemperatureFragment newInstance(int page, String title) {
        TemperatureFragment fragmentFirst = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

        packageManager = this.getActivity().getPackageManager();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        checkTempSensor = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE);

        gps = new TrackGPS(getActivity());

        if(gps.canGetLocation()){
            longitude = Double.toString(gps.getLongitude());
            latitude = Double.toString(gps .getLatitude());
        }


        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                String temp;
                int temp_int;

                tempValueOutdoor.setText("" + weather_temperature);
                city.setText(""+weather_city);
                time.setText("Updated: "+weather_updatedOn);

                temp = weather_temperature.substring(0, weather_temperature.length() - 1);
                temp_int = Integer.parseInt(temp);
                if(temp_int < 5){
                    temp_tips.setText("It's cold! Remember to dress warm!");
                } else {
                    temp_tips.setText("It's warm! GO out and enjoy!");
                }

            }
        });

        asyncTask.execute(latitude,longitude); //  asyncTask.execute("Latitude", "Longitude")
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tempValueIndoor = (TextView) getView().findViewById(R.id.temp_indoor_value);
        tempValueOutdoor = (TextView) getView().findViewById(R.id.temp_outdoor_value);
        city = (TextView) getView().findViewById(R.id.city);
        time = (TextView) getView().findViewById(R.id.time);
        temp_tips = (TextView )getView().findViewById(R.id.temp_tips);
//        longt = (TextView) getView().findViewById(R.id.longt);
//        latt = (TextView) getView().findViewById(R.id.latt);
//
//        longt.setText(longitude);
//        latt.setText(latitude);

        if (checkTempSensor) {
            tempValueIndoor.setText("41%");
        } else {
            tempValueIndoor.setText("None");
        }


    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float millibars_of_pressure = event.values[0];

    }

    @Override
    public void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gps.stopUsingGPS();
    }
}