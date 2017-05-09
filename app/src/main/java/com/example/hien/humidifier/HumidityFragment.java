package com.example.hien.humidifier;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    PackageManager packageManager;
    TextView humidityText;

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

        humidityText = (TextView) getActivity().findViewById(R.id.humidity);

        if (checkHumiditySensor) {
            humidityText.setText("Humidity level is available");
        } else {
            humidityText.setText("Sensor is not available");
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_humidity, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.humid_text);
        tvLabel.setText(page + " -- " + title);
        return view;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float millibars_of_pressure = event.values[0];
        Log.d(getClass().getName(), "value = " + millibars_of_pressure);
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
}