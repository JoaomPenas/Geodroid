package com.example.ps.geodroidapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ps.geodroidapp.R;
import com.example.ps.geodroidapp.Utils.Utils;


public class Compass extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private String usermail;
    private String session;

    ImageView iv_arrow;
    TextView tv_session;
    TextView tv_degrees;
    TextView tv_direction;
    TextView tv_pitch;

    Button button;
    TextView textView;

    LocationManager locationManager;
    LocationListener locationListener;

    private float currentDegree = 0f;
    private int azimuth;
    private int roll;
    private int pitch;
    double latitude;
    double longitude;

    private Intent intentForParamsExtraActivity;

    float[] mGravity;
    float[] mGeomagnetic;
    public int handRigthAzimuth,dip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        iv_arrow = (ImageView) findViewById(R.id.ic_arrow);
        tv_session = (TextView)findViewById(R.id.compass_tv_session);
        tv_degrees = (TextView) findViewById(R.id.compass_tc_degrees);
        tv_direction = (TextView) findViewById(R.id.compass_tc_direction);
        tv_pitch = (TextView) findViewById(R.id.compass_pitch);

        button = (Button) findViewById(R.id.compass_aquire_button);
        textView = (TextView) findViewById(R.id.Coordenadas);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        intentForParamsExtraActivity = new Intent(this, com.example.ps.geodroidapp.Activities.ExtraData.class);
        Intent aux = getIntent();

        Bundle extras = aux.getExtras();
        if (extras!=null){
            session = extras.getString("Session");
            usermail = extras.getString("usermail");
            tv_session.setText("User:"+usermail+"\n"+this.getString(R.string.compass_session) + session);
        }
        intentForParamsExtraActivity.putExtra("Session", session);
        intentForParamsExtraActivity.putExtra("usermail",usermail);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                textView.setText("Lat: " + String.format("%.3f", latitude) + (char) 0x00B0 + " Long:" + String.format("%.3f", longitude) + (char) 0x00B0);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //chamado se o GPS está desligado
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },1);
            }
        } else {
            locationManager.requestLocationUpdates("gps", 0, 1, locationListener);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Compass.this, "Azim:" + azimuth + " roll:" + roll + "\nLat:" + latitude + " Long:" + longitude, Toast.LENGTH_SHORT).show();
                intentForParamsExtraActivity.putExtra("Azimute", ""+ handRigthAzimuth);
                intentForParamsExtraActivity.putExtra("Dip", ""+ dip);
                intentForParamsExtraActivity.putExtra("Latitude", ""+ latitude);
                intentForParamsExtraActivity.putExtra("Longitude", ""+ longitude);

                if(latitude == 0 || longitude == 0 ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Compass.this);
                    builder.setMessage(R.string.compass_msg_loction_not_available)
                            .setPositiveButton(R.string.compass_positive_btn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intentForParamsExtraActivity);
                                }
                            })
                            .setNegativeButton(R.string.compass_negative_btn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    startActivity(intentForParamsExtraActivity);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // todo
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates("gps", 0, 1, locationListener);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAccelerometer != null && mMagnetometer!= null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }else{
            Toast.makeText(this, Compass.this.getString(R.string.compass_compass_not_suported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            if (SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic)) {
                float orientation[] = new float[3];                             // orientation contains: azimut, pitch and roll
                SensorManager.getOrientation(R, orientation);

                azimuth =(int) (Math.round(Math.toDegrees(orientation[0])));    // azimute entre -180 e 180
                if(azimuth < 0){azimuth += 360;}                                // azimute entre 0 e 359
                pitch = (int)Math.toDegrees(orientation[1]);                    // pitch entre -90 e 90
                roll = (int)Math.toDegrees(orientation[2]);                     // roll entre -180 e 180

                handRigthAzimuth = azimuth;
                dip = roll;

                //Transform/calculate to handRightRule
                if( roll > -90 && roll < 90) {                                  // gama de valores para dispositivo virado para cima
                    if (roll < 0) {
                        dip = -roll;
                        //1º e 2º Quadrante
                        if (azimuth < 180) handRigthAzimuth = azimuth + 180;
                        //3º e 4º Quadrante
                        if (azimuth >= 180) handRigthAzimuth = azimuth - 180;
                    }
                    tv_degrees.setText("(" + Integer.toString(handRigthAzimuth) + (char) 0x00B0  +  ", " + Integer.toString(dip) + (char) 0x00B0+")");
                    tv_direction.setText(Utils.getNormaliedAtitudeFromRawAtitude(handRigthAzimuth, dip));
                    tv_pitch.setText("P=" + Integer.toString(pitch) +(char)0x00B0);
                }
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -azimuth,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);

                ra.setDuration(70);
                ra.setInterpolator(new LinearInterpolator());
                ra.setFillAfter(true);
                iv_arrow.startAnimation(ra);
                currentDegree = -azimuth;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        Log.d("CompassAct","callDestroy");
    }
}