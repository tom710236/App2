package com.bagastudio.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences
{
    public static Editor GetEditor(Activity activity, String category)
    {
        return GetPreferences(activity, category).edit();
    }

    public static SharedPreferences GetPreferences(Activity activity, String category)
    {
        return activity.getSharedPreferences(category, Context.MODE_PRIVATE);
    }
    
    public static void SavePreferences(Activity activity, Editor editor)
    {
        editor.commit();
    }

}
