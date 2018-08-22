package com.example.nimeneze.lifeline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context c = this;
    Handler mHandler;
    String IP,lt,ln;
    String[] cnt = new String[6];
    String title = "KA 20 G 2018";
    String text = "The person driving this vehicle is undergoing minor/major attack";
    String title1 = "KA 20 G 2018";
    String text1 = "The person driving this vehicle is alcoholic";
    int count = 1;
    int count1 =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        IP = bundle.getString("IP").toString();

        ArrayList items = new ArrayList();
        MenuClass MenuObject = new MenuClass(items);
        LocationDetails LD = new LocationDetails();
        LD.execute(MenuObject);
        this.mHandler = new Handler();
        this.mHandler.post(m_Runnable);
    }

    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {

            ArrayList items = new ArrayList();
            MenuClass MenuObject = new MenuClass(items);
            LocationDetails LD = new LocationDetails();
            LD.execute(MenuObject);
            MapsActivity.this.mHandler.postDelayed(m_Runnable, 3000);
        }

    };


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    public class LocationDetails extends android.os.AsyncTask<MenuClass, MenuClass, MenuClass> {


        @Override
        protected MenuClass doInBackground(MenuClass... params) {
            MenuClass MenuObject = params[0];
            ArrayList items = MenuObject.getItems();

            try {
                String urlString = "http://"+IP+"/getMarketPlace.php";
                URL url = new URL(urlString);


                HttpURLConnection httpurlConnection = (HttpURLConnection) url.openConnection();
                httpurlConnection.setDoOutput(true);
                InputStream inputStream = httpurlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                JsonReader jsonReader = new JsonReader(inputStreamReader);
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    /**
                     * Get the nextString of the JSON array and add it to the arrayList - 'items'.
                     */
                    String name = jsonReader.nextString();
                    items.add(name);
                }
                jsonReader.endArray();

                return params[0];
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(MenuClass MenuObject) {
            ArrayList arrayList = MenuObject.getItems();
            for (int temp = 0; temp <= arrayList.size() - 1; temp++) {
                cnt[temp] = arrayList.get(temp).toString();
            }
            lt=cnt[0];
            ln=cnt[1];

            if(cnt[0]!=lt || cnt[1]!=ln){
                mMap.clear();
            }
            LatLng currentLocation = new LatLng(Double.parseDouble(cnt[0]),Double.parseDouble(cnt[1]));
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Im Here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            CameraUpdate center = CameraUpdateFactory.newLatLng(currentLocation);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            //mMap.moveCamera(center);
            //mMap.animateCamera(zoom);
            mMap.animateCamera(zoom);
            mMap.setBuildingsEnabled(true);



            if (cnt[3].equals("1")) {
                if (count == 1) {
                    addNotification(title, text);
                    count++;
                }
            }
            else if(cnt[3].equals("2")){
                if(count1 == 1) {
                    addNotification(title1, text1);
                    count1++;
                }
            }
            else {
                count = 1;
                count1 =1;
            }

        }


    }






    private void addNotification(String tit,String txt) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon)
                        .setContentTitle(tit)
                        .setContentText(txt);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(c, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setOngoing(true);




        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification note = new Notification.Builder
                (getApplicationContext()).setContentTitle(tit).setContentText(txt).
                setContentTitle(tit).setSmallIcon(R.mipmap.icon).build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(0, note);




    }



}
