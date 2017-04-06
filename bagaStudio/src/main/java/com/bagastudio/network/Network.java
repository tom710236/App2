package com.bagastudio.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.widget.Toast;

public class Network
{
    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) { return inetAddress.getHostAddress(); }
                }
            }
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getLocalIpAddress(int ip1, int ip2, int ip3, int ip4)
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                    {
                        String hostAddress = inetAddress.getHostAddress();

                        String[] split = hostAddress.split("\\.");
                        if (split.length != 4)
                        {
                            continue;
                        }

                        if ((ip1 == 0 || ("" + ip1).equals(split[0])) && (ip2 == 0 || ("" + ip2).equals(split[1])) && (ip3 == 0 || ("" + ip3).equals(split[2])) && (ip4 == 0 || ("" + ip4).equals(split[3]))) { return hostAddress; }
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
        }
        return "";
    }

}
