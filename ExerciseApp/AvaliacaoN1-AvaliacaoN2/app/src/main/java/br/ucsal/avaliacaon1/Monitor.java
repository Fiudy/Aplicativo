package br.ucsal.avaliacaon1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class Monitor extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, SensorEventListener {
    private static final int REQUEST_LAST_LOCATION = 1;
    private static final int REQUEST_LOCATION_UPDATES = 2;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;
    private TextView speedView, timerView, distanceView, calorieView;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler();
    private long startTime = 0;
    private static boolean monitoring = true;
    private double totalDistance = 0;
    private long lastUpdateTime = 0;

    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private float currentBearing = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);

        speedView = findViewById(R.id.speedValue);
        timerView = findViewById(R.id.timerValue);
        distanceView = findViewById(R.id.distanceValue);
        calorieView = findViewById(R.id.caloriesValue);

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button getLastLocationButton = findViewById(R.id.getLocation);
        getLastLocationButton.setOnClickListener(this);

        Button startLocationUpdatesButton = findViewById(R.id.startActivity);
        startLocationUpdatesButton.setOnClickListener(this);

        Button stopLocationUpdatesButton = findViewById(R.id.endActivity);
        stopLocationUpdatesButton.setOnClickListener(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Atualizar a interface do usuário com a localização
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Localização Atual"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

                    // Update previous location
//                    previousLocation = location;
                    lastUpdateTime = System.currentTimeMillis();

                    // Update the UI with the current location
                    updateLocationUI(location);

                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Recuperar as configurações salvas
        int mapType = sharedPreferences.getInt("MapType", 0);

        // Configurar o mapa de acordo com as preferências salvas
        if (mapType == 2131231107){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
//        mMap.getUiSettings().setRotateGesturesEnabled(orientation != 0);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.getLocation) {
            getLastLocation();
        }
        if (view.getId() == R.id.startActivity) {
            startLocationUpdates();
            startTime = System.currentTimeMillis();
            monitoring = true;
            handler.postDelayed(runnable, 1000);
        }
        if (view.getId() == R.id.endActivity) {
            monitoring = false;
            stopLocationUpdates();
        }
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permissões de localização
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LAST_LOCATION);
            return;
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    // Processar a última localização conhecida
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Localização Atual"));

                    int orientation = sharedPreferences.getInt("Orientation", 0);
                    if (orientation == 2131231064) {
                        // orientação  apontando ao norte
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(20)
                                        .tilt(0)
                                        .bearing(0)
                                        .build()));
                    } else if (orientation == 2131230861) {
                        // orientação  apontando ao curso

                        // Calculate course angle from location
                        float courseAngle = location.getBearing();
                        // Adjust course angle to match device orientation
                        float adjustedCourseAngle = courseAngle + currentBearing;
                        // Move the map camera to course up orientation
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(20)
                                        .tilt(0)
                                        .bearing(adjustedCourseAngle)
                                        .build()));
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20));
                    }
                }

            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permissões de localização
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_UPDATES);
        } else {
            getLastLocation();
            // Iniciar as atualizações de localização
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void updateOverlayInfo() {
        // Atualizar as informações de tempo e calorias
        timerView.setText(getElapsedTime());
        calorieView.setText(getCalorieBurn());
    }

    private void updateLocationUI(Location location) {
        // Atualizar as informações de velocidade e distância
        speedView.setText(getCurrentSpeed(location));
        distanceView.setText(getTotalDistance(location));
    }

    private String getCalorieBurn() {
        String calories = "";
        int exercise = sharedPreferences.getInt("Exercise", 0);
        double weight = sharedPreferences.getFloat("Weight", 0);
        // Cálculo de gasto calórico é feito por hora, editar para por segundo ou minuto para visualizar melhor
        double elapsedTime = (System.currentTimeMillis() - startTime)/3600000;
        double MET = 0;

        switch (exercise) {
            case 2131231256:
                // caminhada
                MET = 3.8;
                break;
            case 2131231106:
                // corrida
                MET = 12.5;
                break;
            case 2131230867:
                // ciclismo
                MET = 8.0;
                break;
        }

        double DE = MET*weight*elapsedTime;
        return calories + DE +" Kcal";
    }

    private String getElapsedTime() {
        long elapsedTime = System.currentTimeMillis() - startTime;

        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String formattedTime = String.format("%02d", hours) + "h " + String.format("%02d", minutes) + "min " + String.format("%02d", seconds % 60) + "s";
        return formattedTime;
    }

    private String getCurrentSpeed(Location location) {
        String speedUnity = "";
        double speed = 0;
        int unity = sharedPreferences.getInt("Unity", 0);
        // aqui vai um cálculo bem bonito da distância percorrida entre previousLocation e currentLocation
        double distance = 0;

        switch (unity) {
            case 2131231013:
                // Calcula a velocidade em metros por segundo
                speed = distance / ((System.currentTimeMillis() - lastUpdateTime) / 1000);
                speedUnity = " m/s";
                break;
            case 2131230971:
                // Converte velocidade em kilometros por hora
                speed = (distance / ((System.currentTimeMillis() - lastUpdateTime) / 1000)) * 3.6;
                speedUnity = " km/h";
                break;
        }

        return speed + speedUnity;
    }

    private String getTotalDistance(Location location) {
        // Calculate distance between current and previous locations
        // double distance = location.distanceBetween(previousLocation);

        // aqui vai um cálculo bem bonito da distância percorrida
        double distance = 0;
        totalDistance += distance;

        return totalDistance + " m";
    }



    private Runnable runnable = new Runnable() {
        // Atualiza as informações da tela de tempo em tempo
        @Override
        public void run() {
            if (!monitoring){
                return;
            }
            updateOverlayInfo();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        float bearing = event.values[0];

        // Update current bearing
        currentBearing = bearing;

        // Update map orientation to course up
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}