package com.example.andreea.shoppingassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.andreea.shoppingassistant.MyApplication.stores;

public class DisplayStores extends AppCompatActivity {
    static DisplayStores instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.stores_layout);

        populateStores();
    }

    public static DisplayStores getInstance() {
        return instance;
    }

    public void populateStores() {
        ListView stores_list = findViewById(R.id.storesList);
        StoresAdapter adapter = new StoresAdapter(this, stores);
        stores_list.setAdapter(adapter);
    }

    public void showProductsForStore(Store store) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.products_in_store, null);
        builder.setView(mView);
        builder.setTitle("Products in " + store.getName());

        ArrayList<Product> productsInStore = store.getProducts_in_store();
        for(int i=0; i<productsInStore.size(); i++) {
            String text = productsInStore.get(i).getName() + "  -  " + productsInStore.get(i).getAmount();
            if(!productsInStore.get(i).getAmountType().equals("Other") && !productsInStore.get(i).getAmountType().equals("Type"))
                text += " " + productsInStore.get(i).getAmountType();

            TextView textView = new TextView(this);
            textView.setText(text);
            textView.setTextSize(19);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(lparams);
            textView.setGravity(Gravity.CENTER);
            LinearLayout layout = mView.findViewById(R.id.seeProducts_linearLayout);
            layout.addView(textView);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
