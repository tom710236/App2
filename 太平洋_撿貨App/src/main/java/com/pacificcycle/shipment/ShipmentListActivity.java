package com.pacificcycle.shipment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bagastudio.backgroundservice.HttpRequestTask;
import com.example.barcodereaderintentreceiver.R;
import com.pacificcycle.data.Shipment;
import com.pacificcycle.listadapter.ShipmentListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShipmentListActivity extends Activity
{
    String account = null;
    private String encryptedAccount = null;
    HttpRequestTask requestTaskRetrieveTableData = null;
    String server = null;
    ShipmentListAdapter shipmentListAdapter = null;
    ArrayList<Shipment> shipments = null;
    ArrayList<Shipment> originalShipments = null;
    private String cUserName;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_shipment_list);

        this.server = this.getIntent().getStringExtra("server");
        this.account = this.getIntent().getStringExtra("account");
        this.cUserName = this.getIntent().getStringExtra("cUserName");
        this.encryptedAccount = this.getIntent().getStringExtra("encryptedAccount");

        this.setupEditTextShipment();
        this.setupTextViewLoginUser(this.cUserName);
        this.setupButtonBack();
        this.setupButtonLogout();

        this.shipments = new ArrayList<Shipment>();
        this.originalShipments = new ArrayList<Shipment>();
        this.shipmentListAdapter = new ShipmentListAdapter(this, R.id.listViewShipment, this.shipments);
        this.setupListViewShipment(this.server, this.account, this.encryptedAccount, this.shipments, this.originalShipments, this.shipmentListAdapter);
    }

    private void setupEditTextShipment()
    {
        final EditText editTextShipment = (EditText) this.findViewById(R.id.editTextShipment);
        if (editTextShipment != null)
        {
            editTextShipment.addTextChangedListener(new TextWatcher()
            {

                @Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
                {
                    shipments.clear();
                    for (int i = 0; i < originalShipments.size(); i++)
                    {
                        Shipment shipment = originalShipments.get(i);
                        if (shipment.getShipNo().contains(cs))
                        {
                            shipments.add(shipment);
                        }
                    }
                    shipmentListAdapter.notifyDataSetChanged();
                }

                @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
                {
                }

                @Override public void afterTextChanged(Editable arg0)
                {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override protected void onResume()
    {
        super.onResume();

        this.reloadTableData(this.server, this.account, this.shipments, this.originalShipments, this.shipmentListAdapter);
    }

    public void reloadTableData(String server, String account, final ArrayList<Shipment> shipments, final ArrayList<Shipment> originalShipments, final ShipmentListAdapter shipmentListAdapter)
    {
        if (this.requestTaskRetrieveTableData != null)
        {
            this.requestTaskRetrieveTableData.cancel(true);
        }
        this.requestTaskRetrieveTableData = new HttpRequestTask()
        {
            @Override protected void onPostExecute(String result)
            {
                if (result == null) { return; }

                try
                {
                    shipments.clear();
                    originalShipments.clear();
                    final JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        final String cShipNo = jsonObject.getString("cShipNo");
                        final String cShipStatus = jsonObject.getString("cShipStatus");
                        final String cCreateDT = jsonObject.getString("cCreateDT");
                        final String cCompanyName = jsonObject.getString("cCompanyName");

                        final EditText editTextShipment = (EditText) ShipmentListActivity.this.findViewById(R.id.editTextShipment);
                        if (editTextShipment == null || editTextShipment.getText().length() <= 0 || cShipNo.contains(editTextShipment.getText()))
                        {
                            shipments.add(new Shipment(cCreateDT, cShipNo, cShipStatus, cCompanyName));
                        }
                        originalShipments.add(new Shipment(cCreateDT, cShipNo, cShipStatus, cCompanyName));
                    }

                    shipmentListAdapter.notifyDataSetChanged();
                }
                catch (final JSONException e)
                {
                    e.printStackTrace();
                }
                
                if (result == null) { return; }

                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("Message"))
                    {
                        CharSequence message = jsonObject.getString("Message");

                        Toast toast = Toast.makeText(ShipmentListActivity.this, message, Toast.LENGTH_SHORT);
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
        this.requestTaskRetrieveTableData.execute(server + "?f=GetShipmentList&Account=" + this.encryptedAccount);
    }

    private void setupButtonBack()
    {
        final Button buttonBack = (Button) this.findViewById(R.id.buttonBack);
        if (buttonBack != null)
        {
            buttonBack.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View arg0)
                {
                    ShipmentListActivity.this.finish();
                }
            });
        }
    }

    private void setupButtonLogout()
    {
        final Button buttonLogout = (Button) this.findViewById(R.id.buttonLogout);
        if (buttonLogout != null)
        {
            buttonLogout.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View arg0)
                {
                    final Intent intent = new Intent(ShipmentListActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ShipmentListActivity.this.startActivity(intent);
                }
            });
        }
    }

    private void setupListViewShipment(final String server, final String account, final String encryptedAccount, ArrayList<Shipment> shipments, ArrayList<Shipment> originalShipments, ShipmentListAdapter shipmentListAdapter)
    {
        final ListView listView = (ListView) this.findViewById(R.id.listViewShipment);
        listView.setAdapter(this.shipmentListAdapter);
        if (listView != null)
        {
            final ShipmentListAdapter shipmentListAdapterFinal = shipmentListAdapter;
            listView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> av, View v, int position, long arg3)
                {
                    final Intent intent = new Intent(ShipmentListActivity.this, ProductListActivity.class);
                    final Shipment shipment = shipmentListAdapterFinal.getItem(position);
                    intent.putExtra("server", server);
                    intent.putExtra("shipNo", shipment.getShipNo());
                    intent.putExtra("shipStatus", shipment.getShipStatus());
                    intent.putExtra("account", account);
                    intent.putExtra("cUserName", cUserName);
                    intent.putExtra("encryptedAccount", encryptedAccount);
                    ShipmentListActivity.this.startActivity(intent);
                }
            });
        }
        this.reloadTableData(server, account, shipments, originalShipments, shipmentListAdapter);
    }

    private void setupTextViewLoginUser(String cUserName)
    {
        final TextView textViewLoginUser = (TextView) this.findViewById(R.id.textViewLoginUser);
        if (textViewLoginUser != null)
        {
            textViewLoginUser.setText(cUserName);
        }
    }
}
