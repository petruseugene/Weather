package com.example.weather.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.weather.R;
import com.example.weather.activities.MainActivity;

public class DeleteCityDialog extends DialogFragment {

    private final static String CITY_NAME_KEY = "CITY_NAME_KEY";

    public static  DeleteCityDialog newInstance(String cityNameAndCountry) {
        DeleteCityDialog fragment = new  DeleteCityDialog();
        Bundle args = new Bundle();
        args.putString(CITY_NAME_KEY, cityNameAndCountry);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String cityNameAndCountry = getArguments().getString(CITY_NAME_KEY);

        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_dialog_title))
                .setMessage(String.format(getString(R.string.format_delete_question), getString(R.string.delete_dialog_text), cityNameAndCountry))
                .setPositiveButton(R.string.delete_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MainActivity)getActivity()).deleteDialogPositive();
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton(R.string.delete_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }
}