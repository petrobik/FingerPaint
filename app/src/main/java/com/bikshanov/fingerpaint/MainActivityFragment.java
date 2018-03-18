package com.bikshanov.fingerpaint;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.thebluealliance.spectrum.SpectrumPalette;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SpectrumPalette.OnColorSelectedListener,
        View.OnClickListener{

    private PaintView paintView;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;
    ImageButton newButton, brushButton, eraserButton, undoButton, redoButton, saveButton;

    private static final int ACCELERATION_THRESHOLD = 100000;

    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;


    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    private void enableAccelerometerListening() {

        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (!dialogOnScreen) {

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                lastAcceleration = currentAcceleration;

                currentAcceleration = x * x + y * y + z * z;

                acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);

                if (acceleration > ACCELERATION_THRESHOLD) {
                    getPaintView().clear();
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening();
    }

    private void disableAccelerometerListening() {

        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        paintView = (PaintView) view.findViewById(R.id.paintView);

        SpectrumPalette palette = (SpectrumPalette) view.findViewById(R.id.palette);
        palette.setOnColorSelectedListener(this);

        int selColor = getResources().getIntArray(R.array.demo_colors)[0];

        palette.setSelectedColor(selColor);

        newButton = (ImageButton) view.findViewById(R.id.button_new);
        newButton.setOnClickListener(this);

        brushButton = (ImageButton) view.findViewById(R.id.button_brush);
        brushButton.setOnClickListener(this);

        eraserButton = (ImageButton) view.findViewById(R.id.button_eraser);
        eraserButton.setOnClickListener(this);

        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        return view;
    }

    @Override
    public void onColorSelected(int color) {
        paintView.setDrawingColor(color);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.paint_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.color:
                ColorDialogFragment colorDialog = new ColorDialogFragment();
                colorDialog.show(getFragmentManager(), "color dialog");

                return true;

            case R.id.brush:

                BrushDialogFragment brushDialog = new BrushDialogFragment();
                brushDialog.show(getFragmentManager(), "brush dialog");

                return true;

            case R.id.eraser:

                getPaintView().clear();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public PaintView getPaintView() {
        return paintView;
    }

    public void setDialogOnScreen(boolean visible) {
        dialogOnScreen = visible;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_new) {
            getPaintView().clear();
        }
        else if (view.getId() == R.id.button_brush) {
            paintView.setErase(false);
            BrushDialogFragment brushDialog = new BrushDialogFragment();
            brushDialog.show(getFragmentManager(), "brush dialog");
        }
        else if (view.getId() == R.id.button_eraser) {
            paintView.setErase(true);
        }

    }
}
