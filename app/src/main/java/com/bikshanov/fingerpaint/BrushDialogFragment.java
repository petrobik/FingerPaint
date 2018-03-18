package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Peter on 14.03.2018.
 */

public class BrushDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageButton smallBrushImageButton, mediumBrushImageButton, largeBrushImageButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View brushDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_brush, null);
        builder.setView(brushDialogView);
        builder.setTitle(R.string.select_brush);

        smallBrushImageButton = (ImageButton) brushDialogView.findViewById(R.id.smallBrushImageButton);
        mediumBrushImageButton = (ImageButton) brushDialogView.findViewById(R.id.mediumBrushImageButton);
        largeBrushImageButton = (ImageButton) brushDialogView.findViewById(R.id.largeBrushImageButton);

        smallBrushImageButton.setOnClickListener(this);
        mediumBrushImageButton.setOnClickListener(this);
        largeBrushImageButton.setOnClickListener(this);

        final PaintView paintView = getPaintFragment().getPaintView();

        smallBrushImageButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);
        mediumBrushImageButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);
        largeBrushImageButton.getDrawable().setColorFilter(paintView.getDrawingColor(), PorterDuff.Mode.SRC_IN);

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

        if (view.getId() == R.id.smallBrushImageButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_small);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
//            getPaintFragment().getPaintView().setBrushSize(brushSize);
            dismiss();
        }
        else if (view.getId() == R.id.mediumBrushImageButton) {

            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_medium);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
//            getPaintFragment().getPaintView().setBrushSize(brushSize);
            dismiss();
        }
        else {
            brushSize = getResources().getDimensionPixelSize(R.dimen.brush_large);
            getPaintFragment().getPaintView().setLineWidth(brushSize);
//            getPaintFragment().getPaintView().setBrushSize(brushSize);
            dismiss();
        }
    }

////    private final SeekBar.OnSeekBarChangeListener brushWidthChanged = new SeekBar.OnSeekBarChangeListener() {
////
////        final Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
////        final Canvas canvas = new Canvas(bitmap);
////
////        @Override
////        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
////            Paint p = new Paint();
////            p.setColor(getPaintFragment().getPaintView().getDrawingColor());
////            p.setStrokeCap(Paint.Cap.ROUND);
////            p.setStrokeWidth(progress);
////
////            bitmap.eraseColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
////            canvas.drawLine(30, 50, 370, 50, p);
////            brushImageView.setImageBitmap(bitmap);
////        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//
//        }
//    };

}
