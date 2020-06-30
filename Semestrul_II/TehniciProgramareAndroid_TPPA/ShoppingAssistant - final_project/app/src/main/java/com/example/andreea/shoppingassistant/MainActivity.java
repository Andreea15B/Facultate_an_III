package com.example.andreea.shoppingassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.andreea.shoppingassistant.MyApplication.CHANNEL_1_ID;
import static com.example.andreea.shoppingassistant.MyApplication.stores;

public class MainActivity extends AppCompatActivity {
    static MainActivity instance;

    ArrayList<Product> products_in_list = new ArrayList<>();
    ListView products_list;
    String[] categories = {"All", "Fruits & Vegetables", "Diary", "Beauty & Skincare", "Books", "Meat & Seafood", "Cleaning", "Beverages", "Pets", "Other", "Kids", "Bread & Cereal", "Condiments & Spices", "Baking", "Pasta & Rice", "Snacks", "Health", "Household"};
    ProductAdapter adapter;

    private void initializeViews() {
        Spinner mySpinner = findViewById(R.id.btn_filter);
        mySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories));

        products_list = findViewById(R.id.productsList);
        adapter = new ProductAdapter(this, products_in_list);
        products_list.setAdapter(adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position >= 0 && position < categories.length)
                    getSelectedCategoryData(categories[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylist_layout);
        initializeViews();

        Product p2 = new Product("Milk", "Diary", 1, "litre");
        products_in_list.add(p2);
        Product p3 = new Product("Bread", "Bread & Cereal", 2, "Other");
        products_in_list.add(p3);

        notificationManager = NotificationManagerCompat.from(this);

        instance = this;

    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void getSelectedCategoryData(String category) {
        ArrayList<Product> pr = new ArrayList<>();
        if (category == "All") {
            adapter = new ProductAdapter(this, products_in_list);
        }
        else {
            for(Product product : products_in_list) {
                if(product.getCategory().equals(category)) {
                    pr.add(product);
                }
            }
            adapter = new ProductAdapter(this, pr);
        }
        products_list.setAdapter(adapter);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        products_in_list = (ArrayList<Product>) ((MyApplication) getApplication()).getData();
        ProductAdapter adapter = new ProductAdapter(this, products_in_list);
        products_list.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ((MyApplication) getApplication()).storeData(products_in_list);
    }

    public void addProduct(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.add_product, null);
        builder.setView(mView);
        builder.setTitle("Add new product");

        final EditText input_name = mView.findViewById(R.id.input_name);
        final Spinner input_category = mView.findViewById(R.id.input_type);
        final EditText input_amount = mView.findViewById(R.id.input_amount);
        final Spinner input_amountType = mView.findViewById(R.id.amountType);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input_name.getText().toString();
                String category = input_category.getSelectedItem().toString();
                int amount = Integer.parseInt(input_amount.getText().toString());
                String amountType = input_amountType.getSelectedItem().toString();

                if(!name.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "You added: " + name + " " + category + " " + amount, Toast.LENGTH_LONG).show();
                    Product p = new Product(name, category, amount, amountType);
                    products_in_list.add(p);
                    ProductAdapter adapter = new ProductAdapter(MainActivity.this, products_in_list);
                    products_list.setAdapter(adapter);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void deleteProduct(Product product) {
        products_in_list.remove(product);
        ProductAdapter adapter = new ProductAdapter(MainActivity.this, products_in_list);
        products_list.setAdapter(adapter);
    }

    public void showStores(View view) {
        Intent intent = new Intent(this, DisplayStores.class);
        startActivity(intent);
    }

    private NotificationManagerCompat notificationManager;
    public void sendNotification(Store store, ArrayList<Product> products_to_display, int id) {
        String products_message = "You can find these items here: ";
        for(Product product : products_to_display) {
            products_message += product.getName() + ", ";
        }
        products_message = products_message.substring(0, products_message.length()-2);
        Log.i("sendNotification", store.getName() + products_message);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(products_message);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_shoppingcart_icon)
                .setContentTitle("You are near " + store.getName())
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setStyle(bigText)
                .build();
         notificationManager.notify(id, notification);
    }

    public void findNearStores(Location currentLocation) {
        int id = 1;
        double distance;
        for(Store store: stores) {
            Location storeLocation = store.getLocation();
            distance = currentLocation.distanceTo(storeLocation)/1000; // in km
            if(distance < 5) {
                ArrayList<Product> products_in_store = store.getProducts_in_store();
                ArrayList<Product> products_from_list_in_store = new ArrayList<>();
                for(Product product_in_list : products_in_list) {
                    for(Product prod_in_store: products_in_store) {
                        if(prod_in_store.getName().equals(product_in_list.getName()))
                            products_from_list_in_store.add(product_in_list);
                    }
                }
                if(!products_from_list_in_store.isEmpty()) {
                    sendNotification(store, products_from_list_in_store, id++);
                }

                products_from_list_in_store.clear();
            }
        }
    }
}
