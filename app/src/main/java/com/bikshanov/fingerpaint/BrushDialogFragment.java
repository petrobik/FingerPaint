package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Created by Peter on 14.03.2018.
 */

public class BrushDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageButton brushWidthButton;
    private Path brushPath;
    private Paint brushWidthPaint;
    private Bitmap brushWidthBitmap;
    private Canvas brushWidthCanvas;
    private SeekBar brushWidthSeekBar;
    private int progressOffset = 20;
    private int progressValue;

    private int min = 20;
    private int max = 70;
    private int step = 1;

    private int displayWidth, displayHeight;

    private int brushPreviewWidth = 0;
    private int brushPreviewHeight = 120;

    private Bitmap bitmap;
    private Canvas canvas;
    Point size;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        View brushWidthDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_brush_width, null);
        builder.setView(brushWidthDialogView);
//        builder.setTitle(R.string.select_brush);

        brushWidthButton = (ImageButton) brushWidthDialogView.findViewById(R.id.brushWidthButton);

        displayWidth = Utils.getSize(getContext()).x;
        displayHeight = Utils.getSize(getContext()).y;

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {

            brushPreviewWidth = displayWidth / 4;
            brushPreviewHeight = displayHeight / 5;

        }

        else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {

            brushPreviewWidth = displayWidth / 3;
            brushPreviewHeight = displayHeight / 7;

        }

        else {

            brushPreviewWidth = displayWidth / 2;
            brushPreviewHeight = displayHeight / 10;
        }

        bitmap = Bitmap.createBitmap(brushPreviewWidth, brushPreviewHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        brushWidthButton.setOnClickListener(this);

        final PaintView paintView = getPaintFragment().getPaintView();

        max = getResources().getDimensionPixelSize(R.dimen.brush_max);
        min = getResources().getDimensionPixelSize(R.dimen.brush_min);

        brushWidthSeekBar = (SeekBar) brushWidthDialogView.findViewById(R.id.brushWidthSeekBar);
        brushWidthSeekBar.setOnSeekBarChangeListener(brushWidthChanged);

        brushWidthSeekBar.setMax((max - min) / step);

        brushWidthSeekBar.setProgress(paintView.getLineWidth() - min);

//        builder.setPositiveButton(R.string.set_brush_width,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        paintView.setLineWidth(brushWidthSeekBar.getProgress() + min);
//                    }
//                });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        setBrushWidth();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        MainActivityFragment fragment = getPaintFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        MainActivityFragment fragment = getPaintFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(false);
        }
    }

    private MainActivityFragment getPaintFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.paintFragment);
    }

    @Override
    public void onClick(View view) {

        setBrushWidth();
        dismiss();
    }

    public void setBrushWidth() {

        int brushSize;

        brushSize = brushWidthSeekBar.getProgress() + min;
        getPaintFragment().getPaintView().setLineWidth(brushSize);
    }

    private void recycleBitmaps() {
        if (!brushWidthBitmap.isRecycled()) {
            brushWidthBitmap.recycle();
        }

        brushPath.reset();
    }

    private final SeekBar.OnSeekBarChangeListener brushWidthChanged = new SeekBar.OnSeekBarChangeListener() {

//        final Bitmap bitmap = Bitmap.createBitmap(brushPreviewWidth, 120, Bitmap.Config.ARGB_8888);
//        final Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

            Paint paint = new Paint();
            paint.setColor(getPaintFragment().getPaintView().getDrawingColor());
            paint.setAlpha(getPaintFragment().getPaintView().getPaintOpacity());
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            paint.setStrokeWidth(min + (progress * step));

            bitmap.eraseColor(getResources().getColor(android.R.color.transparent));

            brushPath = new Path();

            int startX, endX, x1, x2;
            int startY, endY, y1, y2;

            startX = brushPreviewWidth / 10;
            endX = brushPreviewWidth - startX;
            x1 = brushPreviewWidth / 3;
            x2 = brushPreviewWidth - x1;

            startY = brushPreviewHeight - brushPreviewHeight / 3;
            endY = brushPreviewHeight / 3;

            brushPath.moveTo(startX, startY);
            brushPath.cubicTo(x1, 0, x2, brushPreviewHeight, endX, endY);

//            brushPath.moveTo(50, 70);
//            brushPath.cubicTo(150, 10, 250, 110, brushPreviewWidth - 50, 50);

            canvas.drawPath(brushPath, paint);
            brushWidthButton.setImageBitmap(bitmap);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}