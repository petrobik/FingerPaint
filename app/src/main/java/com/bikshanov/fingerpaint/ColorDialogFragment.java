package com.bikshanov.fingerpaint;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.thebluealliance.spectrum.SpectrumDialog;

/**
 * Created by Peter on 14.03.2018.
 */

public class ColorDialogFragment extends DialogFragment {

    private int color;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final PaintView paintView = getPaintFragment().getPaintView();
        color = paintView.getDrawingColor();

        SpectrumDialog.Builder builder = new SpectrumDialog.Builder(getActivity());

        builder
                .setColors(R.array.demo_colors)
                .setSelectedColor(color)
                .setDismissOnColorSelected(true)
                .setOutlineWidth(0)
                .setTitle(R.string.select_color)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, int color) {
                        if (positiveResult) {
                            paintView.setDrawingColor(color);
                            dismiss();
                        } else {
                            dismiss();
                        }

                    }
                }).build().show(getFragmentManager(), "color_dialog");

        return super.onCreateDialog(savedInstanceState);
    }

    private MainActivityFragment getPaintFragment() {

        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.paintFragment);

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
}
