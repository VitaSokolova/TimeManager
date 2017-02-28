package com.example.android.timemanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Комп on 22.02.2017.
 */

public class PriceDialog extends DialogFragment {
    private OnItemClickListener listener;

    public static PriceDialog newInstance() {
        PriceDialog frag = new PriceDialog();
        frag.setCancelable(false);
        return frag;
    }

    public void setListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_enter_price, null);

        final EditText txtView = (EditText) view.findViewById(R.id.price_edit_txt);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                // Add action buttons
                .setTitle(R.string.money)
                .setPositiveButton(R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button buttonOk = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if ((txtView.getText().toString().isEmpty()) || (!txtView.getText().toString().matches("[0-9]+"))) {
                            Toast toast = Toast.makeText(getContext(),
                                    R.string.alertGuestNameIsEmpty, Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            listener.onItemClick(txtView.getText().toString());
                            //если всё хорошо, то можно закрывать диалог
                            dialog.dismiss();
                        }
                    }
                });

                Button buttonCancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PriceDialog.this.getDialog().cancel();
                    }
                });
            }
        });

        return dialog;
    }
}
