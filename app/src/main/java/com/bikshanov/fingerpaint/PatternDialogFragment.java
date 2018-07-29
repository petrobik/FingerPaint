package com.bikshanov.fingerpaint;


import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Peter on 14.03.2018.
 */

public class PatternDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageButton flowersButton, floralButton, starsButton, grassButton, cloudsButton,
            brickButton, partyButton, leavesButton, woodButton, butterflyButton;

    private int result = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        View patternDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_pattern, null);
        builder.setView(patternDialogView);
        builder.setTitle(R.string.select_pattern);

        floralButton = (ImageButton) patternDialogView.findViewById(R.id.floralButton);
        flowersButton = (ImageButton) patternDialogView.findViewById(R.id.flowersButton);
        starsButton = (ImageButton) patternDialogView.findViewById(R.id.starsButton);
        grassButton = (ImageButton) patternDialogView.findViewById(R.id.grassButton);
        cloudsButton = (ImageButton) patternDialogView.findViewById(R.id.cloudsButton);
        brickButton = (ImageButton) patternDialogView.findViewById(R.id.brickButton);
        partyButton = (ImageButton) patternDialogView.findViewById(R.id.partyButton);
        leavesButton = (ImageButton) patternDialogView.findViewById(R.id.leavesButton);
        woodButton = (ImageButton) patternDialogView.findViewById(R.id.woodButton);
        butterflyButton = (ImageButton) patternDialogView.findViewById(R.id.butterflyButton);

        floralButton.setOnClickListener(this);
        flowersButton.setOnClickListener(this);
        starsButton.setOnClickListener(this);
        grassButton.setOnClickListener(this);
        cloudsButton.setOnClickListener(this);
        brickButton.setOnClickListener(this);
        partyButton.setOnClickListener(this);
        leavesButton.setOnClickListener(this);
        woodButton.setOnClickListener(this);
        butterflyButton.setOnClickListener(this);

        final PaintView paintView = getPaintFragment().getPaintView();

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




        if (view.getId() == R.id.floralButton) {

            getPaintFragment().getPaintView().setPattern("floral");
            dismiss();
        }

        else if (view.getId() == R.id.flowersButton) {

            getPaintFragment().getPaintView().setPattern("flowers");
            dismiss();
        }

        else if (view.getId() == R.id.starsButton) {

            getPaintFragment().getPaintView().setPattern("stars");
            dismiss();
        }

        else if (view.getId() == R.id.grassButton) {

            getPaintFragment().getPaintView().setPattern("grass");
            dismiss();
        }

        else if (view.getId() == R.id.leavesButton) {

            getPaintFragment().getPaintView().setPattern("strawberry");
            dismiss();
        }

        else if (view.getId() == R.id.cloudsButton) {

            getPaintFragment().getPaintView().setPattern("clouds");
            dismiss();
        }

        else if (view.getId() == R.id.brickButton) {

            getPaintFragment().getPaintView().setPattern("brick");
            dismiss();
        }

        else if (view.getId() == R.id.woodButton) {

            getPaintFragment().getPaintView().setPattern("wood");
            dismiss();
        }

        else if (view.getId() == R.id.butterflyButton) {

            getPaintFragment().getPaintView().setPattern("butterfly");
            dismiss();
        }

        else if (view.getId() == R.id.partyButton) {

            getPaintFragment().getPaintView().setPattern("party");
            dismiss();
        }
    }
}
