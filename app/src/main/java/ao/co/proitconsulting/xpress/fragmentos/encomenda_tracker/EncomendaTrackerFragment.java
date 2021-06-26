package ao.co.proitconsulting.xpress.fragmentos.encomenda_tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
import ao.co.proitconsulting.xpress.modelos.AdaoTokenAuth;
import ao.co.proitconsulting.xpress.modelos.LoginAdaoRequest;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.utilityClasses.CustomInfoWindow;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
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
    private AdaoTokenAuth adaoTokenAuth;

    private AlertDialog waitingDialog;


//    private Disposable mRestPingDisposable;
//    private CompositeDisposable compositeDisposable;

    public EncomendaTrackerFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_encomenda_tracker, container, false);

        usuarioPerfil = AppPrefsSettings.getInstance().getUser();

        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

        //Maps
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (getContext()!=null){
            Dexter.withContext(getContext())
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {


                                verifConecxao_LOGIN_ADAO();


                            }else{

                                if (getContext()!=null)
                                    Toast.makeText(getContext(), getContext().getString(R.string.msg_permissao_localizacao), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                            if (getContext()!=null)
                                Toast.makeText(getContext(), getContext().getString(R.string.msg_permissao_localizacao), Toast.LENGTH_SHORT).show();
                        }
                    }).check();
        }




        return view;
    }

    private void verifConecxao_LOGIN_ADAO() {
        if (getContext()!=null){
            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo != null){
                    if (verificarNumber())
                        autenticacaoLoginAdao();
                } else {
                    MetodosUsados.mostrarMensagem(getContext(), getString(R.string.msg_erro_internet));
                }
            }
        }
    }

    private boolean verificarNumber(){
        if (usuarioPerfil.contactoMovel == null){
            MetodosUsados.mostrarMensagem(getContext(),"É necessário o seu "+getString(R.string.numero_telefone)+" para continuar.");
            return false;
        }

        if (usuarioPerfil.contactoMovel.equals("") || usuarioPerfil.contactoMovel.equals("?")){
            MetodosUsados.mostrarMensagem(getContext(),"Adicione um "+getString(R.string.numero_telefone)+" ao seu Perfil para continuar.");
            return false;
        }
        if (!usuarioPerfil.contactoMovel.matches("9[1-9][0-9]\\d{6}")){
            MetodosUsados.mostrarMensagem(getContext(),"O seu "+getString(R.string.numero_telefone)+" é inválido. Por favor edite o seu Perfil.");
            return false;
        }

        return true;
    }

    private void autenticacaoLoginAdao() {

        waitingDialog.show();
        LoginAdaoRequest loginAdaoRequest = new LoginAdaoRequest();
        loginAdaoRequest.telefone = usuarioPerfil.contactoMovel;
        loginAdaoRequest.password = usuarioPerfil.contactoMovel;

        String base_url_adao_login = Common.BASE_URL_XPRESS_ADAO_LOGIN;
        base_url_adao_login = base_url_adao_login.trim();
        ApiInterfaceADAO apiInterface = ApiClientADAO.getClientLogin(base_url_adao_login).create(ApiInterfaceADAO.class);
        Call<AdaoTokenAuth> adaoTokenAuthCall = apiInterface.autenticarToGetToken(loginAdaoRequest);
        adaoTokenAuthCall.enqueue(new Callback<AdaoTokenAuth>() {
            @Override
            public void onResponse(@NonNull Call<AdaoTokenAuth> call, @NonNull Response<AdaoTokenAuth> response) {



                if (response.isSuccessful()) {

                    if (response.body()!=null){
                        adaoTokenAuth  = response.body();

                        Log.d(TAG, "AdaoTokenAuth_SUCCESS: "+adaoTokenAuth.token_acesso);
                        setUpLocation();
                    }

                }else{
                    waitingDialog.dismiss();
                    try {
                        Log.d(TAG, "AdaoTokenAuth_NOT_SUCCESS: "+response.errorBody().string()+", ErrorCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<AdaoTokenAuth> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "AdaoTokenAuth_onFailure: "+t.getMessage());


            }
        });
    }

    private void startStompClientRequest(String TOKEN) {
        waitingDialog.dismiss();

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
                .doOnError(throwable -> {
                    Log.d(TAG, "startStompClientdoOnError: "+throwable.getMessage());
                })
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
                            String nomeEstafeta = jsonResponse.getString("nomeEstafeta");

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



                            String getMyEncomendaEnderecoOrigin = getMyAddress(myOriginPosition);
                            String getMyEncomendaEnderecoDestination = getMyAddress(myDestinationPosition);
                            String getMyEncomendaEndereco = getMyAddress(myEncomendaPosition);

                            //Add Marker
                            //Here we will clear all map to delete old position of driver
                            mMap.clear();
//                            mUserMarker = mMap.addMarker(new MarkerOptions()
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
//                                    .position(myPosition)
//                                    .title("Eu")
//                                    .snippet(getMyEndereco));

//                            mOriginMarker = mMap.addMarker(new MarkerOptions()
//                                    .position(myOriginPosition)
//                                    .title(nomeEstabelecimento)
//                                    .snippet(getMyEncomendaEnderecoOrigin));

                            if (getContext()!=null){
                                mDestinationMarker = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                        .position(myDestinationPosition)
                                        .title(getContext().getString(R.string.endereco_de_entrega))
                                        .snippet(getMyEncomendaEnderecoDestination));
                            }


                            mEncomendaMarker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker))
                                    .position(myEncomendaPosition)
                                    .title(nomeEstafeta)
                                    .snippet(new StringBuilder(telefoneEstafeta).append("\n")
                                            .append(estadoEncomenda).append("\n")
                                            .append(getMyEncomendaEndereco).toString()));


                            //Move camera to this position
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myEncomendaPosition, 15.0f));

                            Log.d(TAG, "mStompClient.topic_onNext: "+String.valueOf(jsonResponse));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });


        if (mStompClient != null){
            if (mStompClient.isConnected()){
                mStompClient.disconnect();
            }

            mStompClient.connect(headers);

            if (!mStompClient.isConnected()) return;
            mStompClient.send("/app/tempo/real/encomenda/"+String.valueOf(Common.selectedFactura.idFactura))
                    .subscribeOn(Schedulers.io()).subscribe();
        }






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


        startStompClientRequest(adaoTokenAuth.token_acesso);

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






    @Override
    public void onDestroy() {

        if (mStompClient!=null){
            if (mStompClient.isConnected())
                mStompClient.disconnect();
        }


        super.onDestroy();
    }
}
