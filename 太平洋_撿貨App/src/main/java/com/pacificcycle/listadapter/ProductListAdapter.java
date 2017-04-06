package com.pacificcycle.listadapter;

import java.util.ArrayList;

import com.example.barcodereaderintentreceiver.R;
import com.pacificcycle.data.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductListAdapter extends ArrayAdapter<Product>
{
    public ProductListAdapter(Context context, int resourceId, ArrayList<Product> objects)
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
            view = vi.inflate(R.layout.listrow_product, null);
        }

        if ((position % 2) == 0)
        {
            view.setBackgroundResource(R.drawable.artists_list_backgroundcolor);
        }
        else
        {
            view.setBackgroundResource(R.drawable.artists_list_background_alternate);
        }

        final Product p = this.getItem(position);

        if (p != null)
        {
            final TextView textViewSeqId = (TextView) view.findViewById(R.id.textViewSeqId);
            final TextView textViewProductId = (TextView) view.findViewById(R.id.textViewProductId);
            final TextView textViewQuantity = (TextView) view.findViewById(R.id.textViewQuantity);
            final TextView textViewModelType = (TextView) view.findViewById(R.id.textViewModelType);
            final TextView textViewModel = (TextView) view.findViewById(R.id.textViewModel);
            final TextView textViewModelColor = (TextView) view.findViewById(R.id.textViewModelColor);
            final TextView textViewQuantityCompo = (TextView) view.findViewById(R.id.textViewQuantityCompo);

            if (textViewQuantityCompo != null)
            {
                textViewQuantityCompo.setText("預計: " + p.getShipQty() + " 實際: " + p.getQty());
            }

          

            if (textViewModelType != null)
            {
                textViewModelType.setText(p.getModelType());
            }

            if (textViewModel != null)
            {
                textViewModel.setText(p.getModel());
            }

            if (textViewModelColor != null)
            {
                textViewModelColor.setText(p.getModelColor());
            }

            if (textViewSeqId != null)
            {
                String seqOutput = p.getSeq();
                while(seqOutput.length()<4)
                {
                    seqOutput = "0" + seqOutput;
                }
                textViewSeqId.setText(seqOutput + "/" + p.getProductItemNo());
            }

            if (textViewProductId != null)
            {
                textViewProductId.setText(p.getProductItemNo());
            }

            if (textViewQuantity != null)
            {
                textViewQuantity.setText(p.getShipQty() + "/" + p.getQty());
            }
        }

        return view;
    }
}
