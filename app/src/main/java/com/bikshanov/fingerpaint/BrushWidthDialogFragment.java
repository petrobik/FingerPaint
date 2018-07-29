package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

public class BrushWidthDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageButton smallestBrushButton, smallBrushButton, largeBrushButton, largestBrushButton;
    private Path brushPath;
    private Paint smallestPaint, smallPaint, largePaint, largestPaint;
    private Bitmap smallestBitmap, smallBitmap, largeBitmap, largestBitmap;
    private Canvas smallestCanvas, smallCanvas, largeCanvas, largestCanvas;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        View brushDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_brush, null);
        builder.setView(brushDialogView);
        builder.setTitle(R.string.select_brush);

        smallestBrushButton = (ImageButton) brushDialogView.findViewById(R.id.smallestEraserButton);
        smallBrushButton = (ImageButton) brushDialogView.findViewById(R.id.smallEraserButton);
//        mediumBrushButton = (ImageButton) brushDialogView.findViewById(R.id.mediumEraserButton);
        largeBrushButton = (ImageButton) brushDialogView.findViewById(R.id.largeEraserButton);
        largestBrushButton = (ImageButton) brushDialogView.findViewById(R.id.largestEraserButton);

        smallestBrushButton.setOnClickListener(this);
        smallBrushButton.setOnClickListener(this);
//        mediumBrushButton.setOnClickListener(this);
        largeBrushButton.setOnClickListener(this);
        largestBrushButton.setOnClickListener(this);

        final PaintView paintView = getPaintFragment().getPaintView();

//        smallestBrushButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);
//        smallBrushButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);
////        mediumBrushButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);
//        largeBrushButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);
//        largestBrushButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);

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

        smallestPaint.setColor(paintView.getDrawingColor());
        smallestPaint.setAntiAlias(true);
        smallestPaint.setStrokeCap(Paint.Cap.ROUND);
        smallestPaint.setStrokeJoin(Paint.Join.ROUND);
        smallestPaint.setStyle(Paint.Style.STROKE);
        smallestPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_smallest));

        smallPaint.setColor(paintView.getDrawingColor());
        smallPaint.setAntiAlias(true);
        smallPaint.setStrokeCap(Paint.Cap.ROUND);
        smallPaint.setStrokeJoin(Paint.Join.ROUND);
        smallPaint.setStyle(Paint.Style.STROKE);
        smallPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_small));

        largePaint.setColor(paintView.getDrawingColor());
        largePaint.setAntiAlias(true);
        largePaint.setStrokeCap(Paint.Cap.ROUND);
        largePaint.setStrokeJoin(Paint.Join.ROUND);
        largePaint.setStyle(Paint.Style.STROKE);
        largePaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_large));

        largestPaint.setColor(paintView.getDrawingColor());
        largestPaint.setAntiAlias(true);
        largestPaint.setStrokeCap(Paint.Cap.ROUND);
        largestPaint.setStrokeJoin(Paint.Join.ROUND);
        largestPaint.setStyle(Paint.Style.STROKE);
        largestPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_largest));

        smallestCanvas = new Canvas(smallestBitmap);
        smallCanvas = new Canvas(smallBitmap);
        largeCanvas = new Canvas(largeBitmap);
        largestCanvas = new Canvas(largestBitmap);

        smallestCanvas.drawPath(brushPath, smallestPaint);
        smallestBrushButton.setImageBitmap(smallestBitmap);

        smallCanvas.drawPath(brushPath, smallPaint);
        smallBrushButton.setImageBitmap(smallBitmap);

        largeCanvas.drawPath(brushPath, largePaint);
        largeBrushButton.setImageBitmap(largeBitmap);

        largestCanvas.drawPath(brushPath, largestPaint);
        largestBrushButton.setImageBitmap(largestBitmap);


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

        if (view.getId() == R.id.smallestEraserButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_smallest);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
            dismiss();
            recycleBitmaps();
        }

        else if (view.getId() == R.id.smallEraserButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_small);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
            dismiss();
            recycleBitmaps();
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
            recycleBitmaps();
        }
        else if (view.getId() == R.id.largestEraserButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_largest);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
            dismiss();
            recycleBitmaps();
        }
    }

    private void recycleBitmaps() {
        if (!smallestBitmap.isRecycled()) {
            smallestBitmap.recycle();
        }
        if (!smallBitmap.isRecycled()) {
            smallBitmap.recycle();
        }
        if (!largeBitmap.isRecycled()) {
            largeBitmap.recycle();
        }
        if (!largestBitmap.isRecycled()) {
            largestBitmap.recycle();
        }

        brushPath.reset();
    }
}
