package com.pacificcycle.shipment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bagastudio.backgroundservice.HttpRequestTask;
import com.example.barcodereaderintentreceiver.R;
import com.pacificcycle.data.ProductItem;
import com.pacificcycle.listadapter.ProductItemAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailActivity extends Activity
{
    private static void setupButtonBack(final Activity activity)
    {
        final Button buttonBack = (Button) activity.findViewById(R.id.buttonBack);
        if (buttonBack != null)
        {
            buttonBack.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View arg0)
                {
                    activity.finish();
                }
            });
        }
    }

    private static void SetupButtonLogout(final Activity activity, final Class<?> targetActivityClass)
    {
        final Button buttonLogout = (Button) activity.findViewById(R.id.buttonLogout);
        if (buttonLogout != null)
        {
            buttonLogout.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View arg0)
                {
                    final Intent intent = new Intent(activity, targetActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                }
            });
        }
    }

    String account = null;
    private String encryptedAccount;
    ProductItemAdapter productItemAdapter = null;
    ArrayList<ProductItem> productItems = null;
    HttpRequestTask retrieveTask = null;

    String server = null;

    String shipDetailNo = null;
    String shipNo = null;
    private String cUserName;
    private String modelType;
    private String model;
    private String modelColor;
    private String productItemNo;

    public String getEncryptedAccount()
    {
        return this.encryptedAccount;
    }

    public String getServer()
    {
        return this.server;
    }

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_product_detail);

        this.model = this.getIntent().getStringExtra("model");
        this.modelType = this.getIntent().getStringExtra("modelType");
        this.modelColor = this.getIntent().getStringExtra("modelColor");
        this.productItemNo = this.getIntent().getStringExtra("productItemNo");
        this.server = this.getIntent().getStringExtra("server");
        this.account = this.getIntent().getStringExtra("account");
        this.cUserName = this.getIntent().getStringExtra("cUserName");
        this.encryptedAccount = this.getIntent().getStringExtra("encryptedAccount");
        this.shipNo = this.getIntent().getStringExtra("shipNo");
        final String productItemNo = this.getIntent().getStringExtra("productItemNo");
        this.shipDetailNo = this.getIntent().getStringExtra("shipDetailNo");

        this.setupTextViewLoginUser(this.cUserName);
        this.setupTextViewTrail();
        this.setupTextViewModelType();
        this.setupTextViewModel();
        this.setupTextViewModelColor();

        ProductDetailActivity.SetupButtonLogout(this, LoginActivity.class);
        ProductDetailActivity.setupButtonBack(this);

        final ListView listViewProductItem = (ListView) this.findViewById(R.id.listViewProductItem);
        this.productItems = new ArrayList<ProductItem>();
        this.productItemAdapter = new ProductItemAdapter(this, this, R.id.listViewProductItem, this.productItems);
        this.setupListViewProductItem(listViewProductItem, this.productItems, this.productItemAdapter, this);

    }

    private void setupTextViewModelType()
    {
        TextView textViewModelType = (TextView) this.findViewById(R.id.textViewModelType);
        if (textViewModelType != null)
        {
            textViewModelType.setText(this.modelType);
        }
    }

    private void setupTextViewModel()
    {
        TextView textViewModel = (TextView) this.findViewById(R.id.textViewModel);
        if (textViewModel != null)
        {
            textViewModel.setText(this.model);
        }
    }

    private void setupTextViewModelColor()
    {
        TextView textViewModelColor = (TextView) this.findViewById(R.id.textViewModelColor);
        if (textViewModelColor != null)
        {
            textViewModelColor.setText(this.modelColor);
        }
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    public void reloadTableData()
    {
        this.retrieveTableData(this.server + "?f=GetShipmentProductList&Account=" + this.encryptedAccount + "&shipDetailNo=" + this.shipDetailNo);
    }

    private void retrieveTableData(String url)
    {
        if (this.retrieveTask != null)
        {
            this.retrieveTask.cancel(true);
        }
        this.retrieveTask = new HttpRequestTask()
        {
            @Override protected void onPostExecute(String result)
            {
                if (result == null) { return; }

                try
                {
                    ProductDetailActivity.this.productItems.clear();
                    final JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        final String Seq = jsonObject.getString("Seq");
                        final String cProductNo = jsonObject.getString("cProductNo");
                        final String cBBNo = jsonObject.getString("cBBNo");

                        ProductDetailActivity.this.productItems.add(new ProductItem(Seq, cProductNo, cBBNo, ProductDetailActivity.this.shipNo, "車架編號"));
                    }

                    ProductDetailActivity.this.productItemAdapter.notifyDataSetChanged();

                }
                catch (final JSONException e)
                {
                    e.printStackTrace();
                }

                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("Message"))
                    {
                        CharSequence message = jsonObject.getString("Message");

                        Toast toast = Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_SHORT);
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
        };
        this.retrieveTask.execute(url);
    }

    private void setupListViewProductItem(final ListView listViewProductItem, ArrayList<ProductItem> productItems, ProductItemAdapter adapter, Activity activity)
    {
        this.productItemAdapter.productDetailActivity = this;
        listViewProductItem.setAdapter(this.productItemAdapter);
        listViewProductItem.setAdapter(adapter);

        this.reloadTableData();
    }

    private void setupTextViewLoginUser(String cUserName)
    {
        final TextView textViewLoginUser = (TextView) this.findViewById(R.id.textViewLoginUser);
        if (textViewLoginUser != null)
        {
            textViewLoginUser.setText(cUserName);
        }
    }

    private TextView setupTextViewShipNo(String shipNo)
    {
        final TextView textViewShipNo = (TextView) this.findViewById(R.id.textViewShipNo);
        if (textViewShipNo != null)
        {
            textViewShipNo.setText(shipNo);
        }
        return textViewShipNo;
    }

    private void setupTextViewTrail()
    {
        TextView textViewTrail = (TextView) this.findViewById(R.id.textViewTrail);
        if (textViewTrail != null)
        {
            textViewTrail.setText(this.shipNo + " > " + this.productItemNo);
        }
    }
}
