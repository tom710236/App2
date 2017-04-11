package com.pacificcycle.shipment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bagastudio.backgroundservice.HttpRequestTask;
import com.example.barcodereaderintentreceiver.R;
import com.oem.barcode.BCRIntents;
import com.pacificcycle.data.Product;
import com.pacificcycle.listadapter.ProductListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductListActivity extends Activity
{

    private String account = null;

    private String encryptedAccount;
    ProductListAdapter productListAdapter = null;
    ArrayList<Product> products = null;
    HttpRequestTask requestTaskAddNewProduct = null;
    HttpRequestTask requestTaskRetrieveTableData = null;
    HttpRequestTask requestTaskUpdateShipmentStatus = null;
    String server = null;
    Spinner spinnerStatus = null;
    String strShipNo = null;
    String strShipStatus = null;

    TextView textViewQuantity;

    private String cUserName;

    protected void addNewProductNo(String code)
    {
        final String url = this.server + "?f=AddShipmentProduct&Account=" + this.encryptedAccount + "&cNo=" + this.strShipNo + "&productNo=" + code;
        if (this.requestTaskAddNewProduct != null)
        {
            this.requestTaskAddNewProduct.cancel(true);
        }
        this.requestTaskAddNewProduct = new HttpRequestTask()
        {
            @Override protected void onPostExecute(String result)
            {
                // TODO: 
                // {"Status":0,"Message":此產品編號尚未配對 或 不是此訂單的品項}
                ProductListActivity.this.reloadTableData();
                final EditText editTextProductNo = (EditText) findViewById(R.id.editTextProductNo);
                editTextProductNo.requestFocus();
                editTextProductNo.selectAll();

                if (result == null) { return; }

                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("Message"))
                    {
                        CharSequence message = jsonObject.getString("Message");
                        Log.e("MESSAGE", String.valueOf(message));
                        //如果Message回傳值為 StockCompanyNull 跳出提示 請確認該產品是否入庫
                        if(String.valueOf(message).equals("StockCompanyNull")){
                            Toast toast = Toast.makeText(ProductListActivity.this, "產品編號未入庫", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 0);
                            toast.show();
                            return;
                        }else{
                            Toast toast = Toast.makeText(ProductListActivity.this, message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 0);
                            toast.show();
                        }

                    }
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        this.requestTaskAddNewProduct.execute(url);
    }

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_product_list);

        this.server = this.getIntent().getStringExtra("server");
        this.strShipNo = this.getIntent().getStringExtra("shipNo");
        this.strShipStatus = this.getIntent().getStringExtra("shipStatus");
        this.account = this.getIntent().getStringExtra("account");
        this.cUserName = this.getIntent().getStringExtra("cUserName");
        this.encryptedAccount = this.getIntent().getStringExtra("encryptedAccount");

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BCRIntents.ACTION_NEW_DATA);

        final ListView listView = (ListView) this.findViewById(R.id.listViewProduct);
        this.products = new ArrayList<Product>();
        this.productListAdapter = new ProductListAdapter(this, R.id.listViewProduct, this.products);
        this.setupListViewProduct(listView);
        this.setupSpinnerStatus();
        this.setupButtonLogout();
        this.setupButtonBack();
        this.setupTextViewLoginUser();
        this.setupTextViewShipNo();
        this.setupTextViewCustomer();
        this.setupTextViewQuantity();
        this.setupTextViewTrail();

        final EditText editTextProductNo = this.setupEditTextProductNo();
        this.setupButtonSave(editTextProductNo);

    }

    private void setupTextViewTrail()
    {
        TextView textViewTrail = (TextView) this.findViewById(R.id.textViewTrail);
        if (textViewTrail != null)
        {
            textViewTrail.setText(this.strShipNo);
        }
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override protected void onResume()
    {
        super.onResume();

        this.reloadTableData();
    }

    public void reloadTableData()
    {
        this.retrieveTableData(this.server + "?f=GetShipmentProductItemList&Account=" + this.encryptedAccount + "&cNo=" + this.strShipNo);
    }

    private void retrieveTableData(String url)
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
                    ProductListActivity.this.products.clear();
                    final JSONArray jsonArray = new JSONArray(result);

                    int qty = 0;
                    int shipQty = 0;

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        // [  {    "Seq": "10",    "cBicycleTypeName": "BIRDY",    "cProductName": "Rohloff 內14速",    "cColor": "111",    "cShipDetailNo": "229",    "cProductItemNo": "001PCI15110",    "cQty": "2",    "ShipQty": "0"  }]

                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        final String Seq = jsonObject.getString("Seq");
                        final String ShipQty = jsonObject.getString("ShipQty");
                        final String cQty = jsonObject.getString("cQty");
                        final String cShipDetailNo = jsonObject.getString("cShipDetailNo");
                        final String cProductItemNo = jsonObject.getString("cProductItemNo");
                        qty += Integer.parseInt(cQty);
                        shipQty += Integer.parseInt(ShipQty);
                        final String cBicycleTypeName = jsonObject.getString("cBicycleTypeName");
                        final String cProductName = jsonObject.getString("cProductName");
                        final String cColor = jsonObject.getString("cColor");

                        ProductListActivity.this.products.add(new Product(Seq, cShipDetailNo, cProductItemNo, cQty, ShipQty, cBicycleTypeName, cProductName, cColor, cProductItemNo));
                    }

                    ProductListActivity.this.productListAdapter.notifyDataSetChanged();
                    ProductListActivity.this.textViewQuantity.setText("" + shipQty + "/" + qty);
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

                        Toast toast = Toast.makeText(ProductListActivity.this, message, Toast.LENGTH_SHORT);
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

        this.requestTaskRetrieveTableData.execute(url);
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
                    ProductListActivity.this.finish();
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
                    final Intent intent = new Intent(ProductListActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ProductListActivity.this.startActivity(intent);
                }
            });
        }
    }

    private void setupButtonSave(final EditText editTextProductNo)
    {
        final Button buttonSave = (Button) this.findViewById(R.id.buttonSave);

        if (buttonSave != null)
        {
            buttonSave.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View arg0)
                {
                    String code = editTextProductNo.getText().toString();
                    //把HTTP判斷拿掉
                    if(code.lastIndexOf("n=") > 0)
                    {
                        code = code.substring(code.lastIndexOf("n=") + 2);
                    }
                    if(code.length()>8)
                    {
                        code = code.substring(0, 8);
                    }
                    editTextProductNo.setText(code);
                    editTextProductNo.requestFocus();
                    editTextProductNo.selectAll();

                    ProductListActivity.this.addNewProductNo(code);
                }
            });
        }
    }

    private EditText setupEditTextProductNo()
    {
        final EditText editTextProductNo = (EditText) this.findViewById(R.id.editTextProductNo);
        if (editTextProductNo != null)
        {
            editTextProductNo.setOnKeyListener(new OnKeyListener()
            {

                @Override public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                    {
                        String code = editTextProductNo.getText().toString();
                        //把HTTP判斷拿掉
                        if(code.lastIndexOf("n=") > 0)
                        {
                            code = code.substring(code.lastIndexOf("n=") + 2);
                        }

                        if(code.length()>8)
                        {
                            code = code.substring(0, 8);
                        }
                        editTextProductNo.setText(code);
                        editTextProductNo.requestFocus();
                        editTextProductNo.selectAll();

                        ProductListActivity.this.addNewProductNo(code);

                        return true;
                    }
                    return false;
                }
            });
        }
        return editTextProductNo;
    }

    private void setupListViewProduct(final ListView listView)
    {
        listView.setAdapter(this.productListAdapter);
        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
            {
                final Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                final Product product = ProductListActivity.this.productListAdapter.getItem(arg2);
                intent.putExtra("model", product.getModel());
                intent.putExtra("modelType", product.getModelType());
                intent.putExtra("modelColor", product.getModelColor());
                intent.putExtra("server", ProductListActivity.this.server);
                intent.putExtra("shipNo", ProductListActivity.this.getIntent().getStringExtra("shipNo"));
                intent.putExtra("productItemNo", product.getProductItemNo());
                intent.putExtra("shipDetailNo", product.getShipDetailNo());
                intent.putExtra("account", ProductListActivity.this.account);
                intent.putExtra("cUserName", ProductListActivity.this.cUserName);
                intent.putExtra("encryptedAccount", ProductListActivity.this.encryptedAccount);

                ProductListActivity.this.startActivity(intent);
            }
        });

        this.reloadTableData();
    }

    private void setupSpinnerStatus()
    {
        this.spinnerStatus = (Spinner) this.findViewById(R.id.spinnerStatus);
        if (this.spinnerStatus != null)
        {
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.getResources().getStringArray(R.array.status));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinnerStatus.setAdapter(adapter);
            if (this.strShipStatus.equals("已出貨"))
            {
                this.spinnerStatus.setSelection(1);
            }
            else
            {
                this.spinnerStatus.setSelection(0);
            }

            this.spinnerStatus.setOnItemSelectedListener(new OnItemSelectedListener()
            {
                @Override public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
                {
                    if ((position == 1) && !ProductListActivity.this.strShipStatus.equals("已出貨"))
                    {
                        new AlertDialog.Builder(ProductListActivity.this).setTitle("已出貨").setMessage("您確定要結案嗎？").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override public void onClick(DialogInterface dialog, int which)
                            {
                                ProductListActivity.this.strShipStatus = "已出貨";
                                ProductListActivity.this.updateShipmentStatus("1"); // 手動選擇
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override public void onClick(DialogInterface dialog, int which)
                            {
                                ProductListActivity.this.strShipStatus = "未出貨";
                                spinnerStatus.setSelection(0);
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

                    }
                    else if ((position == 0) && ProductListActivity.this.strShipStatus.equals("已出貨"))
                    {
                        ProductListActivity.this.strShipStatus = "未出貨";
                        ProductListActivity.this.updateShipmentStatus("0"); // 手動選擇
                    }
                }

                @Override public void onNothingSelected(AdapterView<?> arg0)
                {
                }
            });
        }
    }

    private void setupTextViewCustomer()
    {
        final TextView textViewCustomer = (TextView) this.findViewById(R.id.textViewCustomer);
        if (textViewCustomer != null)
        {
            textViewCustomer.setText("textViewCustomer");
        }
    }

    private void setupTextViewLoginUser()
    {
        final TextView textViewLoginUser = (TextView) this.findViewById(R.id.textViewLoginUser);
        if (textViewLoginUser != null)
        {
            textViewLoginUser.setText(this.cUserName);
        }
    }

    private void setupTextViewQuantity()
    {
        this.textViewQuantity = (TextView) this.findViewById(R.id.textViewQuantity);
        if (this.textViewQuantity != null)
        {
            this.textViewQuantity.setText("實際: 0");
        }
    }

    private void setupTextViewShipNo()
    {
        final TextView textViewShipNo = (TextView) this.findViewById(R.id.textViewShipNo);
        if (textViewShipNo != null)
        {
            textViewShipNo.setText(this.strShipNo);
        }
    }

    protected void updateShipmentStatus(String status)
    {
        final String url = this.server + "?f=UpdateShipmentStatus&cNo=" + this.strShipNo + "&Status=" + status;
        if (this.requestTaskUpdateShipmentStatus != null)
        {
            this.requestTaskUpdateShipmentStatus.cancel(true);
        }
        this.requestTaskUpdateShipmentStatus = new HttpRequestTask();
        this.requestTaskUpdateShipmentStatus.execute(url);
    }
}
