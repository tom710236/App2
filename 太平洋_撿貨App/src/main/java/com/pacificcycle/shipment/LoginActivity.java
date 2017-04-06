package com.pacificcycle.shipment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bagastudio.backgroundservice.HttpRequestTask;
import com.example.barcodereaderintentreceiver.R;
import com.pacificcycle.listadapter.ProductItemAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
//    private String account = "000000007";
    private String account = "";
    protected String cUserName;
    private String encryptedAccount = null;
    private String encryptedPassword = null;
    // private String encryptedAccount = "sM5le4igL2O3ul54BSsSNA==";
    // private String encryptedPassword = "834errVfEYA=";
    //private String password = "123";
    private String password = "";
    HttpRequestTask requestTaskLogin = null;

    protected void login(final String server, final String encryptedAccount, final String encryptedPassword)
    {
        final String serverSite = server;
        final String url = server + "?f=GetLoginUser&Password=" + encryptedPassword + "&Account=" + encryptedAccount;
        if (this.requestTaskLogin != null)
        {
            this.requestTaskLogin.cancel(true);
        }
        this.requestTaskLogin = new HttpRequestTask()
        {
            @Override protected void onPostExecute(final String result)
            {
                if (result == null) { return; }

                try
                {
                    final JSONArray jsonArray = new JSONArray(result);

                    if (jsonArray.length() <= 0) { return; }

                    final JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                    if (jsonObject.getString("Status").equals("1"))
                    {
                        final SharedPreferences.Editor editor = LoginActivity.this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE).edit();
                        editor.putString("account", LoginActivity.this.account);
                        editor.putString("password", LoginActivity.this.password);
                        editor.commit();

                        final Intent intent = new Intent(LoginActivity.this, ShipmentListActivity.class);
                        intent.putExtra("server", serverSite);
                        intent.putExtra("account", LoginActivity.this.account);
                        intent.putExtra("cUserName", jsonObject.getString("cUserName"));
                        intent.putExtra("encryptedAccount", LoginActivity.this.encryptedAccount);
                        LoginActivity.this.startActivity(intent);
                    }
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("Message"))
                    {
                        CharSequence message = jsonObject.getString("Message");

                        Toast toast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT);
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
        this.requestTaskLogin.execute(url, server);
    }

    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_login);

        this.setupEditTextAccount();
        this.setupEditTextPassword();
        this.setupButtonLogin();
        this.setupButtonCancel();
    }

    private void setupButtonCancel()
    {
        final Button buttonCancel = (Button) this.findViewById(R.id.button_Cancel);
        buttonCancel.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                LoginActivity.this.account = "";
                LoginActivity.this.password = "";
                LoginActivity.this.encryptedAccount = "";
                LoginActivity.this.encryptedPassword = "";
                final EditText editTextAccount = (EditText) LoginActivity.this.findViewById(R.id.EditText_Account);
                if (editTextAccount != null)
                {
                    LoginActivity.this.account = editTextAccount.getText().toString();
                    LoginActivity.this.encryptedAccount = HttpRequestTask.desEncrypt(LoginActivity.this.account);
                }
                final EditText editTextPassword = (EditText) LoginActivity.this.findViewById(R.id.EditText_Password);
                if (editTextPassword != null)
                {
                    LoginActivity.this.password = editTextPassword.getText().toString();
                    LoginActivity.this.encryptedPassword = HttpRequestTask.desEncrypt(LoginActivity.this.password);
                }
                if ((LoginActivity.this.account.length() > 0) && (LoginActivity.this.password.length() > 0))
                {
                    // LoginActivity.this.login("http://demo.shinda.com.tw/pacificadmin/getAPI.ashx", LoginActivity.this.encryptedAccount, LoginActivity.this.encryptedPassword);
                    LoginActivity.this.login("http://54.65.104.142/PacificAdmin/getAPI.ashx", LoginActivity.this.encryptedAccount, LoginActivity.this.encryptedPassword);
                }
            }
        });
    }

    private void setupButtonLogin()
    {
        final Button buttonLogin = (Button) this.findViewById(R.id.button_Login);
        buttonLogin.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                LoginActivity.this.account = "";
                LoginActivity.this.password = "";
                LoginActivity.this.encryptedAccount = "";
                LoginActivity.this.encryptedPassword = "";
                final EditText editTextAccount = (EditText) LoginActivity.this.findViewById(R.id.EditText_Account);
                if (editTextAccount != null)
                {
                    LoginActivity.this.account = editTextAccount.getText().toString();
                    LoginActivity.this.encryptedAccount = HttpRequestTask.desEncrypt(LoginActivity.this.account);
                }
                final EditText editTextPassword = (EditText) LoginActivity.this.findViewById(R.id.EditText_Password);
                if (editTextPassword != null)
                {
                    LoginActivity.this.password = editTextPassword.getText().toString();
                    LoginActivity.this.encryptedPassword = HttpRequestTask.desEncrypt(LoginActivity.this.password);
                }
                if ((LoginActivity.this.account.length() > 0) && (LoginActivity.this.password.length() > 0))
                {
                    LoginActivity.this.login("http://pqr.pacific-cycles.com/getAPI.ashx", LoginActivity.this.encryptedAccount, LoginActivity.this.encryptedPassword);
                }
            }
        });
    }

    private void setupEditTextAccount()
    {
        final SharedPreferences prefs = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.account = prefs.getString("account", this.account);
        final EditText editTextAccount = (EditText) this.findViewById(R.id.EditText_Account);
        if (editTextAccount != null)
        {
            editTextAccount.setText(this.account);
        }
    }

    private void setupEditTextPassword()
    {
        final SharedPreferences prefs = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.password = prefs.getString("password", this.password);
        final EditText editTextPassword = (EditText) this.findViewById(R.id.EditText_Password);
        if (editTextPassword != null)
        {
            editTextPassword.setText(this.password);
        }
    }
};
