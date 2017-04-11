package com.pacificcycle.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.barcodereaderintentreceiver.R;
import com.pacificcycle.data.Shipment;

import java.util.ArrayList;

public class ShipmentListAdapter extends ArrayAdapter<Shipment>
{
    public ShipmentListAdapter(Context context, int resourceId, ArrayList<Shipment> objects)
    {
        super(context, resourceId, objects);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;

        if (view == null)
        {
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            view = vi.inflate(R.layout.listrow_shipment, null);
        }

        if ((position % 2) == 0)
        {
            view.setBackgroundResource(R.drawable.artists_list_backgroundcolor);
        }
        else
        {
            view.setBackgroundResource(R.drawable.artists_list_background_alternate);
        }

        final Shipment p = this.getItem(position);

        if (p != null)
        {
            final TextView textViewShipNo = (TextView) view.findViewById(R.id.textViewShipNo);
            final TextView textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);
            final TextView textViewCustomer = (TextView) view.findViewById(R.id.textViewCustomer);
            final Button buttonDelete = (Button) view.findViewById(R.id.buttonDelete);

            if (textViewCustomer != null)
            {
                textViewCustomer.setText(p.getCustomer());
            }

            if (textViewShipNo != null)
            {
                textViewShipNo.setText(p.getShipNo());
            }

            if (textViewStatus != null)
            {
                textViewStatus.setText(p.getShipStatus());
            }

            if (buttonDelete != null)
            {
                buttonDelete.setOnClickListener(new OnClickListener()
                {
                    @Override public void onClick(View arg0)
                    {
                    }
                });
            }
        }

        return view;
    }
}
