package ao.co.proitconsulting.xpress.fragmentos.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.annotations.SerializedName;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.ViewPagerAdapterSlider;
import ao.co.proitconsulting.xpress.adapters.homeEstab.MainRecyclerAdapter;
import ao.co.proitconsulting.xpress.adapters.topSlide.TopImageSlideAdapter;
import ao.co.proitconsulting.xpress.api.ADAO.ApiClientADAO;
import ao.co.proitconsulting.xpress.api.ADAO.ApiInterfaceADAO;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.MenuCategory;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "TAG_HomeFragment";

    private View view;

    private LoopingViewPager loopingViewPager;
    private ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private Handler slideHandler = new Handler();
    private static int TIME_DELAY = 3500; // Slide duration 3 seconds

    private TabLayout tabLayout;
    private RecyclerView recyclerViewMenu;

    private AlertDialog waitingDialog;
    private List<TopSlideImages> topSlideImagesList = new ArrayList<>();

//    private List<MenuCategory> menuCategoryList = new ArrayList<>();
    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();
    private List<CategoriaEstabelecimento> categoriaEstabelecimentoList = new ArrayList<>();

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private ImageView imgErro;
    private TextView txtMsgErro;
    private TextView btnTentarDeNovo;

    private String errorMessage;

    private MainRecyclerAdapter mainRecyclerAdapter;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 5000; // 5 secs
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    private String getMyEndereco ="";
    private TextView txtTopMyLocation;

    public HomeFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//

        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();

        if (getContext()!=null){
            Dexter.withContext(getContext())
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                buildLocationCallBack();
                                createLocationRequest();
                                displayLocationOnTop();
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

    //    private void carregarListaMenuCategory() {
//        waitingDialog.show();
//
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<List<MenuCategory>> rv = apiInterface.getMenuCategories();
//        rv.enqueue(new Callback<List<MenuCategory>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<MenuCategory>> call, @NonNull Response<List<MenuCategory>> response) {
//
//                if (response.isSuccessful()) {
//
//                    if (menuCategoryList!=null)
//                        menuCategoryList.clear();
//
//                    if (response.body()!=null){
//
//                        if (response.body().size()>0){
//                            for (MenuCategory menuCategory: response.body()) {
//                                if (menuCategory!=null){
//                                    menuCategoryList.add(menuCategory);
//                                    Log.d(TAG, "onResponseMenuCategory: "+menuCategory.getDescricao());
//                                }
//                            }
//
//                            carregarListaEstabelicimentos();
//
//
//                        }
//
//                    }
//
//                } else {
//
//                    waitingDialog.dismiss();
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<MenuCategory>> call, @NonNull Throwable t) {
//                waitingDialog.dismiss();
//                if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
//                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet);
//                }else  if ("timeout".equals(t.getMessage())) {
//                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet_timeout);
//                }else {
//                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro);
//                }
//            }
//        });
//    }

    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

        loopingViewPager = view.findViewById(R.id.loopingViewPager);

        txtTopMyLocation = view.findViewById(R.id.txtTopMyLocation);
        viewPager = view.findViewById(R.id.viewPager);
        sliderDotspanel = view.findViewById(R.id.SliderDots);

        viewPager.setVisibility(View.INVISIBLE);
        sliderDotspanel.setVisibility(View.INVISIBLE);

        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);

        tabLayout = view.findViewById(R.id.tab_layout);

        loopingViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        coordinatorLayout = view.findViewById(R.id.constraintLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        imgErro = view.findViewById(R.id.imgErro);
        txtMsgErro = view.findViewById(R.id.txtMsgErro);
        btnTentarDeNovo = view.findViewById(R.id.btn);
        btnTentarDeNovo.setVisibility(View.INVISIBLE);


//        CounterFab floatingActionButton = ((MenuActivity) getActivity()).getFloatingActionButton();
//        if (floatingActionButton != null) {
//            floatingActionButton.show();
//        }

        if (getContext()!=null)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }



    //--------------LISTAR_verifConecxaoTOPSLIDEIMAGE-----------------------------///
    //-----------------------------------------------------------------------///
    private void verifConecxaoTOPSLIDEIMAGE() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo != null){
                   loadTopImagesList();
                }
            }
        }

    }

    private void loadTopImagesList() {

        ApiInterfaceADAO apiInterface = ApiClientADAO.getClient(Common.BASE_URL_XPRESS_ADAO_TAXA).create(ApiInterfaceADAO.class);
        Call<List<TopSlideImages>> getTopImageList = apiInterface.getTopSlideImagesList();
        getTopImageList.enqueue(new Callback<List<TopSlideImages>>() {
            @Override
            public void onResponse(@NonNull Call<List<TopSlideImages>> call, @NonNull Response<List<TopSlideImages>> response) {

                if (topSlideImagesList!=null)
                    topSlideImagesList.clear();
                else
                    topSlideImagesList = new ArrayList<>();

                if (response.isSuccessful()) {

                    if (response.body()!=null && response.body().size()>0){


                        for (TopSlideImages topSlideImages :response.body()) {
                            topSlideImagesList.add(topSlideImages);
                        }


                        sliderDotspanel.removeAllViews();
                        TopImageSlideAdapter topImageSlideAdapter = new TopImageSlideAdapter(getContext(),topSlideImagesList,true);
                        loopingViewPager.setAdapter(topImageSlideAdapter);
                        tabLayout.setupWithViewPager(loopingViewPager,true);

                        ViewPagerAdapterSlider viewPagerAdapter = new ViewPagerAdapterSlider(getContext(),topSlideImagesList);

                        viewPager.setAdapter(viewPagerAdapter);

                        dotscount = viewPagerAdapter.getCount();
                        dots = new ImageView[dotscount];

                        for(int i = 0; i < dotscount; i++){

                            dots[i] = new ImageView(getContext());
                            if (getContext()!=null)
                                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_non_active_dot));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            params.setMargins(8, 0, 8, 12);

                            sliderDotspanel.addView(dots[i], params);

                        }

                        if (getContext()!=null)
                            dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_active_dot));


                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {

                                slideHandler.removeCallbacks(sliderRunnable);
                                slideHandler.postDelayed(sliderRunnable,TIME_DELAY); // Slide duration 3 seconds

                                if (getContext()!=null){
                                    for(int i = 0; i< dotscount; i++){
                                        dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_non_active_dot));
                                    }

                                    dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_active_dot));

                                    if (position+1 == topSlideImagesList.size()){

                                        slideHandler.postDelayed(runnable,TIME_DELAY); // Slide duration 3 seconds

                                    }
                                }



                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TopSlideImages>> call, @NonNull Throwable t) {

            }
        });

    }


    //--------------LISTAR_ESTABELECIMENTOS_TODOS-----------------------------///
    //-----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_TODOS() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_TODOS();
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_TODOS() {

        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_TODOS();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        if (getMyEndereco.equals(""))
                            txtTopMyLocation.setVisibility(View.GONE);
                        else
                            txtTopMyLocation.setVisibility(View.VISIBLE);

//                        loopingViewPager.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        sliderDotspanel.setVisibility(View.VISIBLE);
//                        tabLayout.setVisibility(View.VISIBLE);
                        getCategoriesFromEstabelecimento();


                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList = null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();

                        Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }

                    Common.todosEstabelecimentoList = null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }

            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_TODOS-----------------------------///
    //----------------------------------------------------------------------///


    //-------------LISTAR_ESTABELECIMENTOS_PERTO_DE_MIM-----------------------------///
    //----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_PERTO_DE_MIM(double latitude,double longitude) {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_PERTO_DE_MIM(latitude,longitude);
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_PERTO_DE_MIM(double latitude,double longitude) {
        waitingDialog.show();


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_PERTO_DE_MIM(latitude,longitude);
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        if (getMyEndereco.equals(""))
                            txtTopMyLocation.setVisibility(View.GONE);
                        else
                            txtTopMyLocation.setVisibility(View.VISIBLE);
//                        loopingViewPager.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        sliderDotspanel.setVisibility(View.VISIBLE);
//                        tabLayout.setVisibility(View.VISIBLE);
                        getCategoriesFromEstabelecimento();



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList = null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }

                    Common.todosEstabelecimentoList = null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_PERTO_DE_MIM-----------------------------///
    //----------------------------------------------------------------------///


    //-------------LISTAR_ESTABELECIMENTOS_MAIS_POPULARES-----------------------------///
    //----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_MAIS_POPULARES() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_MAIS_POPULARES();
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_MAIS_POPULARES() {
        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_MAISPOPULARES();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        if (getMyEndereco.equals(""))
                            txtTopMyLocation.setVisibility(View.GONE);
                        else
                            txtTopMyLocation.setVisibility(View.VISIBLE);
//                        loopingViewPager.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        sliderDotspanel.setVisibility(View.VISIBLE);
//                        tabLayout.setVisibility(View.VISIBLE);
                        getCategoriesFromEstabelecimento();



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList=null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }

                    Common.todosEstabelecimentoList=null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_MAIS_POPULARES-----------------------------///
    //----------------------------------------------------------------------///


    //-------------LISTAR_ESTABELECIMENTOS_ALTAS_HORAS-----------------------------///
    //----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_ALTASHORAS() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_ALTASHORAS();
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_ALTASHORAS() {
        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_ALTASHORAS();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        if (getMyEndereco.equals(""))
                            txtTopMyLocation.setVisibility(View.GONE);
                        else
                            txtTopMyLocation.setVisibility(View.VISIBLE);
//                        loopingViewPager.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        sliderDotspanel.setVisibility(View.VISIBLE);
//                        tabLayout.setVisibility(View.VISIBLE);
                        getCategoriesFromEstabelecimento();



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList=null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                    Common.todosEstabelecimentoList=null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_ALTAS_HORAS-----------------------------///
    //----------------------------------------------------------------------///


    private void getCategoriesFromEstabelecimento() {
        List<MenuCategory> menuCategoryList = new ArrayList<>();

        for (int i = 0; i <estabelecimentoList.size() ; i++) {
            Estabelecimento estab = estabelecimentoList.get(i);
            MenuCategory menuCategory = new MenuCategory();
            menuCategory.setIdTipo(estab.tipoDeEstabelecimento.idTipo);
            menuCategory.setDescricao(estab.tipoDeEstabelecimento.descricao);
            menuCategoryList.add(menuCategory);
        }

//        // Order the list by regist date.
//        Collections.sort(menuCategories, new MenuCategory());

//        List<MenuCategory> allEvents = new ArrayList<>(menuCategoryList);
        List<MenuCategory> noRepeat = new ArrayList<>();

        for (MenuCategory event : menuCategoryList) {
            boolean isFound = false;
            // check if the event name exists in noRepeat
            for (MenuCategory e : noRepeat) {
                if (e.getDescricao().equals(event.getDescricao()) || (e.equals(event))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);
        }


        menuCategoryList.clear();
        fillList(noRepeat);
    }

    private void fillList(List<MenuCategory> menuCategoryList) {

        if (categoriaEstabelecimentoList!=null)
            categoriaEstabelecimentoList.clear();

        for (int i = 0; i <menuCategoryList.size() ; i++) {

            List<Estabelecimento> newEstab = new ArrayList<>();
            for (Estabelecimento e : estabelecimentoList) {
                if (e.tipoDeEstabelecimento.idTipo == menuCategoryList.get(i).getIdTipo()){
                    if (!newEstab.contains(e)){
                        newEstab.add(e);
                    }
                }
            }
            categoriaEstabelecimentoList.add(new CategoriaEstabelecimento(menuCategoryList.get(i), newEstab));
        }

        menuCategoryList.clear();
        setUpAdapters();



    }

    private void setUpAdapters() {

        waitingDialog.dismiss();
        recyclerViewMenu.setHasFixedSize(true);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        mainRecyclerAdapter = new MainRecyclerAdapter(getContext(),categoriaEstabelecimentoList);
        recyclerViewMenu.setAdapter(mainRecyclerAdapter);
    }


    private void mostarMsnErro(){

        if (errorLayout.getVisibility() == View.GONE){
            errorLayout.setVisibility(View.VISIBLE);

            coordinatorLayout.setVisibility(View.GONE);


        }

        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinatorLayout.setVisibility(View.VISIBLE);

                errorLayout.setVisibility(View.GONE);

            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(0);
        }
    };





    @Override
    public void onResume() {

        verifConecxaoTOPSLIDEIMAGE();
        Log.d(TAG, "onResume: ");

        if (Common.todosEstabelecimentoList == null)
            Common.todosEstabelecimentoList = new ArrayList<>();
        else
            Common.todosEstabelecimentoList.clear();

        if (errorLayout.getVisibility() == View.VISIBLE){
            errorLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

        switch (AppPrefsSettings.getInstance().getEstabFilterView()){
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_TODOS
            case 0:
                verifConecxaoEstabelecimento_TODOS();
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_PERTO_DE_MIM
            case 1:

                buildLocationCallBack();
                createLocationRequest();
                displayLocation();
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_MAIS_POPULARES
            case 2:
                verifConecxaoEstabelecimento_MAIS_POPULARES();
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_ALTAS_HORAS
            case 3:
                verifConecxaoEstabelecimento_ALTASHORAS();
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_TODOS
            default:
                verifConecxaoEstabelecimento_TODOS();
                break;
        }


        super.onResume();
        loopingViewPager.resumeAutoScroll();
        slideHandler.postDelayed(sliderRunnable,TIME_DELAY); // Slide duration 3 seconds

    }

    @Override
    public void onPause() {
        slideHandler.removeCallbacks(sliderRunnable);
        loopingViewPager.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {


        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_options_search, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setQueryHint(getString(R.string.pesquisar));
//
//        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
//        theTextArea.setHintTextColor(ContextCompat.getColor(getContext(), R.color.xpress_green));
//        theTextArea.setTextColor(ContextCompat.getColor(getContext(), R.color.search_text_color));
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//
//
//
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (mainRecyclerAdapter!=null)
//                    mainRecyclerAdapter.getFilter().filter(newText);
//
//
//                return false;
//            }
//        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                mostraTelaDePesquisas();
                return true;

            case R.id.action_filtros:
                mostraTelaDosFiltros();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void mostraTelaDePesquisas() {
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.nav_menu_pesquisar);
    }

    private void mostraTelaDosFiltros() {
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.nav_menu_escolher_filtros);
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

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
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


                    latitude = Common.mLastLocation.getLatitude();
                    longitude = Common.mLastLocation.getLongitude();
                    LatLng center = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());latitude = Common.mLastLocation.getLatitude();


                    getMyEndereco = getMyAddress(center);

                    txtTopMyLocation.setText(getMyEndereco);


                    verifConecxaoEstabelecimento_PERTO_DE_MIM(latitude,longitude);


                    Log.d(TAG, String.format("Your location was changed : %f / %f",
                            latitude, longitude));

                } else {
                    Log.d(TAG, "Can not get your location");
                }
            }
        });

    }

    private void displayLocationOnTop() {

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


                    LatLng center = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());latitude = Common.mLastLocation.getLatitude();
                    latitude = Common.mLastLocation.getLatitude();
                    longitude = Common.mLastLocation.getLongitude();

                    getMyEndereco = getMyAddress(center);

                    txtTopMyLocation.setText(getMyEndereco);



                    Log.d(TAG, String.format("Your location was changed : %f / %f",
                            latitude, longitude));

                } else {
                    Log.d(TAG, "Can not get your location");
                }
            }
        });

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


}
