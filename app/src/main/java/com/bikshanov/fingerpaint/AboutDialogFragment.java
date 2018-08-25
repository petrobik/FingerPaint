package com.bikshanov.fingerpaint;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class AboutDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View aboutDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_about, null);
        builder.setView(aboutDialogView);

        return builder.create();


    }
}
