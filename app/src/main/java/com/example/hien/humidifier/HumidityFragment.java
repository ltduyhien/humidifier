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

    TextView longt;
    TextView latt;
    TextView humidValueIndoor;
    TextView humidValueOutdoor;

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
        mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        checkHumiditySensor = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE);

        gps = new TrackGPS(getActivity());

        if(gps.canGetLocation()){
            longitude = Double.toString(gps.getLongitude());
            latitude = Double.toString(gps .getLatitude());


        }
        else
        {
            gps.showSettingsAlert();
        }


        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                humidValueOutdoor.setText("" + weather_humidity);
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
        longt = (TextView) getView().findViewById(R.id.longt);
        latt = (TextView) getView().findViewById(R.id.latt);

        longt.setText(longitude);
        latt.setText(latitude);

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
    public void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mHumidity, SensorManager.SENSOR_DELAY_NORMAL);
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