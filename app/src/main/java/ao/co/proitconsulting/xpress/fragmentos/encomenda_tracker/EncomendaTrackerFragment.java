package ao.co.proitconsulting.xpress.fragmentos.encomenda_tracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ADAO.ApiClientADAO;
import ao.co.proitconsulting.xpress.api.ADAO.ApiInterfaceADAO;
import ao.co.proitconsulting.xpress.api.ADAO.OkHttpUtils;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.LoginAdaoRequest;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.utilityClasses.CustomInfoWindow;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;


public class EncomendaTrackerFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{
    private static String TAG = "TAG_EncomendaTrackerFragment";
    private View view;

    private UsuarioPerfil usuarioPerfil;

    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private GoogleMap mMap;
    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 5000; // 5 secs
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private Marker mUserMarker,mDestinationMarker,mEncomendaMarker,mOriginMarker;
    private String getMyEndereco;

    private static final String SERVER_URL_HTTP ="https://apilocalizacao.lengueno.com/api-entrega";
    private static final String SERVER_URL_WEBSOCKET ="wss://apilocalizacao.lengueno.com/api-entrega";

    Uri uri = Uri.parse(SERVER_URL_WEBSOCKET);


    private StompClient mStompClient;



//    private Disposable mRestPingDisposable;
//    private CompositeDisposable compositeDisposable;

    public EncomendaTrackerFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_encomenda_tracker, container, false);

        usuarioPerfil = AppPrefsSettings.getInstance().getUser();

        if (getContext()!=null)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


        //Maps
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        setUpLocation();


        return view;
    }

    private void verifConecxao_LOGIN_ADAO() {
        if (getContext()!=null){
            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                } else {

                    autenticacaoLoginAdao();
                }
            }
        }
    }

    private void autenticacaoLoginAdao() {
        if (usuarioPerfil.contactoMovel == null){
            MetodosUsados.mostrarMensagem(getContext(),"É necessário o seu "+getString(R.string.numero_telefone)+" para continuar.");
            return;
        }

        if (usuarioPerfil.contactoMovel.equals("") || usuarioPerfil.contactoMovel.equals("?")){
            MetodosUsados.mostrarMensagem(getContext(),"Adicione um "+getString(R.string.numero_telefone)+" ao seu Perfil para continuar.");
            return;
        }
        if (!usuarioPerfil.contactoMovel.matches("9[1-9][0-9]\\d{6}")){
            MetodosUsados.mostrarMensagem(getContext(),"O seu "+getString(R.string.numero_telefone)+" é inválido. Por favor edite o seu Perfil.");
            return;
        }

        LoginAdaoRequest loginAdaoRequest = new LoginAdaoRequest();
        loginAdaoRequest.telefone = usuarioPerfil.contactoMovel;
        loginAdaoRequest.password = usuarioPerfil.contactoMovel;

        ApiInterfaceADAO apiInterface = ApiClientADAO.getClient(Common.BASE_URL_XPRESS_ADAO_LOGIN).create(ApiInterfaceADAO.class);
        Call<ResponseBody> getToken = apiInterface.autenticarToGetToken(loginAdaoRequest);
        getToken.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {



                if (response.isSuccessful()) {

                    if (response.body()!=null){


                        try {
                            String bodyResponse = response.body().string();
                            JSONObject jsonResponse = new JSONObject(bodyResponse);
                            String token_acesso = jsonResponse.getString("token_acesso");
                            startStompClientRequest(token_acesso);


                            Log.d(TAG, "mStompClient.topic_onNext: "+String.valueOf(jsonResponse));



                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {


                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {



            }
        });
    }

    private void startStompClientRequest(String TOKEN) {

        List<StompHeader> headers = new ArrayList<>();
        headers.add((new StompHeader("X-Authorization",TOKEN)));

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, uri.toString(),null, OkHttpUtils.getUnsafeOkHttpClient());
        mStompClient.withClientHeartbeat(10000);

        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:

                            Log.d(TAG, "lifecycle(): "+"Stomp connection opened");
                            mStompClient.send("/app/tempo/real/encomenda/"+String.valueOf(Common.selectedFactura.idFactura))
                                    .subscribeOn(Schedulers.io()).subscribe();
                            break;
                        case CLOSED:
                            Log.d(TAG, "lifecycle(): "+"Stomp connection closed");
                            break;
                        case ERROR:
                            Log.d(TAG, "lifecycle(): "+"Stomp connection error: "+ lifecycleEvent.getException());
                            break;
                    }
                });




        // Receive greetings
        mStompClient.topic("/user/topic/encomenda")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StompMessage>() {
                    @Override
                    public void accept(StompMessage stompMessage) throws Exception {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(stompMessage.getPayload());

                            String nomeEstabelecimento = jsonResponse.getString("nomeEstabelecimento");
                            double latitudeAtual = Double.parseDouble(jsonResponse.getString("latitudeAtual"));
                            double longitudeAtual = Double.parseDouble(jsonResponse.getString("longitudeAtual"));
                            String estadoEncomenda = jsonResponse.getString("estadoEncomenda");
                            double latitudeOrigem = Double.parseDouble(jsonResponse.getString("latitudeOrigem"));
                            double longitudeOrigem = Double.parseDouble(jsonResponse.getString("longitudeOrigem"));
                            String telefoneEstafeta = jsonResponse.getString("telefoneEstafeta");
                            double latitudeDestino = Double.parseDouble(jsonResponse.getString("latitudeDestino"));
                            double longitudeDestino = Double.parseDouble(jsonResponse.getString("longitudeDestino"));

//                            {"numeroFatura":"904",
//                                    "nomeCliente":"Adão Gaspar",
//                                    "nomeEstabelecimento":"Denia Star",
//                                    "telefoneEstafeta":"940415704",
//                                    "latitudeOrigem":-8.826892810745855,
//                                    "longitudeOrigem":13.22916747076499,
//                                    "longitudeDestino":13.192941,
//                                    "latitudeDestino":-8.915051,
//                                    "latitudeAtual":-8.827631380371402,
//                                    "longitudeAtual":13.228898046293544,
//                                    "estadoEncomenda":"ANDAMENTO"}
                            LatLng myPosition = new LatLng(0,0);
                            LatLng myOriginPosition = new LatLng(latitudeOrigem,longitudeOrigem);
                            LatLng myDestinationPosition = new LatLng(latitudeDestino,longitudeDestino);
                            LatLng myEncomendaPosition = new LatLng(latitudeAtual,longitudeAtual);
                            if (Common.mLastLocation != null) {
                                myPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());

                            }


                            String getMyEncomendaEnderecoOrigin = getMyAddress(myOriginPosition);
                            String getMyEncomendaEnderecoDestination = getMyAddress(myDestinationPosition);
                            String getMyEncomendaEndereco = getMyAddress(myEncomendaPosition);

                            //Add Marker
                            //Here we will clear all map to delete old position of driver
                            mMap.clear();
                            mUserMarker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                    .position(myPosition)
                                    .title("Eu")
                                    .snippet(getMyEndereco));

//                            mOriginMarker = mMap.addMarker(new MarkerOptions()
//                                    .position(myOriginPosition)
//                                    .title(nomeEstabelecimento)
//                                    .snippet(getMyEncomendaEnderecoOrigin));

                            mDestinationMarker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker))
                                    .position(myDestinationPosition)
                                    .title(getContext().getString(R.string.endereco_de_entrega))
                                    .snippet(getMyEncomendaEnderecoDestination));

                            mEncomendaMarker = mMap.addMarker(new MarkerOptions()
                                    .position(myEncomendaPosition)
                                    .title(nomeEstabelecimento)
                                    .snippet(new StringBuilder("EM ").append(estadoEncomenda).append("\n").append(getMyEncomendaEndereco).toString()));


                            //Move camera to this position
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myEncomendaPosition, 15.0f));

                            Log.d(TAG, "mStompClient.topic_onNext: "+String.valueOf(jsonResponse));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


        if (mStompClient.isConnected()){
            mStompClient.disconnect();
        }


        mStompClient.connect(headers);

        if (!mStompClient.isConnected()) return;
        mStompClient.send("/app/tempo/real/encomenda/"+String.valueOf(Common.selectedFactura.idFactura))
                .subscribeOn(Schedulers.io()).subscribe();


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


//                    loadAllAvailableDriver(new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()));
                    verifConecxao_LOGIN_ADAO();

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


        try {
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMyAddress(LatLng location) {
        String address="";
        try {
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






    @Override
    public void onDestroy() {
        if (mStompClient.isConnected())
            mStompClient.disconnect();

        super.onDestroy();
    }
}
