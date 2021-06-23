package ao.co.proitconsulting.xpress.fragmentos.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.homeEstab.MainRecyclerAdapter;
import ao.co.proitconsulting.xpress.adapters.topSlide.TopImageSlideAdapter;
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
    private HomeViewModel homeViewModel;
    private View view;

    private LoopingViewPager loopingViewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    private TabLayout tabLayout;
    private RecyclerView recyclerViewMenu;

    private AlertDialog waitingDialog;


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

    public HomeFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();


        homeViewModel.getListMutableLiveData().observe(this, new Observer<List<TopSlideImages>>() {
            @Override
            public void onChanged(List<TopSlideImages> topSlideImages) {
                TopImageSlideAdapter topImageSlideAdapter = new TopImageSlideAdapter(getContext(),topSlideImages,true);
                loopingViewPager.setAdapter(topImageSlideAdapter);
                tabLayout.setupWithViewPager(loopingViewPager,true);

//                dotscount = loopingViewPager.getIndicatorCount()+1;
//
//                Log.d(TAG, "loopingViewPager: dotscount "+dotscount);
//                dots = new ImageView[dotscount];
//
//                for(int i = 0; i < dotscount; i++){
//
//                    dots[i] = new ImageView(getContext());
//                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_non_active_dot));
//
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                    params.setMargins(8, 0, 8, 0);
//
//                    sliderDotspanel.addView(dots[i], params);
//
//                }
//
//                dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_active_dot));
//
//                loopingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                    @Override
//                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                    }
//
//                    @Override
//                    public void onPageSelected(int position) {
//                        for(int i = position; i< dotscount; i++){
//                            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_non_active_dot));
//                        }
//
//                        Log.d(TAG, "onPageSelected: Position "+position);
//
//                        dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.linear_slide_active_dot));
//                    }
//
//                    @Override
//                    public void onPageScrollStateChanged(int state) {
//
//                    }
//                });


            }
        });





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

//        sliderDotspanel = view.findViewById(R.id.SliderDots);

        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);

        tabLayout = view.findViewById(R.id.tab_layout);

        loopingViewPager.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);

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

                        loopingViewPager.setVisibility(View.VISIBLE);
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

                        loopingViewPager.setVisibility(View.VISIBLE);
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
                        loopingViewPager.setVisibility(View.VISIBLE);
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
                        loopingViewPager.setVisibility(View.VISIBLE);
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





    @Override
    public void onResume() {
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
    }

    @Override
    public void onPause() {
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


                    verifConecxaoEstabelecimento_PERTO_DE_MIM(latitude,longitude);


                    Log.d(TAG, String.format("Your location was changed : %f / %f",
                            latitude, longitude));

                } else {
                    Log.d(TAG, "Can not get your location");
                }
            }
        });

    }
}
