package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class ClearDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.clear_drawing_message);

        builder.setPositiveButton(R.string.button_clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getPaintFragment().getPaintView().clear();
                ClearDialogFragmentListener listener = (ClearDialogFragmentListener) getTargetFragment();
                listener.onClearDrawing();

            }
        });

        builder.setNegativeButton(R.string.cancel, null);

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

    public interface ClearDialogFragmentListener {
        void onClearDrawing();
    }
}
