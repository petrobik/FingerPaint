package com.bikshanov.fingerpaint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SpectrumPalette.OnColorSelectedListener,
        View.OnClickListener, ClearDialogFragment.ClearDialogFragmentListener {

    private PaintView paintView;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;
    ImageButton newButton, pencilButton, brushButton, eraserButton, undoButton, patternButton, saveButton;
//    ImageButton smallestButton, smallButton, smallLargeButton, mediumButton, largeButton;

//    private ViewGroup brushPanel;

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
//                    getPaintView().clear();
                    confirmClear();
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

//        setHasOptionsMenu(true);

        paintView = (PaintView) view.findViewById(R.id.paintView);

        SpectrumPalette palette = (SpectrumPalette) view.findViewById(R.id.palette);
        palette.setOnColorSelectedListener(this);

        int selColor = getResources().getIntArray(R.array.demo_colors)[0];

        palette.setSelectedColor(selColor);

        newButton = (ImageButton) view.findViewById(R.id.button_new);
        newButton.setOnClickListener(this);

        pencilButton = (ImageButton) view.findViewById(R.id.button_pencil);
        pencilButton.setOnClickListener(this);

        brushButton = (ImageButton) view.findViewById(R.id.button_brush);
        brushButton.setOnClickListener(this);

        eraserButton = (ImageButton) view.findViewById(R.id.button_eraser);
        eraserButton.setOnClickListener(this);

        undoButton = (ImageButton) view.findViewById(R.id.button_undo);
        undoButton.setOnClickListener(this);

        patternButton = (ImageButton) view.findViewById(R.id.button_pattern);
        patternButton.setOnClickListener(this);

        saveButton = (ImageButton) view.findViewById(R.id.button_save);
        saveButton.setOnClickListener(this);

        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        pencilButton.setBackgroundResource(R.drawable.select_button_background);

        return view;
    }

    @Override
    public void onColorSelected(int color) {
        paintView.setDrawingColor(color);

        if (paintView.getDrawMode() == DrawModes.PATTERN ||
                paintView.getDrawMode() == DrawModes.ERASE) {
            paintView.setDrawMode(DrawModes.PENCIL);
            setPencilActive();
        }

//        paintView.setErase(false);
//        paintView.setEmptyPattern();
//        pencilButton.setBackgroundResource(R.drawable.select_button_background);
//        eraserButton.setBackgroundResource(R.drawable.button_selector);
//        patternButton.setBackgroundResource(R.drawable.button_selector);
//        setBrushColor();
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
            confirmClear();
        }
        else if (view.getId() == R.id.button_pencil) {

            setPencilActive();

//            if (paintView.isErase()) {
//                paintView.setErase(false);
//            }
//
//            else if (paintView.isPattern()) {
//                paintView.setEmptyPattern();
//            }

//            paintView.setEmptyPattern();

            paintView.setDrawMode(DrawModes.PENCIL);

            BrushDialogFragment brushDialog = new BrushDialogFragment();
//            brushDialog.setCancelable(false);
            brushDialog.show(getFragmentManager(), "brush dialog");

        }

        else if (view.getId() == R.id.button_brush) {

            setBrushActive();
            paintView.setDrawMode(DrawModes.BRUSH);

            BrushDialogFragment brushDialog = new BrushDialogFragment();
//            brushDialog.setCancelable(false);
            brushDialog.show(getFragmentManager(), "brush dialog");

//            BrushWidthDialogFragment widthFragment = new BrushWidthDialogFragment();
//            widthFragment.show(getFragmentManager(), "width dialog");
        }

        else if (view.getId() == R.id.button_eraser) {

//            paintView.setErase(true);
//            paintView.setEmptyPattern();

            paintView.setDrawMode(DrawModes.ERASE);

            setEraserActive();

            BrushDialogFragment brushDialog = new BrushDialogFragment();
//            brushDialog.setCancelable(false);
            brushDialog.show(getFragmentManager(), "brush dialog");

//            EraserDialogFragment eraserDialog = new EraserDialogFragment();
////            eraserDialog.setCancelable(false);
//            eraserDialog.show(getFragmentManager(), "brush dialog");

        }
        else if (view.getId() == R.id.button_undo) {
            paintView.undo();

        }

        else if (view.getId() == R.id.button_pattern) {

//            paintView.setPatternMode(true);

            paintView.setDrawMode(DrawModes.PATTERN);

            setPatternActive();

            PatternDialogFragment patternDialog = new PatternDialogFragment();
//            patternDialog.setCancelable(false);
            patternDialog.show(getFragmentManager(), "pattern dialog");
        }

        else if (view.getId() == R.id.button_save) {

            AlertDialog.Builder saveDialog = new AlertDialog.Builder(getContext());
            saveDialog.setTitle(R.string.save_drawing);
            saveDialog.setMessage(R.string.save_drawing_message);
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkPermission();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            saveDialog.show();

        }
    }

    @TargetApi(23)
    private void checkPermission() {
        if ((Build.VERSION.SDK_INT >= 23) && (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage(R.string.permission_explanation);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[]{
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                            }
                        }
                );

                builder.create().show();

            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
            }

        }

         else {
            paintView.saveDrawing();
        }
    }

    private void saveDrawing() {

//        paintView.setDrawingCacheEnabled(true);
//
//        String imgSaved = MediaStore.Images.Media.insertImage(this.getContext().getContentResolver(),
//                paintView.getDrawingCache(), UUID.randomUUID().toString() + ".png", "drawing");
//
//        if (imgSaved != null) {
//            Toast savedToast = Toast.makeText(getContext(), "Drawing saved", Toast.LENGTH_SHORT);
//            savedToast.show();
//        }
//        else {
//            Toast unsavedToast = Toast.makeText(getContext(), "Drawing not saved", Toast.LENGTH_SHORT);
//            unsavedToast.show();
//        }
//
//        paintView.destroyDrawingCache();

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/FingerPaint";
        File dir = new File(filePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString().concat(".png");
        File file = new File(dir, fileName);

        FileOutputStream fout;

        try {
            fout = new FileOutputStream(file);
            paintView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG, 85, fout);
            fout.flush();
            fout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectBrush(ImageButton button) {
        button.setBackgroundResource(R.drawable.brush_button_selected_border);
    }

    private void confirmClear() {
        ClearDialogFragment clearDialogFragment = new ClearDialogFragment();
        clearDialogFragment.setTargetFragment(MainActivityFragment.this, 300);
        clearDialogFragment.show(getFragmentManager(), "clear dialog");
    }

    public void setPencilActive() {
        pencilButton.setBackgroundResource(R.drawable.select_button_background);
        eraserButton.setBackgroundResource(R.drawable.button_selector);
        patternButton.setBackgroundResource(R.drawable.button_selector);
        brushButton.setBackgroundResource(R.drawable.button_selector);
    }

    public void setBrushActive() {
        brushButton.setBackgroundResource(R.drawable.select_button_background);
        eraserButton.setBackgroundResource(R.drawable.button_selector);
        patternButton.setBackgroundResource(R.drawable.button_selector);
        pencilButton.setBackgroundResource(R.drawable.button_selector);
    }

    public void setEraserActive() {
        pencilButton.setBackgroundResource(R.drawable.button_selector);
        eraserButton.setBackgroundResource(R.drawable.select_button_background);
        patternButton.setBackgroundResource(R.drawable.button_selector);
        brushButton.setBackgroundResource(R.drawable.button_selector);
    }

    public void setPatternActive() {
        pencilButton.setBackgroundResource(R.drawable.button_selector);
        eraserButton.setBackgroundResource(R.drawable.button_selector);
        patternButton.setBackgroundResource(R.drawable.select_button_background);
        brushButton.setBackgroundResource(R.drawable.button_selector);
    }

    @Override
    public void onClearDrawing() {
        setPencilActive();
    }
}
