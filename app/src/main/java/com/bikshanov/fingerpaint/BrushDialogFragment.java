package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * Created by Peter on 14.03.2018.
 */

public class BrushDialogFragment extends DialogFragment {

    private ImageView brushImageView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View brushDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_brush, null);
        builder.setView(brushDialogView);
        builder.setTitle(R.string.select_brush);

        brushImageView = (ImageView) brushDialogView.findViewById(R.id.brushImageView);

        final PaintView paintView = getPaintFragment().getPaintView();
        final SeekBar brushWidthSeekBar = (SeekBar) brushDialogView.findViewById(R.id.brushWidthSeekBar);
        brushWidthSeekBar.setOnSeekBarChangeListener(brushWidthChanged);
        brushWidthSeekBar.setProgress(paintView.getLineWidth());

        builder.setPositiveButton(R.string.set_brush_width,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        paintView.setLineWidth(brushWidthSeekBar.getProgress());
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

    private final SeekBar.OnSeekBarChangeListener brushWidthChanged = new SeekBar.OnSeekBarChangeListener() {

        final Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            Paint p = new Paint();
            p.setColor(getPaintFragment().getPaintView().getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            bitmap.eraseColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            canvas.drawLine(30, 50, 370, 50, p);
            brushImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
