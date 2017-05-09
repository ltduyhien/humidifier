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

public class HumidityFragment extends Fragment implements SensorEventListener {
    // Store instance variables
    private String title;
    private int page;

    private SensorManager mSensorManager;
    private Sensor mHumidity;
    private boolean checkHumiditySensor;

    private TrackGPS gps;

    String longitude;
    String latitude;

    PackageManager packageManager;

    TextView humidValueIndoor;
    TextView humidValueOutdoor;
    TextView city;
    TextView time;
    TextView humid_tips;

    // newInstance constructor for creating fragment with arguments
    public static HumidityFragment newInstance(int page, String title) {
        HumidityFragment fragmentFirst = new HumidityFragment();
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
        mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        checkHumiditySensor = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY);

        gps = new TrackGPS(getActivity());

        if(gps.canGetLocation()){
            longitude = Double.toString(gps.getLongitude());
            latitude = Double.toString(gps .getLatitude());
        }


        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                String humid;
                int humid_int;

                humidValueOutdoor.setText("" + weather_humidity);
                city.setText(""+weather_city);
                time.setText("Updated: "+weather_updatedOn);

                humid = weather_humidity.substring(0, weather_humidity.length() - 1);
                humid_int = Integer.parseInt(humid);
                if(humid_int < 40){
                    humid_tips.setText("The air is pretty dried today!\nDrink more water!");
                } else if(40 <= humid_int && humid_int < 65){
                    humid_tips.setText("The air is pretty damped today! Good!");
                } else {
                    humid_tips.setText("Be careful! It might be raining!");
                }

            }
        });

        asyncTask.execute(latitude,longitude); //  asyncTask.execute("Latitude", "Longitude")


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_humidity, container, false);

        return view;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        humidValueIndoor = (TextView) getView().findViewById(R.id.humid_indoor_value);
        humidValueOutdoor = (TextView) getView().findViewById(R.id.humid_outdoor_value);
        city = (TextView) getView().findViewById(R.id.city);
        time = (TextView) getView().findViewById(R.id.time);
        humid_tips = (TextView) getView().findViewById(R.id.humid_tips);
//        longt = (TextView) getView().findViewById(R.id.longt);
//        latt = (TextView) getView().findViewById(R.id.latt);
//
//        longt.setText(longitude);
//        latt.setText(latitude);

        if (checkHumiditySensor) {
            humidValueIndoor.setText("41%");
        } else {
            humidValueIndoor.setText("None");
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
    public void onDestroyView() {
        super.onDestroyView();
        gps.stopUsingGPS();
    }
}