package com.example.ambevworlds;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, DialogCodigo.DialogCodigoListener {

    public GoogleMap mMap;

    public Marker currentLocationMaker;
    public LatLng currentLocationLatLong;
    public DatabaseReference mDatabase;

    public double camLat1;
    public double camLonge1;

    public double camLatBlock = 0;
    public double camLongeBlock = 0;

    public String userId;
    public boolean firstCam = false;

    //Botões
    public Button btnLoja;
    public Button btnMapa;
    public Button btnCodigo;
    public Button btnPerfil;

    //Check se a cam ja moveu
    public boolean CamCheck = false;

    //Firebase
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Botão da Loja
        btnLoja = (Button) findViewById(R.id.btnLoja);
        btnLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ComprarAcitivity.class);
                intent.putExtra("UserId", userId);
                startActivity(intent);
            }
        });

        //Botão do Mapa
        btnMapa = (Button) findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(currentLocationLatLong).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        //Botão Perfil

        btnPerfil = (Button) findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Perfil.class));
            }
        });

        //Botão Código
        btnCodigo = (Button) findViewById(R.id.btnCodigo);
        btnCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCodigoDialog();
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        startGettingLocations();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getMarkers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userId = "24";
    }


    @Override
    public void onLocationChanged(final Location location) {

        if (currentLocationMaker != null) {
            currentLocationMaker.remove();
        }
        //Add marker
        currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocationLatLong);
        markerOptions.title("Localização atual");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMaker = mMap.addMarker(markerOptions);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!firstCam){
                    final Location loc = location;

                    mDatabase.child("Users").child(userId).child("latitude").setValue(loc.getLatitude());
                    mDatabase.child("Users").child(userId).child("longitude").setValue(loc.getLongitude());


                    firstCam = true;
                }
            }
        }, 8000);

        if(!CamCheck){
            final Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(currentLocationLatLong).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
            }, 3000);
            CamCheck = true;
        }

        getMarkers();
    }



    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS desativado!");
        alertDialog.setMessage("Ativar GPS?");
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 1000 * 10;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions

            // check permissions for later versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }


        //Checks if FINE LOCATION and COARSE Location were granted
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


            } else if (isNetwork) {
                // from Network Provider

                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            }
        } else {
            Toast.makeText(this, "Não é possível obter a localização", Toast.LENGTH_SHORT).show();
        }
    }
    private void getMarkers(){

        mDatabase.child("Bares").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)
                            getAllLocations((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void getAllLocations(Map<String,Object> locations) {

        for (Map.Entry<String, Object> entry : locations.entrySet()){


            Map singleLocation = (Map) entry.getValue();

            double lat = (double)singleLocation.get("latitude");
            double longe = (double)singleLocation.get("longitude");
            if(lat <= (camLat1+ 0.02049) && lat>= (camLat1- 0.02049)){
                if(longe <= (camLonge1+ 0.02049) && longe >= (camLonge1-0.02049)){
                    addGreenMarker(entry.getKey(), lat, longe);
                }
            }
        }
    }

    private void addGreenMarker(String newDate, double lati, double longi) {

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(lati, longi);
        markerOptions.position(latLng);
        markerOptions.title(newDate);
        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bebida);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        markerOptions.icon(smallMarkerIcon);
        mMap.addMarker(markerOptions);
    }




    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(19.0f);



        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                camLat1 = mMap.getCameraPosition().target.latitude;
                camLonge1 = mMap.getCameraPosition().target.longitude;
                if(Math.abs(camLat1 - camLatBlock) >= 0.010245 || Math.abs(camLonge1 - camLongeBlock)>= 0.010245){
                    camLatBlock = camLat1;
                    camLongeBlock = camLonge1;
                    getMarkers();

                }
            }
        });
    }

    public void openCodigoDialog(){
        DialogCodigo dialogCodigo = new DialogCodigo();
        dialogCodigo.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void applyCodigo(final String codigo) {

        final String code = codigo;
        mDatabase.child("Codigos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(code)){
                    //ADD PONTOS
                    final String pontos = dataSnapshot.child(code).getValue().toString();

                    mDatabase.child("Clientes").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String tampinhas = dataSnapshot.child("pontos").getValue().toString();

                            int pontosInt = Integer.parseInt(pontos);
                            int tampinhasInt = Integer.parseInt(tampinhas);

                            tampinhasInt += pontosInt;

                            mDatabase.child("Clientes").child(user.getUid()).child("pontos").setValue(tampinhasInt).toString();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MapsActivity.this, "ERRO DATABASE!", Toast.LENGTH_LONG).show();
                        }
                    });

                    dataSnapshot.child(code).getRef().removeValue();
                    Toast.makeText(MapsActivity.this, "Pontos adicionados!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MapsActivity.this, "Codigo invalido!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
