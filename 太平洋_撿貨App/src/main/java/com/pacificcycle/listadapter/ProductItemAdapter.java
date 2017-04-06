package com.pacificcycle.listadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.barcodereaderintentreceiver.R;
import com.pacificcycle.data.ProductItem;
import com.pacificcycle.shipment.ProductDetailActivity;
import com.pacificcycle.shipment.ProductListActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProductItemAdapter extends ArrayAdapter<ProductItem>
{
    Context context;
    Activity activity;

    class HttpRequestTask extends AsyncTask<String, Void, String>
    {
        @Override protected String doInBackground(String... urls)
        {
            if (urls.length <= 0) { return null; }

            try
            {
                final String url = urls[0];
                final HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
                if (response.getEntity().getContentLength() > 0)
                {
                    final StringBuilder sb = new StringBuilder();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;

                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                    return sb.toString();
                }
            }
            catch (final Exception e)
            {
                // writing exception to log
                e.printStackTrace();
            }

            return null;
        }

        @Override protected void onPostExecute(String result)
        {
            if (ProductItemAdapter.this.productDetailActivity != null)
            {
                ProductItemAdapter.this.productDetailActivity.reloadTableData();
            }

            if (result == null) { return; }

            try
            {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("Message"))
                {
                    CharSequence message = jsonObject.getString("Message");

                    Toast toast = Toast.makeText(productDetailActivity, message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public ProductDetailActivity productDetailActivity = null;

    HttpRequestTask requestTask = null;

    public ProductItemAdapter(Context context, Activity activity, int resourceId, ArrayList<ProductItem> objects)
    {
        super(context, resourceId, objects);
        this.context = context;
        this.activity = activity;
    }

    private void executeHttpRequest(String url)
    {
        if (this.requestTask != null)
        {
            this.requestTask.cancel(true);
        }
        this.requestTask = new HttpRequestTask()
        {
            @Override protected void onPostExecute(String result)
            {
                if (result == null) { return; }

                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("Message"))
                    {
                        CharSequence message = jsonObject.getString("Message");

                        Toast toast = Toast.makeText(productDetailActivity, message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    }
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                productDetailActivity.reloadTableData();
            }
        };
        this.requestTask.execute(url);
    }

    @Override public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(this.getContext());
            view = layoutInflater.inflate(R.layout.listrow_productitem, null);
        }

        if ((position % 2) == 0)
        {
            view.setBackgroundResource(R.drawable.artists_list_backgroundcolor);
        }
        else
        {
            view.setBackgroundResource(R.drawable.artists_list_background_alternate);
        }

        final ProductItem productItem = this.getItem(position);

        if (productItem != null)
        {
            final TextView textViewSeqId = (TextView) view.findViewById(R.id.textViewSeqId);
            final TextView textViewProductId = (TextView) view.findViewById(R.id.textViewProductId);
            final Button buttonDelete = (Button) view.findViewById(R.id.buttonDelete);
            final TextView textViewBBNo = (TextView) view.findViewById(R.id.textViewBBNo);

            if (textViewBBNo != null)
            {
                textViewBBNo.setText(productItem.getBBNo());
            }

            if (textViewSeqId != null)
            {
                textViewSeqId.setText(productItem.getSeq());
            }

            if (textViewProductId != null)
            {
                textViewProductId.setText(productItem.getProductNo());
            }

            if (buttonDelete != null)
            {
                buttonDelete.setOnClickListener(new OnClickListener()
                {

                    @Override public void onClick(View v)
                    {
                        new AlertDialog.Builder(activity).setTitle("您確定刪除嗎？").setMessage("").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override public void onClick(DialogInterface dialog, int which)
                            {
                                final String url = ProductItemAdapter.this.productDetailActivity.getServer() + "?f=DeleteShipmentProduct&Account=" + ProductItemAdapter.this.productDetailActivity.getEncryptedAccount() + "&cNo=" + productItem.getShipNo() + "&productNo=" + productItem.getProductNo();
                                ProductItemAdapter.this.executeHttpRequest(url);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override public void onClick(DialogInterface dialog, int which)
                            {
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

                    }
                });
            }
        }

        return view;
    }
}
