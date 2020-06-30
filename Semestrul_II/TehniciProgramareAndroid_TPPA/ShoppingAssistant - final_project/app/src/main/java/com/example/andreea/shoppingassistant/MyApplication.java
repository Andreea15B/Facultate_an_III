package com.example.andreea.shoppingassistant;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.location.Location;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyApplication extends Application {
    private List<Product> data;
    public static final String CHANNEL_1_ID = "channel1";
    public static ArrayList<Store> stores = new ArrayList<>();
    public static ArrayList<Product> products = new ArrayList<>();

    public void storeData(List<Product> data) {
        this.data = data;
    }

    public List<Product> getData() {
        return data;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        populateStores();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public void populateStores() {
        MyLocation g = new MyLocation(getApplicationContext());
        Location loc = g.getLocation();

        Product p1 = new Product("Milk", "Diary", 10, "boxes");
        Product p2 = new Product("Bread", "Bread & Cereal", 20, "Other");
        Product p3 = new Product("Cheese", "Diary", 15, "Other");
        Product p4 = new Product("Chocolate", "Snacks", 20, "tablets");
        Product p5 = new Product("Chips", "Snacks", 20, "Other");
        products.add(p1); products.add(p2); products.add(p3); products.add(p4); products.add(p5);

        Random randomizer = new Random();
        Store s1 = new Store("Kaufland", products, loc, randomizer);
        stores.add(s1);
        products.clear();

        p1 = new Product("Bread", "Bread & Cereal", 40, "Other");
        products.add(p1);
        randomizer.setSeed(System.currentTimeMillis()+1000);
        Store s2 = new Store("Penny Market", products, loc, randomizer);
        stores.add(s2);
        products.clear();

        p1 = new Product("Pasta", "Pasta & Rice", 1, "kg");
        products.add(p1);
        randomizer.setSeed(System.currentTimeMillis()+2000);
        Store s3 = new Store("Auchan", products, loc, randomizer);
        stores.add(s3);
        products.clear();

        p1 = new Product("Milk", "Diary", 10, "liters");
        p2 = new Product("Bread", "Bread & Cereal", 20, "Other");
        products.add(p1); products.add(p2);
        randomizer.setSeed(System.currentTimeMillis()+3000);
        Store s4 = new Store("Selgros", products, loc, randomizer);
        stores.add(s4);
        products.clear();
    }
}
