package com.bagastudio.backgroundservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class PostRequestTask extends AsyncTask<String, Void, String>
{
    byte[] filedata = null;

    float connectionTimeout = 26.0f;
    float socketTimeout = 35.0f;

    @Override protected String doInBackground(final String... urls)
    {
        if (urls.length <= 0) { return null; }

        final String url = urls[0];
        final HttpPost httppost = new HttpPost(url);

        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        for (int i = 1; i < urls.length; i += 2)
        {
            final String name = urls[i];
            final String value = (i + 1) < urls.length ? urls[i + 1] : "";
            nameValuePairs.add(new BasicNameValuePair(name, value));
        }
        
        try
        {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            return "{error:"+e.getMessage()+"}";
        }

        final int timeoutConnection = (int)(this.connectionTimeout * 1000);
        final HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        final int timeoutSocket = (int)(socketTimeout * 1000);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        final HttpClient httpclient = new DefaultHttpClient(httpParameters);

        HttpResponse response;
        try
        {
            response = httpclient.execute(httppost);
        }
        catch (ClientProtocolException e)
        {
            return "{error:"+e.getMessage()+"}";
        }
        catch (IOException e)
        {
            return "{error:"+e.getMessage()+"}";
        }
        catch (Exception e)
        {
            return "{error:"+e.getMessage()+"}";
        }

        try
        {
            this.filedata = EntityUtils.toByteArray(response.getEntity());
        }
        catch (IOException e)
        {
            return "{error:"+e.getMessage()+"}";
        }
        try
        {
            return new String(this.filedata, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return "{error:"+e.getMessage()+"}";
        }
    }

    public byte[] getFiledata()
    {
        return this.filedata;
    }

    @Override protected void onPostExecute(final String result)
    {
    }

    public void setConnectionTimeout(final float connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }
    
    public void setSocketTimeout(final float socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }
}
