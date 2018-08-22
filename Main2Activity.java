package com.example.nimeneze.lifeline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;



import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.os.Handler;
import android.widget.TextView;


public class Main2Activity extends AppCompatActivity {
    String IP;
    TextView lat, lon, hrt,tp;
    String[] cnt = new String[6];
    Context c = this;
    Handler mHandler;
    String title = "Heart Rate";
    String text = "Critical";
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle bundle = getIntent().getExtras();
        IP = bundle.getString("IP").toString();

        lat = (TextView) findViewById(R.id.lat);
        lon = (TextView) findViewById(R.id.lon);
        hrt = (TextView) findViewById(R.id.hr);
        tp =  (TextView) findViewById(R.id.temp);

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
            Main2Activity.this.mHandler.postDelayed(m_Runnable, 1000);
        }

    };






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
            lat.setText(cnt[0]);
            lon.setText(cnt[1]);
            hrt.setText(cnt[2]);
            tp.setText(cnt[4]);

            if (cnt[3].equals("1")) {
                if (count == 1) {
                   addNotification();
                    count++;
                }
            } else {
                count = 1;
            }

        }


    }

    private void addNotification() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon)
                        .setContentTitle(title)
                        .setContentText(text);
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
                (getApplicationContext()).setContentTitle(title).setContentText(text).
                setContentTitle(title).setSmallIcon(R.mipmap.icon).build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(0, note);




    }





    public void sendMap(View v){
        Intent i=new Intent(Main2Activity.this,MapsActivity.class);
        i.putExtra("IP",IP);
        startActivity(i);
    }
}
