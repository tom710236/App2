package com.bagastudio.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtility
{
    public static JSONObject jsonObjectFromJsonString(final String resultString)
    {
        JSONObject resultObject = null;
        try
        {
            resultObject = new JSONObject(resultString);
        }
        catch (final JSONException e)
        {
        }
        return resultObject;
    }

    public static String stringFromJsonObjectWithKey(final JSONObject resultJsonObject, String key)
    {
        String result = "";
        try
        {
            result = resultJsonObject.getString(key);
        }
        catch (final JSONException e)
        {
        }
        return result;
    }

    public static JSONArray arrayFromJsonObjectWithKey(final JSONObject resultJsonObject, String key)
    {
        JSONArray result;
        try
        {
            result = resultJsonObject.getJSONArray(key);
        }
        catch (final JSONException e)
        {
            result = new JSONArray();
        }
        return result;
    }

    public static JSONObject jsonObjectFromJsonArrayWithIndex(JSONArray statusJsonArray, int i)
    {
        try
        {
            return (JSONObject) statusJsonArray.get(i);
        }
        catch (JSONException e)
        {
            return new JSONObject();
        }
    }

}
