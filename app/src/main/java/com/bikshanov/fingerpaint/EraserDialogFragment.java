package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Peter on 14.03.2018.
 */

public class EraserDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageButton smallestEraserButton, smallEraserButton, mediumEraserButton, largeEraserButton, largestEraserButton;

    private Path brushPath;
    private Paint smallestPaint, smallPaint, largePaint, largestPaint;
    private Bitmap smallestBitmap, smallBitmap, largeBitmap, largestBitmap;
    private Canvas smallestCanvas, smallCanvas, largeCanvas, largestCanvas;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        View brushDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_eraser, null);
        builder.setView(brushDialogView);
        builder.setTitle(R.string.select_eraser);

        smallestEraserButton = (ImageButton) brushDialogView.findViewById(R.id.brushWidthButton);
        smallEraserButton = (ImageButton) brushDialogView.findViewById(R.id.smallEraserButton);
//        mediumEraserButton = (ImageButton) brushDialogView.findViewById(R.id.mediumEraserButton);
        largeEraserButton = (ImageButton) brushDialogView.findViewById(R.id.largeEraserButton);
        largestEraserButton = (ImageButton) brushDialogView.findViewById(R.id.largestEraserButton);

        smallestEraserButton.setOnClickListener(this);
        smallEraserButton.setOnClickListener(this);
//        mediumEraserButton.setOnClickListener(this);
        largeEraserButton.setOnClickListener(this);
        largestEraserButton.setOnClickListener(this);

        final PaintView paintView = getPaintFragment().getPaintView();

        smallestBitmap = Bitmap.createBitmap(400, 120, Bitmap.Config.ARGB_8888);
        smallBitmap = Bitmap.createBitmap(400, 120, Bitmap.Config.ARGB_8888);
        largeBitmap = Bitmap.createBitmap(400, 120, Bitmap.Config.ARGB_8888);
        largestBitmap = Bitmap.createBitmap(400, 120, Bitmap.Config.ARGB_8888);

        brushPath = new Path();
        brushPath.moveTo(50, 70);
        brushPath.cubicTo(150, 10, 250, 110, 350, 50);

        smallestPaint = new Paint();
        smallPaint = new Paint();
        largePaint = new Paint();
        largestPaint = new Paint();

        smallestPaint.setColor(Color.WHITE);
        smallestPaint.setAntiAlias(true);
        smallestPaint.setStrokeCap(Paint.Cap.ROUND);
        smallestPaint.setStrokeJoin(Paint.Join.ROUND);
        smallestPaint.setStyle(Paint.Style.STROKE);
        smallestPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_min));

        smallPaint.setColor(Color.WHITE);
        smallPaint.setAntiAlias(true);
        smallPaint.setStrokeCap(Paint.Cap.ROUND);
        smallPaint.setStrokeJoin(Paint.Join.ROUND);
        smallPaint.setStyle(Paint.Style.STROKE);
        smallPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_small));

        largePaint.setColor(Color.WHITE);
        largePaint.setAntiAlias(true);
        largePaint.setStrokeCap(Paint.Cap.ROUND);
        largePaint.setStrokeJoin(Paint.Join.ROUND);
        largePaint.setStyle(Paint.Style.STROKE);
        largePaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_large));

        largestPaint.setColor(Color.WHITE);
        largestPaint.setAntiAlias(true);
        largestPaint.setStrokeCap(Paint.Cap.ROUND);
        largestPaint.setStrokeJoin(Paint.Join.ROUND);
        largestPaint.setStyle(Paint.Style.STROKE);
        largestPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_max));

        smallestCanvas = new Canvas(smallestBitmap);
        smallCanvas = new Canvas(smallBitmap);
        largeCanvas = new Canvas(largeBitmap);
        largestCanvas = new Canvas(largestBitmap);

        smallestCanvas.drawPath(brushPath, smallestPaint);
        smallestEraserButton.setImageBitmap(smallestBitmap);

        smallCanvas.drawPath(brushPath, smallPaint);
        smallEraserButton.setImageBitmap(smallBitmap);

        largeCanvas.drawPath(brushPath, largePaint);
        largeEraserButton.setImageBitmap(largeBitmap);

        largestCanvas.drawPath(brushPath, largestPaint);
        largestEraserButton.setImageBitmap(largestBitmap);

//        final SeekBar brushWidthSeekBar = (SeekBar) brushDialogView.findViewById(R.id.brushWidthSeekBar);
//        brushWidthSeekBar.setOnSeekBarChangeListener(brushWidthChanged);
//        brushWidthSeekBar.setProgress(paintView.getLineWidth());

//        builder.setPositiveButton(R.string.set_brush_width,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        paintView.setLineWidth(brushWidthSeekBar.getProgress());
//                    }
//                });

        return builder.create();
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

        int brushSize;

        if (view.getId() == R.id.brushWidthButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_min);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
            dismiss();
        }

        else if (view.getId() == R.id.smallEraserButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_small);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
            dismiss();
        }
//        else if (view.getId() == R.id.mediumEraserButton) {
//
//            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_medium);
//            getPaintFragment().getPaintView().setLineWidth(brushSize);
//            dismiss();
//        }

        else if (view.getId() == R.id.largeEraserButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_large);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
            dismiss();
        }
        else if (view.getId() == R.id.largestEraserButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_max);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
            dismiss();
        }
    }
}
