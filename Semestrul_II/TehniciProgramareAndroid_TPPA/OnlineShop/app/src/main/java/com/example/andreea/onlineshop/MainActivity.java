package com.example.andreea.onlineshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

//    private static final String FILE_NAME = "data.txt";

    TextView textPrice, textDescription;
    List<String> products = new ArrayList<>();
    List<String> prices = new ArrayList<>();
    List<String> description = new ArrayList<>();

    private static final String KEY_TEXT_VALUE_price = "textValue price";
    private static final String KEY_TEXT_VALUE_description = "textValue description";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textPrice = findViewById(R.id.textPrice);
        textDescription = findViewById(R.id.textDescriere);
        if (savedInstanceState != null) {
            CharSequence savedText_price = savedInstanceState.getString(KEY_TEXT_VALUE_price);
            CharSequence savedText_description = savedInstanceState.getString(KEY_TEXT_VALUE_description);
            textPrice.setText(savedText_price);
            textDescription.setText(savedText_description);
        }

        ListView l = findViewById(R.id.listView);
        products.add("mere"); products.add("pere");
        prices.add("1 leu"); prices.add("10 lei");
        description.add("descriere mere"); description.add("descriere pere");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, products);
        l.setAdapter(adapter);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textPrice.setText(prices.get(position));
                textDescription.setText(description.get(position));
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        textPrice.setText(savedInstanceState.getString(KEY_TEXT_VALUE_price));
        textDescription.setText(savedInstanceState.getString(KEY_TEXT_VALUE_description));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE_price, textPrice.getText());
        outState.putCharSequence(KEY_TEXT_VALUE_description, textDescription.getText());
    }

    private static final String TAG = "MyActivity";

    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Now onStart() calls");
        String startUpMessage = "Application started";
//        Toast.makeText(MainActivity.this, startUpMessage, Toast.LENGTH_LONG).show();
    }
//
//    protected void onRestart() {
//        super.onRestart();
//        Log.d(TAG, "Now onRestart() calls");
//        String restartMessage = "Application restarted";
////        Toast.makeText(MainActivity.this, restartMessage, Toast.LENGTH_LONG).show();
//    }
//
//    protected void onResume() {
//        super.onResume();
//        // change the background of the application according to user settings
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        String color = prefs.getString("background", "white");
//        View view = findViewById(android.R.id.content).getRootView();
//        view.setBackgroundColor(Color.parseColor(color));
//    }
//
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "Now onPause() calls");
//        String pauseMessage = "Application paused";
////        Toast.makeText(MainActivity.this, pauseMessage, Toast.LENGTH_LONG).show();
//    }
//
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "Now onStop() calls");
//        String stopMessage = "Application stopped. Bye bye!";
////        Toast.makeText(MainActivity.this, stopMessage, Toast.LENGTH_LONG).show();
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "Now onDestroy() calls");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.new_product:
//                newProduct();
//                return true;
////            case R.id.hi:
////                sayHi();
////                return true;
////            case R.id.send_sms:
////                sendSMS();
////                return true;
//            case R.id.settings:
//                startActivity(new Intent(this, MyPreferences.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public boolean newProduct(View view) {
//        TODO: make the new product appear on the list in the main page
//        Toast.makeText(getApplicationContext(),"Add New Product option selected",Toast.LENGTH_LONG).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(mView);
        builder.setTitle("Add new product");

        final EditText input_name = mView.findViewById(R.id.input_name);
//        final EditText input_price = mView.findViewById(R.id.input_price);
//        final EditText input_description = mView.findViewById(R.id.input_description);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nume_produs = input_name.getText().toString();
//                String pret_produs = input_price.getText().toString();
//                String descriere_produs = input_description.getText().toString();
                if(!nume_produs.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "You added: " + nume_produs + " " + pret_produs + " " + descriere_produs, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "You added: " + nume_produs, Toast.LENGTH_LONG).show();
                    products.add(nume_produs);
//                    prices.add(pret_produs);
//                    description.add(descriere_produs);
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
        return true;
    }

//    public boolean sayHi() {
//        Toast.makeText(getApplicationContext(),"Hello there. Have a nice day :)",Toast.LENGTH_LONG).show();
//        return true;
//    }
//
//    public boolean sendSMS() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        final EditText input_nr = new EditText(this);
//        input_nr.setHint("Phone number");
//        input_nr.setHintTextColor(Color.GRAY);
//
//        builder.setView(input_nr);
//        builder.setTitle("Send a SMS");
//
//        // Set up the buttons
//        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String number = input_nr.getText().toString();
//                if(!number.isEmpty()) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//
//        return true;
//    }

//    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

//    /** Called when the user taps the Send button */
//    public void sendMessage(View view) {
//        Toast.makeText(getApplicationContext(),"Message sent.",Toast.LENGTH_LONG).show();
//
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }

    // saving products to Internal Storage
//    public void saveData(View view) {
//        FileOutputStream fos;
//        try {
//            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            for (int counter = 0; counter < products.size(); counter++) {
//                fos.write(products.get(counter).getBytes());
//                fos.write("\n".getBytes());
//            }
//            Toast.makeText(this, "Saved to: " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void sensorsInfo(View view) {
//        startActivity(new Intent(this, SensorActivity.class));
//    }
//
//    public void GPSlocation(View view) {
//        startActivity(new Intent(this, GPSActivity.class));
//    }
//
//    public void takePhoto(View view) {
//        startActivity(new Intent(this, CameraActivity.class));
//    }
}