package com.bagastudio.backgroundservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Base64;

public class HttpRequestTask extends AsyncTask<String, Void, String>
{
    public static byte[] aesEncrypt(final String msg)
    {
        try
        {
            final KeyGenerator keyG = KeyGenerator.getInstance("AES");
            // keyG.init(256);
            final Cipher cipher = Cipher.getInstance("AES");
            final String keyBase64 = "HD2mFi3ioAztaeZFRxnLusnPjeerYO/NlkeQ32NwQ8U=";
            // byte[] key = Base64.decode(keyBase64, Base64.DEFAULT);
            // cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            final String key = "HD2mFi3ioAztaeZFRxnLusnPjeerYO/N";
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
            return cipher.doFinal(msg.getBytes());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String desEncrypt(final String source)
    {
        try
        {
            final String key = "12345678";
            final String iv = "87654321";
            final IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
            final SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "DES");
            final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, zeroIv);
            final byte[] encryptedData = cipher.doFinal(source.getBytes());
            String encodeToString = Base64.encodeToString(encryptedData, Base64.DEFAULT);
            if (encodeToString.endsWith("\n"))
            {
                encodeToString = encodeToString.substring(0, encodeToString.length() - 1);
            }
            return encodeToString;

        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    void aesEncryptTest()
    {
        try
        {
            // 欲加密的字串
            final String msg = "This is a message.";
            System.out.println("原始字串：" + new String(msg));
            // 設定要使用的加密演算法
            final KeyGenerator keyG = KeyGenerator.getInstance("AES");
            // 設定key的長度
            keyG.init(256);
            // 產生SecretKey
            final SecretKey secuK = keyG.generateKey();
            // 取得要用來加密的key(解密也需使用這把key)
            final byte[] key = secuK.getEncoded();
            System.out.println("key：" + new String(key));
            SecretKeySpec spec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // 設定為加密模式
            cipher.init(Cipher.ENCRYPT_MODE, spec);
            // 將字串加密，並取得加密後的資料
            final byte[] encryptData = cipher.doFinal(msg.getBytes());
            System.out.println("加密後字串：" + new String(encryptData));

            // 使用剛剛用來加密的key進行解密
            spec = new SecretKeySpec(key, "AES");
            cipher = Cipher.getInstance("AES");
            // 設定為解密模式
            cipher.init(Cipher.DECRYPT_MODE, spec);
            final byte[] original = cipher.doFinal(encryptData);
            System.out.println("解密後字串：" + new String(original));
        }
        catch (final Exception e)
        {

        }
    }

    @Override protected String doInBackground(final String... urls)
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
            e.printStackTrace();
        }

        return null;
    }

    @Override protected void onPostExecute(final String result)
    {
    }
}
