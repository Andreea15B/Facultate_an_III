package com.example.andreea.shoppingassistant;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StoresAdapter extends ArrayAdapter<Store> {
    public StoresAdapter(Context context, ArrayList<Store> stores) {
        super(context, 0, stores);
    }

    static int id_ = 0;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Store store = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stores_details_layout, parent, false);

        // Lookup view for data population
        TextView textName = convertView.findViewById(R.id.textName);
        TextView textLatitude = convertView.findViewById(R.id.textLatitude);
        TextView textLongitude = convertView.findViewById(R.id.textLongitude);

        // Populate the data into the template view using the data object
        textName.setText(store.getName());
        textLatitude.setText(store.getLatitude());
        textLongitude.setText(store.getLongitude());

        Button myButton = new Button(getContext());
        myButton.setBackgroundResource(R.drawable.round_button);
        myButton.setTextColor(Color.parseColor("#4BB3C9"));
        myButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        myButton.setId(id_++);
        myButton.setText("See products");
        RelativeLayout layout = convertView.findViewById(R.id.see_products_layout);
        layout.addView(myButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayStores displayStoresActivity = DisplayStores.getInstance();
                displayStoresActivity.showProductsForStore(store);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
