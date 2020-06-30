package com.example.andreea.shoppingassistant;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    public ProductAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
    }

    static int id_ = 0;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_details_layout, parent, false);

        // Lookup view for data population
        TextView textName = convertView.findViewById(R.id.textName);
        TextView textCategory = convertView.findViewById(R.id.textCategory);
        TextView textAmount = convertView.findViewById(R.id.textAmount);

        String entire_amount = Integer.toString(product.getAmount());
        if(!product.getAmountType().equals("Other") && !product.getAmountType().equals("Type"))
            entire_amount += " " + product.getAmountType();

        // Populate the data into the template view using the data object
        textName.setText(product.getName());
        textCategory.setText(product.getCategory());
        textAmount.setText(entire_amount);

        Button myButton = new Button(getContext());
        myButton.setId(id_++);
        myButton.setBackgroundResource(R.drawable.ic_trash_icon);
        RelativeLayout layout = convertView.findViewById(R.id.delete_button_layout);
        layout.addView(myButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = MainActivity.getInstance();
                mainActivity.deleteProduct(product);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
