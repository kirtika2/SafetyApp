package com.example.vaibhavgupta.feature403;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

//import com.google.android.gms.location.LocationClient;


public class MainActivity extends ActionBarActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        ,SensorListener {


    //widgets declaration
    Button button1, button2, button3;
    ImageButton imageButton;
    TextView t;
    TextView t2;

    //For SMS
    String message;
    String phone_no;

    //Count of the SOS button pressed
    static int cnt = 0;



    //For Location
    GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    Double lat,lng;


    //To detect Shake
    SensorManager sensorManager;
    private static final int SHAKE_THRESHOLD = 1000000000;
    double last_x,last_y,last_z,x,y,z;
    long lastUpdate;


    MainActivity a;
    FetchData fetchData;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e("New Create","------------");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchData = new FetchData(getApplicationContext());

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();







        //To REGISTER THE LISTENER
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
        a = this;

        t = (TextView) findViewById(R.id.debug);
        t2 = (TextView) findViewById(R.id.debug2);


        button1 = (Button) findViewById(R.id.safety);//initialize keep me safe button
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                phone_no = fetchData.getmobno();
                message = "Travelling from DB_SRCLCN to DB_DESTLCN \n\nCHAUFFER:DB_DRIVERNAME\n\nCAB:DB_CABNUMMBER";
                sendsms(message, phone_no);
            }


        });


        button2 = (Button) findViewById(R.id.track);//initialize track a cab button
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Uri myMessage = Uri.parse("content://sms/");
                ContentResolver cr = a.getContentResolver();

                String phoneNumber = "+16509965356";
                String sms = "address='"+ phoneNumber + "'";
                Cursor cursor = cr.query(myMessage, new String[] { "_id", "body" }, sms, null,   null);
                cursor.moveToFirst();
                String  body= cursor.getString( cursor.getColumnIndex("body") );
                Log.e("::::::",body);


            }
        });

        button3 = (Button) findViewById(R.id.lcn);//initialize where am i button
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(a,MapsActivity.class);
                startActivity(intent);

            }
        });

        imageButton = (ImageButton) findViewById(R.id.sos);//initialize SOS image button
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
               SOS();


            }
        });


       // mLocationClient = new LocationClient(this, this, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendsms(String message, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    public void SOS() {
        Log.e("New SOS","------------");
        cnt++;
        Log.e("SOS", "SOS");
        if (cnt == 1) {
            //Send SOS message
            lat=mCurrentLocation.getLatitude();
            lng=mCurrentLocation.getLongitude();
            String addr=getCompleteAddressString(lat,lng);
            phone_no = fetchData.getmobno();
            message="SOS MESSAGE\n\nLOCATION:"+addr;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone_no, null,message, null, null);
            }
        else if (cnt >= 4) {
            //Call Emergency Number
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+61"));
            startActivity(callIntent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnect the client.
        super.onStop();
        mGoogleApiClient.disconnect();

    }


    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        startLocationUpdates();
        }



    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(":::::", "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }




    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location address is", "" + strReturnedAddress.toString());
            }
       else {
                Log.w("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address","Cannot get Address!");
        }
        return strAdd;
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {

        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];
                double speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                 if (speed > SHAKE_THRESHOLD) {
                    SOS();
                  }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        lat = mCurrentLocation.getLatitude();
        lng = mCurrentLocation.getLongitude();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d("::::::", "Location update stopped ..........");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(":::::::", "Location update resumed ..........");
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {
        //Do Nothing
   }

}
