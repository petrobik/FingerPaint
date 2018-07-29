package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        View brushWidthDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_brush_width, null);
        builder.setView(brushWidthDialogView);
//        builder.setTitle(R.string.select_brush);

        brushWidthButton = (ImageButton) brushWidthDialogView.findViewById(R.id.smallestEraserButton);

        brushWidthButton.setOnClickListener(this);

        final PaintView paintView = getPaintFragment().getPaintView();

        brushWidthSeekBar = (SeekBar) brushWidthDialogView.findViewById(R.id.brushWidthSeekBar);
        brushWidthSeekBar.setOnSeekBarChangeListener(brushWidthChanged);

        brushWidthSeekBar.setMax((max - min) / step);

        brushWidthSeekBar.setProgress(paintView.getLineWidth() - min);

        builder.setPositiveButton(R.string.set_brush_width,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        paintView.setLineWidth(brushWidthSeekBar.getProgress() + min);
                    }
                });

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

        brushSize = brushWidthSeekBar.getProgress() + min;
        getPaintFragment().getPaintView().setLineWidth(brushSize);
        dismiss();
//        recycleBitmaps();

    }

    private void recycleBitmaps() {
        if (!brushWidthBitmap.isRecycled()) {
            brushWidthBitmap.recycle();
        }

        brushPath.reset();
    }

    private final SeekBar.OnSeekBarChangeListener brushWidthChanged = new SeekBar.OnSeekBarChangeListener() {

        final Bitmap bitmap = Bitmap.createBitmap(400, 120, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);


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
            brushPath.moveTo(50, 70);
            brushPath.cubicTo(150, 10, 250, 110, 350, 50);

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
