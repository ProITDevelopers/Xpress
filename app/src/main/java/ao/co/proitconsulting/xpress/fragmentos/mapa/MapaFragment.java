package ao.co.proitconsulting.xpress.fragmentos.mapa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import java.util.List;
import java.util.Locale;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.utilityClasses.CustomInfoWindow;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private View view;

    private static final String TAG = "TAG_MapaActivity";
    private SupportMapFragment mapFragment;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;


    //Location

    private GoogleMap mMap;

    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;


    private LocationRequest mLocationRequest;


    private static int UPDATE_INTERVAL = 5000; // 5 secs
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;


    private Marker mUserMarker;

    private String getMyEndereco;



    public MapaFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mapa, container, false);
        if (getContext()!=null)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


        //Maps
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);



        setUpLocation();



        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                setUpLocation();
            } else {
                Toast.makeText(getContext(), getString(R.string.msg_permissao_localizacao), Toast.LENGTH_SHORT).show();
            }
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void setUpLocation() {

        if (getContext()!=null && getActivity()!=null){
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //Request runtime permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_REQUEST_CODE);
            } else {


                buildLocationCallBack();
                createLocationRequest();
                displayLocation();

            }
        }


    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Common.mLastLocation = locationResult.getLocations().get(locationResult.getLocations().size() - 1); //Get last location
                displayLocation();
            }
        };
    }

    private void displayLocation() {

        if (getContext()!=null){
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Common.mLastLocation = location;

                if (Common.mLastLocation != null) {


                    //Create LatLng from mLastLocation and this is center point
                    LatLng center = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
                    // distance in metters
                    // heading 0 is northSide, 90 is east, 180 is south and 270 is west
                    // base on compact :)
                    LatLng northSide = SphericalUtil.computeOffset(center, 100000, 0);
                    LatLng southSide = SphericalUtil.computeOffset(center, 100000, 180);

                    getMyEndereco = getMyAddress(center);

                    LatLngBounds bounds = LatLngBounds.builder()
                            .include(northSide)
                            .include(southSide)
                            .build();



                    final double latitude = Common.mLastLocation.getLatitude();
                    final double longitude = Common.mLastLocation.getLongitude();


                    loadAllAvailableDriver(new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()));


                    Log.d(TAG, String.format("Your location was changed : %f / %f",
                            latitude, longitude));

                } else {
                    Log.d(TAG, "Can not get your location");
                }
            }
        });

    }

    private void loadAllAvailableDriver(final LatLng location) {

        //Add Marker
        //Here we will clear all map to delete old position of driver


        mMap.clear();
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .position(location)
                .title("Eu")
                .snippet(getMyEndereco));


        //Move camera to this position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));


    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        int nightModeFlags =getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {

            Log.d(TAG, "onMapReady: darkmodeOn");
//            try {
//
//                boolean isSucess = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map)
//                );
//
//                if (!isSucess)
//                    Log.d(TAG, "onMapReady_ERROR: Map style load failed !!!");
//            } catch (Resources.NotFoundException ex) {
//                ex.printStackTrace();
//            }
        }else{
            Log.d(TAG, "onMapReady: darkmodeOff");

        }



        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        if (getContext()!=null)
            mMap.setInfoWindowAdapter(new CustomInfoWindow(getContext()));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


            }
        });


        mMap.setOnInfoWindowClickListener(this);

        if (getContext()!=null){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }


        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }

    private String getMyAddress(LatLng location) {
        String address="";
        try {
            if (getContext()!=null){
                Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(location.latitude, location.longitude, 1);
                if (addresses.isEmpty()) {
                    Log.d(TAG, "Waiting for Location");
//                Toast.makeText(mActivity.getApplicationContext(), "Waiting for Location", Toast.LENGTH_SHORT).show();

                }
                else {
                    address = addresses.get(0).getAddressLine(0);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        return address;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //If marker info windows is your location, don't apply this event

        if (marker.getTitle().equals("Eu")){
//                Toast.makeText(this, ""+marker.getTitle(), Toast.LENGTH_SHORT).show();
            if (getMyEndereco == null || getMyEndereco.isEmpty()){
                MetodosUsados.mostrarMensagem(getContext(),"Nenhum endereço encontrado.");
            }

        }


    }
}
