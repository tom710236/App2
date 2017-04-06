package com.bagastudio.uiOrientation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;

public class UIOrientation
{
    public static void setAutoOrientationEnabled(final Context context, final boolean enabled)
    {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

}
