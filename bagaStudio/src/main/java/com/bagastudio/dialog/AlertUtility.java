package com.bagastudio.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

public class AlertUtility
{

    public static void showAlert(final Activity activity, final String title, final String message, final OnClickListener positiveButtonClickListener,  final OnClickListener negativeButtonClickListener)
    {
        final Builder alertDialogBuilder = new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_alert);

        if (positiveButtonClickListener != null)
        {
            alertDialogBuilder.setPositiveButton(android.R.string.yes, positiveButtonClickListener);
        }
        
        if (negativeButtonClickListener != null)
        {
            alertDialogBuilder.setNegativeButton(android.R.string.no, negativeButtonClickListener);
        }
        
        final AlertDialog alertDialog = alertDialogBuilder.create();
        
        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {


            @Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                activity.finish();
                dialog.dismiss();
                return false;
            }
        });
        
        alertDialog.show();
    }
}
