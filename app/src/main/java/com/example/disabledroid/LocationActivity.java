package com.example.disabledroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.disabledroid.SQLiteDatabase.DatabaseLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private TextView lat, lon, addr, pin, loc, admin, country;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseLocation databaseLocation;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        addr = findViewById(R.id.address);
        pin = findViewById(R.id.pin);
        loc = findViewById(R.id.location);
        admin = findViewById(R.id.admin);
        country = findViewById(R.id.country);

        databaseLocation = new DatabaseLocation(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Language is supported", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void getLocation(View view) {
        if ((ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lon.setText(String.valueOf(location.getLongitude()));
                        lat.setText(String.valueOf(location.getLatitude()));

                        try {
                            Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            addr.setText(addresses.get(0).getAddressLine(0));
                            pin.setText(addresses.get(0).getPostalCode());
                            loc.setText(addresses.get(0).getLocality());
                            admin.setText(addresses.get(0).getAdminArea());
                            country.setText(addresses.get(0).getCountryName());

                            textToSpeech.speak(addr.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                            boolean checkInsertion = databaseLocation.insertLocation(addresses.get(0).getAddressLine(0));
                            if (checkInsertion)
                                Toast.makeText(LocationActivity.this, "Location is saved", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LocationActivity.this, "Location is not saved", Toast.LENGTH_SHORT).show();

                            new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), SavedLocationActivity.class)), 2000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                lon.setText(String.valueOf(location1.getLongitude()));
                                lat.setText(String.valueOf(location1.getLatitude()));

                                try {
                                    Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1);
                                    addr.setText(addresses.get(0).getAddressLine(0));
                                    pin.setText(addresses.get(0).getPostalCode());
                                    loc.setText(addresses.get(0).getLocality());
                                    admin.setText(addresses.get(0).getAdminArea());
                                    country.setText(addresses.get(0).getCountryName());

                                    textToSpeech.speak(addr.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                                    boolean checkInsertion = databaseLocation.insertLocation(addresses.get(0).getAddressLine(0));
                                    if (checkInsertion)
                                        Toast.makeText(LocationActivity.this, "Location is saved", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(LocationActivity.this, "Location is not saved", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), SavedLocationActivity.class)), 2000);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(LocationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void showMap(View view) {
        startActivity(new Intent(getApplicationContext(), MapActivity.class));
    }

    public void showLocations(View view) {
        startActivity(new Intent(getApplicationContext(), SavedLocationActivity.class));
    }
}